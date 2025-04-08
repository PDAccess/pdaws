package com.h2h.test.it;

import com.h2h.pda.entity.ExecShellSessionEntity;
import com.h2h.pda.entity.ExecShellTraceDataEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.ExecShellSessionResponse;
import com.h2h.pda.pojo.ExecShellTraceResponse;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.repository.ExecShellSessionRepository;
import com.h2h.pda.repository.ExecShellTraceDataRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExShellActionTest extends BaseIntegrationTests {

    @Autowired
    ExecShellSessionRepository execShellSessionRepository;

    @Autowired
    ExecShellTraceDataRepository execShellTraceDataRepository;

    @Test
    @Order(600)
    public void getSessionListTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));

        ServiceEntity entity = new ServiceEntity();
        entity.setName(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        String serviceId = createService(entity, groupId);

        String sessionId = loginSession(serviceId).getSessionId();
        ResponseEntity<ExecShellSessionResponse> getSessionsResponse = call("/api/v1/shell/action/sessions/" + serviceId, HttpMethod.POST, new Pagination(0, 10), ExecShellSessionResponse.class);
        assertThat(getSessionsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getSessionsResponse.getBody()).isNotNull();

        ExecShellSessionEntity execShellSessionEntity = getSessionsResponse.getBody().getLogs().stream().filter(session -> session.getSessionId().equals(sessionId)).findFirst().get();
        assertThat(execShellSessionEntity).isNotNull();
        assertThat(execShellSessionEntity.getEndTime()).isNull();

        ResponseEntity<ExecShellSessionResponse> getSessionsResponseByGroupId = call("/api/v1/shell/action/sessions/group/" + groupId, HttpMethod.POST, new Pagination(0, 10), ExecShellSessionResponse.class);
        assertThat(getSessionsResponseByGroupId.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getSessionsResponseByGroupId.getBody()).isNotNull();

        execShellSessionEntity = getSessionsResponse.getBody().getLogs().stream().filter(session -> session.getSessionId().equals(sessionId)).findFirst().get();
        assertThat(execShellSessionEntity).isNotNull();
        assertThat(execShellSessionEntity.getEndTime()).isNull();

        logoutSession(sessionId);

        getSessionsResponse = call("/api/v1/shell/action/sessions/" + serviceId, HttpMethod.POST, new Pagination(0, 10), ExecShellSessionResponse.class);
        assertThat(getSessionsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getSessionsResponse.getBody()).isNotNull();

        execShellSessionEntity = getSessionsResponse.getBody().getLogs().stream().filter(session -> session.getSessionId().equals(sessionId)).findFirst().get();
        assertThat(execShellSessionEntity).isNotNull();
        assertThat(execShellSessionEntity.getEndTime()).isNotNull();

        getSessionsResponseByGroupId = call("/api/v1/shell/action/sessions/group/" + groupId, HttpMethod.POST, new Pagination(0, 10), ExecShellSessionResponse.class);
        assertThat(getSessionsResponseByGroupId.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getSessionsResponseByGroupId.getBody()).isNotNull();

        execShellSessionEntity = getSessionsResponse.getBody().getLogs().stream().filter(session -> session.getSessionId().equals(sessionId)).findFirst().get();
        assertThat(execShellSessionEntity).isNotNull();
        assertThat(execShellSessionEntity.getEndTime()).isNotNull();

        execShellTraceDataRepository.deleteAll();
        execShellSessionRepository.deleteAll();
    }

    @Test
    @Order(601)
    public void getSessionActivitiesTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));

        ServiceEntity entity = new ServiceEntity();
        entity.setName(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));
        entity.setOperatingSystemId(ServiceOs.UBUNTU);
        entity.setServiceTypeId(ServiceType.SSH);
        String serviceId = createService(entity, groupId);

        ExecShellSessionEntity execShellSessionEntity = loginSession(serviceId);
        String sessionExternalId = execShellSessionEntity.getSessionId();
        long sessionId = execShellSessionEntity.getId();

        ResponseEntity<ExecShellTraceResponse> getSessionActivityResponse = call("/api/v1/shell/action/session/" + sessionId, HttpMethod.POST, new Pagination(0, 10), ExecShellTraceResponse.class);
        assertThat(getSessionActivityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getSessionActivityResponse.getBody()).getLogs().size()).isZero();

        for(int i = 0 ; i < 10 ; i++) {
            addActivity(sessionExternalId, serviceId);
        }

        getSessionActivityResponse = call("/api/v1/shell/action/session/" + sessionId, HttpMethod.POST, new Pagination(0, 10), ExecShellTraceResponse.class);
        assertThat(getSessionActivityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getSessionActivityResponse.getBody()).getLogs().size()).isGreaterThan(0);

        execShellTraceDataRepository.deleteAll();
        execShellSessionRepository.deleteAll();
    }

    ExecShellSessionEntity loginSession(String serviceId) {
        ExecShellSessionEntity execShellSessionEntity = new ExecShellSessionEntity();
        execShellSessionEntity.setClientIp("127.0.0.1");
        execShellSessionEntity.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        execShellSessionEntity.seteUserId(1000);
        execShellSessionEntity.seteUsername("root");
        execShellSessionEntity.setSessionId(UUID.randomUUID().toString());
        execShellSessionEntity.setUserId(123);
        execShellSessionEntity.setUsername("test");
        execShellSessionEntity.setServiceId(serviceId);
        execShellSessionEntity = execShellSessionRepository.save(execShellSessionEntity);
        return execShellSessionEntity;
    }

    String logoutSession(String sessionId) {
        Optional<ExecShellSessionEntity> optionalExecShellSessionEntity = execShellSessionRepository.findBySessionId(sessionId);
        if (optionalExecShellSessionEntity.isPresent()) {
            ExecShellSessionEntity execShellSessionEntity = optionalExecShellSessionEntity.get();
            execShellSessionEntity.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
            execShellSessionEntity = execShellSessionRepository.save(execShellSessionEntity);
            return execShellSessionEntity.getSessionId();
        }
        return null;
    }

    long addActivity(String sessionId, String serviceId) {
        ExecShellTraceDataEntity execShellTraceDataEntity = new ExecShellTraceDataEntity();
        execShellTraceDataEntity.setExecCommand(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));
        execShellTraceDataEntity.setExecTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
        execShellTraceDataEntity.setServiceId(serviceId);
        execShellTraceDataEntity.setSessionId(sessionId);
        execShellTraceDataEntity.setStdOut(RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH));
        execShellTraceDataEntity.setReportTime(Timestamp.valueOf(LocalDateTime.now()));
        execShellTraceDataEntity = execShellTraceDataRepository.save(execShellTraceDataEntity);
        return execShellTraceDataEntity.getId();
    }

}
