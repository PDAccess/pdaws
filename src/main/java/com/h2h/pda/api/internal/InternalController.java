package com.h2h.pda.api.internal;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/internal")
public class InternalController {

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    UsersOps usersOps;

    @GetMapping(path = "service/ip/{ip_address}")
    public ResponseEntity<ServiceEntityWrapper> getServiceByIp(@PathVariable("ip_address") String ipAddress) {
        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byIp(ipAddress);
        return optionalServiceEntity.map(serviceEntity -> ResponseEntity.ok(new ServiceEntityWrapper(serviceEntity))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping(path = "service/check/{service_id}/{username}")
    public ResponseEntity<Void> getServiceByIp(@PathVariable("service_id") String serviceId, @PathVariable("username") String username) {
        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Optional<UserEntity> optionalUserEntity = usersOps.byName(username);
        if (!optionalUserEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if (!serviceOps.isMembership(serviceId, optionalUserEntity.get().getUserId())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "user/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username) {
        Optional<UserEntity> optionalUserEntity = usersOps.byName(username);
        return optionalUserEntity.map(userEntity -> ResponseEntity.ok(new UserDTO().wrap(userEntity))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }

    @GetMapping(path = "service/groups/{service_id}")
    public ResponseEntity<List<GroupsEntityWrapper>> getServiceByGroups(@PathVariable("service_id") String serviceId) {
        List<GroupsEntity> groupsEntities = serviceOps.effectiveGroups(serviceId);
        return ResponseEntity.ok(groupsEntities.stream().map(groupsEntity -> new GroupsEntityWrapper().wrap(groupsEntity)).collect(Collectors.toList()));
    }

    @GetMapping(path = "service/groups/{service_id}/{user_id}")
    public ResponseEntity<List<GroupsEntityWrapper>> getServiceByGroups(@PathVariable("service_id") String serviceId, @PathVariable("user_id") String userId) {
        List<GroupsEntity> groupsEntities = serviceOps.effectiveGroupsByServiceAndUser(serviceId, userId);
        return ResponseEntity.ok(groupsEntities.stream().map(groupsEntity -> new GroupsEntityWrapper().wrap(groupsEntity)).collect(Collectors.toList()));
    }
}
