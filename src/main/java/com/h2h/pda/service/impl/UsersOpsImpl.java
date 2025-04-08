package com.h2h.pda.service.impl;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.Credential;
import com.h2h.pda.pojo.Password;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.*;
import com.h2h.pda.service.api.SendEmailService;
import com.h2h.pda.service.api.UsersOps;
import com.h2h.pda.service.api.VaultService;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.VaultException;

import java.sql.Timestamp;
import java.util.*;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;
import static com.h2h.pda.service.api.VaultService.VAULT_ERROR;

@Service
public class UsersOpsImpl implements UsersOps {
    private Logger log = LoggerFactory.getLogger(UsersOpsImpl.class);

    @Autowired
    TenantRepository tenantRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VaultService vaultService;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    UserIpAddressesRepository userIpAddressesRepository;

    @Autowired
    ProfileImageRepository profileImageRepository;

    @Autowired
    PasswordResetRepository passwordResetRepository;

    @JmsListener(destination = "multicast://pda.user.create", containerFactory = "queueListenerFactory")
    void sendCreateUserMail(@Payload UserEntity userEntity) {
        log.info("Send E-mail User is {}", userEntity.getUsername());

        EmailData emailData = new EmailData();
        emailData.setMailName(EmailNames.NEW_USER);
        emailData.setSubject("Your Account Crated");
        emailData.getHtml().put("message", "Your account created");
        emailData.getHtml().put("name", userEntity.getFirstName());
        emailData.getHtml().put(USERNAME, userEntity.getUsername());
        //emailData.getHtml().put("password", password.getUserPassword());
        emailData.setToMail(userEntity.getEmail());
        sendEmailService.pushEmailRequest(emailData);
    }

    @Override
    public Optional<UserEntity> byId(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<UserEntity> anyUserById(String userId) {
        return userRepository.findByIdAnyThing(userId);
    }

    @Override
    public Optional<UserEntity> byName(String username) {
        return userRepository.userWithUsername(username);
    }

    @Override
    public Optional<UserEntity> byEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public UserEntity securedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrUserId = (String) authentication.getPrincipal();
        Optional<UserEntity> user = byName(usernameOrUserId);
        if (!user.isPresent()) {
            user = byId(usernameOrUserId);
        }

        return user.get();
    }

    @Override
    public UserEntity newUser(UserEntity userEntity) {
        return newUser(userEntity, null);
    }

    @Override
    public UserEntity newUser(UserEntity userEntity, Password password) {
        String id = UUID.randomUUID().toString();

        Optional<UserEntity> userEntityConfirm = Optional.ofNullable(userRepository.findByUsername(userEntity.getUsername()));
        Optional<UserEntity> userDeletedEntityConfirm = Optional.ofNullable(userRepository.findByDeletedOrderByUsernameAsc(userEntity.getUsername()));

        if (userEntityConfirm.isPresent() || userDeletedEntityConfirm.isPresent())
            throw new IllegalArgumentException("Username is all ready exists");

        if (userEntity.getRole() == null) {
            throw new IllegalArgumentException("User don't have valid role");
        }

        userEntity.setUserId(id);
        userEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userEntity.setTwofactorauth(false);

        if (vaultService.isVaultEnabled() && password != null) {
            try {
                vaultService.write(AUTH_USERPASS_USERS
                        + userEntity.getUsername(), password);

                Credential credential = new Credential();
                credential.setUsername(userEntity.getUsername());
                credential.setPassword(password.getUserPassword());

                vaultService.write(SECRET_INVENTORY + userEntity.getUserId(), (Object) credential);

            } catch (VaultException ve) {
                log.error(VAULT_ERROR, ve.getMessage());
            }
        }

        userEntity = userRepository.save(userEntity);

        jmsTemplate.convertAndSend("multicast://pda.user.create", userEntity);

        return userEntity;
    }

