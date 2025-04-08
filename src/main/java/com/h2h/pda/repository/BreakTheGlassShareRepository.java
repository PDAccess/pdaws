package com.h2h.pda.repository;

import com.h2h.pda.entity.BreakTheGlassShareEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BreakTheGlassShareRepository extends CrudRepository<BreakTheGlassShareEntity, String>, JpaSpecificationExecutor {

    @Query("SELECT b FROM BreakTheGlassShareEntity b WHERE b.id=:id AND NOW()<b.expiredAt")
    Optional<BreakTheGlassShareEntity> findById(@Param("id") String id);

}
