package com.h2h.pda.repository;

import com.h2h.pda.api.services.ActionController;
import com.h2h.pda.entity.ActionEntity;
import com.h2h.pda.pojo.service.ActionsStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ActionRepository extends CrudRepository<ActionEntity, String>, JpaSpecificationExecutor<ActionEntity> {

    @Query(value = "SELECT c FROM ActionEntity c")
    @Deprecated
    List<ActionEntity> findByAll(Pageable pageable);

    @Query("SELECT c FROM ActionEntity c WHERE c.sessionEntity.username=?1 ORDER BY c.sessionEntity.startTime DESC")
    List<ActionEntity> findByUsername(String username, Pageable pageable);

    @Query("SELECT c FROM ActionEntity c WHERE c.sessionEntity.serviceEntity.inventoryId=?1 ORDER BY c.sessionEntity.startTime DESC")
    List<ActionEntity> findByServiceId(String inventoryId, Pageable pageable);

    @Deprecated
    @Query("SELECT count(c) FROM ActionEntity c WHERE c.sessionEntity.username=?1")
    Integer countByUsername(String username);

    @Query("SELECT count(c) FROM ActionEntity c")
    Integer countByAll();

    @Query("SELECT c FROM ActionEntity c WHERE c.sessionEntity.sessionId= :sessionId AND c.proxyAction LIKE CONCAT(:query,'%')")
    List<ActionEntity> findByQueryTypeDatabaseSessions(@Param("sessionId") int sessionId, @Param("query") String query);

    @Query(nativeQuery = true, value =
            "SELECT " +
                    "    DATE(action_time) AS actions, COUNT(c) AS cnt " +
                    "FROM " +
                    "    plogs c " +
                    "WHERE " +
                    "     DATE(action_time) > DATE(?1)  " +
                    "GROUP BY " +
                    "    DATE(action_time)")
    List<ActionsStatistics> findActionsByDateForAdmin(Timestamp timestamp);

    @Query(nativeQuery = true, value ="SELECT DATE(c.actionTime) AS actions, COUNT(c) AS cnt " +
            "FROM plogs c JOIN psessions p ON c.sessionId = p.sessionId " +
            "WHERE p.username=?1 AND DATE(c.actionTime) > DATE(?2) " +
            "GROUP BY DATE(c.actionTime)")
    List<ActionsStatistics> findActionsByDateForUsername(String username, Timestamp timestamp);

    @Query(nativeQuery = true, value = "SELECT DATE(c.actionTime) AS actions, COUNT(c) AS cnt " +
            "FROM plogs c JOIN psessions p ON c.sessionId = p.sessionId " +
            "WHERE p.inventoryId=?1 AND DATE(c.actionTime) > DATE(?2) " +
            "GROUP BY DATE(c.actionTime)")
    List<ActionsStatistics> findActionsByDateForService(String inventoryId, Timestamp timestamp);


    class QueryFilter {
        public static Specification<ActionEntity> findByActionsForUser(String username) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get(ActionController.SESSION_ENTITY).get("username")).value(username);
        }

        public static Specification<ActionEntity> findByActionsFilterByUsername(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(ActionController.SESSION_ENTITY).get("username")), "%" + filter + "%");
        }

        public static Specification<ActionEntity> findByDate(Timestamp start, Timestamp end) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("actionTime"), start, end);
        }

        public static Specification<ActionEntity> findByActionsFilterByService(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(ActionController.SESSION_ENTITY).get("serviceEntity").get("name")), "%" + filter + "%");
        }

        public static Specification<ActionEntity> findByActionsFilterByAction(String filter) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("proxyAction")), "%" + filter + "%");
        }

        private QueryFilter() {
        }
    }
}
