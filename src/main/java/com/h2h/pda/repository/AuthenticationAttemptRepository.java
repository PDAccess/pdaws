package com.h2h.pda.repository;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.pojo.group.GroupRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import java.util.List;

@Repository
public interface AuthenticationAttemptRepository extends JpaRepository<AuthenticationAttemptEntity, Integer>, JpaSpecificationExecutor {

    @Query("SELECT c FROM AuthenticationAttemptEntity c WHERE c.username=?1 ORDER BY c.attemptedAt DESC")
    Page<AuthenticationAttemptEntity> findByUsername(String username, Pageable pageable);

    class QueryFilter {

        private QueryFilter() {
        }

        public static Specification<AuthenticationAttemptEntity> serviceFilter(String serviceId) {
            return (root, criteriaQuery, criteriaBuilder) -> {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("attemptedAt")));
                return criteriaBuilder.equal(root.get("serviceId"), serviceId);
            };
        }

        public static Specification<AuthenticationAttemptEntity> servicesFilter(List<String> serviceIds) {
            return (root, criteriaQuery, criteriaBuilder) -> {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("attemptedAt")));
                return criteriaBuilder.in(root.get("serviceId")).value(serviceIds);
            };
        }

        public static Specification<AuthenticationAttemptEntity> findByAuthByUser(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("username"), filter);
        }

        public static Specification<AuthenticationAttemptEntity> findByAuthFilterByUser(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + filter + "%");
        }

        public static Specification<AuthenticationAttemptEntity> findByAuthFilterByHost(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ipAddress")), "%" + filter + "%");
        }

        public static Specification<AuthenticationAttemptEntity> findByAuthFilterByUserAgent(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("userAgent")), "%" + filter + "%");
        }

        public static Specification<AuthenticationAttemptEntity> findByNullService() {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.isNull(root.get("service"));
        }

        public static Specification<AuthenticationAttemptEntity> serviceFilterByMember(String userId) {
            return (root, criteriaQuery, criteriaBuilder) -> {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("attemptedAt")));
                Join<AuthenticationAttemptEntity, GroupUserEntity> members = root.join("service").join("memberOf").join("group").join("members");

                return criteriaBuilder.and(criteriaBuilder.equal(members.join("user").get("userId"), userId)
                        , criteriaBuilder.equal(members.get("membershipRole"), GroupRole.ADMIN)
                        , criteriaBuilder.isNotNull(root.get("service")));
            };
        }
    }
}