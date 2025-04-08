package com.h2h.pda.api.notification;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.notification.NotificationType;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.NotifyService;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    NotifyService notifyService;

    @PostMapping(path = "/check/group/{groupId}")
    public ResponseEntity<Boolean> checkGroupNotification(@PathVariable String groupId) {
        UserEntity user = usersOps.securedUser();
        Optional<GroupsEntity> entity = groupOps.byId(groupId);

        if (entity.isPresent()) {
            boolean notifyStatus = notifyService.hasActiveNotification(user.getUserId(), groupId, NotificationType.GROUP);
            return ResponseEntity.ok(notifyStatus);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/check/service/{serviceId}")
    public ResponseEntity<String> checkServiceNotification(@PathVariable String serviceId) {

        UserEntity user = usersOps.securedUser();
        Optional<ServiceEntity> entity = serviceOps.byId(serviceId);

        if (entity.isPresent()) {
            if (notifyService.hasActiveNotification(user.getUserId(), serviceId, NotificationType.SERVICE))
                return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/group/user/{groupId}")
    public ResponseEntity<Void> enableGroupNotification(@PathVariable String groupId) {
        UserEntity user = usersOps.securedUser();
        Optional<GroupsEntity> entity = groupOps.byId(groupId);

        if (entity.isPresent()) {
            notifyService.addNotification(user.getUserId(), groupId, NotificationType.GROUP, true);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/group/user/{groupId}")
    public ResponseEntity<String> disableGroupNotification(@PathVariable String groupId) {
        UserEntity user = usersOps.securedUser();
        Optional<GroupsEntity> entity = groupOps.byId(groupId);

        if (entity.isPresent()) {
            notifyService.addNotification(user.getUserId(), groupId, NotificationType.GROUP, false);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();

    }

    @PutMapping(path = "/service/user/{serviceId}")
    public ResponseEntity<Void> enableServiceNotification(@PathVariable String serviceId) {
        UserEntity user = usersOps.securedUser();
        Optional<ServiceEntity> entity = serviceOps.byId(serviceId);

        if (entity.isPresent()) {
            notifyService.addNotification(user.getUserId(), serviceId, NotificationType.SERVICE, true);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/service/user/{serviceId}")
    public ResponseEntity<String> disableServiceNotification(@PathVariable String serviceId) {
        UserEntity user = usersOps.securedUser();
        Optional<ServiceEntity> entity = serviceOps.byId(serviceId);

        if (entity.isPresent()) {
            notifyService.addNotification(user.getUserId(), serviceId, NotificationType.SERVICE, false);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/user/{userId}")
    public ResponseEntity<Void> enableUserNotification(@PathVariable String userId) {
        UserEntity user = usersOps.securedUser();
        if (!user.getUserId().equalsIgnoreCase(userId))
            return ResponseEntity.noContent().build();

        notifyService.addNotification(user.getUserId(), userId, NotificationType.USER, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/user/{userId}")
    public ResponseEntity<String> disableUserNotification(@PathVariable String userId) {
        UserEntity user = usersOps.securedUser();
        if (!user.getUserId().equalsIgnoreCase(userId))
            return ResponseEntity.noContent().build();

        notifyService.addNotification(user.getUserId(), userId, NotificationType.USER, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/check/user")
    public ResponseEntity<String> checkUserNotification() {
        UserEntity user = usersOps.securedUser();

        if (notifyService.hasActiveNotification(user.getUserId(), user.getUserId(), NotificationType.USER))
            return ResponseEntity.ok().build();

        return ResponseEntity.noContent().build();
    }
}
