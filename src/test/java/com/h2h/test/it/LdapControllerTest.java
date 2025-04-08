package com.h2h.test.it;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jobs.LdapSynchronizedJob;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.group.GroupUserGetParams;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.ldap.*;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.LdapService;
import com.h2h.pda.service.api.UsersOps;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.web.client.HttpClientErrorException;

import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled("Disabled until AD server has been embedded for test")
public class LdapControllerTest extends BaseIntegrationTests {

    //LDAP General Settings
    static final String LDAP_HOST = "localhost";
    static final String LDAP_PORT = "10389";
    static final String BASE_DN = "dc=pdaccess,dc=com";
    static final String BIND_DN = "cn=Administrator,cn=Users,dc=pdaccess,dc=com";
    static final String BIND_PASS = "GlABmOw5jMkG3dErLJP6tW5g";
    static final Boolean INSECURE_TLS = true;
    static final Boolean START_TLS = true;
    static final Boolean SSL = false;

    //LDAP AUTH Settings
    static final String USER_DN = "CN=Users,DC=pdaccess,DC=com";
    static final String GROUP_DN = "CN=Administrators,CN=Builtin,DC=pdaccess,DC=com";
    static final String GROUP_FILTER = "(|(memberUid={{.Username}})(member={{.UserDN}})(uniqueMember={{.UserDN}}))";
    static final String GROUP_ATTR = "cn";
    static final String USER_ATTR = "sAMAccountName";
    static final Boolean DISCOVER_DN = true;

    @Autowired
    LdapSynchronizedJob ldapSynchronizedJob;

    @Autowired
    LdapService ldapService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    LdapUserAttributes ldapUserAttributes;

