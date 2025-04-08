package com.h2h.pda.api.services;

import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.repository.FavoriteServiceRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fav")
public class FavoriteServiceController {
    private final Logger log = LoggerFactory.getLogger(FavoriteServiceController.class);

    @Autowired
    FavoriteServiceRepository repo;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @PutMapping(path = "/mark/{serviceId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String serviceId) {
        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity userEntity = usersOps.securedUser();
        serviceOps.followUser(userEntity.getUserId(), Collections.singletonList(serviceId));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/mark/{serviceId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String serviceId) {
        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        UserEntity userEntity = usersOps.securedUser();
        serviceOps.unfollowUser(userEntity.getUserId(), Collections.singletonList(serviceId));

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/check/{userId}/{serviceId}")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable String userId, @PathVariable String serviceId) {
        Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(serviceId);
        if (!optionalServiceEntity.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        Optional<UserEntity> user = usersOps.byId(userId);
        if (!user.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        List<ServiceEntity> wrappers = serviceOps.userFollowList(userId);

        return wrappers != null && wrappers.size() > 0 && wrappers.contains(optionalServiceEntity.get()) ? ResponseEntity.ok().build() : ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/service")
    public ResponseEntity<List<ServiceEntityWrapper>> serviceFavorites() {
        UserEntity userEntity = usersOps.securedUser();
        List<ServiceEntity> wrappers = serviceOps.userFollowList(userEntity.getUserId());

        return new ResponseEntity<>(wrappers.stream().map(s -> new ServiceEntityWrapper().wrap(s)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(path = "/group")
    public ResponseEntity<List<ServiceEntityWrapper>> groupFavorites() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/vault")
    public ResponseEntity<List<ServiceEntityWrapper>> vaultFavorites() {
        return ResponseEntity.noContent().build();
    }
}
