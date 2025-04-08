package com.h2h.test.it;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Password;
import com.h2h.pda.pojo.UserCreateParams;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.user.UserRole;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserCRUDTest extends BaseIntegrationTests {

    @Test
    @Order(100)
    public void createUserTest() {
        loginWithDefaultUserToken();

        UserCreateParams params = new UserCreateParams();
        Password password = new Password();
        password.setUserPassword("123123123");
        params.setPassword(password);
        UserEntity entity = new UserEntity();
        entity.setFirstName("deneme");
        entity.setLastName("deneme");
        entity.setUsername("testuser");
        entity.setEmail("test@h2hsecure.com");
        entity.setPhone("+905555555555");
        entity.setRole(UserRole.SYSTEM);
        entity.setExternal(false);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.3");

        params.setUserEntity(new UserDTO(entity));
        params.setIpAddress(ipAddresses);
        ResponseEntity<String> callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        String userId = callString.getBody();


        ResponseEntity<UserEntity> callUser = call("/api/v1/user/id/" + userId, HttpMethod.GET, UserEntity.class);
        assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUser.getBody()).isNotNull();

        UserEntity user = callUser.getBody();
        assertThat(user.getUsername()).isEqualTo(entity.getUsername());
        assertThat(user.getEmail()).isEqualTo(entity.getEmail());
        assertThat(user.getFirstName()).isEqualTo(entity.getFirstName());
        assertThat(user.getLastName()).isEqualTo(entity.getLastName());
        assertThat(user.getPhone()).isEqualTo(entity.getPhone());
        assertThat(user.getRole()).isEqualTo(entity.getRole());


        loginWithUserToken(entity.getUsername(), "123123123");

        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        entity.setEmail(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setEmail("");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setEmail("test");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setEmail("test@h2hsecure.com");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<Void> callVoid = call("/api/v1/user/id/" + userId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        entity.setEmail(getSaltString() + "@test.com");
        params.setPassword(null);
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        password.setUserPassword(null);
        params.setPassword(password);
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        password.setUserPassword("");
        params.setPassword(password);
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        password.setUserPassword("123123123");
        params.setPassword(password);
        params.setUserEntity(null);
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setFirstName(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setFirstName("");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        entity.setFirstName("test first name");
        entity.setLastName(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setLastName("");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        entity.setLastName("test last name");
        entity.setUsername(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setUsername("");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setUsername("testuser");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        entity.setUsername(getSaltString().toLowerCase());
        entity.setPhone(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setPhone("");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setPhone("+905555555555");
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        entity.setRole(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        entity.setRole(UserRole.UNKNOWN_ROLE);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
//        entity.setURole("test");
//        params.setUserEntity(entity);
//        try {
//            callString = call("/api/user", HttpMethod.POST, params, String.class);
//            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        } catch (HttpClientErrorException ex) {
//            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        }

        entity.setRole(UserRole.USER);
        entity.setExternal(null);
        params.setUserEntity(new UserDTO(entity));
        try {
            callString = call("/api/v1/user/create", HttpMethod.POST, params, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

/*
        ResponseEntity<UserEntity> blockCall = call("/api/userblock", HttpMethod.POST, userId, UserEntity.class);
        assertThat(blockCall.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<UserEntity> unBlockCall = call("/api/userunblock", HttpMethod.POST, userId, UserEntity.class);
        assertThat(unBlockCall.getStatusCode()).isEqualTo(HttpStatus.OK);*/
    }

    @Test
    @Order(101)
    public void getUserByUsernameTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);

        ResponseEntity<UserEntity> userCall = call("/api/v1/user/public//username/" + user.getUsername(), HttpMethod.GET, UserEntity.class);
        assertThat(userCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userCall.getBody()).isNotNull();
        UserEntity userEntity = userCall.getBody();
        assertThat(user.getRole()).isEqualTo(userEntity.getRole());
        assertThat(user.getUserId()).isEqualTo(userEntity.getUserId());
        assertThat(user.getPhone()).isEqualTo(userEntity.getPhone());
        assertThat(user.getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(user.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(user.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(user.isExternal()).isEqualTo(userEntity.isExternal());

        userCall = call("/api/v1/user/public/username/" + getSaltString(), HttpMethod.GET, UserEntity.class);
        assertThat(userCall.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        deleteTenant(tenantId);
        deleteUser(user.getUserId());
    }

    @Test
    @Order(102)
    public void getUserByUserIdTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);

        ResponseEntity<UserEntity> userCall = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.GET, UserEntity.class);
        assertThat(userCall.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userCall.getBody()).isNotNull();
        UserEntity userEntity = userCall.getBody();
        assertThat(user.getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(user.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(user.getRole()).isEqualTo(userEntity.getRole());
        assertThat(user.getPhone()).isEqualTo(userEntity.getPhone());
        assertThat(user.getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(user.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(user.isExternal()).isEqualTo(userEntity.isExternal());

        userCall = call("/api/v1/user/id/" + getSaltString(), HttpMethod.GET, UserEntity.class);
        assertThat(userCall.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        deleteUser(user.getUserId());
        deleteTenant(tenantId);
    }

    @Test
    @Order(103)
    @Disabled
    public void userListTest() {
        loginWithDefaultUserToken();

        ResponseEntity<UserEntity[]> callUsers = call("/api/v1/user", HttpMethod.GET, UserEntity[].class);
        assertThat(callUsers.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<UserEntity> userEntityList = Arrays.asList(callUsers.getBody());
        for (UserEntity user : userEntityList) {
            if (!user.getUsername().equals(DEFAULT_USER)) deleteUser(user.getUserId());
        }

        String tenantId = createTenant();
        List<UserEntity> users = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        List<String> usernames = new ArrayList<>();
        List<String> emails = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UserEntity user = createUser(tenantId);
            users.add(user);
            usernames.add(user.getUsername());
            userIds.add(user.getUserId());
            emails.add(user.getEmail());
        }

        callUsers = call("/api/v1/user", HttpMethod.GET, UserEntity[].class);
        assertThat(callUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUsers.getBody().length).isEqualTo(6);

        String deleteUserId = null;

        userEntityList = Arrays.asList(callUsers.getBody());
        for (UserEntity user : userEntityList) {
            if (!user.getUserId().equals("123")) {
                assertThat(usernames.contains(user.getUsername())).isTrue();
                assertThat(userIds.contains(user.getUserId())).isTrue();
                assertThat(emails.contains(user.getEmail())).isTrue();
                assertThat(user.getRole()).isEqualTo(users.get(0).getRole());
                assertThat(user.getPhone()).isEqualTo(users.get(0).getPhone());
                assertThat(user.getLastName()).isEqualTo(users.get(0).getLastName());
                assertThat(user.getFirstName()).isEqualTo(users.get(0).getFirstName());
                assertThat(user.isExternal()).isEqualTo(users.get(0).isExternal());
                deleteUserId = user.getUserId();
            }
        }

        deleteUser(deleteUserId);

        callUsers = call("/api/v1/user", HttpMethod.GET, UserEntity[].class);
        assertThat(callUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUsers.getBody().length).isEqualTo(5);

//        for (int i = 0; i < 5; i++) {
//            if (!users.get(i).getUserId().equals("123") && !users.get(i).getUserId().equals(deleteUserId))
//                deleteUser(users.get(i).getUserId());
//        }
        deleteTenant(tenantId);
    }

    @Test
    @Order(104)
    public void softDeleteUserTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);
        UserEntity user2 = createUser(tenantId);

        loginWithUserToken(user2.getUsername(), "123123123");
        ResponseEntity<Void> callVoid;

        try {
            callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        callVoid = call("/api/v1/user/id/test", HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        deleteUser(user2.getUserId());
        deleteTenant(tenantId);
    }

    @Test
    @Order(105)
    public void hardDeleteUserTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);
        UserEntity user2 = createUser(tenantId);

        loginWithUserToken(user2.getUsername(), "123123123");
        ResponseEntity<Void> callVoid;

        try {
            callVoid = call("/api/v1/user/harddeleteuser/" + user.getUserId(), HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        callVoid = call("/api/v1/user/harddeleteuser/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/user/harddeleteuser/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        callVoid = call("/api/v1/user/harddeleteuser/test", HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        callVoid = call("/api/v1/user/harddeleteuser/" + user2.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteTenant(tenantId);
    }

    @Test
    @Order(106)
    @Disabled
    public void updateUserTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        String tenantId2 = createTenant();
        UserEntity user = createUser(tenantId);
        UserEntity user2 = createUser(tenantId);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setEmail(getSaltString() + "@h2hsecure.com");
        userDTO.setUsername(getSaltString().toLowerCase());
        userDTO.setExternal(true);
        userDTO.setPhone("+906666666666");
        userDTO.setFirstName("updatefirstname");
        userDTO.setLastName("updatelastname");
        userDTO.setRole(UserRole.ADMIN);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.4");
        ipAddresses.add("1.1.1.5");

        userDTO.setIpAddress(ipAddresses);

        ResponseEntity<Void> callVoid = call("/api/v1/user/user", HttpMethod.PUT, userDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/" + user.getUserId(), HttpMethod.GET, UserEntity.class);
        assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUser.getBody()).isNotNull();

        UserEntity updatedUser = callUser.getBody();
        assertThat(updatedUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(updatedUser.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(updatedUser.getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(updatedUser.getPhone()).isEqualTo(userDTO.getPhone());
        assertThat(updatedUser.getRole()).isEqualTo(userDTO.getRole());
        assertThat(updatedUser.isExternal()).isEqualTo(userDTO.getExternal());

        userDTO.setRole(UserRole.USER);
        callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        userDTO.setUserId(user.getUserId());
        userDTO.setLastName("updatedlastname2");
        callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        callUser = call("/api/v1/user/" + user.getUserId(), HttpMethod.GET, UserEntity.class);
        assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUser.getBody()).isNotNull();

        updatedUser = callUser.getBody();
        assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());


        userDTO.setRole(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setRole(UserRole.UNKNOWN_ROLE);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setRole(UserRole.USER);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setRole(UserRole.USER);
        userDTO.setEmail(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setEmail("");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setEmail("null");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setEmail(user2.getEmail());
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setEmail(getSaltString() + "@h2hsecure.com");
        userDTO.setUsername(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setUsername("");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setUsername(user2.getUsername());
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setUsername(getSaltString().toLowerCase());
        userDTO.setFirstName(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setFirstName("");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setFirstName("testname");
        userDTO.setLastName(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setLastName("");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setLastName("testlastname");
        userDTO.setPhone(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setPhone("");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setPhone("+905555555555");
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        userDTO.setExternal(null);
        try {
            callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        userDTO.setExternal(false);
        userDTO.setUserId(null);
        callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        userDTO.setUserId("");
        callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        userDTO.setUserId("test");
        callVoid = call("/api/v1/user", HttpMethod.PUT, userDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        deleteTenant(tenantId);
        deleteTenant(tenantId2);
        deleteUser(user.getUserId());
        deleteUser(user2.getUserId());
    }

    @Test
    @Order(107)
    public void getUserIpAddresses(){
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);

        ResponseEntity<String[]> callIpAddress = call("/api/v1/user/ipAddresses/" + user.getUserId(), HttpMethod.GET, String[].class);
        assertThat(callIpAddress.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callIpAddress.getBody().length).isEqualTo(3);

        List<String> ipAddresses = Arrays.asList(callIpAddress.getBody());

        assertThat(ipAddresses.contains("1.1.1.1")).isTrue();
        assertThat(ipAddresses.contains("1.1.1.2")).isTrue();
        assertThat(ipAddresses.contains("1.1.1.3")).isTrue();

        deleteUser(user.getUserId());
        deleteTenant(tenantId);
    }

/*
    @Test
    @Order(107)
    public void updateUserPasswordTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");

        PasswordUpdate passwordUpdate = new PasswordUpdate();
        passwordUpdate.setCurrentPass("123123123");
        passwordUpdate.setNewPass("124124124");


        ResponseEntity<Void> callVoid = call("/api/updatepassword", HttpMethod.PUT, passwordUpdate, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        loginWithUserToken(user.getUsername(), "124124124");

        try {
            loginWithUserToken(user.getUsername(), "123123123");
        } catch (HttpClientErrorException ex) {
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @Test
    @Order(108)
    public void whoAmITest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");

        ResponseEntity<UserEntity> callUser = call("/api/v1/user/who", HttpMethod.GET, UserEntity.class);
        assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUser.getBody()).isNotNull();

        UserEntity userEntity = callUser.getBody();
        assertThat(userEntity.getRole()).isEqualTo(user.getRole());
        assertThat(userEntity.getUserId()).isEqualTo(user.getUserId());
        assertThat(userEntity.getPhone()).isEqualTo(user.getPhone());
        assertThat(userEntity.getLastName()).isEqualTo(user.getLastName());
        assertThat(userEntity.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userEntity.getEmail()).isEqualTo(user.getEmail());
        assertThat(userEntity.isExternal()).isEqualTo(user.isExternal());
        loginWithDefaultUserToken();

        deleteTenant(tenantId);
        deleteUser(user.getUserId());
    }

    @Test
    @Order(109)
    public void softDeleteExternalUserTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createExternalUser(tenantId);

        try {
            ResponseEntity<Void> callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        hardDeleteUser(user.getUserId());
        deleteTenant(tenantId);
    }

    @Test
    @Order(111)
    public void updateExternalUserTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createExternalUser(tenantId);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setEmail(getSaltString() + "@h2hsecure.com");
        userDTO.setUsername(getSaltString().toLowerCase());
        userDTO.setExternal(true);
        userDTO.setPhone("+906666666666");
        userDTO.setFirstName("updatefirstname");
        userDTO.setLastName("updatelastname");
        userDTO.setRole(UserRole.ADMIN);

        List<String> ipAddresses = new ArrayList<>();

        ipAddresses.add("1.1.1.1");
        ipAddresses.add("1.1.1.2");
        ipAddresses.add("1.1.1.4");
        ipAddresses.add("1.1.1.5");

        userDTO.setIpAddress(ipAddresses);

        try {
            ResponseEntity<Void> callVoid = call("/api/v1/user/user", HttpMethod.PUT, userDTO, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        hardDeleteUser(user.getUserId());
        deleteTenant(tenantId);
    }

    @Test
    @Order(112)
    public void unDeleteUserTest() {
        loginWithDefaultUserToken();

        String tenantId = createTenant();
        UserEntity user = createUser(tenantId);
        UserEntity externalUser = createExternalUser(tenantId);

        try {
            ResponseEntity<Void> unDeleteUserResponse = call("/api/v1/user/un-delete/" + externalUser.getUserId(), HttpMethod.POST, Void.class);
            assertThat(unDeleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        ResponseEntity<Void> unDeleteUserResponse = call("/api/v1/user/un-delete/" + user.getUserId(), HttpMethod.POST, Void.class);
        assertThat(unDeleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        deleteUser(user.getUserId());
        deleteTenant(tenantId);
    }
}
