package com.h2h.test.it;

import com.h2h.pda.entity.TenantEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.TenantDTO;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TenantControllerTests extends BaseIntegrationTests{

    @Test
    @Order(490)
    public void addTenantTest() {
        loginWithDefaultUserToken();
        TenantDTO tenantDTO = new TenantDTO();

        tenantDTO.setCompanyName("test company");
        tenantDTO.setCountry("Turkey");
        ResponseEntity<String> callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tenantId = callString.getBody();

        ResponseEntity<TenantEntity> callTenant = call("/api/v1/tenant/" + tenantId, HttpMethod.GET, TenantEntity.class);
        assertThat(callTenant.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callTenant.getBody()).isNotNull();

        TenantEntity tenant = callTenant.getBody();
        assertThat(tenant.getCompanyName()).isEqualTo(tenantDTO.getCompanyName());
        assertThat(tenant.getCountry()).isEqualTo(tenantDTO.getCountry());

        UserEntity user = createUser(tenantId);
        loginWithUserToken(user.getUsername(), "123123123");
        try {
            callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
        loginWithDefaultUserToken();

        tenantDTO.setCountry(null);
        try {
            callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        tenantDTO.setCountry("");
        try {
            callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        tenantDTO.setCountry("Turkey");
        tenantDTO.setCompanyName(null);
        try {
            callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        tenantDTO.setCompanyName("");
        try {
            callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<Void> callVoid = call("/api/v1/tenant/" + tenantId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(491)
    public void updateTenantTest() {
        loginWithDefaultUserToken();
        TenantDTO tenantDTO = new TenantDTO();

        tenantDTO.setCompanyName("test company");
        tenantDTO.setCountry("Turkey");
        ResponseEntity<String> callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        tenantDTO.setTenantId(callString.getBody());

        tenantDTO.setCompanyName("test update company");
        tenantDTO.setCountry("test update country");

        ResponseEntity<Void> callVoid = call("/api/v1/tenant", HttpMethod.PUT, tenantDTO, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<TenantEntity> callTenant = call("/api/v1/tenant/" + tenantDTO.getTenantId(), HttpMethod.GET, TenantEntity.class);
        assertThat(callTenant.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callTenant.getBody()).isNotNull();

        TenantEntity tenant = callTenant.getBody();
        assertThat(tenant.getCompanyName()).isEqualTo(tenantDTO.getCompanyName());
        assertThat(tenant.getCountry()).isEqualTo(tenantDTO.getCountry());

        UserEntity user = createUser(tenantDTO.getTenantId());
        loginWithUserToken(user.getUsername(), "123123123");
        try {
            callString = call("/api/v1/tenant", HttpMethod.PUT, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
        loginWithDefaultUserToken();

        tenantDTO.setCountry(null);
        try {
            callString = call("/api/v1/tenant", HttpMethod.PUT, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        tenantDTO.setCountry("");
        try {
            callString = call("/api/v1/tenant", HttpMethod.PUT, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        tenantDTO.setCountry("Turkey");
        tenantDTO.setCompanyName(null);
        try {
            callString = call("/api/v1/tenant", HttpMethod.PUT, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        tenantDTO.setCompanyName("");
        try {
            callString = call("/api/v1/tenant", HttpMethod.PUT, tenantDTO, String.class);
            assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        callVoid = call("/api/v1/tenant/" + tenantDTO.getTenantId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(492)
    public void deleteTenantTest() {
        loginWithDefaultUserToken();
        TenantDTO tenantDTO = new TenantDTO();

        tenantDTO.setCompanyName("test company");
        tenantDTO.setCountry("Turkey");
        ResponseEntity<String> callString = call("/api/v1/tenant", HttpMethod.POST, tenantDTO, String.class);
        assertThat(callString.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tenantId = callString.getBody();
        ResponseEntity<Void> callVoid;

        UserEntity user = createUser(tenantId);
        loginWithUserToken(user.getUsername(), "123123123");
        try {
            callVoid = call("/api/v1/tenant/" + tenantId, HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
        loginWithDefaultUserToken();

        try {
            callVoid = call("/api/v1/tenant/test", HttpMethod.DELETE, Void.class);
            assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        callVoid = call("/api/v1/tenant/" + tenantId, HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<TenantEntity> callTenant = call("/api/v1/tenant/" + tenantId, HttpMethod.GET, TenantEntity.class);
        assertThat(callTenant.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callTenant.getBody().getDeletedAt()).isNotNull();

        callVoid = call("/api/v1/user/id/" + user.getUserId(), HttpMethod.DELETE, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
