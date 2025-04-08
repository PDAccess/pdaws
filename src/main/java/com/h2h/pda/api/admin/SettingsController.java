package com.h2h.pda.api.admin;

import com.h2h.pda.config.LdapTemplateWrapper;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.MailTestDto;
import com.h2h.pda.pojo.SettingParam;
import com.h2h.pda.pojo.VaultAuthStatus;
import com.h2h.pda.pojo.VaultPolicy;
import com.h2h.pda.pojo.ldap.*;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.pojo.mail.EmailPropsData;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private static final String VAULT_ERROR = "Vault error: {}";
    private static final String MAIL_ERROR = "Mail error: {}";
    private static final String X_VAULT_TOKEN = "X-Vault-Token";
    private static final String EMAIL_REGEX = "^(.+)@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    private static final String LDAP_PATH = "/sys/auth/ldap";
    private static final String ADMIN_PATH = "/sys/auth/admin";

    private boolean emailValid(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    VaultService vaultService;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    LdapTemplateWrapper templateWrapper;

    @Autowired
    ActionPdaService actionPdaService;

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "mail")
    public ResponseEntity<String> testMail(@RequestBody MailTestDto mail) {
        UserEntity user = usersOps.securedUser();
        try {
            EmailData emailData = new EmailData();
            EmailPropsData emailPropsData = new EmailPropsData();
            if (mail.getHost() == null || mail.getHost().equals("")) {
                return ResponseEntity.badRequest().body("Mail Host cannot be empty");
            }

            if (mail.getPort() == null || mail.getPort() < 0) {
                return ResponseEntity.badRequest().body("illegal Mail Port:" + mail.getPort());
            }

            if (mail.getEmail() == null || mail.getEmail().equals("")) {
                return ResponseEntity.badRequest().body("Mail Address cannot be empty");
            }

            if (!emailValid(mail.getEmail())) {
                return ResponseEntity.badRequest().body("Invalid Mail Address:" + mail.getEmail());
            }

            if (mail.getAuth() == null || mail.getAuth().equals("")) {
                return ResponseEntity.badRequest().body("Authentication Cannot be empty");
            } else if (Boolean.parseBoolean(mail.getAuth())) {
                if (mail.getPassword() == null || mail.getPassword().equals("")) {
                    return ResponseEntity.badRequest().body("Password Cannot be empty when authentication is enabled");
                }
            }

            if (mail.getSmtp() == null || mail.getSmtp().equals("")) {
                mail.setSmtp("smtp");
            }

            if (mail.getAuthType() == null || mail.getAuthType().equals("")) {
                return ResponseEntity.badRequest().body("Auth Type Cannot be empty when authentication is enabled");
            }

            if (mail.getStarttls() == null || mail.getStarttls().equals("")) {
                return ResponseEntity.badRequest().body("Start TLS Cannot be empty when authentication is enabled");
            }

            if (mail.getSsl() == null || mail.getSsl().equals("")) {
                return ResponseEntity.badRequest().body("SSL Cannot be empty when authentication is enabled");
            }

            emailPropsData.setHost(mail.getHost());
            emailPropsData.setPort(mail.getPort());
            emailPropsData.setEmail(mail.getEmail());
            emailPropsData.setPassword(mail.getPassword());
            emailPropsData.setSmtp(mail.getSmtp());
            emailPropsData.setAuth(mail.getAuth());
            emailPropsData.setAuthType(mail.getAuthType());
            emailPropsData.setStarttls(mail.getStarttls());
            emailPropsData.setSsl(mail.getSsl());

            emailData.setEmailPropsData(emailPropsData);
            emailData.setMailName(EmailNames.DEFAULT_MAIL);
            emailData.setToMail(user.getEmail());
            emailData.setSubject("Testing Mail from PDAccess");
            emailData.setText("If you have received this e-mail you sent a new empty e-mail template from the PDAccess e-mail section to your mail address.");
            emailData.getHtml().put("username", user.getUsername());

            sendEmailService.pushEmailRequest(emailData);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(MAIL_ERROR, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/{category}")
    public ResponseEntity<List<SettingParam>> fetchSettings(@PathVariable String category) {

        ArrayList settingParamList = new ArrayList();
        systemSettings.forEachTag(category, (t, v) -> {
            SettingParam settingParam = new SettingParam();
            settingParam.setTag(t);
            settingParam.setValue(v);

            settingParamList.add(settingParam);
        });

        if (settingParamList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(settingParamList, HttpStatus.OK);
    }

    @GetMapping(path = "/{category}/{tag}")
    public ResponseEntity<String> fetchSettings(@PathVariable String category, @PathVariable String tag) {
        Optional<String> tagValue = systemSettings.tagValue(category, tag);

        return tagValue.isPresent() ? ResponseEntity.ok(tagValue.get()) : ResponseEntity.noContent().build();
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping()
    public ResponseEntity<Void> fetchSettings(@RequestBody List<SettingParam> settingParams) {

        for (SettingParam settingParam : settingParams) {
            if (settingParam != null && settingParam.getTag() != null && settingParam.getValue() != null) {

                if (!systemSettings.hasTag(settingParam.getTag())) {
                    return ResponseEntity.notFound().build();
                }

                if (settingParam.getTag().equals("max_session_minute")) {
                    try {
                        Integer.parseInt(settingParam.getValue());
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("idle_timeout")) {
                    try {
                        Integer.parseInt(settingParam.getValue());
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("default_password_refreshing")) {
                    try {
                        int time = Integer.parseInt(settingParam.getValue());
                        if(time < 3600){
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("break_check_out_time")) {
                    try {
                        int time = Integer.parseInt(settingParam.getValue());
                        if(time < 1){
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("default_password_length")) {
                    try {
                        int time = Integer.parseInt(settingParam.getValue());
                        if(time < 1){
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("password_min_chars")) {
                    try {
                        int min = Integer.parseInt(settingParam.getValue());
                        if(min < 1){
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("registration_page_visibility")) {
                    try {
                        int regPageVis = Integer.parseInt(settingParam.getValue());
                        if (regPageVis != 0 && regPageVis != 1)
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("two_factor_auth")) {
                    try {
                        int twoFac = Integer.parseInt(settingParam.getValue());
                        if (twoFac != 0 && twoFac != 1)
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("password_min_special_chars") || settingParam.getTag().equals("password_min_numeral") || settingParam.getTag().equals("password_min_lowercase") || settingParam.getTag().equals("password_min_uppercase")) {
                    try {
                        int min = Integer.parseInt(settingParam.getValue());
                        if(min < 0){
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } catch (NumberFormatException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (settingParam.getTag().equals("default_password_refreshing_type") &&
                        (!(settingParam.getValue().equals("day") || settingParam.getValue().equals("week") || settingParam.getValue().equals("month") || settingParam.getValue().equals("year")))) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                systemSettings.updateTag(settingParam.getTag(), settingParam.getValue());
            }
        }

        actionPdaService.saveAction("System Settings is updated");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/ldap/general")
    public ResponseEntity<LdapSetting> getLdapGeneralSettings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        VaultResponseSupport<LdapSetting> vaultResponseSupport;

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            vaultResponseSupport = template.read(SECRET_INVENTORY + 1234, LdapSetting.class);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(vaultResponseSupport.getData(), HttpStatus.OK);
    }

    @PostMapping(path = "/ldap/general")
    public ResponseEntity<Void> setLdapGeneralSettings(@RequestBody LdapSetting setting) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        if (vaultService.isVaultEnabled()) {
            try {
                VaultTemplate template = vaultService.newTemplate(details.getToken());

                template.write(SECRET_INVENTORY + 1234, (Object) setting);


                // --> /api/ldap/v1/setting

                LdapBaseSetting baseSetting = new LdapBaseSetting();
                baseSetting.setUrl((setting.getSsl() ? "ldaps://" : "ldap://") + setting.getHost() + ":" + setting.getPort());
                baseSetting.setUserDn(setting.getBindDN());
                baseSetting.setPassword(setting.getBindPass());
                baseSetting.setBase(setting.getBaseDN());
                baseSetting.setStartTLS(setting.getStartTLS());
                baseSetting.setInsecureTLS(setting.getInsecureTLS());

                templateWrapper.setLdapSettings(baseSetting.getUrl(), baseSetting.getBase(), baseSetting.getUserDn(), baseSetting.getPassword(), baseSetting.getStartTLS(), baseSetting.getInsecureTLS());

            } catch (VaultException ve) {
                log.error(VAULT_ERROR, ve.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(path = "/ldap/vault")
    public ResponseEntity<LdapVaultSetting> getLdapVaultSettings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        LdapVaultSetting settings;

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            VaultResponseSupport<LdapVaultSetting> vaultResponseSupport = template.read(SECRET_INVENTORY + "12345", LdapVaultSetting.class);
            settings = vaultResponseSupport.getData();

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(settings, HttpStatus.OK);

    }

    @GetMapping(path = "/ldap/vault/admin")
    public ResponseEntity<LdapVaultSetting> getLdapVaultAdminSettings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        LdapVaultSetting settings = new LdapVaultSetting();

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            VaultResponseSupport<LdapVaultSetting> vaultResponseSupport = template.read(SECRET_INVENTORY + "1234567", LdapVaultSetting.class);
            if (vaultResponseSupport != null) {
                settings = vaultResponseSupport.getData();
            }

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(settings, HttpStatus.OK);

    }

    @PostMapping(path = "/ldap/vault")
    public ResponseEntity<Void> setLdapVaultSettings(@RequestBody LdapVaultSetting vaultSetting) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            template.write(SECRET_INVENTORY + "12345", (Object) vaultSetting);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(path = "/ldap/vault/admin")
    public ResponseEntity<Void> setLdapVaultAdminSettings(@RequestBody LdapVaultSetting vaultSetting) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            template.write(SECRET_INVENTORY + "1234567", (Object) vaultSetting);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(path = "/ldap/vault/enable")
    public ResponseEntity<Void> enableVaultLdap() {

        try {
            Map<String, String> authTypeMap = new HashMap<>();
            authTypeMap.put("type", "ldap");
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set(X_VAULT_TOKEN, vaultService.getRootToken());
            HttpEntity entity = new HttpEntity(authTypeMap, httpHeader);

            Boolean status = vaultService.doWithVaultUsingRootToken(restOperations -> {
                restOperations.exchange(LDAP_PATH, HttpMethod.DELETE, entity, String.class);
                ResponseEntity<String> responseEntity = restOperations.exchange(LDAP_PATH, HttpMethod.POST, entity, String.class);
                return (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT || responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);
            });

            if (status == null || !status) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            VaultResponseSupport<LdapSetting> vaultLdapSettingResponse = vaultService.read(SECRET_INVENTORY + "1234", LdapSetting.class);
            LdapSetting ldapSetting = vaultLdapSettingResponse.getData();

            VaultResponseSupport<LdapVaultSetting> vaultSettingResponse = vaultService.read(SECRET_INVENTORY + "12345", LdapVaultSetting.class);
            LdapVaultSetting vaultSetting = vaultSettingResponse.getData();

            VaultLdapConfig config = new VaultLdapConfig();
            config.setUrl((ldapSetting.getSsl() ? "ldaps://" : "ldap://") + ldapSetting.getHost() + ":" + ldapSetting.getPort() + "/");
            config.setInsecureTLS(ldapSetting.getInsecureTLS());
            config.setStartTLS(ldapSetting.getStartTLS());
            config.setBindDN(ldapSetting.getBindDN());
            config.setBindPass(ldapSetting.getBindPass());
            config.setUserDN(vaultSetting.getUserDN());
            config.setGroupDN(vaultSetting.getGroupDN());
            config.setGroupFilter(vaultSetting.getGroupFilter());
            config.setGroupAttr(vaultSetting.getGroupAttr());
            config.setUserAttr(vaultSetting.getUserAttr());
            config.setDiscoverDN(vaultSetting.getDiscoverDN());

            vaultService.write("auth/ldap/config", (Object) config);

            VaultPolicy policy = new VaultPolicy();
            policy.setPolicies("inventory");
            vaultService.write("auth/ldap/groups/" + config.getGroupDN().split(",")[0].split("=")[1], (Object) policy);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(path = "/ldap/vault/disable")
    public ResponseEntity<Void> disableVaultLdap() {

        try {
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set(X_VAULT_TOKEN, vaultService.getRootToken());
            HttpEntity entity = new HttpEntity(httpHeader);
            Boolean status = vaultService.doWithVaultUsingRootToken(restOperations -> {
                ResponseEntity<String> responseEntity = restOperations.exchange(LDAP_PATH, HttpMethod.DELETE, entity, String.class);
                return responseEntity.getStatusCode() == HttpStatus.NO_CONTENT;
            });

            if (status == null || !status) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(path = "/ldap/vault/status")
    public ResponseEntity<Void> getLdapVaultStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set(X_VAULT_TOKEN, details.getToken());
        HttpEntity entity = new HttpEntity(httpHeader);
        VaultAuthStatus authStatus = template.doWithVault(restOperations -> {
            ResponseEntity<VaultAuthStatus> responseEntity = restOperations.exchange("/sys/auth", HttpMethod.GET, entity, VaultAuthStatus.class);
            return responseEntity.getBody();
        });

        if (authStatus == null || authStatus.getLdap() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/ldap/vault/connection")
    public ResponseEntity<Void> testLdapAuthentication(@RequestParam String username,
                                                       @RequestParam String password) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            Map<String, String> credentialMap = new HashMap<>();
            credentialMap.put("password", password);
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set(X_VAULT_TOKEN, details.getToken());
            HttpEntity entity = new HttpEntity(credentialMap, httpHeader);
            Boolean status = template.doWithVault(restOperations -> {
                ResponseEntity<String> responseEntity = restOperations.exchange("/auth/ldap/login/" + username, HttpMethod.POST, entity, String.class);
                return responseEntity.getStatusCode() == HttpStatus.OK;
            });

            if (status == null || !status) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/ldap/vault/admin/enable")
    public ResponseEntity<Void> enableVaultAdminLdap() {

        try {

            Map<String, String> authTypeMap = new HashMap<>();
            authTypeMap.put("type", "ldap");
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set(X_VAULT_TOKEN, vaultService.getRootToken());
            HttpEntity entity = new HttpEntity(authTypeMap, httpHeader);
            Boolean status = vaultService.doWithVaultUsingRootToken(restOperations -> {
                restOperations.exchange(ADMIN_PATH, HttpMethod.DELETE, entity, String.class);
                ResponseEntity<String> responseEntity = restOperations.exchange(ADMIN_PATH, HttpMethod.POST, entity, String.class);
                return (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT || responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);
            });

            if (status == null || !status) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            VaultResponseSupport<LdapSetting> vaultLdapSettingResponse = vaultService.read(SECRET_INVENTORY + "1234", LdapSetting.class);
            LdapSetting ldapSetting = vaultLdapSettingResponse.getData();

            VaultResponseSupport<LdapVaultSetting> vaultSettingResponse = vaultService.read(SECRET_INVENTORY + "1234567", LdapVaultSetting.class);
            LdapVaultSetting vaultSetting = vaultSettingResponse.getData();

            VaultLdapConfig config = new VaultLdapConfig();
            config.setUrl((ldapSetting.getSsl() ? "ldaps://" : "ldap://") + ldapSetting.getHost() + ":" + ldapSetting.getPort() + "/");
            config.setInsecureTLS(ldapSetting.getInsecureTLS());
            config.setStartTLS(ldapSetting.getStartTLS());
            config.setBindDN(ldapSetting.getBindDN());
            config.setBindPass(ldapSetting.getBindPass());
            config.setUserDN(vaultSetting.getUserDN());
            config.setGroupDN(vaultSetting.getGroupDN());
            config.setGroupFilter(vaultSetting.getGroupFilter());
            config.setGroupAttr(vaultSetting.getGroupAttr());
            config.setUserAttr(vaultSetting.getUserAttr());
            config.setDiscoverDN(vaultSetting.getDiscoverDN());

            vaultService.write("auth/admin/config", (Object) config);

            VaultPolicy policy = new VaultPolicy();
            policy.setPolicies("inventory");
            vaultService.write("auth/admin/groups/" + config.getGroupDN().split(",")[0].split("=")[1], (Object) policy);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(path = "/ldap/vault/admin/disable")
    public ResponseEntity<Void> disableVaultAdminLdap() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set(X_VAULT_TOKEN, vaultService.getRootToken());
            HttpEntity entity = new HttpEntity(httpHeader);
            Boolean status = vaultService.doWithVaultUsingRootToken(restOperations -> {
                ResponseEntity<String> responseEntity = restOperations.exchange(ADMIN_PATH, HttpMethod.DELETE, entity, String.class);
                return responseEntity.getStatusCode() == HttpStatus.NO_CONTENT;
            });

            if (status == null || !status) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(path = "/ldap/vault/admin/status")
    public ResponseEntity<Void> getLdapVaultAdminStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        VaultTemplate template = vaultService.newTemplate(details.getToken());

        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set(X_VAULT_TOKEN, details.getToken());
        HttpEntity entity = new HttpEntity(httpHeader);
        VaultAuthStatus authStatus = vaultService.doWithVault(restOperations -> {
            ResponseEntity<VaultAuthStatus> responseEntity = restOperations.exchange("/sys/auth", HttpMethod.GET, entity, VaultAuthStatus.class);
            return responseEntity.getBody();
        });

        if (authStatus == null || authStatus.getAdmin() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/ldap/vault/admin/connection")
    public ResponseEntity<Void> testLdapAdminAuthentication(@RequestParam String username,
                                                            @RequestParam String password) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            Map<String, String> credentialMap = new HashMap<>();
            credentialMap.put("password", password);
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set(X_VAULT_TOKEN, details.getToken());
            HttpEntity entity = new HttpEntity(credentialMap, httpHeader);
            Boolean status = template.doWithVault(restOperations -> {
                ResponseEntity<String> responseEntity = restOperations.exchange("/auth/admin/login/" + username, HttpMethod.POST, entity, String.class);
                return responseEntity.getStatusCode() == HttpStatus.OK;
            });

            if (status == null || !status) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(path = "/ldap/panel/settings")
    public ResponseEntity<LdapPanelSetting> getLdapPanelSettings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {

            VaultTemplate template = vaultService.newTemplate(details.getToken());

            VaultResponseSupport<LdapPanelSetting> vaultResponseSupport = template.read(SECRET_INVENTORY + "123456", LdapPanelSetting.class);
            LdapPanelSetting panelSetting = vaultResponseSupport.getData();

            return new ResponseEntity<>(panelSetting, HttpStatus.OK);
        } catch (VaultException ve) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(path = "ldap-panel/settings")
    public ResponseEntity<Void> setLdapPanelSettings(@RequestBody LdapPanelSetting setting) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());
            template.write(SECRET_INVENTORY + "123456", (Object) setting);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (VaultException ve) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

