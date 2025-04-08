package com.h2h.test.it;

import com.auth0.jwt.JWT;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.h2h.pda.PdaWsApplication;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.service.ServiceCreateParams;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.service.ServiceMeta;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.vault.SealStatusResponse;
import com.h2h.pda.repository.SystemTokenRepository;
import com.h2h.pda.service.api.SessionService;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.h2h.pda.jwt.SecurityConstants.*;
import static com.h2h.pda.service.api.VaultService.VAULT_ROOT_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles
@ComponentScan(basePackages = {"com.h2h.pda"})
@ContextConfiguration(classes = PdaWsApplication.class, initializers = {BaseIntegrationTests.Initializer.class})
public abstract class BaseIntegrationTests {
    public static final String DEFAULT_USER = "admin2";
    public static final String DEFAULT_USER_PASSWORD = "H2HSecure123";
    static Logger log = LoggerFactory.getLogger(BaseIntegrationTests.class);
    private static boolean isPartitionEnabled = true;
    @LocalServerPort
    protected int port;
    protected RestTemplate restTemplate;
    @Autowired
    SessionService sessionService;
    @Autowired
    SystemTokenRepository systemTokenRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    TransactionTemplate transactionTemplate;
    private String token;
    private String vaultKey;
    private String rootToken;

    protected String getToken() {
        return token;
    }

