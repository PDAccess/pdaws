package com.h2h.pda.repository;

import com.h2h.pda.entity.BreakTheGlassEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreakTheGlassRepository extends CrudRepository<BreakTheGlassEntity, String>, JpaSpecificationExecutor {

    @Query(value = "SELECT b FROM BreakTheGlassEntity b WHERE b.connectionUserEntity.id=:user AND b.serviceid=:service_id AND (checked IS NULL OR checked IS FALSE)")
    List<BreakTheGlassEntity> findByServiceAndUserAndCheck(@Param("user") int user, @Param("service_id") String serviceId);

    @Query(value = "SELECT b FROM BreakTheGlassEntity b WHERE b.serviceid=:service_id AND (checked IS NULL OR checked IS FALSE)")
    List<BreakTheGlassEntity> findByServiceAndCheck(@Param("service_id") String serviceId);

    BreakTheGlassEntity findTop1ByCredentialEntityCredentialIdOrderByCheckedTimeDesc(String credentialId);

    class QueryFilter {

        public static Specification<BreakTheGlassEntity> findByUsers(List<String> users) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("userEntity").get("userId")).value(users);
        }

        public static Specification<BreakTheGlassEntity> findByServices(List<String> services) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("serviceid")).value(services);
        }

        private QueryFilter() {
        }
    }

}
