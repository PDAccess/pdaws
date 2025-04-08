package com.h2h.pda.repository;

import com.h2h.pda.entity.SessionEntity;
import com.h2h.pda.pojo.session.SessionsStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<SessionEntity, Integer>, JpaSpecificationExecutor<SessionEntity> {

    @Query("SELECT c FROM SessionEntity c WHERE c.inventoryId=?1 ORDER BY c.startTime DESC ")
    List<SessionEntity> findByLastSession(String inventoryId);

    @Query("SELECT COUNT(c) FROM SessionEntity c WHERE c.inventoryId=?1")
    String findBySessionCount(String inventoryId);

    @Query("SELECT c FROM SessionEntity c ORDER BY c.startTime DESC")
    List<SessionEntity> findByStartimeDesc(Pageable pageable);

    @Query("SELECT c FROM SessionEntity c WHERE c.username=?1 ORDER BY c.startTime DESC")
    List<SessionEntity> findByUsername(String username, Pageable pageable);

    @Query("SELECT c FROM SessionEntity c WHERE c.inventoryId=?1 ORDER BY c.startTime DESC")
    List<SessionEntity> findByService(String inventoryId, Pageable pageable);

    Optional<SessionEntity> findFirstByUsernameAndInventoryIdOrderByStartTimeDesc(String username, String serviceId);

    @Query("SELECT count(c) FROM SessionEntity c WHERE c.endTime IS NULL")
    Long countByLive();

    @Query("SELECT count(c) FROM SessionEntity c WHERE c.endTime IS NULL AND c.username=?1")
    Long countByLiveUser(String username);

    @Query(value="SELECT c FROM SessionEntity c WHERE c.endTime IS NULL")
    List<SessionEntity> findByLive(Pageable pageable);

    @Query(value="SELECT c FROM SessionEntity c WHERE c.endTime IS NULL AND c.username=?1")
    List<SessionEntity> findByLiveUsername(String username, Pageable pageable);


    @Query(nativeQuery = true, value =
            "SELECT " +
                    "    DATE(start_time) AS sessions, COUNT(c) AS cnt " +
                    "FROM " +
                    "    psessions c " +
                    "WHERE " +
                    "     DATE(start_time) > DATE(?1)  " +
                    "GROUP BY " +
                    "    DATE(start_time)")
    List<SessionsStatistics> findSessionsByDate(String fromDate);

    @Query(nativeQuery = true, value ="SELECT DATE(start_time) AS sessions, COUNT(c) AS cnt " +
            "FROM psessions c " +
            "WHERE c.username=?1 " +
            "AND DATE(start_time) > DATE(?2) " +
            "GROUP BY DATE(start_time)")
    List<SessionsStatistics> findSessionsByDateForUsername(String username, String fromDate);

    @Query(nativeQuery = true, value ="SELECT DATE(start_time) AS sessions, COUNT(c) AS cnt " +
            "FROM psessions c " +
            "WHERE c.inventory_id=?1 " +
            "AND DATE(start_time) > DATE(?2) " +
            "GROUP BY DATE(start_time)")
    List<SessionsStatistics> findSessionsByDateForService(String serviceId, String fromDate);

    @Query("SELECT s FROM SessionEntity s WHERE s.sessionId=?1")
    SessionEntity findBySessionId(int sessionId);

    class QueryFilter {

        private QueryFilter() {
        }

        public static Specification<SessionEntity> findByUserList(List<String> ids){
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("userEntity").get("userId")).value(ids);
        }

        public static Specification<SessionEntity> findByServicenameList(List<String> ids) {

            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("serviceEntity").get("inventoryId")).value(ids);

        }

        public static Specification<SessionEntity> findByDate(Timestamp start, Timestamp end) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("startTime"), start, end);
        }

        public static Specification<SessionEntity> findBySessionFilterByUrole(String username) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("username"), username);
        }

        public static Specification<SessionEntity> findBySessionFilterByEndTime(boolean isNull) {
            return isNull ? (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.isNull(root.get("endTime"))
                    : (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.isNotNull(root.get("endTime"));
        }
        public static Specification<SessionEntity> extractSessionType(String sessionType) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.notEqual(root.get("sessionType"), sessionType);
        }

        public static Specification<SessionEntity> findBySessionFilterByUsers(List<String> users) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("username")).value(users);
        }

        public static Specification<SessionEntity> findBySessionFilterByServices(List<String> services) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("serviceEntity").get("name")).value(services);
        }
    }
}
