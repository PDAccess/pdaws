package com.h2h.pda.api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.h2h.pda.entity.AuthenticationTokenEntity;
import com.h2h.pda.entity.MfaEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.MfaDto;
import com.h2h.pda.pojo.MfaVerification;
import com.h2h.pda.pojo.SmsData;
import com.h2h.pda.pojo.auth.LoginType;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.repository.AuthenticationTokenRepository;
import com.h2h.pda.repository.MfaRepository;
import com.h2h.pda.service.api.*;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static com.h2h.pda.jwt.SecurityConstants.*;

@RestController
@RequestMapping("/api/v1/mfa")
public class MfaController {

    private final Logger log = LoggerFactory.getLogger(MfaController.class);
    private static final String TOTP_ERROR = "TOTP error";

    @Autowired
    MfaRepository mfaRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    SendSmsService sendSmsService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ActionPdaService actionPdaService;

    @GetMapping(path = "/verification")
    public ResponseEntity<MfaVerification> getMfaVerification() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        MfaVerification mfaVerification = new MfaVerification();

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        if (optionalMfaEntity.isPresent()) {
            MfaEntity mfaEntity = optionalMfaEntity.get();
            mfaVerification.setGoogleAuthenticator(mfaEntity.getGoogleAuthenticator());
            mfaVerification.setEmail(mfaEntity.getEmail());
            mfaVerification.setSms(mfaEntity.getSms());
        }

