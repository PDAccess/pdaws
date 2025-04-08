package com.h2h.pda.api.services;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.MaintenanceEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.*;
import com.h2h.pda.repository.MaintenanceRepository;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceController.class);
    private static final String SERVICE_NOT_FOUND = "Service not found!";
    private static final String SERVICE_ID_EMPTY = "Service id cannot be empty!";
    private static final String GROUP_ID_EMPTY = "Group id cannot be empty!";

    //@Autowired
    //ServiceRepository serviceRepository;

    @Autowired
    MaintenanceRepository maintenanceRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    GroupOps groupOps;

    @GetMapping("unique/{uniqueId}")
    public ResponseEntity<List<MaintenanceSingleParams>> getMaintenance(@PathVariable String uniqueId) {
        Optional<MaintenanceEntity> optionalMaintenanceEntity = maintenanceRepository.findByUniqueId(uniqueId);
        if (!optionalMaintenanceEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        MaintenanceEntity maintenanceEntity = optionalMaintenanceEntity.get();
        List<MaintenanceEntity> maintenanceEntities = maintenanceRepository.findAllByGroupIdAndStartDateAnAndEndDate(maintenanceEntity.getGroupsEntity().getGroupId(), maintenanceEntity.getStartDate(), maintenanceEntity.getEndDate());
        List<MaintenanceSingleParams> maintenanceSingleParamsList = new ArrayList<>();
        for (MaintenanceEntity maintenance:maintenanceEntities) {
            MaintenanceSingleParams maintenanceSingleParams = new MaintenanceSingleParams();
            maintenanceSingleParams.setUniqueId(maintenance.getUniqueId());
            maintenanceSingleParams.setStartDate(maintenance.getStartDate());
            maintenanceSingleParams.setEndDate(maintenance.getEndDate());
            maintenanceSingleParams.setGroupId(maintenance.getGroupsEntity().getGroupId());
            maintenanceSingleParams.setUserId(maintenance.getUserId());
            maintenanceSingleParamsList.add(maintenanceSingleParams);
        }

        return ResponseEntity.ok(maintenanceSingleParamsList);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<MaintenanceSingleParams>> listMaintenances(@PathVariable String groupId) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (!optionalGroupsEntity.isPresent()) {
            log.error(SERVICE_NOT_FOUND);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<MaintenanceSingleParams> maintenanceSingleParamsList = new ArrayList<>();
        MaintenanceSingleParams maintenanceSingleParams;
        for (MaintenanceEntity maintenanceEntity1 : maintenanceRepository.findAllByGroupId(groupId)) {
            Optional<UserEntity> byName = usersOps.byId(maintenanceEntity1.getUserId());
            if (byName.isPresent()) {
                maintenanceSingleParams = new MaintenanceSingleParams(byName.get().getUsername(), maintenanceEntity1.getUserId());
                maintenanceSingleParams.setUniqueId(maintenanceEntity1.getUniqueId());
                maintenanceSingleParams.setStartDate(maintenanceEntity1.getStartDate());
                maintenanceSingleParams.setEndDate(maintenanceEntity1.getEndDate());
                maintenanceSingleParams.setGroupId(groupId);
                maintenanceSingleParamsList.add(maintenanceSingleParams);
            }
        }
        return new ResponseEntity<>(maintenanceSingleParamsList, HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<List<MaintenanceSingleParams>> listUniqueMaintenance(@RequestBody DeleteMaintenanceParams deleteMaintenanceParams) {
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(deleteMaintenanceParams.getGroupId());
        if (!optionalGroupsEntity.isPresent()) {
            log.error(SERVICE_NOT_FOUND);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<MaintenanceSingleParams> maintenanceSingleParamsList = new ArrayList<>();
        MaintenanceSingleParams maintenanceSingleParams;
        Date dateStart = deleteMaintenanceParams.getDateRange().getStart();
        Date dateEnd = deleteMaintenanceParams.getDateRange().getEnd();
        Timestamp timestampStart = new Timestamp(dateStart.getTime());
        Timestamp timestampEnd = new Timestamp(dateEnd.getTime());
        for (MaintenanceEntity maintenanceEntity1 : maintenanceRepository.findAllByGroupIdAndStartDateAnAndEndDate(deleteMaintenanceParams.getGroupId(), timestampStart, timestampEnd)) {
            Optional<UserEntity> byName = usersOps.byId(maintenanceEntity1.getUserId());

            if (byName.isPresent()) {
                maintenanceSingleParams = new MaintenanceSingleParams(byName.get().getUsername(), maintenanceEntity1.getUserId());
                maintenanceSingleParams.setGroupId(deleteMaintenanceParams.getGroupId());
                maintenanceSingleParams.setStartDate(maintenanceEntity1.getStartDate());
                maintenanceSingleParams.setEndDate(maintenanceEntity1.getEndDate());
                maintenanceSingleParamsList.add(maintenanceSingleParams);
            }
        }
        return new ResponseEntity<>(maintenanceSingleParamsList, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<Void> maintenanceService(@RequestBody MaintenanceParams maintenanceParams) {
        DateRange dateRange = maintenanceParams.getDateRange();
        if (checkNullDateRange(dateRange)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (checkMaintenanceParams(maintenanceParams.getGroupId(), maintenanceParams.getUserIds()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Date dateStart = dateRange.getStart();
        Date dateEnd = dateRange.getEnd();
        Timestamp timestampStart = new Timestamp(dateStart.getTime());
        Timestamp timestampEnd = new Timestamp(dateEnd.getTime());
        log.info("maintenanceParams: {}", maintenanceParams);
        maintenanceParams.getUserIds()
                .forEach(f -> {
                            String uniqueId = UUID.randomUUID().toString();
                            log.info("maintenance. f: {}", f);
                            MaintenanceEntity maintenanceEntity = new MaintenanceEntity(uniqueId, maintenanceParams.getGroupId(), f);
                            maintenanceEntity.setStartDate(timestampStart);
                            maintenanceEntity.setEndDate(timestampEnd);
                            maintenanceRepository.save(maintenanceEntity);
                        }
                );
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<Void> maintenanceServiceEdit(@RequestBody MaintenanceEditParams maintenanceEditParams) {

        DateRange oldDateRange = maintenanceEditParams.getOldDateRange();
        DateRange newDateRange = maintenanceEditParams.getNewDateRange();
        if (checkNullDateRange(oldDateRange)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (checkNullDateRange(newDateRange)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (checkMaintenanceParams(maintenanceEditParams.getGroupId(), maintenanceEditParams.getUserIds()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Date oldDateStart = oldDateRange.getStart();
        Date oldDateEnd = oldDateRange.getEnd();
        Date newDateStart = newDateRange.getStart();
        Date newDateEnd = newDateRange.getEnd();
        Timestamp timestampOldStart = new Timestamp(oldDateStart.getTime());
        Timestamp timestampOldEnd = new Timestamp(oldDateEnd.getTime());
        Timestamp timestampNewStart = new Timestamp(newDateStart.getTime());
        Timestamp timestampNewEnd = new Timestamp(newDateEnd.getTime());
        if (deleteMaintenance(timestampOldStart, timestampOldEnd, maintenanceEditParams.getGroupId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        maintenanceEditParams.getUserIds()
                .forEach(f -> {
                    String uniqueId = UUID.randomUUID().toString();
                    MaintenanceEntity maintenanceEntity = new MaintenanceEntity(uniqueId, maintenanceEditParams.getGroupId(), f);
                    maintenanceEntity.setStartDate(timestampNewStart);
                    maintenanceEntity.setEndDate(timestampNewEnd);
                    maintenanceRepository.save(maintenanceEntity);
                });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean checkMaintenanceParams(String groupId, List<String> userIds) {
        if (groupId == null || groupId.equals("")) {
            log.error(GROUP_ID_EMPTY);
            return true;
        }
        if (userIds == null || userIds.isEmpty()) {
            log.error("users cannot be null");
            return true;
        }
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (!optionalGroupsEntity.isPresent()) {
            return true;
        }
        for (String userId : userIds) {
            Optional<UserEntity> userEntity = usersOps.byId(userId);
            if (!userEntity.isPresent()) {
                log.error("User not found {}", userId);
                return true;
            }
        }
        return false;
    }

    public boolean checkNullDateRange(DateRange newDateRange) {
        if (newDateRange == null) {
            log.error("Daterange cannot be null");
            return true;
        }
        if (newDateRange.getStart() == null || newDateRange.getEnd() == null) {
            log.error("start date and end date cannot be null");
            return true;
        }
        if (newDateRange.getStart().before(new Timestamp(System.currentTimeMillis()))) {
            log.error("start date cannot be before at now");
            return true;
        }
        if (newDateRange.getStart().after(newDateRange.getEnd())) {
            log.error("start date cannot be after end date");
            return true;
        }
        return false;
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<Void> deleteUserFromMaintenance(@RequestBody MaintenanceSinglestParams maintenanceSingleParams) {
        if (maintenanceSingleParams.getGroupId() == null || maintenanceSingleParams.getGroupId().equals("")) {
            log.error(GROUP_ID_EMPTY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (maintenanceSingleParams.getUserId() == null || maintenanceSingleParams.getUserId().equals("")) {
            log.error(GROUP_ID_EMPTY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (maintenanceSingleParams.getDateRange() == null || maintenanceSingleParams.getDateRange().getStart() == null || maintenanceSingleParams.getDateRange().getEnd() == null) {
            log.error("Daterange, startdate, enddate cannot be empty!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Date dateStart = maintenanceSingleParams.getDateRange().getStart();
        Date dateEnd = maintenanceSingleParams.getDateRange().getEnd();
        Timestamp timestampStart = new Timestamp(dateStart.getTime());
        Timestamp timestampEnd = new Timestamp(dateEnd.getTime());
        List<MaintenanceEntity> maintenanceEntities =  maintenanceRepository.findAllByGroupIdAndUserIdAndStartDateAnAndEndDate(maintenanceSingleParams.getGroupId(), maintenanceSingleParams.getUserId(), timestampStart, timestampEnd);
        if (maintenanceEntities.isEmpty()) {
            log.error("Maintenance not found!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        for (MaintenanceEntity maintenanceEntity1 : maintenanceEntities) {
            maintenanceRepository.delete(maintenanceEntity1);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteMaintenance(@RequestBody DeleteMaintenanceParams deleteMaintenanceParams) {
        if (deleteMaintenanceParams.getGroupId() == null || deleteMaintenanceParams.getGroupId().equals("")) {
            log.error(GROUP_ID_EMPTY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (deleteMaintenanceParams.getDateRange() == null || deleteMaintenanceParams.getDateRange().getStart() == null || deleteMaintenanceParams.getDateRange().getEnd() == null) {
            log.error("Daterange, startdate, enddate cannot be empty!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Date dateStart = deleteMaintenanceParams.getDateRange().getStart();
        Date dateEnd = deleteMaintenanceParams.getDateRange().getEnd();
        Timestamp timestampStart = new Timestamp(dateStart.getTime());
        Timestamp timestampEnd = new Timestamp(dateEnd.getTime());
        if (deleteMaintenance(timestampStart, timestampEnd, deleteMaintenanceParams.getGroupId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean deleteMaintenance(Timestamp timestampStart, Timestamp timestampEnd, String groupId) {
        List<MaintenanceEntity> maintenanceEntities = maintenanceRepository.findAllByGroupIdAndStartDateAnAndEndDate(groupId, timestampStart, timestampEnd);
        if (maintenanceEntities.isEmpty()) {
            log.error("Maintenance not found!");
            return true;
        }

        for (MaintenanceEntity maintenanceEntity1 : maintenanceEntities) {
            maintenanceRepository.delete(maintenanceEntity1);
        }
        return false;
    }

    @PostMapping("/date/{groupId}/{userId}")
    public ResponseEntity<Boolean> getDateOfService(@PathVariable String groupId, @PathVariable String userId) {
        Timestamp startDate = null;
        Timestamp endDate = null;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<MaintenanceEntity> maintenanceEntityJustService = maintenanceRepository.findAllByGroupId(groupId);

        if (maintenanceEntityJustService.isEmpty())
            return new ResponseEntity<>(true, HttpStatus.OK);


        boolean available = false;
        boolean pastFuture = true;

        for (MaintenanceEntity maintenanceEntity : maintenanceEntityJustService) {
            startDate = maintenanceEntity.getStartDate();
            endDate = maintenanceEntity.getEndDate();
            try {
                if (now.after(startDate) && now.before(endDate)) {
                    pastFuture = false;
                    if (maintenanceEntity.getUserId().equals(userId))
                        available = true;
                }
            } catch (NullPointerException e) {
                available = false;
            }
        }
        if (pastFuture)
            return new ResponseEntity<>(true, HttpStatus.OK);

        if (available)
            return new ResponseEntity<>(true, HttpStatus.OK);
        else
            return new ResponseEntity<>(false, HttpStatus.OK);
    }
}