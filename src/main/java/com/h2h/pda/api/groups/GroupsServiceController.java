package com.h2h.pda.api.groups;

import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.SessionEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.group.GroupServices;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/group/service")
public class GroupsServiceController {
    private Logger log = LoggerFactory.getLogger(GroupsServiceController.class);

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    GroupOps groupOps;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    ActionPdaService actionPdaService;

    @PostMapping(path = "/delete/{groupId}")
    public ResponseEntity<Void> removeGroupServices(@PathVariable String groupId, @RequestBody List<String> serviceList) {
        groupOps.removeServicesFrom(groupId, serviceList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{groupId}")
    public ResponseEntity<Integer> addGroupServices(@PathVariable String groupId, @RequestBody List<String> serviceList) {
        Integer a = groupOps.addServicesTo(groupId, serviceList);
        actionPdaService.saveAction("Group services updated");
        return ResponseEntity.ok(a);
    }

    @PostMapping(path = "/{groupId}/{serviceId}")
    public ResponseEntity<Integer> addService(@PathVariable String groupId, @PathVariable String serviceId) {
        return addGroupServices(groupId, Collections.singletonList(serviceId));
    }

    @GetMapping(path = "/{groupId}")
    public ResponseEntity<List<ServiceEntityWrapper>> getGroupServices(@PathVariable String groupId) {
        List<ServiceEntity> groupServiceEntities = groupOps.effectiveServices(groupId);

        return ResponseEntity.ok(groupServiceEntities.stream()
                .map(s -> new ServiceEntityWrapper(s)).collect(Collectors.toList()));
    }


    @PostMapping(path = "/{groupId}")
    public ResponseEntity<GroupServices> getGroupServices(@PathVariable String groupId, @RequestBody Pagination pagination) {
        PageRequest request = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage());
        List<ServiceEntityWrapper> services = new ArrayList<>();
        List<ServiceEntity> groupServiceEntityList = groupOps.effectiveServices(groupId);
        int count = 0;

        for (ServiceEntity service : groupServiceEntityList) {
            if (pagination.getFilter() != null && !pagination.getFilter().equals("")) {
                if (service.getName().toLowerCase().contains(pagination.getFilter().toLowerCase())) {
                    servicesListCreate(services, service);
                }
            } else {
                servicesListCreate(services, service);
            }
            count++;
        }

        List<ServiceEntity> entities = groupOps.effectiveServices(groupId);

        return ResponseEntity.ok(new GroupServices(services, count, entities.size()));
    }

    public void servicesListCreate(List<ServiceEntityWrapper> services, ServiceEntity service) {
        ServiceEntityWrapper serviceEntityWrapper = new ServiceEntityWrapper(service);
        Optional<UserEntity> userEntity = usersOps.byId(service.getWhoCreate());

        userEntity.ifPresent(value -> serviceEntityWrapper.setServiceUser(value.getUsername()));

        serviceEntityWrapper.setSessioncount(sessionRepository.findBySessionCount(service.getInventoryId()));

        List<SessionEntity> sessionlist;
        sessionlist = sessionRepository.findByLastSession(service.getInventoryId());
        if (!(sessionlist.isEmpty())) {
            SessionEntity session = sessionlist.get(0);
            serviceEntityWrapper.setLastSessionStart(session.getStartTime());
            serviceEntityWrapper.setLastSessionEnd(session.getEndTime());
        }
        services.add(serviceEntityWrapper);
    }

    @GetMapping(path = "/{groupid}/{userid}")
    public ResponseEntity<List<ServiceEntityWrapper>> getGroupServicesByUser(@PathVariable String groupid, @PathVariable String userid) {
        Iterable<GroupUserEntity> groupUserEntities = groupOps.effectiveMembers(groupid);
        boolean isMember = false;
        for (GroupUserEntity entity : groupUserEntities) {
            if (userid.equals(entity.getId().getUserId()) && entity.getMembershipRole() == GroupRole.ADMIN) {
                isMember = true;
                break;
            }
        }

        if (isMember) {
            List<ServiceEntity> serviceEntities = groupOps.effectiveServices(groupid);

            return ResponseEntity.ok(serviceEntities
                    .stream().map(s -> new ServiceEntityWrapper(s)).collect(Collectors.toList()));
        } else
            return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{groupId}/{serviceId}")
    public ResponseEntity<Void> deleteGroupService(@PathVariable String groupId, @PathVariable String serviceId) {
        groupOps.removeServicesFrom(groupId, Collections.singletonList(serviceId));
        actionPdaService.saveAction("Group deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}