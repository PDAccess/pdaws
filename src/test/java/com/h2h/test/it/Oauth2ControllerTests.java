package com.h2h.test.it;

import com.h2h.pda.entity.Oauth2Entity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Oauth2EditWrapper;
import com.h2h.pda.pojo.Oauth2Wrapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Oauth2ControllerTests extends BaseIntegrationTests {

    @Test
    @Order(500)
    public void oauth2Create() {
        loginWithDefaultUserToken();

        Oauth2Wrapper data = new Oauth2Wrapper();
        data.setTrusted(1);
        data.setName("test oauth");
        data.setScopes("email");
        data.setCallbackUrl("https://pda.h2hsecure.com");

        ResponseEntity<String> callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        String oauthId = callString.getBody();
        String tenantId = createTenant();

        ResponseEntity<Oauth2Entity> callOauth2 = call("/api/v1/oauth2/id/" + oauthId, HttpMethod.GET, Oauth2Entity.class);
        assertThat(callOauth2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callOauth2.getBody()).isNotNull();
        Oauth2Entity oauth2Entity = callOauth2.getBody();

        assertThat(oauth2Entity.getName()).isEqualTo(data.getName());
        assertThat(oauth2Entity.getTrusted()).isEqualTo(data.getTrusted());
        assertThat(oauth2Entity.getCallbackUrl()).isEqualTo(data.getCallbackUrl());
        assertThat(oauth2Entity.getScopes()).isEqualTo(data.getScopes());

        ResponseEntity<Void> callVoid = call("/api/v1/oauth2/" + oauthId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserEntity userEntity = createUser(tenantId);
        loginWithUserToken(userEntity.getUsername(), "123123123");

        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteUser(userEntity.getUserId());

        data.setName(null);
        try {
            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setName("");
        try {
            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setName("test");
        data.setTrusted(5);
        try {
            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setTrusted(0);
        data.setCallbackUrl("test");
        try {
            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setCallbackUrl(null);
        try {
            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        data.setCallbackUrl("https://pda.h2hsecure.com");
        data.setScopes("test1,test2");
        try {
            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        data.setScopes("");

        callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/oauth2/" + callString.getBody(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        data.setScopes(null);

        callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/oauth2/" + callString.getBody(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        deleteTenant(tenantId);
    }

    @Test
    @Order(501)
    public void oauth2UpdateTest() {
        loginWithDefaultUserToken();

        Oauth2Wrapper data = new Oauth2Wrapper();
        data.setTrusted(1);
        data.setName("test oauth");
        data.setScopes("email");
        data.setCallbackUrl("https://pda.h2hsecure.com");

        ResponseEntity<String> callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        String oauthId = callString.getBody();

        Oauth2EditWrapper oauth2EditWrapper = new Oauth2EditWrapper();

        oauth2EditWrapper.setName("test oauth updated");
        oauth2EditWrapper.setTrusted(0);
        oauth2EditWrapper.setScopes("api,profile");
        oauth2EditWrapper.setCallbackUrl("https://google.com");
        oauth2EditWrapper.setId(oauthId);

        callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Oauth2Entity> callOauth2 = call("/api/v1/oauth2/id/" + oauthId, HttpMethod.GET, Oauth2Entity.class);
        assertThat(callOauth2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callOauth2.getBody()).isNotNull();

        Oauth2Entity oauth2Entity = callOauth2.getBody();

        assertThat(oauth2Entity.getName()).isEqualTo(oauth2EditWrapper.getName());
        assertThat(oauth2Entity.getTrusted()).isEqualTo(oauth2EditWrapper.getTrusted());
        assertThat(oauth2Entity.getCallbackUrl()).isEqualTo(oauth2EditWrapper.getCallbackUrl());
        assertThat(oauth2Entity.getScopes()).isEqualTo(oauth2EditWrapper.getScopes());

        ResponseEntity<Void> callVoid = call("/api/v1/oauth2/" + oauthId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tenantId = createTenant();
        UserEntity userEntity = createUser(tenantId);
        loginWithUserToken(userEntity.getUsername(), "123123123");

        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteUser(userEntity.getUserId());

        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        oauthId = callString.getBody();
        oauth2EditWrapper.setId(oauthId);

        oauth2EditWrapper.setName(null);
        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        oauth2EditWrapper.setName("");
        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        oauth2EditWrapper.setName("test");
        oauth2EditWrapper.setTrusted(5);
        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        oauth2EditWrapper.setTrusted(0);
        oauth2EditWrapper.setCallbackUrl("test");
        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        oauth2EditWrapper.setCallbackUrl(null);
        try {
            callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        oauth2EditWrapper.setCallbackUrl("https://pda.h2hsecure.com");
        oauth2EditWrapper.setScopes("");

        callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/oauth2/" + oauthId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        oauthId = callString.getBody();
        oauth2EditWrapper.setId(oauthId);

        oauth2EditWrapper.setScopes(null);

        callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/oauth2/" + oauthId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        oauth2EditWrapper.setId("test");
        callString = call("/api/v1/oauth2", HttpMethod.PUT, oauth2EditWrapper, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        deleteTenant(tenantId);
    }

    @Test
    @Order(502)
    public void deleteOauthTests() {
        loginWithDefaultUserToken();

        Oauth2Wrapper data = new Oauth2Wrapper();
        data.setTrusted(1);
        data.setName("test oauth");
        data.setScopes("email");
        data.setCallbackUrl("https://pda.h2hsecure.com");

        ResponseEntity<String> callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        String oauthId = callString.getBody();

        ResponseEntity<Void> callVoid;

        String tenantId = createTenant();

        UserEntity userEntity = createUser(tenantId);
        loginWithUserToken(userEntity.getUsername(), "123123123");

        try {
            callVoid = call("/api/v1/oauth2/" + oauthId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteUser(userEntity.getUserId());

        callVoid = call("/api/v1/oauth2/" + oauthId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Oauth2Entity> callOauth2;

        try {
            callOauth2 = call("/api/v1/oauth2/id/" + oauthId, HttpMethod.GET, Oauth2Entity.class);
            assertThat(callOauth2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        callVoid = call("/api/v1/oauth2/test", HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        deleteTenant(tenantId);
    }

    @Test
    @Order(503)
    public void getOauthListTest() {
        loginWithDefaultUserToken();
        ResponseEntity<String> callString;
        List<String> oauthList = new ArrayList<>();
        ResponseEntity<Oauth2Entity[]> callOauthList;

        for (int i = 0 ; i < 5 ; i++) {
            Oauth2Wrapper data = new Oauth2Wrapper();
            data.setTrusted(1);
            data.setName("test oauth "+i);
            data.setScopes("email");
            data.setCallbackUrl("https://pda.h2hsecure.com");

            callString = call("/api/v1/oauth2", HttpMethod.POST, data, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);
            oauthList.add(callString.getBody());
        }

        ResponseEntity<Void> callVoid;

        String tenantId = createTenant();

        UserEntity userEntity = createUser(tenantId);
        loginWithUserToken(userEntity.getUsername(), "123123123");

        try {
            callOauthList = call("/api/v1/oauth2", HttpMethod.GET, Oauth2Entity[].class);
            assertThat(callOauthList.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();

        deleteUser(userEntity.getUserId());

        callOauthList = call("/api/v1/oauth2", HttpMethod.GET, Oauth2Entity[].class);
        assertThat(callOauthList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callOauthList.getBody()).length).isEqualTo(5);

        List<Oauth2Entity> oauth2Entities = Arrays.asList(callOauthList.getBody());

        for (Oauth2Entity oauth2Entity : oauth2Entities) {
            assertThat(oauthList.contains(oauth2Entity.getId())).isTrue();
        }

        callVoid = call("/api/v1/oauth2/" + oauthList.get(0), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);


        callOauthList = call("/api/v1/oauth2", HttpMethod.GET, Oauth2Entity[].class);
        assertThat(callOauthList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callOauthList.getBody()).length).isEqualTo(4);

        oauth2Entities = Arrays.asList(callOauthList.getBody());

        for (Oauth2Entity oauth2Entity : oauth2Entities) {
            if (!oauthList.contains(oauth2Entity.getId())) assertThat(oauth2Entity.getId()).isEqualTo(oauthList.get(0));
        }

        for (int i = 1 ; i < 5 ; i++) {
            callVoid = call("/api/v1/oauth2/" + oauthList.get(i), HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        deleteTenant(tenantId);
    }

    @Test
    @Order(504)
    public void getWrongOauth2Test(){
        loginWithDefaultUserToken();
        ResponseEntity<Oauth2Entity> call = call("/api/v1/oauth2/id/test", HttpMethod.GET, Oauth2Entity.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
