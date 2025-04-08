package com.h2h.test.it;

import com.h2h.pda.entity.ExecFileFilter;
import com.h2h.pda.entity.ExecTraceFilter;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.*;
import com.h2h.pda.pojo.service.ServiceOs;
import com.h2h.pda.pojo.service.ServiceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Disabled
public class ExActionTests extends BaseIntegrationTests {

    @Test
    @Order(5)
    public void createVirtualExecTraceData() {
        loginWithDefaultUserToken();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "ps");
        map.add("param", "aux");
        map.add("user", "localuser");
        map.add("user_id", "1001");
        map.add("time", LocalDateTime.now().toString());

        ResponseEntity<Void> call = call("/api/v1/action/pushcommand/service1/group1", HttpMethod.POST, map, Void.class);

        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(6)
    public void testExActionRun() {
        loginWithDefaultUserToken();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("paths", "[\"admin\"]");
        map.add("service_id", "admin");
        map.add("group_id", "admin");

        ResponseEntity<Void> call = call("/api/v1/filter/file", HttpMethod.POST, map, Void.class);

        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<CommandFilter> call1 = call(String.format("/api/v1/filter/file/all/%s/%s", "admin", "admin"), HttpMethod.GET, CommandFilter.class);

        assertThat(call1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(call1.getBody().getUsers().size()).isEqualTo(1);
    }

    @Test
    @Order(7)
    public void pushAuthActionTest() {
        loginWithDefaultUserToken();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("user", "admin");
        map.add("user_id", "123");
        map.add("tty", "test");
        map.add("status", "teststatus");
        Timestamp t = new Timestamp(System.currentTimeMillis());
        map.add("time", String.valueOf(t.getTime()));
        map.add("function", "test function");
        map.add("flags", "test flags");
        map.add("service", "test service");
        map.add("rhost", "test rhost");
        map.add("ruser", "test ruser");

        ResponseEntity<Void> callVoid = call("/api/v1/action/pushauth/service1/group1", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(8)
    public void pushFileActionTest() {
        loginWithDefaultUserToken();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("path", "test path");
        map.add("file_name", "test file name");
        map.add("user", "admin");
        map.add("user_id", "123");
        Timestamp t = new Timestamp(System.currentTimeMillis());
        map.add("date", String.valueOf(t.getTime()));
        map.add("action", "test action");

        ResponseEntity<Void> callVoid = call("/api/v1/action/pushfile/service1/group1", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(9)
    public void pushAllCommand() {
        loginWithDefaultUserToken();

        List<ExecTraceWrap> execTraceWraps = new ArrayList<>();

        for (int i = 0 ; i < 3 ; i++) {
            ExecTraceWrap execTraceWrap = new ExecTraceWrap();
            execTraceWrap.setName("test name "+i);
            execTraceWrap.setTime(LocalDateTime.now().toString());
            execTraceWrap.setUser("admin");
            execTraceWrap.setParam("test param");
            execTraceWrap.setUserId("123");
            execTraceWraps.add(execTraceWrap);
        }

        ResponseEntity<Void> callVoid = call("/api/v1/action/pushallcommand/service1/group1", HttpMethod.POST, execTraceWraps, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(13)
    public void pushAllFileTest() {
        loginWithDefaultUserToken();

        List<ExecFileWrap> execFileWraps = new ArrayList<>();

        for (int i = 0 ; i < 3 ; i++) {
            ExecFileWrap execFileWrap = new ExecFileWrap();
            execFileWrap.setFileName("test file name "+i);
            Timestamp t = new Timestamp(System.currentTimeMillis());
            execFileWrap.setDate(String.valueOf(t.getTime()));
            execFileWrap.setUser("admin");
            execFileWrap.setAction("test action");
            execFileWrap.setUserId("123");
            execFileWrap.setPath("test path");
            execFileWraps.add(execFileWrap);
        }

        ResponseEntity<Void> callVoid = call("/api/v1/action/pushallfile/service1/group1", HttpMethod.POST, execFileWraps, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(14)
    public void pushAllAuthTest() {
        loginWithDefaultUserToken();

        List<ExecAuthWrap> execAuthWraps = new ArrayList<>();

        for (int i = 0 ; i < 3 ; i++) {
            ExecAuthWrap execAuthWrap = new ExecAuthWrap();
            execAuthWrap.setFlags("test name "+i);
            Timestamp t = new Timestamp(System.currentTimeMillis());
            execAuthWrap.setTime(String.valueOf(t.getTime()));
            execAuthWrap.setUser("admin");
            execAuthWrap.setFunction("test function");
            execAuthWrap.setrUser("admin");
            execAuthWrap.setService("service");
            execAuthWrap.setTty("test tty");
            execAuthWrap.setrHost("test host");
            execAuthWraps.add(execAuthWrap);
        }

        ResponseEntity<Void> callVoid = call("/api/v1/action/pushallauth/service1/group1", HttpMethod.POST, execAuthWraps, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(15)
    public void getCommandFiltersTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("regexes", "[\"admin\"]");
        map.add("service_id", serviceId);
        map.add("group_id", groupId);

        ResponseEntity<Void> call = call("/api/v1/filter/command", HttpMethod.POST, map, Void.class);
        assertThat(call.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<CommandFilter> callFileFilters = call("/api/v1/filter/command/all/"+serviceId+"/"+groupId, HttpMethod.GET, CommandFilter.class);
        assertThat(callFileFilters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callFileFilters.getBody().getRegex().size()).isEqualTo(1);
        assertThat(callFileFilters.getBody().getRegex().get(0)).isEqualTo("admin");
        assertThat(callFileFilters.getBody().getUsers().get(0)).isEqualTo("admin");

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(16)
    public void getFileFiltersTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("paths", "[\"admin\"]");
        map.add("service_id", serviceId);
        map.add("group_id", groupId);

        ResponseEntity<Void> callVoid = call("/api/v1/filter/file", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<FileFilter> callFileFilters = call("/api/v1/filter/file/all/"+serviceId+"/"+groupId, HttpMethod.GET, FileFilter.class);
        assertThat(callFileFilters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(callFileFilters.getBody().getPaths().size()).isEqualTo(1);
        assertThat(callFileFilters.getBody().getPaths().get(0)).isEqualTo("admin");
        assertThat(callFileFilters.getBody().getUsers().get(0)).isEqualTo("admin");

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(17)
    public void getFileServiceFilterTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("paths", "[\"admin\"]");
        map.add("service_id", serviceId);
        map.add("group_id", groupId);


        ResponseEntity<Void> callVoid = call("/api/v1/filter/file", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ExecFileFilter[]> callFileFilters = call("/api/v1/filter/file/service/"+serviceId, HttpMethod.GET, ExecFileFilter[].class);
        assertThat(callFileFilters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callFileFilters.getBody()).length).isEqualTo(1);
        assertThat(callFileFilters.getBody()[0].getPaths()).isEqualTo("[\"admin\"]");
        assertThat(callFileFilters.getBody()[0].getUsers()).isEqualTo("[\"admin\"]");

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(18)
    public void getFileGroupFilterTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("paths", "[\"admin\"]");
        map.add("service_id", serviceId);
        map.add("group_id", groupId);


        ResponseEntity<Void> callVoid = call("/api/v1/filter/file", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ExecFileFilter[]> callFileFilters = call("/api/v1/filter/file/group/"+groupId, HttpMethod.GET, ExecFileFilter[].class);
        assertThat(callFileFilters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callFileFilters.getBody()).length).isEqualTo(1);
        assertThat(callFileFilters.getBody()[0].getPaths()).isEqualTo("[\"admin\"]");
        assertThat(callFileFilters.getBody()[0].getUsers()).isEqualTo("[\"admin\"]");

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(19)
    public void getCommandServiceFilter() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("regexes", "[\"admin\"]");
        map.add("service_id", serviceId);
        map.add("group_id", groupId);


        ResponseEntity<Void> callVoid = call("/api/v1/filter/command", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ExecTraceFilter[]> callTraceFilters = call("/api/v1/filter/command/service/"+serviceId, HttpMethod.GET, ExecTraceFilter[].class);
        assertThat(callTraceFilters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callTraceFilters.getBody()).length).isEqualTo(1);
        assertThat(callTraceFilters.getBody()[0].getRegexes()).isEqualTo("[\"admin\"]");
        assertThat(callTraceFilters.getBody()[0].getUsers()).isEqualTo("[\"admin\"]");

        deleteGroup(groupId);
        deleteService(serviceId);
    }

    @Test
    @Order(20)
    public void getCommandGroupFilterTest() {
        loginWithDefaultUserToken();

        String groupId = createGroup();
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceTypeId(ServiceType.MYSQL);
        serviceEntity.setOperatingSystemId(ServiceOs.REDHAT);
        serviceEntity.setName("test");
        String serviceId = createService(serviceEntity, groupId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "admin");
        map.add("description", "admin");
        map.add("users", "[\"admin\"]");
        map.add("regexes", "[\"admin\"]");
        map.add("service_id", serviceId);
        map.add("group_id", groupId);


        ResponseEntity<Void> callVoid = call("/api/v1/filter/command", HttpMethod.POST, map, Void.class);
        assertThat(callVoid.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ExecTraceFilter[]> callTraceFilters = call("/api/v1/filter/command/group/"+groupId, HttpMethod.GET, ExecTraceFilter[].class);
        assertThat(callTraceFilters.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(callTraceFilters.getBody()).length).isEqualTo(1);
        assertThat(callTraceFilters.getBody()[0].getRegexes()).isEqualTo("[\"admin\"]");
        assertThat(callTraceFilters.getBody()[0].getUsers()).isEqualTo("[\"admin\"]");

        deleteGroup(groupId);
        deleteService(serviceId);
    }
}
