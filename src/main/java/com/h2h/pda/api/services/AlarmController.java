package com.h2h.pda.api.services;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.AlarmWrapper;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.alarm.AlarmHistoryResponse;
import com.h2h.pda.repository.AlarmRepository;
import com.h2h.pda.service.api.AlarmService;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/alarm")
public class AlarmController {

    @Autowired
    @Deprecated
    AlarmRepository alarmRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    AlarmService alarmService;

    @GetMapping(path = "/{alarmId}")
    public ResponseEntity<AlarmWrapper> getAlarm(@PathVariable Integer alarmId) {
        Optional<AlarmEntity> optionalAlarmEntity = alarmService.byId(alarmId);
        if (optionalAlarmEntity.isPresent()) {
            AlarmWrapper alarmData = new AlarmWrapper(optionalAlarmEntity.get());
            return new ResponseEntity<>(alarmData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(path = "/services/{serviceId}")
    public ResponseEntity<List<AlarmWrapper>> getServiceAlarms(@PathVariable String serviceId) {
        List<AlarmEntity> alarmEntities = alarmService.byServiceId(serviceId);
        return new ResponseEntity<>(alarmEntities.stream().map(a -> new AlarmWrapper(a)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(path = "/groups/{groupId}")
    public ResponseEntity<List<AlarmWrapper>> getGroupAlarms(@PathVariable String groupId) {
        List<AlarmEntity> alarmEntities = alarmService.byGroupId(groupId);
        return new ResponseEntity<>(alarmEntities.stream().map(a -> new AlarmWrapper(a)).collect(Collectors.toList()), HttpStatus.OK);
    }

    public Boolean checkAlarmData(AlarmWrapper alarmData) {
        return (alarmData.getUsers() == null || alarmData.getUsers().isEmpty()) ||
                (alarmData.getRegex() == null || alarmData.getRegex().isEmpty()) ||
                (alarmData.getName() == null || alarmData.getName().equals("")) ||
                (alarmData.getMessage() == null || alarmData.getMessage().length() < 20);
    }

    @PostMapping(path = "/groups/{groupId}")
    public ResponseEntity<Void> createGroupAlarm(@PathVariable String groupId, @RequestBody AlarmWrapper alarmData) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (!optionalGroupsEntity.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Boolean check = this.checkAlarmData(alarmData);
        if (Boolean.TRUE.equals(check))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setName(alarmData.getName());
        alarmEntity.setDescription(alarmData.getDescription());
        alarmEntity.setMessage(alarmData.getMessage());
        alarmEntity.setGroupsEntity(optionalGroupsEntity.get());
        alarmEntity.setActive(true);
        alarmEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return getVoidResponseEntity(alarmData, alarmEntity);
    }

    @PutMapping(path = "/{alarmId}")
    public ResponseEntity<Void> updateAlarm(@PathVariable Integer alarmId, @RequestBody AlarmWrapper alarmData) {
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findById(alarmId);
        if (!optionalAlarmEntity.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Boolean check = this.checkAlarmData(alarmData);
        if (Boolean.TRUE.equals(check))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        AlarmEntity alarmEntity = optionalAlarmEntity.get();
        alarmEntity.setName(alarmData.getName());
        alarmEntity.setDescription(alarmData.getDescription());
        alarmEntity.setMessage(alarmData.getMessage());
        return getVoidResponseEntity(alarmData, alarmEntity);
    }

    public ResponseEntity<Void> getVoidResponseEntity(@RequestBody AlarmWrapper alarmData, AlarmEntity alarmEntity) {
        alarmEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Set<UserEntity> userEntities = new HashSet<>();
        for (String userId : alarmData.getUsers()) {
            Optional<UserEntity> optionalUserEntity = usersOps.byId(userId);
            optionalUserEntity.ifPresent(userEntities::add);
        }

        return getVoidResponseEntity(alarmData, alarmEntity, userEntities);
    }

    public ResponseEntity<Void> getVoidResponseEntity(@RequestBody AlarmWrapper alarmData, AlarmEntity alarmEntity, Set<UserEntity> userEntities) {
        Set<String> regexEntities = new HashSet<>();
        for (String regex : alarmData.getRegex()) {
            regexEntities.add(regex);
        }

        alarmEntity.setUserEntities(userEntities);
        alarmEntity.setAlarmRegexEntities(regexEntities);
        alarmRepository.save(alarmEntity);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(path = "/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Integer alarmId) {
        alarmRepository.deleteById(alarmId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/activate/{alarmId}")
    public ResponseEntity<Void> activateAlarm(@PathVariable Integer alarmId) {
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findById(alarmId);
        if (optionalAlarmEntity.isPresent()) {
            AlarmEntity alarmEntity = optionalAlarmEntity.get();
            alarmEntity.setActive(true);
            alarmRepository.save(alarmEntity);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/deactivate/{alarmId}")
    public ResponseEntity<Void> deactivateAlarm(@PathVariable Integer alarmId) {
        Optional<AlarmEntity> optionalAlarmEntity = alarmRepository.findById(alarmId);
        if (optionalAlarmEntity.isPresent()) {
            AlarmEntity alarmEntity = optionalAlarmEntity.get();
            alarmEntity.setActive(false);
            alarmRepository.save(alarmEntity);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> getAlarmCount() {
        return ResponseEntity.ok(alarmService.getAlarmCount());
    }

    @PostMapping(path = "/histories")
    public ResponseEntity<List<AlarmHistoryResponse>> getAlarmHistories(@RequestBody Pagination pagination) {
        List<AlarmHistoryEntity> alarmHistoryEntities = alarmService.getAlarmHistories(pagination);
        List<AlarmHistoryResponse> alarmHistoryResponses = new ArrayList<>();
        for (AlarmHistoryEntity alarmHistoryEntity : alarmHistoryEntities) {
            AlarmHistoryResponse alarmHistoryResponse = new AlarmHistoryResponse().wrap(alarmHistoryEntity);
            Optional<ServiceEntity> optionalServiceEntity = serviceOps.byId(alarmHistoryEntity.getServiceId());
            optionalServiceEntity.ifPresent(serviceEntity -> alarmHistoryResponse.setServiceName(serviceEntity.getName()));
            alarmHistoryResponses.add(alarmHistoryResponse);
        }
        return ResponseEntity.ok(alarmHistoryResponses);
    }
}