    @Test
    @Order(550)
    public void checkLdapCredentialsTest() {

        loginWithDefaultUserToken();

        LdapCredential ldapCredential = new LdapCredential();
        ldapCredential.setHost(LDAP_HOST);
        ldapCredential.setPort(LDAP_PORT);
        ldapCredential.setBindDN(BIND_DN);
        ldapCredential.setBindPass(BIND_PASS);
        ldapCredential.setInsecureTLS(INSECURE_TLS);
        ldapCredential.setStartTLS(START_TLS);
        ldapCredential.setSsl(SSL);

        ResponseEntity<String> checkLdapCredentialsResponse = call("/api/v1/ldap/check", HttpMethod.POST, ldapCredential, String.class);
        assertThat(checkLdapCredentialsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            ldapCredential.setBindPass("wrong pass");
            checkLdapCredentialsResponse = call("/api/v1/ldap/check", HttpMethod.POST, ldapCredential, String.class);
            assertThat(checkLdapCredentialsResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException exception) {
            log.error("Check LDAP credentials error: {}", exception.getMessage());
            assertThat(Objects.requireNonNull(exception.getMessage())).contains("400 Bad Request");
        }
    }

    @Test
    @Order(551)
    public void saveLdapCredentials() {

        loginWithDefaultUserToken();
        checkLdapCredentialsTest();

        LdapSetting ldapSetting = new LdapSetting();
        ldapSetting.setBaseDN(BASE_DN);
        ldapSetting.setBindDN(BIND_DN);
        ldapSetting.setBindPass(BIND_PASS);
        ldapSetting.setHost(LDAP_HOST);
        ldapSetting.setPort(LDAP_PORT);
        ldapSetting.setInsecureTLS(INSECURE_TLS);
        ldapSetting.setSsl(SSL);
        ldapSetting.setStartTLS(START_TLS);
        ResponseEntity<String> saveLdapCredentialsResponse = call("/api/v1/settings/ldap/general", HttpMethod.POST, ldapSetting, String.class);
        assertThat(saveLdapCredentialsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(552)
    public void getLdapGroupsTest() {

        loginWithDefaultUserToken();
        saveLdapCredentials();

        ResponseEntity<LdapGroup[]> getLdapGroupsResponse = call("/api/v1/ldap/group", HttpMethod.GET, LdapGroup[].class);
        assertThat(getLdapGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getLdapGroupsResponse.getBody())).isNotEmpty();
    }

    @Test
    @Order(553)
    public void addLdapGroupTest() {

        loginWithDefaultUserToken();
        saveLdapCredentials();

        ResponseEntity<LdapGroup[]> getLdapGroupsResponse = call("/api/v1/ldap/group", HttpMethod.GET, LdapGroup[].class);
        assertThat(getLdapGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getLdapGroupsResponse.getBody())).isNotEmpty();

        LdapGroup[] ldapGroups = getLdapGroupsResponse.getBody();
        LdapGroup createdLdapGroup = ldapGroups[0];
        ResponseEntity<String> createLdapGroupResponse = call("/api/v1/ldap/group", HttpMethod.POST, createdLdapGroup, String.class);
        assertThat(createLdapGroupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GroupsEntityWrapper[]> groupListResponse = call("/api/v1/group/query/yours", HttpMethod.POST, new Pagination(0, 10, "create-desc"), GroupsEntityWrapper[].class);
        assertThat(groupListResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<GroupsEntityWrapper> optionalGroupsEntity = Arrays.stream(Objects.requireNonNull(groupListResponse.getBody())).filter(group -> group.getGroupName().equals(createdLdapGroup.getName())).findFirst();
        assertThat(optionalGroupsEntity).isNotEmpty();

        Optional<GroupsEntity> groupsEntityOptional = groupOps.byName(createdLdapGroup.getName());
        if (groupsEntityOptional.isPresent()) {
            String groupId = groupsEntityOptional.get().getGroupId();
            for (UserEntity userEntity : groupOps.effectiveUsers(groupId)) {
                if (!userEntity.getUsername().equals(DEFAULT_USER)) {
                    groupOps.removeUsersFrom(groupId, Collections.singletonList(userEntity.getUserId()));
                    hardDeleteUser(userEntity.getUserId());
                }
            }
            deleteGroup(groupId);
        }
    }

    @Test
    @Order(554)
    public void addLdapSyncTest() throws Exception {

        loginWithDefaultUserToken();
        saveLdapCredentials();

        createOUInLdap("groups");
        createOUInLdap("users");

        String groupName = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        createGroupInLdap(groupName);

        ResponseEntity<LdapGroup[]> getLdapGroupsResponse = call("/api/v1/ldap/group", HttpMethod.GET, LdapGroup[].class);
        assertThat(getLdapGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<LdapGroup> optionalLdapGroup = Arrays.stream(Objects.requireNonNull(getLdapGroupsResponse.getBody())).filter(ldapGroup -> ldapGroup.getName().equals(groupName)).findFirst();
        assertThat(optionalLdapGroup).isNotEmpty();

        ResponseEntity<String> createLdapGroupResponse = call("/api/v1/ldap/group", HttpMethod.POST, optionalLdapGroup.get(), String.class);
        assertThat(createLdapGroupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GroupsEntityWrapper[]> groupList = call("/api/v1/group/query/yours", HttpMethod.POST, new Pagination(0, 10, "create-desc"), GroupsEntityWrapper[].class);
        assertThat(groupList.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<GroupsEntityWrapper> optionalGroupsEntity = Arrays.stream(Objects.requireNonNull(groupList.getBody())).filter(group -> group.getGroupName().equals(groupName)).findFirst();
        assertThat(optionalGroupsEntity).isNotEmpty();

        ResponseEntity<GroupUserGetParams[]> getGroupUsersResponse = call("/api/v1/group/user/members/" + optionalGroupsEntity.get().getGroupId(), HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(getGroupUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<GroupUserGetParams> optionalGroupUser = Arrays.stream(Objects.requireNonNull(getGroupUsersResponse.getBody())).filter(groupUser -> groupUser.getUser().getUsername().equals(DEFAULT_USER)).findFirst();
        assertThat(optionalGroupUser).isNotEmpty();

        String username = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        String userPass = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        createUserInLdap(username, userPass);
        addMemberToGroupInLdap(username, groupName);
        ldapSynchronizedJob.synchronizeGroupMembers();

        getGroupUsersResponse = call("/api/v1/group/user/members/" + optionalGroupsEntity.get().getGroupId(), HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(getGroupUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getGroupUsersResponse.getBody())).anyMatch(groupUser -> groupUser.getUser().getUsername().equals(username));
        assertThat(Objects.requireNonNull(getGroupUsersResponse.getBody())).anyMatch(groupUser -> groupUser.getUser().getUsername().equals(DEFAULT_USER));

        removeMemberFromGroupInLdap(username, groupName);
        ldapSynchronizedJob.synchronizeGroupMembers();

        getGroupUsersResponse = call("/api/v1/group/user/members/" + optionalGroupsEntity.get().getGroupId(), HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(getGroupUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getGroupUsersResponse.getBody())).noneMatch(groupUser -> groupUser.getUser().getUsername().equals(username));
        assertThat(getGroupUsersResponse.getBody()).anyMatch(groupUser -> groupUser.getUser().getUsername().equals(DEFAULT_USER));

        Optional<UserEntity> optionalUserEntity = usersOps.byName(username);
        if (optionalUserEntity.isPresent()) {
            String userId = optionalUserEntity.get().getUserId();
            groupOps.removeUsersFrom(optionalGroupsEntity.get().getGroupId(), Collections.singletonList(userId));
            hardDeleteUser(userId);
        }
        deleteGroup(optionalGroupsEntity.get().getGroupId());
        removeUserInLdap(username);
        removeGroupInLdap(groupName);
        removeOUInLdap("groups");
        removeOUInLdap("users");
    }

    @Test
    @Order(555)
    public void ldapUserSyncTest() throws Exception {
        loginWithDefaultUserToken();
        saveLdapCredentials();

        createOUInLdap("groups");
        createOUInLdap("users");

        String groupName = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        createGroupInLdap(groupName);

        ResponseEntity<LdapGroup[]> getLdapGroupsResponse = call("/api/v1/ldap/group", HttpMethod.GET, LdapGroup[].class);
        assertThat(getLdapGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<LdapGroup> optionalLdapGroup = Arrays.stream(Objects.requireNonNull(getLdapGroupsResponse.getBody())).filter(ldapGroup -> ldapGroup.getName().equals(groupName)).findFirst();
        assertThat(optionalLdapGroup).isNotEmpty();

        ResponseEntity<String> createLdapGroupResponse = call("/api/v1/ldap/group", HttpMethod.POST, optionalLdapGroup.get(), String.class);
        assertThat(createLdapGroupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<GroupsEntityWrapper[]> groupList = call("/api/v1/group/query/yours", HttpMethod.POST, new Pagination(0, 10, "create-desc"), GroupsEntityWrapper[].class);
        assertThat(groupList.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<GroupsEntityWrapper> optionalGroupsEntity = Arrays.stream(Objects.requireNonNull(groupList.getBody())).filter(group -> group.getGroupName().equals(groupName)).findFirst();
        assertThat(optionalGroupsEntity).isNotEmpty();

        String username = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        String userPass = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        createUserInLdap(username, userPass);
        addMemberToGroupInLdap(username, groupName);
        ldapSynchronizedJob.synchronizeGroupMembers();

        ResponseEntity<GroupUserGetParams[]> getGroupUsersResponse = call("/api/v1/group/user/members/" + optionalGroupsEntity.get().getGroupId(), HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(getGroupUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getGroupUsersResponse.getBody()).anyMatch(groupUserGetParams -> groupUserGetParams.getUser().getUsername().equals(username));

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setFirstName(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));
        userDTO.setLastName(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));
        userDTO.setPhone(RandomStringUtils.randomNumeric(10).toLowerCase(Locale.ENGLISH));
        userDTO.setEmail(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH) + "@email.com");

        updateUserAttributes(userDTO);

        ldapSynchronizedJob.synchronizeLdapUsers();

        getGroupUsersResponse = call("/api/v1/group/user/members/" + optionalGroupsEntity.get().getGroupId(), HttpMethod.GET, GroupUserGetParams[].class);
        assertThat(getGroupUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Optional<GroupUserGetParams> optionalGroupUser = Arrays.stream(Objects.requireNonNull(getGroupUsersResponse.getBody())).filter(groupUserGetParams -> groupUserGetParams.getUser().getUsername().equals(username)).findFirst();
        assertThat(optionalGroupUser.isPresent()).isTrue();
        UserDTO updatedUser = optionalGroupUser.get().getUser();

        assertThat(userDTO.getUsername()).isEqualTo(updatedUser.getUsername());
        assertThat(userDTO.getFirstName()).isEqualTo(updatedUser.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(updatedUser.getLastName());
        assertThat(userDTO.getPhone()).isEqualTo(updatedUser.getPhone());
        assertThat(userDTO.getEmail()).isEqualTo(updatedUser.getEmail());

        Optional<UserEntity> optionalUserEntity = usersOps.byName(username);
        if (optionalUserEntity.isPresent()) {
            String userId = optionalUserEntity.get().getUserId();
            groupOps.removeUsersFrom(optionalGroupsEntity.get().getGroupId(), Collections.singletonList(userId));
            hardDeleteUser(userId);
        }
        deleteGroup(optionalGroupsEntity.get().getGroupId());
        removeUserInLdap(username);
        removeGroupInLdap(groupName);
        removeOUInLdap("groups");
        removeOUInLdap("users");
    }

    @Test
    @Order(556)
    public void ldapUserAuthSettingsTest() {

        loginWithDefaultUserToken();
        saveLdapCredentials();

        LdapVaultSetting ldapVaultSetting = new LdapVaultSetting();
        ldapVaultSetting.setUserDN(USER_DN);
        ldapVaultSetting.setGroupDN(GROUP_DN);
        ldapVaultSetting.setUserAttr(USER_ATTR);
        ldapVaultSetting.setGroupAttr(GROUP_ATTR);
        ldapVaultSetting.setGroupFilter(GROUP_FILTER);
        ldapVaultSetting.setDiscoverDN(DISCOVER_DN);

        ResponseEntity<String> checkLdapCredentialsResponse = call("/api/v1/settings/ldap/vault", HttpMethod.POST, ldapVaultSetting, String.class);
        assertThat(checkLdapCredentialsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getLdapEnableResponse = call("/api/v1/settings/ldap/vault/enable", HttpMethod.POST, String.class);
        assertThat(getLdapEnableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getLdapDisableResponse = call("/api/v1/settings/ldap/vault/disable", HttpMethod.POST, String.class);
        assertThat(getLdapDisableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(557)
    public void ldapAdminAuthSettingsTest() {

        loginWithDefaultUserToken();
        saveLdapCredentials();

        LdapVaultSetting ldapVaultSetting = new LdapVaultSetting();
        ldapVaultSetting.setUserDN(USER_DN);
        ldapVaultSetting.setGroupDN(GROUP_DN);
        ldapVaultSetting.setUserAttr(USER_ATTR);
        ldapVaultSetting.setGroupAttr(GROUP_ATTR);
        ldapVaultSetting.setGroupFilter(GROUP_FILTER);
        ldapVaultSetting.setDiscoverDN(DISCOVER_DN);

        ResponseEntity<String> checkLdapCredentialsResponse = call("/api/v1/settings/ldap/vault/admin", HttpMethod.POST, ldapVaultSetting, String.class);
        assertThat(checkLdapCredentialsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getLdapEnableResponse = call("/api/v1/settings/ldap/vault/admin/enable", HttpMethod.POST, String.class);
        assertThat(getLdapEnableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getLdapDisableResponse = call("/api/v1/settings/ldap/vault/admin/disable", HttpMethod.POST, String.class);
        assertThat(getLdapDisableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(558)
    public void addLdapParentGroupTest() {

        loginWithDefaultUserToken();
        saveLdapCredentials();

        ResponseEntity<LdapGroup[]> getLdapGroupsResponse = call("/api/v1/ldap/group", HttpMethod.GET, LdapGroup[].class);
        assertThat(getLdapGroupsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getLdapGroupsResponse.getBody())).isNotEmpty();

        LdapGroup[] ldapGroups = getLdapGroupsResponse.getBody();
        LdapGroup parentLdapGroup = ldapGroups[0];
        ResponseEntity<String> createLdapGroupResponse = call("/api/v1/ldap/group", HttpMethod.POST, parentLdapGroup, String.class);
        assertThat(createLdapGroupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String parentGroupId = createLdapGroupResponse.getBody();
        assertThat(parentGroupId).isNotNull();

        LdapGroup childLdapGroup = ldapGroups[1];
        childLdapGroup.setParentGroup(parentGroupId);
        ResponseEntity<String> createLdapGroupResponse2 = call("/api/v1/ldap/group", HttpMethod.POST, childLdapGroup, String.class);
        assertThat(createLdapGroupResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        String childGroupId = createLdapGroupResponse2.getBody();
        assertThat(childGroupId).isNotNull();

        ResponseEntity<GroupsEntityWrapper[]> groupListResponse = call("/api/v1/group/query/all", HttpMethod.POST, new Pagination(0, 10, "create-desc"), GroupsEntityWrapper[].class);
        assertThat(groupListResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<GroupsEntityWrapper> optionalParentGroup = Arrays.stream(Objects.requireNonNull(groupListResponse.getBody())).filter(group -> group.getGroupName().equals(parentLdapGroup.getName())).findFirst();
        assertThat(optionalParentGroup).isNotEmpty();

        Optional<GroupsEntityWrapper> optionalChildGroup = Arrays.stream(Objects.requireNonNull(groupListResponse.getBody())).filter(group -> group.getGroupName().equals(childLdapGroup.getName())).findFirst();
        assertThat(optionalChildGroup).isNotEmpty();

        GroupsEntityWrapper parentGroup = optionalParentGroup.get();
        GroupsEntityWrapper childGroup = optionalChildGroup.get();

        assertThat(parentGroup.getParent()).isNull();
        assertThat(childGroup.getParent()).isEqualTo(parentGroupId);

        Optional<GroupsEntity> groupsEntityOptional = groupOps.byName(parentLdapGroup.getName());
        if (groupsEntityOptional.isPresent()) {
            String groupId = groupsEntityOptional.get().getGroupId();
            for (UserEntity userEntity : groupOps.effectiveUsers(groupId)) {
                if (!userEntity.getUsername().equals(DEFAULT_USER)) {
                    groupOps.removeUsersFrom(groupId, Collections.singletonList(userEntity.getUserId()));
                    hardDeleteUser(userEntity.getUserId());
                }
            }
            deleteGroup(groupId);
        }

        groupsEntityOptional = groupOps.byName(childLdapGroup.getName());
        if (groupsEntityOptional.isPresent()) {
            String groupId = groupsEntityOptional.get().getGroupId();
            for (UserEntity userEntity : groupOps.effectiveUsers(groupId)) {
                if (!userEntity.getUsername().equals(DEFAULT_USER)) {
                    groupOps.removeUsersFrom(groupId, Collections.singletonList(userEntity.getUserId()));
                    hardDeleteUser(userEntity.getUserId());
                }
            }
            deleteGroup(groupId);
        }
    }

    public void createUserInLdap(String username, String password) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "users")
                .add("cn", username)
                .build();
        DirContextAdapter context = new DirContextAdapter(dn);

        context.setAttributeValues(
                "objectclass",
                new String[]
                        { "top",
                                "person",
                                "organizationalPerson",
                                "user" });
        context.setAttributeValue("cn", username);
        context.setAttributeValue("sn", username);
        context.setAttributeValue("sAMAccountName", username);
        context.setAttributeValue("userPassword", MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8)));

        ldapService.getTemplateWrapper().getLdapTemplate().bind(context);
    }

    public void removeUserInLdap(String username) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "users")
                .add("cn", username)
                .build();
        ldapService.getTemplateWrapper().getLdapTemplate().unbind(dn);
    }

    public void createGroupInLdap(String groupName) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();
        DirContextAdapter context = new DirContextAdapter(dn);

        context.setAttributeValues(
                "objectClass",
                new String[]
                        { "top",
                                "group"
                        });
        context.setAttributeValue("cn", groupName);

        ldapService.getTemplateWrapper().getLdapTemplate().bind(context);
    }

    public void removeGroupInLdap(String groupName) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();
        ldapService.getTemplateWrapper().getLdapTemplate().unbind(dn);
    }

    public void addMemberToGroupInLdap(String username, String groupName) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();
        DirContextOperations ctx = ldapService.getTemplateWrapper().getLdapTemplate().lookupContext(dn);

        Name userDn = LdapNameBuilder
                .newInstance(ldapService.getTemplateWrapper().getLdapContextSource().getBaseLdapName())
                .add("ou", "users")
                .add("cn", username)
                .build();

        ctx.addAttributeValue("member", userDn.toString());
        ldapService.getTemplateWrapper().getLdapTemplate().modifyAttributes(ctx);
    }

    public void updateUserAttributes(UserDTO userDTO) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "users")
                .add("cn", userDTO.getUsername())
                .build();
        DirContextOperations ctx = ldapService.getTemplateWrapper().getLdapTemplate().lookupContext(dn);
        ctx.setAttributeValue(ldapUserAttributes.getFirstName(), userDTO.getFirstName());
        ctx.setAttributeValue(ldapUserAttributes.getLastName(), userDTO.getLastName());
        ctx.setAttributeValue(ldapUserAttributes.getMail(), userDTO.getEmail());
        ctx.setAttributeValue(ldapUserAttributes.getTelephoneNumber(), userDTO.getPhone());
        ldapService.getTemplateWrapper().getLdapTemplate().modifyAttributes(ctx);
    }

    public DirContextOperations updateAttribute(DirContextOperations ctx, String attributeName, String attributeValue) throws Exception {
        try {
            ctx.getAttributes(attributeName);
            ctx.setAttributeValue(attributeName, attributeValue);
        } catch (NameNotFoundException exception) {
            ctx.addAttributeValue(attributeName, attributeValue);
        }

        return ctx;
    }

    public void removeMemberFromGroupInLdap(String username, String groupName) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();

        Name userDn = LdapNameBuilder
                .newInstance(ldapService.getTemplateWrapper().getLdapContextSource().getBaseLdapName())
                .add("ou", "users")
                .add("cn", username)
                .build();

        ldapService.getTemplateWrapper().getLdapTemplate().modifyAttributes(dn, new ModificationItem[] {
                new ModificationItem(
                        DirContext.REMOVE_ATTRIBUTE,
                        new BasicAttribute("member", userDn.toString()))
        });
    }

    public void createOUInLdap(String ou) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", ou)
                .build();

        DirContextAdapter context = new DirContextAdapter(dn);
        context.setAttributeValues(
                "objectClass",
                new String[]
                        { "top",
                                "organizationalUnit"
                        });
        context.setAttributeValue("ou", ou);

        ldapService.getTemplateWrapper().getLdapTemplate().bind(context);
    }

    public void removeOUInLdap(String ou) throws Exception {
        Name dn = LdapNameBuilder
                .newInstance()
                .add("ou", ou)
                .build();
        ldapService.getTemplateWrapper().getLdapTemplate().unbind(dn);
    }

}
