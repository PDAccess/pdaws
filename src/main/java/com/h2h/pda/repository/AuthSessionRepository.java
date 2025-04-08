package com.h2h.pda.repository;

import com.h2h.pda.entity.AuthSessionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface AuthSessionRepository extends CrudRepository<AuthSessionEntity, Integer>, JpaSpecificationExecutor {

    @Query("SELECT c FROM AuthSessionEntity c WHERE c.username=?1 ORDER BY c.createdAt DESC")
    List<AuthSessionEntity> findByUsername(String username, Pageable pageable);

    @Query("SELECT COUNT(c) FROM AuthSessionEntity c WHERE c.updatedAt IS NULL")
    Long findByLiveCount();

    class QueryFilter {

        private QueryFilter() {
        }

        public static Specification<AuthSessionEntity> findByAuthFilterByUser(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + filter + "%");
        }

        public static Specification<AuthSessionEntity> findByAuthFilterByHost(String filter){
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ipAddress")), "%" + filter + "%");
        }

        public static Specification<AuthSessionEntity> findByAuthFilterByUserAgent(String filter){
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("userAgent")), "%" + filter + "%");
        }

        public static Specification<AuthSessionEntity> findByAuthFilterByUpdatedAt(){
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.isNull(root.get("updatedAt"));
        }
    }
}
