package com.h2h.test.it;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.MailTestDto;
import com.h2h.pda.pojo.SettingParam;
import com.h2h.pda.pojo.system.SystemSettingTags;
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

public class SettingsControllerTests extends BaseIntegrationTests {

    @Test
    @Order(350)
    public void testMailTest() {
        loginWithDefaultUserToken();
        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("mail_host", "mail1.h2hsecure.com"));

        settingParams.add(new SettingParam("mail_port", "587"));

        settingParams.add(new SettingParam("mail_address", "pdaccess@h2hsecure.com"));

        settingParams.add(new SettingParam("mail_password", "34Ahsen44"));

        settingParams.add(new SettingParam("mail_smtp", "smtp"));

        settingParams.add(new SettingParam("mail_auth", "true"));

        settingParams.add(new SettingParam("mail_auth_type", "PLAIN"));

        settingParams.add(new SettingParam("mail_starttls", "true"));

        settingParams.add(new SettingParam("mail_ssl", "false"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<SettingParam[]> call2 = call("/api/v1/settings/generalSettings", HttpMethod.GET, SettingParam[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        MailTestDto mailTestDto = new MailTestDto();
        mailTestDto.setHost("mail1.h2hsecure.com");
        mailTestDto.setPort(587);
        mailTestDto.setEmail("pdaccess@h2hsecure.com");
        mailTestDto.setPassword("34Ahsen44");
        mailTestDto.setSmtp("smtp");
        mailTestDto.setAuth("true");
        mailTestDto.setAuthType("PLAIN");
        mailTestDto.setStarttls("true");
        mailTestDto.setSsl("false");

        call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);


        mailTestDto.setHost(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setHost("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setHost("mail1.h2hsecure.com");
        mailTestDto.setPort(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setPort(-1);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setPort(587);
        mailTestDto.setEmail(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setEmail("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setEmail("@domain.com");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setEmail("pdaccess@h2hsecure.com");
        mailTestDto.setAuth("true");
        mailTestDto.setPassword(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setPassword("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        
        mailTestDto.setSmtp("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setSmtp("smtp");
        mailTestDto.setAuth(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setAuth("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setAuth("true");
        mailTestDto.setAuthType(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setAuthType("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setAuthType("PLAIN");
        mailTestDto.setStarttls(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setStarttls("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setStarttls("true");
        mailTestDto.setSsl(null);
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        mailTestDto.setSsl("");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        String tenantId = createTenant();
        mailTestDto.setSsl("false");
        UserEntity user = createUser(tenantId);

        loginWithUserToken(user.getUsername(), "123123123");
        try {
            call = call("/api/v1/settings/mail", HttpMethod.POST, mailTestDto, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();
        deleteUser(user.getUserId());
        deleteTenant(tenantId);
    }

    @Test
    @Order(351)
    public void settingAddTest() {
        loginWithDefaultUserToken();
        List<SettingParam> settingParams = new ArrayList<>();

        String tenantId = createTenant();

        UserEntity user = createUser(tenantId);
        loginWithUserToken(user.getUsername(), "123123123");
        settingParams.add(new SettingParam("default_system_host_name", "test"));
        ResponseEntity<Void> call;

        try {
            call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        loginWithDefaultUserToken();
        deleteUser(user.getUserId());

        call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<SettingParam[]> call2 = call("/api/v1/settings/generalSettings", HttpMethod.GET, SettingParam[].class);
        assertThat(call2.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<SettingParam> settingParams1 = Arrays.asList(Objects.requireNonNull(call2.getBody()));

        for (SettingParam settingParam : settingParams1) {
            if (settingParam.getTag().equals("default_system_host_name")) {
                assertThat(settingParam.getValue().equals("test")).isEqualTo(true);
            }
        }

        settingParams.clear();

        settingParams.add(new SettingParam("test-tag", "test-value"));

        try {
            call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
        deleteTenant(tenantId);
    }

    @Test
    @Order(352)
    public void getSettingsWrongCategoryTest() {
        loginWithDefaultUserToken();

        try {
            ResponseEntity<SettingParam[]> call = call("/api/v1/settings/test", HttpMethod.GET, SettingParam[].class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @Order(353)
    public void setGeneralSettingTest() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("mail_host", "mail1.h2hsecure.com"));

        settingParams.add(new SettingParam("mail_port", "587"));

        settingParams.add(new SettingParam("mail_address", "pdaccess@h2hsecure.com"));

        settingParams.add(new SettingParam("mail_password", "34Ahsen44"));

        settingParams.add(new SettingParam("mail_smtp", "smtp"));

        settingParams.add(new SettingParam("mail_auth", "true"));

        settingParams.add(new SettingParam("mail_auth_type", "PLAIN"));

        settingParams.add(new SettingParam("mail_starttls", "true"));

        settingParams.add(new SettingParam("mail_ssl", "false"));

        settingParams.add(new SettingParam("max_session_minute", "88"));

        settingParams.add(new SettingParam("ldap_proxy_port", "5432"));

        settingParams.add(new SettingParam("ldap_proxy_host", "ldap host"));

        settingParams.add(new SettingParam("base_dn", "basedn"));

        settingParams.add(new SettingParam("user_dn", "userdn"));

        settingParams.add(new SettingParam("ldap_password", "password"));

        settingParams.add(new SettingParam("ldap_group_dn", "groupdn"));

        settingParams.add(new SettingParam("ldap_group_filter", "groupfilter"));

        settingParams.add(new SettingParam("idle_timeout", "37"));

        settingParams.add(new SettingParam("ssh_port", "2222"));

        settingParams.add(new SettingParam("ldap_port", "10389"));

        settingParams.add(new SettingParam("default_system_host_name", "pda.h2hsecure.com"));

        settingParams.add(new SettingParam("default_system_host_port", "81"));

        settingParams.add(new SettingParam("default_system_proxy_name", "test"));

        settingParams.add(new SettingParam("default_system_proxy_port", "81"));


        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();
        settingParams.add(new SettingParam("max_session_minute", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("idle_timeout", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(354)
    public void setVaultAutoSettingsTest() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("default_password_refreshing", "86400"));

        settingParams.add(new SettingParam("default_password_refreshing_type", "day"));

        settingParams.add(new SettingParam("default_password_chars", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"));

        settingParams.add(new SettingParam("break_check_out_time", "2"));

        settingParams.add(new SettingParam("default_password_length", "16"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();


        settingParams.add(new SettingParam("default_password_refreshing", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


        settingParams.add(new SettingParam("default_password_refreshing", "3599"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("break_check_out_time", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("break_check_out_time", "0"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("default_password_length", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("default_password_length", "0"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("default_password_refreshing_type", "week"));

        call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();
        settingParams.add(new SettingParam("default_password_refreshing_type", "month"));

        call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();
        settingParams.add(new SettingParam("default_password_refreshing_type", "year"));

        call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();
        settingParams.add(new SettingParam("default_password_refreshing_type", "TEST"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(355)
    public void setPasswordValidationRulesTest() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("password_min_special_chars", "1"));

        settingParams.add(new SettingParam("password_min_numeral", "1"));

        settingParams.add(new SettingParam("password_min_lowercase", "1"));

        settingParams.add(new SettingParam("password_min_chars", "8"));

        settingParams.add(new SettingParam("password_min_uppercase", "1"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();


        settingParams.add(new SettingParam("password_min_special_chars", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


        settingParams.add(new SettingParam("password_min_special_chars", "-1"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_numeral", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_numeral", "-1"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_lowercase", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_lowercase", "-1"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_uppercase", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_uppercase", "-1"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_chars", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        settingParams.clear();
        settingParams.add(new SettingParam("password_min_chars", "0"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(356)
    public void setLookFeelSettingsTest() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("registration_page_visibility", "1"));

        settingParams.add(new SettingParam("login_page_logo", "data:image/png;base64,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"));

        settingParams.add(new SettingParam("login_page_message", "#### H2HSecure" ));

        settingParams.add(new SettingParam("navbar_logo", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI8AAABWCAYAAAD2UsV0AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABl1JREFUeNrsnc112zgQgGE93aUOjL3sVepA3AriPeVol+BUYJXgVLDc456sVLBIBWGue1m4gtAVKEACJ5YimTPA4IfgzHt80kEE8fNh/gSAF/v9/loIIQWNaHf1FxcXnwWhmHpemo8bwiI7W0/7aer6JDIIok3a1PFvUZqYBvy7jyNfzPVg4TTXgqCem308+d9cf9lnJO77O2hfigJlFrHspbmuzNXameM6aiHKFOk0gHIgvUn4XFBfOi01GXiOQdpaE5F6dnsO6M5p5EUh8GB/WxU8LztAOT+rdGmcxlxFfkaM31YJz7O0IwFo6WAnB8jDDEmG5xCgzUgA2kUwYZLhCQdoMQKApPPZcpksNltnBuVWjENuiSMetCaJ7H+NDh4xIngEsfaRie6JJnOMiRHfs8dDqhWrXm0O45o4gzo0yM85KOxg3Ji63hJlpH3M0NpcH4qhB5Fh3gDLu3SZZYw8AMoFZ5gRbbdlfkLW9Zqgz1eemfCHkjQPudkys/LRXH8K3P9QWZxBU8+P7tld4rrKxPeNy+dxZmiHMF2rTAA9OSD6hPCsE983Sof5dgwd4wC6Tzj7vdtaUm5sFnlQHhEmIbdKbhMOoMx07+hCdTWGTnGg60SPWzM8MNEj6hQde/AJfLtmSvB0oj5ZZtQck9I8YxKV4BmhgYEs5f9Ahie9rAspg+EZoUiGhyXnwEuGZ2JCmEVnzcMm66S0DA+L76BrMZxvKmIrDsNTHjxKwJKVkuFhs3UsvYDlmxqGhzXPgbg9/j0RiFFlzuNZVKTVHX0GwfP7Pxrz73/331v5xPCM22RB4WmAz2yh8BjYGgxAbLbKc5afF6cNmq6hiMuAYFdz3iDqpwxACww8PY9rEoFoCn3ChAUBGROgmSu447EtwmyRwxMToNmLBeCxAFoyN2B4uhP+TzA8sQCavbCxsQBaT50a4Jrn/mgzoaIO16kBmr3IL8QCCNrAmk2n9Gi/jjExKQE6iLYiAXQF/F0/cXj00Vg8ArXaKhdAv4TqlAC5rblQn0dVDA820sL0iZdbQAHQyTwPBUBune0WcYuuGB4fswXVxtK3UqEAnU0ShgDkwFGIhmmomp6K2UL4gU1IxUIAejXDjAXInZBx5zoCo053tVID3V165tDzLqbmCQVoDmjUkz2Txny9N599pBnQstbxNuWSopIWIAOEBLoaFqB2Dpg5K6cZYiX7FPWrBmqBx/YL5Lghq93ccTHeYsCx4ww9mMIqke0MAI4ScbPEW1G3QDSyCgwk1gTgQMfZgmP/ff88ywzOLnTGVKJ5+kB4ZGpwzjrMicDRgvYtNrWF6RCtFKR5QsA5CU8icGxFrnK9qqi0SGsAnihmKxScX+BJBI7tqHXlTjLKnAxMIgg8S8zhBxTgHMCTCBx7dFtTeUIQC48CTDZo/iUZOD/giQyOrUBrO9JA8652U+URaWmAVuopnkUJjpV5BHB6N1s6V66aGDBYzQMxSx0ADpkSnG/wOFMCKbCZQFhdWqRFAk8McM6G6ixJIy0NHFAvsxULHIYnroAcWGDUqYDAXqYC59lssZTt72B+Z4F9PNJG0MPJdxhwGJ78mgcEhU1tAN/HcvBWHAPD+5gNZLOVFx6FKE8RPZPhKdxZXiD8DEwKhMJUMjw1OMsCt8S3I3wuw8PwnNR6K4an/khLIDPvmhhchmcizrJArEKQDE/98PjskIVonyZVIznPkzHS8njpW1ERF8OTz1m+EXGW4bLZmgA8MbXfhuFJL82YZn5ugBkeP+lL1jypAGZ4/Dq9Kxwe1jyJ/YTL0BmLiLQYnsoEHPm8shy3lPMXlyneQ8rw/NQY0E3+unCTlawucwbnx0FUFMffQc1eEwjFPfB3HxmeeOBs3EBgZqkKne0hu1BMnTUCHtY8Hh18N+QTiO+ntPo4yLtAjaJC2oZYkioZHj/ZRiq3PbeMAvHaRk1Qjw6gWZrYncwOMx2UMiE8oDJiv4eU4YHL/cABDdCZTnFAOrQMyfDkFw0whdCB6hPC0zA8eQV6EBUIHqL9/lDTt2Z48oLTAJeANoSDPgRgEUtSGZ7XQ2oJGajEkRamLNY8Gfwba6b+QOxugM7wLjE8UbfiMDw/B6J1Juo3c31A3g91TClfC6Vym6656zRFRXrmiAcbsXw7xYzg5DIFrFtLDM82MbAH8lWAAQBxzn4jFUFELwAAAABJRU5ErkJggg=="));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();


        settingParams.add(new SettingParam("registration_page_visibility", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(357)
    public void setAuthSettingsTest() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("after_sign_out_path", "test"));

        settingParams.add(new SettingParam("home_page_url", "/activities/nav/livesessions?sort=createddesc"));

        settingParams.add(new SettingParam("two_factor_auth", "0" ));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        settingParams.clear();


        settingParams.add(new SettingParam("two_factor_auth", "test"));
        try {
            call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
            assertThat(call.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(358)
    public void setAppLookFeelTest() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();

        settingParams.add(new SettingParam("app_login_page_logo", "data:image/png;base64,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"));

        settingParams.add(new SettingParam("app_login_page_message", "##### H2HSecure\n``` Welcome PDAccess Desktop App 3```"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(359)
    public void groupMembershipSettings() {
        loginWithDefaultUserToken();

        List<SettingParam> settingParams = new ArrayList<>();
        settingParams.add(new SettingParam(SystemSettingTags.ADD_ALL_ADMIN_TO_GROUPS, "true"));
        settingParams.add(new SettingParam(SystemSettingTags.ADD_EXTERNAL_ADMIN_TO_GROUPS, "true"));
        settingParams.add(new SettingParam(SystemSettingTags.NO_LOGIN_TO_DEVICE_FROM_ADMIN_USERS, "true"));

        ResponseEntity<Void> call = call("/api/v1/settings", HttpMethod.POST, settingParams, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
