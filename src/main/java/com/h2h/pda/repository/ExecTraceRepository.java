package com.h2h.pda.repository;

import com.h2h.pda.entity.ExecTrace;
import com.h2h.pda.pojo.ExecTraceWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ExecTraceRepository extends PagingAndSortingRepository<ExecTrace, String>, JpaSpecificationExecutor {
    List<ExecTrace> findByHost(String host);

    @Query("SELECT c FROM ExecTrace c WHERE (reportTime > :startTime and reportTime < :endTime) AND c.serviceId=:serviceId AND (:filter IS NULL OR LOWER(c.command) LIKE %:filter% OR c.params LIKE %:filter%)")
    Page<ExecTrace> findByServiceId(@Param("serviceId") String serviceId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);

    @Query("SELECT c FROM ExecTrace c WHERE (reportTime > :startTime and reportTime < :endTime) AND c.serviceId=:serviceId AND (:filter IS NULL OR LOWER(c.command) LIKE %:filter% OR c.params LIKE %:filter%) ORDER BY c.time DESC")
    List<ExecTrace> findAllByServiceId(@Param("serviceId") String serviceId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    @Query("SELECT new com.h2h.pda.pojo.ExecTraceWrapper(c, s.name) FROM ExecTrace c LEFT JOIN ServiceEntity s ON s.inventoryId=c.serviceId WHERE (report_time > :startTime and report_time < :endTime) AND c.groupid=:groupid AND (:command IS NULL OR LOWER(c.command) LIKE %:command% OR c.params LIKE %:command%)")
    List<ExecTraceWrapper> findByGroupIdAndCommand(@Param("groupid") String groupid, @Param("command") String command, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);

    @Query(value = "SELECT DISTINCT c.user FROM ExecTrace c")
    List<String> findAllUser();

    @Query(value = "SELECT COUNT(e) FROM ExecTrace e WHERE e.serviceId=:serviceId")
    Long findCountByServiceId(@Param("serviceId") String serviceId);

    @Query(value = "SELECT c FROM ExecTrace c WHERE c.serviceId=:serviceId AND c.loginTime=:time AND c.loginTerminal=:terminal AND (report_time > :startTime and report_time < :endTime) AND (:command IS NULL OR LOWER(c.command) LIKE %:command% OR c.params LIKE %:command%)")
    Page<ExecTrace> findByServiceAndTimeAndTerminal(@Param("serviceId") String serviceId, @Param("time") Timestamp time, @Param("terminal") String terminal, @Param("command") String command, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);

    @Query(value = "SELECT c FROM ExecTrace c WHERE c.serviceId=:serviceId AND c.loginTime=:time AND c.loginTerminal=:terminal AND (report_time > :startTime and report_time < :endTime) AND (:command IS NULL OR LOWER(c.command) LIKE %:command% OR c.params LIKE %:command%) ORDER BY c.time DESC")
    List<ExecTrace> findAllByServiceAndTimeAndTerminal(@Param("serviceId") String serviceId, @Param("time") Timestamp time, @Param("terminal") String terminal, @Param("command") String command, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    @Query(value = "SELECT * FROM (SELECT MAX(c.id) as id,MAX(c.host) as host,MAX(c.user_name) as user_name,MAX(c.user_id) as user_id,string_agg(c.command,' | ') as command,string_agg(c.params,' | ') as params,MAX(c.exec_time) as exec_time,MAX(c.groupid) as groupid,c.serviceid as serviceid,MAX(c.report_time) as report_time,MAX(c.login_user) as login_user,MAX(c.login_address) as login_address,MAX(c.login_time) as login_time,MAX(c.login_terminal) as login_terminal,MAX(c.username) as username,MAX(c.e_username) as e_username,MAX(c.exec_timestamp) as exec_timestamp,MAX(c.server_hostname) as server_hostname,MAX(c.client_hostname) as client_hostname,MAX(c.ppid) as ppid,c.pgid as pgid,c.psid as psid,c.tgid as tgid FROM exec_trace_data c WHERE (c.report_time > (:startTime) and c.report_time < (:endTime)) AND c.serviceid=:serviceId GROUP BY c.serviceid, c.pgid, c.psid, c.tgid HAVING c.pgid IS NOT NULL AND (:filter IS NULL OR string_agg(c.command, ' | ') LIKE %:filter% OR string_agg(c.params, ' | ') LIKE %:filter%)) AS clist ORDER BY exec_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ExecTrace> findByServiceIdWithPipe(@Param("serviceId") String serviceId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "SELECT * FROM (SELECT MAX(c.id) as id,MAX(c.host) as host,MAX(c.user_name) as user_name,MAX(c.user_id) as user_id,string_agg(c.command,' | ') as command,string_agg(c.params,' | ') as params,MAX(c.exec_time) as exec_time,MAX(c.groupid) as groupid,c.serviceid as serviceid,MAX(c.report_time) as report_time,MAX(c.login_user) as login_user,MAX(c.login_address) as login_address,MAX(c.login_time) as login_time,MAX(c.login_terminal) as login_terminal,MAX(c.username) as username,MAX(c.e_username) as e_username,MAX(c.exec_timestamp) as exec_timestamp,MAX(c.server_hostname) as server_hostname,MAX(c.client_hostname) as client_hostname,MAX(c.ppid) as ppid,c.pgid as pgid,c.psid as psid,c.tgid as tgid FROM exec_trace_data c WHERE (c.report_time > :startTime and c.report_time < :endTime) AND c.serviceid=:serviceId GROUP BY c.serviceid, c.pgid, c.psid, c.tgid HAVING c.pgid IS NOT NULL AND (:filter IS NULL OR string_agg(c.command, ' | ') LIKE %:filter% OR string_agg(c.params, ' | ') LIKE %:filter%)) AS clist ORDER BY exec_time DESC", nativeQuery = true)
    List<ExecTrace> findAllByServiceIdWithPipe(@Param("serviceId") String serviceId, @Param("filter") String filter, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    @Query(value = "SELECT * FROM (SELECT MAX(c.id) as id,MAX(c.host) as host,MAX(c.user_name) as user_name,MAX(c.user_id) as user_id,string_agg(c.command,' | ') as command,string_agg(c.params,' | ') as params,MAX(c.exec_time) as exec_time,MAX(c.groupid) as groupid,c.serviceid as serviceid,MAX(c.report_time) as report_time,MAX(c.login_user) as login_user,MAX(c.login_address) as login_address,MAX(c.login_time) as login_time,MAX(c.login_terminal) as login_terminal,MAX(c.username) as username,MAX(c.e_username) as e_username,MAX(c.exec_timestamp) as exec_timestamp,MAX(c.server_hostname) as server_hostname,MAX(c.client_hostname) as client_hostname,MAX(c.ppid) as ppid,c.pgid as pgid,c.psid as psid,c.tgid as tgid FROM exec_trace_data c WHERE c.serviceid=:serviceId AND c.login_time=:time AND c.login_terminal=:terminal AND (c.report_time > :startTime and c.report_time < :endTime) GROUP BY c.serviceid, c.pgid, c.psid, c.tgid HAVING c.pgid IS NOT NULL AND (:command IS NULL OR string_agg(c.command, ' | ') LIKE %:command% OR string_agg(c.params, ' | ') LIKE %:command%)) AS clist ORDER BY exec_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ExecTrace> findByServiceAndTimeAndTerminalWithPipe(@Param("serviceId") String serviceId, @Param("time") Timestamp time, @Param("terminal") String terminal, @Param("command") String command, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "SELECT * FROM (SELECT MAX(c.id) as id,MAX(c.host) as host,MAX(c.user_name) as user_name,MAX(c.user_id) as user_id,string_agg(c.command,' | ') as command,string_agg(c.params,' | ') as params,MAX(c.exec_time) as exec_time,MAX(c.groupid) as groupid,c.serviceid as serviceid,MAX(c.report_time) as report_time,MAX(c.login_user) as login_user,MAX(c.login_address) as login_address,MAX(c.login_time) as login_time,MAX(c.login_terminal) as login_terminal,MAX(c.username) as username,MAX(c.e_username) as e_username,MAX(c.exec_timestamp) as exec_timestamp,MAX(c.server_hostname) as server_hostname,MAX(c.client_hostname) as client_hostname,MAX(c.ppid) as ppid,c.pgid as pgid,c.psid as psid,c.tgid as tgid FROM exec_trace_data c WHERE c.serviceid=:serviceId AND c.login_time=:time AND c.login_terminal=:terminal AND (c.report_time > :startTime and c.report_time < :endTime) GROUP BY c.serviceid, c.pgid, c.psid, c.tgid HAVING c.pgid IS NOT NULL AND (:command IS NULL OR string_agg(c.command, ' | ') LIKE %:command% OR string_agg(c.params, ' | ') LIKE %:command%)) AS clist ORDER BY exec_time DESC", nativeQuery = true)
    List<ExecTrace> findAllByServiceAndTimeAndTerminalWithPipe(@Param("serviceId") String serviceId, @Param("time") Timestamp time, @Param("terminal") String terminal, @Param("command") String command, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    class QueryFilter {

        public static Specification<ExecTrace> findByExecFilterByUsers(List<String> users) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.in(root.get("user")).value(users);
        }

        public static Specification<ExecTrace> findByExecFilterByCommand(String command) {
            return (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("command")), "%" + command + "%");
        }

        private QueryFilter() {
        }
    }
}
