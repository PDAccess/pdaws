package com.h2h.pda.api.services;

import com.h2h.pda.entity.AutoCredantialSettingsEntity;
import com.h2h.pda.entity.AutoCredentialsHistoryEntity;
import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.pojo.AutoCredentialSettingsWrapper;
import com.h2h.pda.repository.AutoCredentialHistoryRepository;
import com.h2h.pda.repository.AutoCredentialSettingsRepository;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.CredentialManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auto/credential/")
public class AutoCredentialSettingsController {

    @Autowired
    AutoCredentialSettingsRepository autoCredentialSettingsRepository;

    @Autowired
    AutoCredentialHistoryRepository historyRepository;

    @Autowired
    CredentialManager credentialManager;

    @Autowired
    ActionPdaService actionPdaService;

    @PostMapping("{credentialId}")
    public ResponseEntity<Void> addCredantial(@PathVariable("credentialId") String credentialId, @RequestBody AutoCredentialSettingsWrapper param) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<AutoCredantialSettingsEntity> autoCredantialSettingsEntity = Optional.ofNullable(autoCredentialSettingsRepository.findByCredantialId(credentialId));
        if (param.isEnabled()) {
            AutoCredantialSettingsEntity autoCredantialSettingsEntity1;
            if (param.getAutoCredentialTime() < 3600) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (param.getAutoCredentialTimeType() == null || param.getAutoCredentialTimeType().equals("")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (!param.getAutoCredentialTimeType().equals("hour") && !param.getAutoCredentialTimeType().equals("day") && !param.getAutoCredentialTimeType().equals("week") && !param.getAutoCredentialTimeType().equals("month") && !param.getAutoCredentialTimeType().equals("year")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (param.getAutoCredentialTimeType().equals("hour")) {
                param.setAutoCredentialTime(param.getAutoCredentialTime() - param.getAutoCredentialTime() % 3600);
                if (param.getAutoCredentialTime() < 3600) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (param.getAutoCredentialTimeType().equals("day")) {
                param.setAutoCredentialTime(param.getAutoCredentialTime() - param.getAutoCredentialTime() % 86400);
                if (param.getAutoCredentialTime() < 86400) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (param.getAutoCredentialTimeType().equals("week")) {
                param.setAutoCredentialTime(param.getAutoCredentialTime() - param.getAutoCredentialTime() % 604800);
                if (param.getAutoCredentialTime() < 604800) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (param.getAutoCredentialTimeType().equals("month")) {
                param.setAutoCredentialTime(param.getAutoCredentialTime() - param.getAutoCredentialTime() % 2629743);
                if (param.getAutoCredentialTime() < 2629743) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (param.getAutoCredentialTimeType().equals("year")) {
                param.setAutoCredentialTime(param.getAutoCredentialTime() - param.getAutoCredentialTime() % 31556926);
                if (param.getAutoCredentialTime() < 31556926) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (!autoCredantialSettingsEntity.isPresent()) {
                autoCredantialSettingsEntity1 = new AutoCredantialSettingsEntity();
                autoCredantialSettingsEntity1.setCredantialId(credentialEntity.getCredentialId());
                autoCredantialSettingsEntity1.setLastAction(new Timestamp(System.currentTimeMillis()));
                autoCredantialSettingsEntity1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            } else {
                autoCredantialSettingsEntity1 = autoCredantialSettingsEntity.get();
            }
            autoCredantialSettingsEntity1.setAutoCredantialTime(param.getAutoCredentialTime());
            autoCredantialSettingsEntity1.setAutoCredantialTimeType(param.getAutoCredentialTimeType());
            autoCredentialSettingsRepository.save(autoCredantialSettingsEntity1);
            credentialEntity.setAutoCredantialSettingsEntity(autoCredantialSettingsEntity1);
        } else {
            autoCredantialSettingsEntity.ifPresent(credantialSettingsEntity -> autoCredentialSettingsRepository.delete(credantialSettingsEntity));
        }
        credentialManager.updateCredential(credentialEntity);

        actionPdaService.saveAction(String.format("Auto credential created for %s credential", credentialEntity.getUsername()));

        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping("{inventoryId}")
    public ResponseEntity<Void> deleteCredential(@PathVariable("credentialId") String credentialId) {
        Optional<AutoCredantialSettingsEntity> autoCredentialSettingsEntity = Optional.ofNullable(autoCredentialSettingsRepository.findByCredantialId(credentialId));

        if (autoCredentialSettingsEntity.isPresent()) {
            autoCredentialSettingsRepository.delete(autoCredentialSettingsEntity.get());
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    // TODO: Entity Fix
    @GetMapping({"history/{inventoryId}"})
    public ResponseEntity<List<AutoCredentialsHistoryEntity>> getHistory(@PathVariable("inventoryId") String inventoryId) {
        return new ResponseEntity(historyRepository.findAllByInventoryId(inventoryId), HttpStatus.OK);
    }

    @PostMapping("manage/{credentialId}")
    public ResponseEntity<Void> manageCredentialSettings(@PathVariable("credentialId") String credentialId, @RequestBody AutoCredentialSettingsWrapper data) {
        CredentialEntity credentialEntity = credentialManager.getCredential(credentialId);
        if (credentialEntity == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        credentialEntity.setCredentialManageTime(data.getAutoCredentialTime());
        credentialEntity.setCredentialManageTimeType(data.getAutoCredentialTimeType());
        credentialManager.updateCredential(credentialEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

