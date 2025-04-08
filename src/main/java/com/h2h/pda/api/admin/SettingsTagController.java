package com.h2h.pda.api.admin;

import com.h2h.pda.service.api.SystemSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tag")
public class SettingsTagController {

    @Autowired
    SystemSettings systemSettings;

    @GetMapping("/app-login-page-image")
    public ResponseEntity<String> getAppLoginPageImage() {
        return getValueFromTag("app_login_page_logo");
    }

    @GetMapping("/app-login-page-message")
    public ResponseEntity<String> getAppLoginPageMessage() {
        return getValueFromTag("app_login_page_message");
    }

    @GetMapping("/login-page-image")
    public ResponseEntity<String> getLoginPageImage() {
        return getValueFromTag("login_page_logo");
    }

    @GetMapping("/login-page-message")
    public ResponseEntity<String> getLoginPageMessage() {
        return getValueFromTag("login_page_message");
    }

    @GetMapping("/navbar-logo")
    public ResponseEntity<String> getNavbarLogo() {
        return getValueFromTag("navbar_logo");
    }

    @GetMapping("/sign-out-path")
    public ResponseEntity<String> getSignOutPath(){
        return getValueFromTag("after_sign_out_path");
    }

    @GetMapping("/home-page-url")
    public ResponseEntity<String> getHomePageUrl(){
        return getValueFromTag("home_page_url");
    }

    @GetMapping("/mfa-status")
    public ResponseEntity<String> getTwoFactorAuthStatus(){
        return getValueFromTag("two_factor_auth");
    }

    public ResponseEntity<String> getValueFromTag(String tag){
        Optional<String> settingsEntity = systemSettings.tagValue(tag);
        if (!settingsEntity.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(String.format("Related Settings %s not found", tag));
        }
        String value = settingsEntity.get();

        return ResponseEntity.ok()
                .body(value);
    }
}
