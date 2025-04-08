package com.h2h.test.it;

import com.h2h.pda.entity.PolicyEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.policy.PolicyCreateParam;
import com.h2h.pda.pojo.policy.PolicyEntityWrapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
public class PolicyDAOControllerTests extends BaseIntegrationTests {

    @Test
    @Order(480)
    public void groupPolicyCreateTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();
        PolicyCreateParam policyCreateParam = createPolicy(groupId);
        PolicyEntityWrapper policyEntity = policyCreateParam.getPolicyEntity();
        String policyId = policyEntity.getId();

        ResponseEntity<PolicyEntityWrapper> callPolicy = call("/api/v1/policy/id/" + policyId, HttpMethod.GET, PolicyEntityWrapper.class);
        assertThat(callPolicy.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callPolicy.getBody()).isNotNull();

        PolicyEntityWrapper policyEntity1 = callPolicy.getBody();
        assertThat(policyEntity.getName()).isEqualTo(policyEntity1.getName());
//        assertThat(policyEntity.getBehavior()).isEqualTo(policyEntity1.getBehavior());
//        assertThat(policyEntity.getOperatingsystem()).isEqualTo(policyEntity1.getOperatingsystem());
//        assertThat(policyEntity.getServicemeta()).isEqualTo(policyEntity1.getServicemeta());
//        assertThat(policyEntity.getServicetype()).isEqualTo(policyEntity1.getServicetype());
//        assertThat(policyEntity1.getUpperid()).isEqualTo(groupId);
//        assertThat(policyEntity1.getIdtype()).isEqualTo("group");

        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");
        try {
            ResponseEntity<String>callString = call("/api/v1/policy", HttpMethod.PUT, policyCreateParam, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (String userId : policyCreateParam.getUserList()) {
            ResponseEntity<UserEntity> callUser = call("/api/v1/user/id/" + userId, HttpMethod.GET, UserEntity.class);
            assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
            user = callUser.getBody();
            deleteUser(user.getUserId());
        }
        deleteTenant(tenantId);

        ResponseEntity<Void> callVoid = call("/api/v1/policy/delete/"+policyId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(481)
    @Disabled
    public void updateGroupPolicyTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();
        PolicyCreateParam policyCreateParam = createPolicy(groupId);
        PolicyEntityWrapper policyEntity = policyCreateParam.getPolicyEntity();
        String policyId = policyEntity.getId();
        List<String> userList = policyCreateParam.getUserList();

        ResponseEntity<PolicyEntityWrapper> callPolicy = call("/api/v1/policy/id/" + policyId, HttpMethod.GET, PolicyEntityWrapper.class);
        assertThat(callPolicy.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callPolicy.getBody()).isNotNull();

        policyEntity = callPolicy.getBody();

        policyEntity.setName("test policy update");
//        policyEntity.setBehavior("B");
//        policyEntity.setServicemeta("D");
//        policyEntity.setServicetype("2");
//        policyEntity.setOperatingsystem("7");
        policyEntity.setId(policyId);
        policyCreateParam.setPolicyEntity(policyEntity);

        ResponseEntity<Void> callVoid = call("/api/v1/policy/update", HttpMethod.PUT, policyCreateParam, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        callPolicy = call("/api/v1/policy/id/" + policyId, HttpMethod.GET, PolicyEntityWrapper.class);
        assertThat(callPolicy.getStatusCode()).isEqualTo(HttpStatus.OK);

        PolicyEntityWrapper policyEntity1 = callPolicy.getBody();
        assertThat(policyEntity.getName()).isEqualTo(policyEntity1.getName());
//        assertThat(policyEntity.getBehavior()).isEqualTo(policyEntity1.getBehavior());
//        assertThat(policyEntity.getOperatingsystem()).isEqualTo(policyEntity1.getOperatingsystem());
//        assertThat(policyEntity.getServicemeta()).isEqualTo(policyEntity1.getServicemeta());
//        assertThat(policyEntity.getServicetype()).isEqualTo(policyEntity1.getServicetype());
//        assertThat(policyEntity1.getUpperid()).isEqualTo(groupId);
//        assertThat(policyEntity1.getIdtype()).isEqualTo("group");

        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");
        try {
            callVoid = call("/api/v1/policy/update", HttpMethod.PUT, policyCreateParam, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (String userId : policyCreateParam.getUserList()) {
            ResponseEntity<UserEntity> callUser = call("/api/v1/user/"+userId, HttpMethod.GET, UserEntity.class);
            assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
            user = callUser.getBody();
            deleteUser(user.getUserId());
        }

        callVoid = call("/api/v1/policy/delete/"+policyId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        deleteTenant(tenantId);
    }

    @Test
    @Order(482)
    public void policyDeleteTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String tenantId = createTenant();
        PolicyCreateParam policyCreateParam = createPolicy(groupId);
        String policyId = policyCreateParam.getPolicyEntity().getId();
        List<String> userList = policyCreateParam.getUserList();

        ResponseEntity<Void> callVoid = call("/api/v1/policy/delete/"+policyId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            ResponseEntity<PolicyEntity> callPolicy = call("/api/v1/policy/id/" + policyId, HttpMethod.GET, PolicyEntity.class);
            assertThat(callPolicy.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");
        try {
            callVoid = call("/api/v1/policy/delete/"+policyId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteGroup(groupId);
        deleteTenant(tenantId);
        for (String userId : policyCreateParam.getUserList()) {
            ResponseEntity<UserEntity> callUser = call("/api/v1/user/id/" + userId, HttpMethod.GET, UserEntity.class);
            assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
            user = callUser.getBody();
            deleteUser(user.getUserId());
        }
        deleteTenant(tenantId);
    }

    @Test
    @Order(483)
    @Disabled
    public void getGroupUpperTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        String groupId2 = createGroup();
        List<PolicyCreateParam> policyCreateParams = new ArrayList<>();
        PolicyCreateParam policyCreateParam = createPolicy(groupId);
        policyCreateParams.add(policyCreateParam);
        policyCreateParams.add(createPolicy(groupId));
        policyCreateParams.add(createPolicy(groupId));
        policyCreateParams.add(createPolicy(groupId));
        policyCreateParams.add(createPolicy(groupId2));
        policyCreateParams.add(createPolicy(groupId2));
        policyCreateParams.add(createPolicy(groupId2));

        PolicyEntityWrapper policyEntity = policyCreateParam.getPolicyEntity();
        String policyId = policyEntity.getId();

        ResponseEntity<PolicyEntity[]> callPolicy = call("/api/v1/policy/"+groupId, HttpMethod.GET, PolicyEntity[].class);
        assertThat(callPolicy.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callPolicy.getBody()).length).isEqualTo(4);

        deleteGroup(groupId);
        String tenantId = null;
        for (String userId : policyCreateParam.getUserList()) {
            ResponseEntity<UserEntity> callUser = call("/api/v1/user/id/" + userId, HttpMethod.GET, UserEntity.class);
            assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserEntity user = callUser.getBody();
            deleteUser(user.getUserId());
        }
        deleteTenant(tenantId);

        ResponseEntity<Void> callVoid;

        for (PolicyCreateParam policyCreateParam1 : policyCreateParams) {
            callVoid = call("/api/v1/policy/delete/"+policyCreateParam1.getPolicyEntity().getId(), HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
/*
        try {
            callPolicy = call("/api/policy/"+groupId, HttpMethod.GET, PolicyEntity[].class);
            assertThat(callPolicy.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }*/
    }

    @Test
    @Order(484)
    public void getPolicyUserTest() {
        loginWithDefaultUserToken();
        String groupId = createGroup();
        PolicyCreateParam policyCreateParam = createPolicy(groupId);
        PolicyEntityWrapper policyEntity = policyCreateParam.getPolicyEntity();
        String policyId = policyEntity.getId();

        ResponseEntity<UserEntity[]> callUserList = call("/api/v1/policy/users/"+policyId, HttpMethod.GET, UserEntity[].class);
        assertThat(callUserList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callUserList.getBody()).isNotNull();

        for (UserEntity user : callUserList.getBody()) {
            assertThat(policyCreateParam.getUserList().contains(user.getUserId())).isTrue();
        }

        deleteGroup(groupId);
        String tenantId = null;
        for (String userId : policyCreateParam.getUserList()) {
            ResponseEntity<UserEntity> callUser = call("/api/v1/user/id/" + userId, HttpMethod.GET, UserEntity.class);
            assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserEntity user = callUser.getBody();
            deleteUser(user.getUserId());
        }
        deleteTenant(tenantId);

        ResponseEntity<Void> callVoid = call("/api/v1/policy/delete/"+policyId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public PolicyCreateParam createPolicy(String groupId) {
        PolicyCreateParam policyCreateParam = new PolicyCreateParam();
        List<String> regexs = new ArrayList<>();
        List<String> userList = new ArrayList<>();
        String tenantId = createTenant();

        regexs.add("test1");
        regexs.add("test2");
        regexs.add("test3");

        policyCreateParam.setRegexList(regexs);

        for (int i = 0; i < 5; i++) {
            userList.add(createUser(tenantId).getUserId());
        }
        policyCreateParam.setUserList(userList);
        PolicyEntityWrapper policyEntity = new PolicyEntityWrapper();
        policyEntity.setId(groupId);
//        policyEntity.setBehavior("W");
//        policyEntity.setServicemeta("T");
//        policyEntity.setServicetype("5");
//        policyEntity.setOperatingsystem("1");
        policyEntity.setName("test Policy");
        policyCreateParam.setPolicyEntity(policyEntity);

        ResponseEntity<String> callString = call("/api/v1/policy/proxy", HttpMethod.PUT, policyCreateParam, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);
        policyEntity.setId(callString.getBody());
        policyCreateParam.setPolicyEntity(policyEntity);

        return policyCreateParam;
    }

    @Test
    @Order(485)
    public void getUserPolicy(){
        loginWithDefaultUserToken();
        String groupId = createGroup();
        PolicyCreateParam policyCreateParam = createPolicy(groupId);
        ResponseEntity<PolicyEntity[]> callPolicyList;

        Pagination policyPaginationParams = new Pagination();
        policyPaginationParams.setCurrentPage(0);
        policyPaginationParams.setPerPage(10);

        for (String userid: policyCreateParam.getUserList()){
            callPolicyList = call("/api/v1/policy/user/"+userid, HttpMethod.POST, policyPaginationParams, PolicyEntity[].class);
            assertThat(callPolicyList.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(callPolicyList.getBody().length).isEqualTo(1);
            PolicyEntity policyEntities = callPolicyList.getBody()[0];
            assertThat(policyEntities.getId()).isEqualTo(policyCreateParam.getPolicyEntity().getId());
        }

        deleteGroup(groupId);
        String tenantId = null;
        for (String userId : policyCreateParam.getUserList()) {
            ResponseEntity<UserEntity> callUser = call("/api/v1/user/id/" + userId, HttpMethod.GET, UserEntity.class);
            assertThat(callUser.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserEntity user = callUser.getBody();
            deleteUser(user.getUserId());
        }
        deleteTenant(tenantId);

        ResponseEntity<Void> callVoid = call("/api/v1/policy/delete/"+policyCreateParam.getPolicyEntity().getId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
