package com.h2h.pda.api.user;

import com.h2h.pda.entity.*;
import com.h2h.pda.jwt.LoginRequest;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.user.UserShell;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultTokenResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String VAULT_ERROR = "Vault error: {}";
    private static final String USERNAME = "username";

    private final Random rand = SecureRandom.getInstanceStrong();

    @Autowired
    UsersOps usersOps;

    @Autowired
    VaultService vaultService;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    ActionPdaService actionPdaService;

    @Autowired
    MfaService mfaService;

    @Autowired
    TenantService tenantService;

    private static final String EMAIL_REGEX = "^(.+)@(.+)$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    public UserController() throws NoSuchAlgorithmException {
    }

    private String vaultPath(UserRole role) {
        return role == UserRole.ADMIN ? UsersOps.AUTH_ADMINPASS_USERS : UsersOps.AUTH_USERPASS_USERS;
    }

    private boolean emailValid(String email) {
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }

    @GetMapping()
    public List<UserDTO> getUsers() {
        List<UserDTO> userWrappers = new ArrayList<>();
        List<UserEntity> userEntities = usersOps.findUsers();
        for (UserEntity userEntity : userEntities) {
            UserDTO userWrapper = new UserDTO(userEntity);

            Optional<MfaEntity> optionalMfaEntity = mfaService.getMfaStatusByUsername(userEntity.getUsername());
            MfaVerification mfaVerification = new MfaVerification();
            if (optionalMfaEntity.isPresent()) {
                MfaEntity mfaEntity = optionalMfaEntity.get();
                mfaVerification.setSms(mfaEntity.getSms());
                mfaVerification.setEmail(mfaEntity.getEmail());
                mfaVerification.setGoogleAuthenticator(mfaEntity.getGoogleAuthenticator());
            }
            userWrapper.setMfaVerification(mfaVerification);
            userWrappers.add(userWrapper);
        }
        return userWrappers;
    }

    @GetMapping(path = "/ipAddresses/{userId}")
    public ResponseEntity<List<String>> getUserIpAddresses(@PathVariable String userId){
        List<UserIpAddresses> userIpAddresses = usersOps.findIpAddresses(userId);
        List<String> ipAddresses = userIpAddresses.stream().map(UserIpAddresses::getIpAddress).collect(Collectors.toList());
        return ResponseEntity.ok(ipAddresses);
    }

    @GetMapping(path = "/id/{userid}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userid) {
        Optional<UserEntity> userEntity = usersOps.byId(userid);
        return userEntity.map(entity -> ResponseEntity.ok(new UserDTO(entity))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping(path = "/name/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        Optional<UserEntity> userEntity = usersOps.byName(username);
        return userEntity.map(entity -> new ResponseEntity<>(new UserDTO(entity), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    // TODO: Entity Fix
    @GetMapping(path = "public/username/{username}")
    public ResponseEntity<UserDTO> getPublicUserByUsername(@PathVariable String username) {
        Optional<UserEntity> userEntity = usersOps.byName(username);
        return userEntity.map(entity -> new ResponseEntity<>(new UserDTO(entity), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "/create")
    public ResponseEntity<String> addUser(@RequestBody UserCreateParams param) {
        if (param.getUserEntity() == null || param.getUserEntity().getUsername() == null || param.getUserEntity().getUsername().equals(""))
            return ResponseEntity.badRequest().body("Username cannot be empty");

        Optional<UserEntity> optionalUserEntity = usersOps.byName(param.getUserEntity().getUsername());
        if (optionalUserEntity.isPresent()) {
            return new ResponseEntity<>("Username already exist", HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO = param.getUserEntity();

        if (userDTO.getRole() == null || userDTO.getRole() == UserRole.UNKNOWN_ROLE)
            return new ResponseEntity<>("User role cannot be empty or user role is wrong.", HttpStatus.BAD_REQUEST);

        if (userDTO.getEmail() == null || !emailValid(userDTO.getEmail()))
            return new ResponseEntity<>("Email cannot be empty or wrong.", HttpStatus.BAD_REQUEST);

        if (userDTO.getFirstName() == null || userDTO.getFirstName().equals(""))
            return new ResponseEntity<>("User first name cannot be empty.", HttpStatus.BAD_REQUEST);


        if (userDTO.getLastName() == null || userDTO.getLastName().equals(""))
            return new ResponseEntity<>("User last name cannot be empty.", HttpStatus.BAD_REQUEST);


        if (userDTO.getPhone() == null || userDTO.getPhone().equals(""))
            return new ResponseEntity<>("User phone cannot be empty.", HttpStatus.BAD_REQUEST);

        if (param.getPassword() == null || param.getPassword().getUserPassword() == null || param.getPassword().getUserPassword().equals(""))
            return new ResponseEntity<>("User password cannot be empty.", HttpStatus.BAD_REQUEST);

        UserEntity userEntity = userDTO.unWrap();
        userEntity.setTwofactorauth(false);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();

        if (vaultService.isVaultEnabled()) {
            try {
                VaultTemplate template = vaultService.newTemplate(details.getToken());

                template.write(vaultPath(userDTO.getRole())
                        + userDTO.getUsername(), param.getPassword());

                Credential credential = new Credential();
                credential.setUsername(userDTO.getUsername());
                credential.setPassword(param.getPassword().getUserPassword());

                template.write(SECRET_INVENTORY + userDTO.getUserId(), (Object) credential);

            } catch (VaultException ve) {
                log.error(VAULT_ERROR, ve.getMessage());
            }
        }

        try {
            userEntity = usersOps.newUser(userEntity);
            EmailData emailData = new EmailData();
            emailData.setMailName(EmailNames.NEW_USER);
            emailData.setSubject("Your Account Crated");
            emailData.getHtml().put("message", "Your account created");
            emailData.getHtml().put("name", userDTO.getFirstName());
            emailData.getHtml().put(USERNAME, userDTO.getUsername());
            emailData.getHtml().put("password", param.getPassword().getUserPassword());
            emailData.setToMail(userDTO.getEmail());
            sendEmailService.pushEmailRequest(emailData);

            if (param.getIpAddress() != null && !param.getIpAddress().isEmpty()) {
                for (String ipAddress : param.getIpAddress()) {
                    usersOps.saveIpAddresses(userEntity.getUserId(), ipAddress);
                }
            }
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        }

        actionPdaService.saveAction(String.format("%s user is created", userDTO.getUsername()));

        return ResponseEntity.ok(userEntity.getUserId());
    }

    @PostMapping(path = "public/user")
    public ResponseEntity<String> addPublicUser(@RequestBody UserEntity userEntity) {

        String id = UUID.randomUUID().toString();

        userEntity.setUserId(id);
        userEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userEntity.setTwofactorauth(false);

        usersOps.newUser(userEntity);

        return ResponseEntity.ok(userEntity.getUserId());
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping(path = "/id/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable String id) {
        Optional<UserEntity> optionalUserEntity = usersOps.byId(id);
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            if (userEntity.isExternal()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            usersOps.remove(userEntity.getUserId());
            usersOps.deleteIpAddresses(userEntity.getUserId());
            actionPdaService.saveAction(String.format("%s user is deleted", userEntity.getUsername()));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "/un-delete/{id}")
    public ResponseEntity<Void> unDeleteUser(@PathVariable String id) {
        Optional<UserEntity> optionalUserEntity = usersOps.byId(id);
        if (!optionalUserEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        UserEntity userEntity = optionalUserEntity.get();
        if (userEntity.isExternal()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userEntity.setDeletedAt(null);
        usersOps.update(userEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping(path = "harddeleteuser/{id}")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable String id) {
        Optional<UserEntity> userEntity = usersOps.anyUserById(id);
        if (userEntity.isPresent()) {
            usersOps.hardRemove(userEntity.get().getUserId());
            usersOps.deleteIpAddresses(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "user")
    public ResponseEntity<Void> updateUser(@RequestBody UserDTO dto) {
        Optional<UserEntity> userEntity1 = usersOps.byId(dto.getUserId());
        if (userEntity1.isPresent()) {
            UserEntity user = userEntity1.get();
            if (user.isExternal()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (dto.getRole() == null || dto.getRole().equals("") || (!dto.getRole().equals("Admin") && !dto.getRole().equals("User") && !dto.getRole().equals("System")))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (dto.getEmail() == null || !emailValid(dto.getEmail()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Optional<UserEntity> userEmailConfirm = usersOps.byEmail(dto.getEmail());
            if (userEmailConfirm.isPresent() && !userEmailConfirm.get().getEmail().equals(user.getEmail()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (dto.getUsername() == null || dto.getUsername().equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Optional<UserEntity> userEntityConfirm = usersOps.byName(dto.getUsername());
            if (userEntityConfirm.isPresent() && !userEntityConfirm.get().getUsername().equals(user.getUsername()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (dto.getFirstName() == null || dto.getFirstName().equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (dto.getLastName() == null || dto.getLastName().equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (dto.getPhone() == null || dto.getPhone().equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (dto.getExternal() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (user.getEmail() != null && !user.getEmail().equals(dto.getEmail())) {
                EmailData emailData = new EmailData();
                emailData.setMailName(EmailNames.CHANGE_EMAIL);
                emailData.setSubject("Your Email Changed");
                emailData.getHtml().put("message", "Your Email updated as");
                emailData.getHtml().put("newmail", dto.getEmail());
                emailData.getHtml().put(USERNAME, user.getUsername());
                emailData.setToMail(dto.getEmail());

                sendEmailService.pushEmailRequest(emailData);
            }
            usersOps.deleteIpAddresses(dto.getUserId());

            if(dto.getIpAddress() != null && dto.getIpAddress().isEmpty()){
                for (String ipAddress : dto.getIpAddress()) {
                    usersOps.saveIpAddresses(dto.getUserId(), ipAddress);
                }
            }

            user.setEmail(dto.getEmail());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setExternal(dto.getExternal());
            user.setPhone(dto.getPhone());
            user.setRole(dto.getRole());
            user.setUsername(dto.getUsername());
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            usersOps.update(user);
            actionPdaService.saveAction("User profile is updated");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping(path = "/update/email")
    public ResponseEntity<Void> updateEmail(@RequestBody String email) {
        UserEntity userEntity = usersOps.securedUser();
        userEntity.setEmail(email);
        usersOps.update(userEntity);
        actionPdaService.saveAction("User profile is updated");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdate passwordUpdate) {
        UserEntity user = usersOps.securedUser();

        try {
            VaultTemplate template = vaultService.newTemplate();

            template.doWithVault(restOperations -> {
                LoginRequest entity = new LoginRequest();
                entity.setPassword(passwordUpdate.getCurrentPass());

                ResponseEntity<VaultTokenResponse> responseEntity = restOperations.
                        postForEntity(String.format("/auth/userpass/login/%s", user.getUsername()), entity, VaultTokenResponse.class);
                log.info("vault response is: {} {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());

                return responseEntity.getBody().getToken();
            });


            template.write(vaultPath(user.getRole()) + user.getUsername(), new Password(passwordUpdate.getNewPass()));

            Credential credential = new Credential();
            credential.setUsername(user.getUsername());
            credential.setPassword(passwordUpdate.getNewPass());

            template.write(SECRET_INVENTORY + user.getUserId(), (Object) credential);

        } catch (VaultException ve) {
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/who")
    public ResponseEntity<UserWrapper> whoAmI() {
        return ResponseEntity.ok(new UserWrapper(usersOps.securedUser()));
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "/block")
    public ResponseEntity<UserEntity> userBlock(@RequestBody String userid) {
        Optional<UserEntity> userEntity = usersOps.byId(userid);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            if (user.getDeletedAt() != null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            user.setBlocked(new Timestamp(System.currentTimeMillis()));
            usersOps.update(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "/unblock")
    public ResponseEntity<UserEntity> userUnblock(@RequestBody String userid) {
        Optional<UserEntity> userEntity = usersOps.byId(userid);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            user.setBlocked(null);
            user = usersOps.update(user);
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/blocked")
    public List<UserEntity> getBlockList() {
        return usersOps.findBlockedUsers();
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/deleted")
    public List<UserEntity> getDeletedAtList() {
        return usersOps.findDeletedUsers();
    }

    @PostMapping(path = "changetwofactorauth/{status}")
    public ResponseEntity<Void> changeTwoFactorAuth(@PathVariable Integer status) {
        UserEntity user = usersOps.securedUser();
        user.setTwofactorauth(status != null && status == 1);
        usersOps.update(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/status")
    public ResponseEntity<Void> statusUpdate(@RequestBody String status) {
        UserEntity user = usersOps.securedUser();
        user.setStatus(status);
        usersOps.update(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/shell")
    public ResponseEntity<Void> updateUserShell(@RequestBody UserShell shell) {
        UserEntity user = usersOps.securedUser();
        user.setShell(shell);
        usersOps.update(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/image/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("image") MultipartFile file) {

        if (!file.getContentType().matches("image/(.*)")) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String encodeImage;
        String image;
        try {
            encodeImage = Base64.getEncoder().encodeToString(file.getBytes());
            image = "data:image/png;base64," + encodeImage;
        } catch(IOException e) {
            log.error("IO Error: {}" ,e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserEntity userEntity = usersOps.securedUser();
        usersOps.saveProfileImage(userEntity.getUserId(), image);

        return ResponseEntity.ok(image);
    }

    @DeleteMapping(path = "/image/remove")
    public ResponseEntity<Void> removeFile() {
        UserEntity userEntity = usersOps.securedUser();
        usersOps.deleteProfileImage(userEntity.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/image/{user}")
    public ResponseEntity<String> getImage(@PathVariable String user, WebRequest request) {
        CacheControl cacheControl = CacheControl.maxAge(30, TimeUnit.MINUTES);
        Optional<UserEntity> byName = usersOps.byName(user);

        if (byName.isPresent()) {
            UserEntity userEntity = byName.get();
            Optional<ProfileImageEntity> profileImageEntity = usersOps.findProfileImage(userEntity.getUserId());

            if (!profileImageEntity.isPresent()) {
                return ResponseEntity.ok()
                        .eTag("")
                        .cacheControl(cacheControl)
                        .body(null);
            }
            String image = profileImageEntity.get().getImage();

            String changedAt = profileImageEntity.get().getChangedAt();
            if (request.checkNotModified(changedAt)) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }

            return ResponseEntity.ok()
                    .eTag(changedAt)
                    .cacheControl(cacheControl)
                    .body(image);
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/password/reset")
    public ResponseEntity<Void> resetPassword(@RequestParam (name = "email") String userEmail) {
        Optional<UserEntity> optionalUserEntity = usersOps.byEmail(userEmail);
        if (!optionalUserEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserEntity userEntity = optionalUserEntity.get();

        Optional<PasswordResetEntity> optionalPasswordResetEntity = usersOps.getPasswordResetStatusByEmail(userEmail);
        if (optionalPasswordResetEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PasswordResetEntity passwordResetEntity = new PasswordResetEntity(userEntity, false);
        passwordResetEntity.setRequestedAt(new Timestamp(System.currentTimeMillis()));
        passwordResetEntity.setId(UUID.randomUUID().toString());

        usersOps.savePasswordResetStatus(passwordResetEntity);

        EmailData emailData = new EmailData();
        emailData.setMailName(EmailNames.PASSWORD_RESET);
        emailData.setSubject(String.format("Password Reset: %s", userEntity.getUsername()));
        emailData.getHtml().put(USERNAME, userEntity.getUsername());
        emailData.setToMail(userEmail);
        sendEmailService.pushEmailRequest(emailData);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "/password/change")
    public ResponseEntity<Void> changePassword(@RequestParam String userid) {

        Optional<UserEntity> byId = usersOps.byId(userid);
        if (!byId.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserEntity userEntity = byId.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenDetails details = (TokenDetails) authentication.getDetails();
        String pass = generatePassword(8);
        Password password = new Password(pass);

        try {
            VaultTemplate template = vaultService.newTemplate(details.getToken());

            template.write(vaultPath(userEntity.getRole()) + userEntity.getUsername(), password);

            Credential credential = new Credential();
            credential.setUsername(userEntity.getUsername());
            credential.setPassword(password.getUserPassword());

            template.write(SECRET_INVENTORY + userEntity.getUserId(), (Object) credential);
        }catch (VaultException ve){
            log.error(VAULT_ERROR, ve.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<PasswordResetEntity> optionalPasswordResetEntity = usersOps.getPasswordResetStatusByUserId(userid);
        if (optionalPasswordResetEntity.isPresent()) {
            PasswordResetEntity passwordResetEntity = optionalPasswordResetEntity.get();
            passwordResetEntity.setApproved(true);
            usersOps.savePasswordResetStatus(passwordResetEntity);
        }

        EmailData emailData = new EmailData();
        emailData.setMailName(EmailNames.APPROVE_PASSWORD_CHANGE);
        emailData.setSubject(String.format("Approved Password Reset: %s", userEntity.getUsername()));
        emailData.getHtml().put(USERNAME, userEntity.getUsername());
        emailData.getHtml().put("password", password.getUserPassword());
        emailData.setToMail(userEntity.getEmail());
        sendEmailService.pushEmailRequest(emailData);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping(path = "/password/change/{userid}")
    public ResponseEntity<Void> rejectChangePassword(@PathVariable String userid) {
        Optional<PasswordResetEntity> optionalPasswordResetEntity = usersOps.getPasswordResetStatusByUserId(userid);
        if (!optionalPasswordResetEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        usersOps.deletePasswordResetStatus(optionalPasswordResetEntity.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "getForgetPasswordUser")
    public List<PasswordResetEntity> getForgetUsers() {
        return usersOps.getNotApprovedPasswordReset();
    }

    private String generatePassword(int n) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789" +
                "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = rand.nextInt(alphaNumericString.length());
            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}