        return new ResponseEntity<>(mfaVerification, HttpStatus.OK);
    }

    @GetMapping(path = "/secret")
    public ResponseEntity<HashMap<String, String>> getMfaSecret() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        if (!optionalMfaEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("secretKey", optionalMfaEntity.get().getSecretKey());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> disableMfa(@RequestBody MfaDto mfaDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        Optional<UserEntity> byName = usersOps.byName(username);
        if (!byName.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        UserEntity userEntity = byName.get();

        if (mfaDto.getType() == null || mfaDto.getType().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        MfaEntity mfaEntity;
        if (optionalMfaEntity.isPresent()) {
            mfaEntity = optionalMfaEntity.get();
        } else {
            mfaEntity = new MfaEntity();
            mfaEntity.setUsername(username);
        }

        mfaEntity.disableMfa(mfaDto.getType());
        mfaRepository.save(mfaEntity);

        userEntity.setTwofactorauth(mfaEntity.isMfaEnabled());
        usersOps.update(userEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/verification/{username}")
    public ResponseEntity<MfaVerification> getMfaVerificationForUser(@PathVariable String username) {

        MfaVerification mfaVerification = new MfaVerification();

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        if (optionalMfaEntity.isPresent()) {
            MfaEntity mfaEntity = optionalMfaEntity.get();
            mfaVerification.setGoogleAuthenticator(mfaEntity.getGoogleAuthenticator());
            mfaVerification.setEmail(mfaEntity.getEmail());
            mfaVerification.setSms(mfaEntity.getSms());
        }

        return new ResponseEntity<>(mfaVerification, HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/secret/{username}")
    public ResponseEntity<HashMap<String, String>> getMfaSecretForUser(@PathVariable String username) {

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        if (!optionalMfaEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("secretKey", optionalMfaEntity.get().getSecretKey());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping(path = "/{username}")
    public ResponseEntity<Void> disableMfaForUser(@PathVariable String username, @RequestBody MfaDto mfaDto) {

        Optional<UserEntity> byName = usersOps.byName(username);
        if (!byName.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserEntity userEntity = byName.get();

        if (mfaDto.getType() == null || mfaDto.getType().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        MfaEntity mfaEntity;
        if (optionalMfaEntity.isPresent()) {
            mfaEntity = optionalMfaEntity.get();
        } else {
            mfaEntity = new MfaEntity();
            mfaEntity.setUsername(username);
        }

        mfaEntity.disableMfa(mfaDto.getType());
        mfaRepository.save(mfaEntity);

        userEntity.setTwofactorauth(mfaEntity.isMfaEnabled());
        usersOps.update(userEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/check")
    public ResponseEntity<Boolean> mfaCheck(@RequestBody MfaDto mfaDto, HttpServletRequest request) throws NoSuchAlgorithmException {
        Optional<UserEntity> byName = usersOps.byName(mfaDto.getUsername());
        if (!byName.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserEntity userEntity = byName.get();

        if (userEntity.getTwofactorauth() != null && !userEntity.getTwofactorauth() && !systemSettings.isTagValue("two_factor_auth")) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(mfaDto.getUsername());
        if (!optionalMfaEntity.isPresent()) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }

        MfaEntity mfaEntity = optionalMfaEntity.get();
        mfaEntity.setLastCheckedAt(Timestamp.valueOf(LocalDateTime.now()));
        mfaEntity = mfaRepository.save(mfaEntity);

        String otpCode;
        try {
            otpCode = tokenGenerator.totp(mfaEntity.getSecretKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(TOTP_ERROR, e);
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(mfaEntity.getSms())) {
            //Sending sms
            SmsData smsData = new SmsData();
            smsData.setToPhone(userEntity.getEmail());
            smsData.setMessage(String.format("OTP Code is: %s", otpCode));
            sendSmsService.pushSMSRequest(smsData);
        }

        if (Boolean.TRUE.equals(mfaEntity.getEmail())) {
            EmailData emailData = new EmailData();
            emailData.setToMail(userEntity.getEmail());
            emailData.setSubject(String.format("MFA Authentication:%s %s ", userEntity.getUsername(), request.getHeader("X-FORWARDED-FOR")));
            emailData.setText(otpCode);
            emailData.setMailName(EmailNames.MFA_MAIL);
            sendEmailService.pushEmailRequest(emailData);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(path = "/auth")
    public ResponseEntity<String> mfaAuth(@RequestBody MfaDto mfaDto, HttpServletRequest request) {

        Optional<UserEntity> byName = usersOps.byName(mfaDto.getUsername());
        if (!byName.isPresent()) {
            authenticationService.saveAuthenticationAttempt(request, mfaDto.getUsername(), LoginType.MFA, "User not found", false);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserEntity userEntity = byName.get();
        Optional<MfaEntity> mfaEntity = mfaRepository.findById(mfaDto.getUsername());

        if (mfaEntity.isPresent() && StringUtils.hasText(mfaDto.getCode())) {
            MfaEntity mfa = mfaEntity.get();

            try {
                if (mfaDto.getCode().equals(tokenGenerator.totp(mfa.getSecretKey()))) {
                    AuthenticationTokenEntity authenticationTokenEntity = authenticationTokenRepository.findByUsername(userEntity.getUsername());
                    if (authenticationTokenEntity == null) {
                        authenticationService.saveAuthenticationAttempt(request, userEntity.getUsername(), LoginType.MFA, "Token is not found", false);
                        return new ResponseEntity<>("Token is not found", HttpStatus.NO_CONTENT);
                    } else {
                        String token = authenticationTokenEntity.getToken();
                        int sessionId = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                                .build()
                                .verify(token.replace(TOKEN_PREFIX, "")).getClaim(PDA_AUTH_ID).asInt();
                        authenticationTokenRepository.delete(authenticationTokenEntity);
                        authenticationService.saveAuthenticationAttempt(request, userEntity.getUsername(), LoginType.MFA, "OTP Code is valid", true);
                        actionPdaService.saveAction("Logined", sessionId);
                        return ResponseEntity.ok(token);
                    }
                } else {
                    log.warn("OTP coming {} expected {}", mfaDto.getCode(), tokenGenerator.totp(mfa.getSecretKey()));
                    authenticationService.saveAuthenticationAttempt(request, userEntity.getUsername(), LoginType.MFA, "OTP Code is invalid", false);
                    return ResponseEntity.badRequest().body("otp code is wrong");
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                log.error(TOTP_ERROR, e);
                authenticationService.saveAuthenticationAttempt(request, userEntity.getUsername(), LoginType.MFA, "OTP algorithm error", false);
                return ResponseEntity.badRequest().body("otp algo error");
            }
        } else {
            authenticationService.saveAuthenticationAttempt(request, userEntity.getUsername(), LoginType.MFA, "OTP Code is not found", false);
            return ResponseEntity.badRequest().body("mfa code is not found");
        }
    }

    @PostMapping(path = "/enable/check")
    public ResponseEntity<Boolean> enableMfaCheck(@RequestBody MfaDto mfaDto, HttpServletRequest request) throws NoSuchAlgorithmException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Optional<UserEntity> byName = usersOps.byName(username);
        if (!byName.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserEntity userEntity = byName.get();

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        MfaEntity mfaEntity;
        if (optionalMfaEntity.isPresent()) {
            mfaEntity = optionalMfaEntity.get();
        } else {
            mfaEntity = new MfaEntity();
            mfaEntity.setUsername(username);
            mfaEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }

        if (!StringUtils.hasText(mfaEntity.getSecretKey())) {
            //String chrs = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);

            Base32 base32 = new Base32();
            String secret = base32.encodeAsString(bytes);
            mfaEntity.setSecretKey(secret);
        }

        mfaEntity.setLastCheckedAt(Timestamp.valueOf(LocalDateTime.now()));
        mfaEntity = mfaRepository.save(mfaEntity);

        if (mfaDto.getType() != null && !mfaDto.getType().isEmpty()) {
            mfaEntity.setType(mfaDto.getType());
        }

        String otpCode;
        try {
            otpCode = tokenGenerator.totp(mfaEntity.getSecretKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(TOTP_ERROR, e);
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        if (mfaEntity.getSms()){
            //Sending sms
            SmsData smsData = new SmsData();
            smsData.setToPhone(userEntity.getEmail());
            smsData.setMessage(String.format("OTP Code is: %s", otpCode));
            sendSmsService.pushSMSRequest(smsData);
        }

        if (mfaEntity.getEmail()) {
            EmailData emailData = new EmailData();
            emailData.setToMail(userEntity.getEmail());
            emailData.setSubject(String.format("MFA Authentication:%s %s ", userEntity.getUsername(), request.getHeader("X-FORWARDED-FOR")));
            emailData.setText(otpCode);
            emailData.setMailName(EmailNames.MFA_MAIL);
            sendEmailService.pushEmailRequest(emailData);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(path = "/enable/auth")
    public ResponseEntity<String> enableMfaAuth(@RequestBody MfaDto mfaDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        Optional<UserEntity> byName = usersOps.byName(username);
        if (!byName.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserEntity userEntity = byName.get();

        if (mfaDto.getType() == null || mfaDto.getType().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<MfaEntity> mfaEntity = mfaRepository.findById(username);

        if (mfaEntity.isPresent() && StringUtils.hasText(mfaDto.getCode())) {
            MfaEntity mfa = mfaEntity.get();

            try {
                if (mfaDto.getCode().equals(tokenGenerator.totp(mfa.getSecretKey()))) {
                    mfa.enableMfa(mfaDto.getType());
                    mfa = mfaRepository.save(mfa);
                    userEntity.setTwofactorauth(mfa.isMfaEnabled());
                    usersOps.update(userEntity);
                    return ResponseEntity.ok("enable success");
                } else {
                    log.warn("OTP coming {} expected {}", mfaDto.getCode(), tokenGenerator.totp(mfa.getSecretKey()));
                    return ResponseEntity.badRequest().body("otp code is wrong");
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                log.error(TOTP_ERROR, e);
                return ResponseEntity.badRequest().body("otp algo error");
            }
        } else {
            return ResponseEntity.badRequest().body("mfa code is not found");
        }
    }
}