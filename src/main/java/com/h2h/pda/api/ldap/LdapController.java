package com.h2h.pda.api.ldap;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.ldap.*;
import com.h2h.pda.pojo.system.SystemSettingTags;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NamingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ldap")
public class LdapController {
    Logger log = LoggerFactory.getLogger(LdapController.class);
    @Autowired
    LdapService ldapService;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    ActionPdaService actionPdaService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    GroupOps groupOps;

    @GetMapping(path = "group")
    public ResponseEntity<List<LdapGroup>> getLdapGroups() {
        List<LdapGroup> ldapGroups;
        try {
            ldapGroups = ldapService.getLdapGroups();
        } catch (Exception exception) {
            log.error("error in group configuration", exception);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(ldapGroups);
    }

    @PostMapping(path = "check")
    public ResponseEntity<Void> checkLdapCredentials(@RequestBody LdapCredential credential) {
        try {
            if (ldapService.checkLdapCredentials(credential)) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (NamingException ne) {
            log.error("Error while checking connection", ne);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = "group")
    public ResponseEntity<String> saveLdapGroup(@RequestBody LdapGroup ldapGroup) {
        String groupId = "";
        try {
            groupId = ldapService.saveLdapGroup(ldapGroup);
            if (systemSettings.checkTagValue(SystemSettingTags.ADD_ALL_ADMIN_TO_GROUPS, "true")) {
                List<String> userIds;
                if (systemSettings.checkTagValue(SystemSettingTags.ADD_EXTERNAL_ADMIN_TO_GROUPS, "true")) {
                    userIds = usersOps.findUsersByExternalAndRole(true, UserRole.ADMIN).stream().map(UserEntity::getUserId).collect(Collectors.toList());
                } else {
                    userIds = usersOps.findUsersByRole(UserRole.ADMIN).stream().map(UserEntity::getUserId).collect(Collectors.toList());
                }
                groupOps.addUsersTo(groupId, userIds, GroupRole.ADMIN);
            }
        } catch (Exception exception) {
            log.error("saveLdapGroup error", exception);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        actionPdaService.saveAction(String.format("The LDAP group named %s has been created!", ldapGroup.getName()));
        return ResponseEntity.ok(groupId);
    }

    @PostMapping(path = "entry")
    public ResponseEntity<List<LdapEntry>> getEntries(@RequestParam("object_class") String objectClass,
                                                      @RequestParam("filter") String filter) {
        List<LdapEntry> ldapEntries;
        try {
            ldapEntries = ldapService.getLdapEntries(objectClass, filter);
        } catch (Exception exception) {
            log.error("getEntries error", exception);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ldapEntries == null ? ResponseEntity.noContent().build() : new ResponseEntity<>(ldapEntries, HttpStatus.OK);
    }

    @PostMapping(path = "create")
    public ResponseEntity<Void> saveEntries(@RequestBody List<LdapEntry> ldapEntries) {
        try {
            ldapService.saveLdapEntries(ldapEntries);
        } catch (Exception exception) {
            log.error("saveEntries error", exception);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "account")
    public ResponseEntity<Void> saveAccounts(@RequestBody LdapBaseSetting baseSetting) {

        List<LdapAccount> ldapAccounts;
        try {
            ldapAccounts = ldapService.getLdapAccounts(baseSetting);
        } catch (Exception exception) {
            log.error("saveAccounts error", exception);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        for (LdapAccount ldapAccount:ldapAccounts) {
            serviceOps.addAccount(baseSetting.getServiceId(), ldapAccount);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping(path = "sync-user/{user_id}")
    public ResponseEntity<Void> syncLdapUser(@PathVariable("user_id") String userId) {
        Optional<UserEntity> optionalUserEntity = usersOps.byId(userId);
        if (optionalUserEntity.isPresent()) {
            try {
                ldapService.syncLdapUser(optionalUserEntity.get());
            } catch (Exception exception) {
                log.error("Syncing ldap user exception: {}", exception.getMessage());
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "external/sync-user/{user_id}")
    public ResponseEntity<Void> syncLdapUserExternal(@PathVariable("user_id") String userId) {
        Optional<UserEntity> optionalUserEntity = usersOps.byId(userId);
        if (optionalUserEntity.isPresent()) {
            try {
                ldapService.syncLdapUser(optionalUserEntity.get());
            } catch (Exception exception) {
                log.error("Syncing ldap user exception: {}", exception.getMessage());
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}