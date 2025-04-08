package com.h2h.pda.service.api;

import com.h2h.pda.entity.PasswordResetEntity;
import com.h2h.pda.entity.ProfileImageEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.entity.UserIpAddresses;
import com.h2h.pda.pojo.Password;
import com.h2h.pda.pojo.user.UserRole;

import java.util.List;
import java.util.Optional;

public interface UsersOps {
    String USERNAME = "username";

    String AUTH_USERPASS_USERS = "/auth/userpass/users/";


    String AUTH_ADMINPASS_USERS = "/auth/adminpass/users/";

    Optional<UserEntity> byId(String userId);

    Optional<UserEntity> anyUserById(String userId);

    Optional<UserEntity> byName(String username);

    Optional<UserEntity> byEmail(String userEmail);

    UserEntity securedUser();

    UserEntity newUser(UserEntity userEntity);

    UserEntity newUser(UserEntity userEntity, Password password) throws IllegalArgumentException;

    UserEntity update(UserEntity userEntity);

    void remove(String userId);

    void hardRemove(String userId);

    List<UserEntity> findUsersByExternal(Boolean isExternal);

    List<UserEntity> findUsersByExternalAndRole(Boolean isExternal, UserRole role);

    List<UserEntity> findUsers();

    List<UserEntity> findUsersByRole(UserRole userRole);

    List<UserEntity> findDeletedUsers();

    List<UserEntity> findBlockedUsers();

    List<UserIpAddresses> findIpAddresses(String userId);

    void deleteIpAddresses(String userId);

    void saveIpAddresses(String userId, String ipAddress);

    void saveProfileImage(String userId, String image);

    void deleteProfileImage(String userId);

    Optional<ProfileImageEntity> findProfileImage(String userId);

    Optional<PasswordResetEntity> getPasswordResetStatusByEmail(String userEmail);

    Optional<PasswordResetEntity> getPasswordResetStatusByUserId(String userId);

    void savePasswordResetStatus(PasswordResetEntity passwordResetEntity);

    void deletePasswordResetStatus(PasswordResetEntity passwordResetEntity);

    List<PasswordResetEntity> getNotApprovedPasswordReset();
}