    @Override
    public UserEntity update(UserEntity userEntity) {
        UserEntity save = userRepository.save(userEntity);
        jmsTemplate.convertAndSend("multicast://pda.user.update", save);

        return save;
    }

    @Override
    @Transactional
    public void remove(String userId) {
        Objects.requireNonNull(userId);
        Optional<UserEntity> byName = byId(userId);
        if (byName.isPresent()) {
            UserEntity user = byName.get();
            user.setDeletedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
        }
    }

    @Override
    public void hardRemove(String userId) {
        Objects.requireNonNull(userId);
        Optional<UserEntity> optionalUserEntity = byId(userId);
        optionalUserEntity.ifPresent(userEntity -> userRepository.delete(userEntity));
    }

    public List<UserEntity> findUsersByExternal(Boolean isExternal) {
        return userRepository.findAllUsersByExternal(isExternal);
    }

    @Override
    public List<UserEntity> findUsersByExternalAndRole(Boolean isExternal, UserRole role) {
        return userRepository.findAllUsersByExternalAndRole(isExternal, role);
    }

    @Override
    public List<UserEntity> findUsers() {
        return userRepository.findByNotDeletedOrderByUsernameAsc();
    }

    @Override
    public List<UserEntity> findUsersByRole(UserRole userRole) {
        return userRepository.findAllByRole(userRole);
    }

    @Override
    public List<UserEntity> findDeletedUsers() {
        return userRepository.findByDeletedAtIsNotNull();
    }

    @Override
    public List<UserEntity> findBlockedUsers() {
        return userRepository.findByBlocked();
    }

    @Override
    public List<UserIpAddresses> findIpAddresses(String userId) {
        return userIpAddressesRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteIpAddresses(String userId) {
        List<UserIpAddresses> userIpAddresses = userIpAddressesRepository.findAllByUserId(userId);
        for (UserIpAddresses userIpAddress : userIpAddresses) {
            userIpAddressesRepository.delete(userIpAddress);
        }
    }

    @Override
    public void saveIpAddresses(String userId, String ipAddress) {
        userIpAddressesRepository.save(new UserIpAddresses(userId, ipAddress));
    }

    @Override
    public void saveProfileImage(String userId, String image) {
        ProfileImageEntity profileImageEntity =  new ProfileImageEntity(userId, image);
        profileImageEntity.setChangedAt(new Timestamp(System.currentTimeMillis()).toString());
        profileImageRepository.save(profileImageEntity);
    }

    @Override
    public void deleteProfileImage(String userId) {
        Optional<ProfileImageEntity> profileImageEntity = profileImageRepository.findById(userId);
        profileImageEntity.ifPresent(imageEntity -> profileImageRepository.delete(imageEntity));
    }

    @Override
    public Optional<ProfileImageEntity> findProfileImage(String userId) {
        return profileImageRepository.findById(userId);
    }

    @Override
    public Optional<PasswordResetEntity> getPasswordResetStatusByEmail(String userEmail) {
        return passwordResetRepository.findByUseremail(userEmail);
    }

    @Override
    public Optional<PasswordResetEntity> getPasswordResetStatusByUserId(String userId) {
        return passwordResetRepository.findByUserid(userId);
    }

    @Override
    public void savePasswordResetStatus(PasswordResetEntity passwordResetEntity) {
        passwordResetRepository.save(passwordResetEntity);
    }

    @Override
    public void deletePasswordResetStatus(PasswordResetEntity passwordResetEntity) {
        passwordResetRepository.delete(passwordResetEntity);
    }

    @Override
    public List<PasswordResetEntity> getNotApprovedPasswordReset() {
        return passwordResetRepository.findAllByApprove();
    }

    @Bean
    public UserDetailsService findDetailService() {
        return username -> {
            Optional<UserEntity> byName = byName(username);

            if (!byName.isPresent()) {
                throw new UsernameNotFoundException(username);
            }
            UserEntity applicationUser = byName.get();
            return new User(applicationUser.getUsername(), "", Collections.emptyList());
        };
    }
}