    @BeforeEach
    public void init() {
        restTemplate = new RestTemplateBuilder().build();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));
        restTemplate.setInterceptors(Collections.singletonList((HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.debug("Request to {} {} body: {}", request.getMethod(), request.getHeaders(), request.getURI());
            return execution.execute(request, body);
        }));
        if (this.rootToken == null)
            checkVault();
    }

    @BeforeEach
    void initDB() {
        if (isPartitionEnabled) {
            LocalDate beforeDate = LocalDate.now().minusDays(1L);
            LocalDate afterDate = LocalDate.now().plusDays(1L);
            String createPartition = "CREATE TABLE IF NOT EXISTS %s PARTITION OF %s FOR VALUES FROM ('%s') TO ('%s')";
            String partitionTable = "exec_shell_trace_data" + '_' + beforeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String createPartitionQuery = String.format(createPartition,
                    partitionTable, "exec_shell_trace_data",
                    beforeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), afterDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            transactionTemplate.execute(transactionStatus -> {
                entityManager.createNativeQuery(createPartitionQuery).executeUpdate();
                transactionStatus.flush();
                return null;
            });
            isPartitionEnabled = false;
        }
    }

    public void checkVault() {
        ResponseEntity<Boolean> call = call("/api/v1/vault/init", HttpMethod.GET, Boolean.class);

        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        if (!call.getBody()) {
            SealInfo info = new SealInfo().setSecretThreshold(1).setSecretShares(1);
            ResponseEntity<InitRequest> call1 = call("/api/v1/vault/init", HttpMethod.POST, info, InitRequest.class);

            AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(call1.getBody().getKeys().size()).isEqualTo(1);

            this.vaultKey = call1.getBody().getKeys().get(0);
            this.rootToken = call1.getBody().getRootToken();

            AuthUserPass pass = new AuthUserPass().setUsername(DEFAULT_USER).setPassword(DEFAULT_USER_PASSWORD).setRootToken(call1.getBody().getRootToken()).setPolicy("inventorys");

            ResponseEntity<Void> call2 = call("/api/v1/vault/auth-user", HttpMethod.POST, pass, Void.class);

            assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<SealStatusResponse> call3 = call("/api/v1/vault/status", HttpMethod.POST, SealStatusResponse.class);
            assertThat(call3.getBody().isSealed()).isEqualTo(false);

        } else {
            ResponseEntity<SealStatusResponse> call1 = call("/api/v1/vault/status", HttpMethod.POST, SealStatusResponse.class);
            if (call1.getBody().isSealed()) {
                SealRequest request = new SealRequest();
                request.setKey(vaultKey);
                ResponseEntity<SealStatusResponse> call2 = call("/api/v1/vault/unlock", HttpMethod.POST, request, SealStatusResponse.class);
                assertThat(call2.getBody().isSealed()).isEqualTo(false);
            }
        }
    }

    protected String loginWithDefaultUserToken() {
        return loginWithUserToken(DEFAULT_USER, UserRole.ADMIN.getName());
    }

    public String loginWithUserToken(String name, String role) {
        UserRole userRole = UserRole.of(role) == UserRole.UNKNOWN_ROLE ? UserRole.USER : UserRole.of(role);

        return loginWithUser(name, userRole);
    }

    String loginWithUser(String name, UserRole role) {
        Integer sessionId = sessionService.start(name,
                ServiceMeta.PDA, "123", "localhost");

        this.token = JWT.create()
                .withSubject(name)
                .withClaim(VAULT_TOKEN, systemTokenRepository.findByName(VAULT_ROOT_TOKEN).get().getToken())
                .withClaim(PDA_AUTH_ID, sessionId)
                .withClaim(USER_ROLE, role.getName())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.MILLIS)))
                .sign(HMAC512(SECRET.getBytes()));

        return this.token;
    }

    <S> ResponseEntity<S> call(String url, HttpMethod method, Class<S> class1) {
        return call(url, method, null, class1);
    }

    <T, S> ResponseEntity<S> call(String url, HttpMethod method, T t, Class<S> class1) {
        HttpEntity<T> entity = new HttpEntity<>(t, token == null ? null : getHeaders());

        ResponseEntity<S> exchange = restTemplate.exchange(url, method, entity, class1);
        assertThat(exchange).isNotNull();
        return exchange;
    }

    <T, S> ResponseEntity<S> call(String url, HttpMethod method, T t, ParameterizedTypeReference<S> class1) {
        HttpEntity<T> entity = new HttpEntity<>(t, token == null ? null : getHeaders());

        ResponseEntity<S> exchange = restTemplate.exchange(url, method, entity, class1);
        assertThat(exchange).isNotNull();
        return exchange;
    }

    <T, S> ResponseEntity<S> callWithForm(String url, HttpMethod method, T t, Class<S> class1) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers = getHeaders();
        }

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<T> entity = new HttpEntity<>(t, headers);

        return restTemplate.exchange(url, method, entity, class1);
    }

    UserEntity getLoggedInUser() {
        ResponseEntity<UserEntity> user = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);
        return user.getBody();
    }

    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", token));

        return headers;
    }

    public String createGroup() {
        return createGroup(UUID.randomUUID().toString());
    }

    public String createGroup(String name) {
        GroupsEntityWrapper groupParams = new GroupsEntityWrapper();
        groupParams.setGroupName(name);
        groupParams.setGroupType("test group");
        groupParams.setDescription("test description");
        groupParams.setGroupCategory("normal");
        ResponseEntity<String> createCall = call("/api/v1/group/create", HttpMethod.POST, groupParams, String.class);
        Assertions.assertThat(createCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        return createCall.getBody();
    }

    public void deleteGroup(String groupId) {
        ResponseEntity<Void> deleteCall = call("/api/v1/group/" + groupId, HttpMethod.DELETE, Void.class);
        Assertions.assertThat(deleteCall.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public String createService(ServiceEntity serviceEntity, String groupId) {
        ServiceCreateParams params = new ServiceCreateParams();
        params.setIpaddress(generateRandomIpAddress());
        params.setPort(22);
        Credential inventory = new Credential();
        inventory.setUsername("admin");

        params.setGroupid(groupId);

        params.setVaults(Collections.singletonList(inventory));

        params.setDbname("test");
        params.setServiceEntity(new ServiceEntityWrapper(serviceEntity));
        params.setAdmin(inventory);

        // success service added
        ResponseEntity<String> call = call("/api/v1/service/service", HttpMethod.POST, params, String.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        return call.getBody();
    }

    public String createTenant() {
        TenantDTO tenantDTO = new TenantDTO();

        tenantDTO.setCompanyName("test company");
        tenantDTO.setCountry("Turkey");
        ResponseEntity<String> callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
        AssertionsForClassTypes.assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        return callString.getBody();
    }

    public void deleteTenant(String tenantId) {
        ResponseEntity<Void> callVoid = call("/api/v1/tenant/" + tenantId, HttpMethod.DELETE, Void.class);
        AssertionsForClassTypes.assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public UserEntity createUser(String tenanId) {

        UserCreateParams userParams = new UserCreateParams();
        Password password = new Password();
        password.setUserPassword("123123123");
        userParams.setPassword(password);
        UserDTO entity = new UserDTO();
        entity.setFirstName("deneme");
        entity.setLastName("deneme");
        entity.setUsername(getSaltString().toLowerCase());
        entity.setEmail(getSaltString() + "@h2hsecure.com");
        entity.setPhone("+905555555555");
        entity.setRole(UserRole.USER);
        entity.setExternal(false);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.3");

        userParams.setUserEntity(entity);
        userParams.setIpAddress(ipAddresses);
        ResponseEntity<String> call = call("/api/v1/user/create", HttpMethod.POST, userParams, String.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
        entity.setUserId(call.getBody());

        return entity.unWrap();
    }

    public UserEntity createAdminUser(String tenanId) {

        UserCreateParams userParams = new UserCreateParams();
        Password password = new Password();
        password.setUserPassword("123123123");
        userParams.setPassword(password);
        UserDTO entity = new UserDTO();
        entity.setFirstName("deneme");
        entity.setLastName("deneme");
        entity.setUsername(getSaltString().toLowerCase());
        entity.setEmail(getSaltString() + "@h2hsecure.com");
        entity.setPhone("+905555555555");
        entity.setRole(UserRole.ADMIN);
        entity.setExternal(false);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.3");

        userParams.setUserEntity(entity);
        userParams.setIpAddress(ipAddresses);
        ResponseEntity<String> call = call("/api/v1/user/create", HttpMethod.POST, userParams, String.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
        entity.setUserId(call.getBody());

        return entity.unWrap();
    }

    public UserEntity createExternalUser(String tenanId) {

        UserCreateParams userParams = new UserCreateParams();
        Password password = new Password();
        password.setUserPassword("123123123");
        userParams.setPassword(password);
        UserDTO entity = new UserDTO();
        entity.setFirstName("deneme");
        entity.setLastName("deneme");
        entity.setUsername(getSaltString().toLowerCase());
        entity.setEmail(getSaltString() + "@h2hsecure.com");
        entity.setPhone("+905555555555");
        entity.setRole(UserRole.USER);
        entity.setExternal(true);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.3");

        userParams.setUserEntity(entity);
        userParams.setIpAddress(ipAddresses);
        ResponseEntity<String> call = call("/api/v1/user/create", HttpMethod.POST, userParams, String.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
        entity.setUserId(call.getBody());

        return entity.unWrap();
    }

    public UserEntity createExternalAdminUser(String tenanId) {

        UserCreateParams userParams = new UserCreateParams();
        Password password = new Password();
        password.setUserPassword("123123123");
        userParams.setPassword(password);
        UserDTO entity = new UserDTO();
        entity.setFirstName("deneme");
        entity.setLastName("deneme");
        entity.setUsername(getSaltString().toLowerCase());
        entity.setEmail(getSaltString() + "@h2hsecure.com");
        entity.setPhone("+905555555555");
        entity.setRole(UserRole.ADMIN);
        entity.setExternal(true);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.3");

        userParams.setUserEntity(entity);
        userParams.setIpAddress(ipAddresses);
        ResponseEntity<String> call = call("/api/v1/user/create", HttpMethod.POST, userParams, String.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
        entity.setUserId(call.getBody());

        return entity.unWrap();
    }

    public void deleteServices(List<String> serviceList) {
        for (String id : serviceList) {
            deleteService(id);
        }
    }

    public void deleteService(String serviceId) {
        ResponseEntity<Void> call = call("/api/v1/service/" + serviceId, HttpMethod.DELETE, Void.class);
        AssertionsForClassTypes.assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public void deleteUser(String userId) {
        ResponseEntity<Void> softDeleteCall = call("/api/v1/user/id/" + userId, HttpMethod.DELETE, Void.class);
        AssertionsForClassTypes.assertThat(softDeleteCall.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public void hardDeleteUser(String userId) {
        ResponseEntity<Void> softDeleteCall = call("/api/v1/user/harddeleteuser/" + userId, HttpMethod.DELETE, Void.class);
        AssertionsForClassTypes.assertThat(softDeleteCall.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    protected String getSaltString() {
        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString();
    }

    int randomNumber() {
        Random rn = new Random();
        return rn.nextInt(8) + 1;
    }

    String generateRandomIpAddress() {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }

    @AfterAll
    public static void clean() {
        log.info("cleaning tests");
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public Network network;
        public GenericContainer<?> postgresqlDB, pVault;

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            network = Network.newNetwork();

            Consumer<OutputFrame> pvaultConsumer = outputFrame -> {
                log.info("pvault: {}", outputFrame.getUtf8String());
            };

            Consumer<OutputFrame> postgresdbConsumer = outputFrame -> {
                log.info("postgresdb: {}", outputFrame.getUtf8String());
            };

            Consumer<CreateContainerCmd> cc = cc2 -> cc2.getHostConfig().withMemory(1024 * 1024 * 1024L);

            postgresqlDB = new GenericContainer(DockerImageName.parse(
                    "registry.h2hsecure.com/pda/postgresqldb:63cb2ea2")).
                    withNetwork(network)
                    .withEnv("POSTGRES_PASSWORD", "password").withExposedPorts(5432).
                    withNetworkAliases("postgresqldb").withLogConsumer(postgresdbConsumer);

            postgresqlDB.withCreateContainerCmdModifier(cc);

            try {
                postgresqlDB.start();

                Container.ExecResult result = postgresqlDB.execInContainer(
                        "bash", "-c", "/schema/vault/init.sh postgresqldb");
                log.info("schema result {}", result.toString());
            } catch (InterruptedException | IOException e) {
                log.error("creating schema for pvault", e);
            } catch (ContainerLaunchException cle) {
                log.error("postgresql container(s) startup failed");
                System.exit(1);
            }


            pVault = new GenericContainer(DockerImageName.parse(
                    "registry.h2hsecure.com/pda/pvault:647caaf0"))
                    .withNetwork(network)
                    .withExposedPorts(4050).withNetworkAliases("pvault").withLogConsumer(pvaultConsumer);

            try {
                pVault.start();
            } catch (ContainerLaunchException cle) {
                log.error("pvault container(s) startup failed");
                if (postgresqlDB.isRunning()) {
                    postgresqlDB.stop();
                }
                System.exit(1);
            }

            TestPropertyValues.of(
                    "spring.datasource.url=jdbc:postgresql://"
                            + postgresqlDB.getContainerIpAddress()
                            + ":" + postgresqlDB.getMappedPort(5432)
                            + "/pda",
                    "vault.endpoint=http://"
                            + pVault.getContainerIpAddress()
                            + ":" + pVault.getMappedPort(4050),
                    "opensshServer.hostname=openssh-server",
                    "opensshServer.port=22",
                    "opensshServer.username=root",
                    "opensshServer.password=password",
                    "opensshServer.new_password=new_password",
                    "opensshServer.protocol=ssh"
            ).applyTo(configurableApplicationContext.getEnvironment());

            configurableApplicationContext.addApplicationListener(event -> {
                if (event instanceof org.springframework.context.event.ContextClosedEvent) {
                    log.error("closing containers");
                    network.close();

                    if (postgresqlDB.isRunning()) {
                        postgresqlDB.close();
                    }

                    if (pVault.isRunning()) {
                        pVault.close();
                    }
                }
            });
        }
    }

}
