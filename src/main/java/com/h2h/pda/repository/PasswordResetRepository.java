package com.h2h.pda.repository;

import com.h2h.pda.entity.PasswordResetEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends CrudRepository<PasswordResetEntity, String> {

    @Query(value = "SELECT p FROM PasswordResetEntity p WHERE " +
            "p.userEntity.deletedAt IS NULL AND " +
            "                                            p.userEntity.email=:email AND " +
            "                                            p.isApproved IS FALSE")
    Optional<PasswordResetEntity> findByUseremail(@Param("email") String email);

    @Query(value = "SELECT p FROM PasswordResetEntity p WHERE " +
            "p.userEntity.deletedAt IS NULL AND " +
            "p.userEntity.userId=:id AND " +
            "p.isApproved IS FALSE")
    Optional<PasswordResetEntity> findByUserid(@Param("id") String id);

    @Query(value = "SELECT p FROM PasswordResetEntity p WHERE p.userEntity.deletedAt IS NULL AND " +
            "p.isApproved IS FALSE")
    List<PasswordResetEntity> findAllByApprove();

}
