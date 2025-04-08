package com.h2h.test.it;

import com.h2h.pda.pojo.CredentialManagerAccountResponse;
import com.h2h.pda.pojo.CredentialManagerResponse;
import com.h2h.pda.pojo.VerifyRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Objects;

@Disabled
public class CredentialManagerTests extends BaseIntegrationTests {

    private static final String CM_MS_URL = "%s/%s";

    @Value("${credentialManager.endpoint}")
    private String credentialManagerEndpoint;

    @Value("${opensshServer.hostname}")
    private String hostname;

    @Value("${opensshServer.port}")
    private Integer port;

    @Value("${opensshServer.username}")
    private String username;

    @Value("${opensshServer.password}")
    private String password;

    @Value("${opensshServer.protocol}")
    private String protocol;

    @Value("${opensshServer.new_password}")
    private String newPassword;

    @Test
    @Order(606)
    public void verifyAccount() {
        VerifyRequest vr = new VerifyRequest();
        vr.setUsername(username);
        vr.setPassword(password);
        vr.setHostname(hostname);
        vr.setPort(port);
        vr.setNewpassword(newPassword);
        vr.setProto(protocol);

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/verify"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();
    }

    @Test
    @Order(607)
    public void getAccounts() {

        VerifyRequest vr = new VerifyRequest();
        vr.setUsername(username);
        vr.setPassword(password);
        vr.setHostname(hostname);
        vr.setPort(port);
        vr.setNewpassword(newPassword);
        vr.setProto(protocol);

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();
    }

    @Test
    @Order(608)
    public void addAccount() {

        VerifyRequest vr = new VerifyRequest();
        vr.setUsername(username);
        vr.setPassword(password);
        vr.setHostname(hostname);
        vr.setPort(port);
        vr.setNewpassword(newPassword);
        vr.setProto(protocol);

        String user = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);

        ResponseEntity<CredentialManagerAccountResponse> accountResponse = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerAccountResponse.class);

        int accountCount = Objects.requireNonNull(accountResponse.getBody()).getAccounts().length;

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account/" + user), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();

        accountResponse = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerAccountResponse.class);

        assert Objects.requireNonNull(accountResponse.getBody()).getAccounts().length == accountCount + 1;
    }

    @Test
    @Order(609)
    public void removeAccount() {

        VerifyRequest vr = new VerifyRequest();
        vr.setUsername(username);
        vr.setPassword(password);
        vr.setHostname(hostname);
        vr.setPort(port);
        vr.setNewpassword(newPassword);
        vr.setProto(protocol);

        String user = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);

        restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account/" + user), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        ResponseEntity<CredentialManagerAccountResponse> accountResponse = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerAccountResponse.class);

        int accountCount = Objects.requireNonNull(accountResponse.getBody()).getAccounts().length;

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account/" + user), HttpMethod.DELETE, new HttpEntity<>(vr), CredentialManagerResponse.class);

        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();

        accountResponse = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerAccountResponse.class);

        assert Objects.requireNonNull(accountResponse.getBody()).getAccounts().length == accountCount - 1;
    }

    @Test
    @Order(610)
    public void changePassword() {

        VerifyRequest vr = new VerifyRequest();
        vr.setUsername(username);
        vr.setPassword(password);
        vr.setHostname(hostname);
        vr.setPort(port);
        vr.setNewpassword(newPassword);
        vr.setProto(protocol);

        String user = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);

        restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account/" + user), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/changepass/" + user), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);
        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();

        vr.setUsername(user);
        vr.setPassword(vr.getNewpassword());

        response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/verify"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();

    }

    @Test
    @Order(611)
    public void changeOwnPassword() {

        VerifyRequest vr = new VerifyRequest();
        vr.setUsername(username);
        vr.setPassword(password);
        vr.setHostname(hostname);
        vr.setPort(port);
        vr.setNewpassword(newPassword);
        vr.setProto(protocol);

        String user = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);

        restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/account/" + user), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        ResponseEntity<CredentialManagerResponse> response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/changepass/" + user), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);
        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();

        String newPassword = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        vr.setUsername(user);
        vr.setPassword(vr.getNewpassword());
        vr.setNewpassword(newPassword);

        restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/changepass"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        vr.setPassword(vr.getNewpassword());

        response = restTemplate.exchange(String.format(CM_MS_URL, credentialManagerEndpoint, "/verify"), HttpMethod.POST, new HttpEntity<>(vr), CredentialManagerResponse.class);

        assert response.getStatusCode().equals(HttpStatus.OK) && Objects.requireNonNull(response.getBody()).isResult();

    }

}
