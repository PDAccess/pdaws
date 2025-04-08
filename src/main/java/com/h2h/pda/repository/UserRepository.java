package com.h2h.pda.repository;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.user.UserRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

    @Query(value = "SELECT u FROM UserEntity u WHERE u.username=:username")
    UserEntity findByUsername(@Param("username") String username);

    @Query(value = "SELECT u FROM UserEntity u WHERE lower(u.username)=lower(:username) AND u.deletedAt IS NULL")
    Optional<UserEntity> userWithUsername(@Param("username") String username);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.email=:email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.deletedAt IS NOT NULL AND u.username=:username")
    UserEntity findByDeletedOrderByUsernameAsc(@Param("username") String username);

    @Query("SELECT c FROM UserEntity c WHERE c.deletedAt IS NULL AND c.blocked IS NULL order by username asc")
    List<UserEntity> findByNotDeletedOrderByUsernameAsc();

     @Query("SELECT c FROM UserEntity c WHERE c.blocked IS NOT NULL")
     List<UserEntity> findByBlocked();

     List<UserEntity> findByDeletedAtIsNotNull();

     @Query(value = "UPDATE UserEntity u SET u.email=?1 WHERE u.username =?2")
     @Modifying
     @Transactional
     int updateEmailWithUsername(String email, String username);

     @Query(value = "SELECT u FROM UserEntity u WHERE u.userId=:id")
     UserEntity findByUserid(@Param("id") String id);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.userId=:id AND u.deletedAt IS NULL")
    Optional<UserEntity> findById(@Param("id") String id);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.userId=:id")
    Optional<UserEntity> findByIdDetails(@Param("id") String id);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.userId=:id")
    Optional<UserEntity> findByIdAnyThing(@Param("id") String id);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.role=:role AND u.deletedAt IS NULL AND u.blocked IS NULL")
    List<UserEntity> findAllByRole(@Param("role") UserRole role);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL AND u.blocked IS NULL AND u.userId IN (SELECT g.id.userId FROM GroupUserEntity g WHERE g.id.groupId=:groupId)")
    List<UserEntity> findByGroupId(@Param("groupId") String groupId);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.external=:external AND u.deletedAt IS NULL AND u.blocked IS NULL")
    List<UserEntity> findAllUsersByExternal(@Param("external") Boolean external);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.external=:external AND u.role=:role AND u.deletedAt IS NULL AND u.blocked IS NULL")
    List<UserEntity> findAllUsersByExternalAndRole(@Param("external") Boolean external, @Param("role") UserRole role);
}
