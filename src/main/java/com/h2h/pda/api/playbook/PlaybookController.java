package com.h2h.pda.api.playbook;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.PlaybookInstallerWrapper;
import com.h2h.pda.pojo.PlaybookService;
import com.h2h.pda.pojo.PlaybookStatus;
import com.h2h.pda.pojo.PlaybookUsername;
import com.h2h.pda.repository.PlaybookHistoryRepository;
import com.h2h.pda.repository.PlaybookInstallerRepository;
import com.h2h.pda.repository.PlaybookInstallerServiceRepository;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/v1/playbook/")
public class PlaybookController {

    private static final String ANSIBLE_SERVER_URL = "http://ansible-installer";

    @Autowired
    PlaybookInstallerRepository playbookInstallerRepository;

    @Autowired
    PlaybookInstallerServiceRepository playbookInstallerServiceRepository;

    @Autowired
    PlaybookHistoryRepository playbookHistoryRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ServiceOps serviceOps;

    @GetMapping(path = "myplaybook/{user_id}")
    public ResponseEntity<List<PlaybookUsername>> getMyAnsibleInstallers(@PathVariable("user_id") String userId) {
        return new ResponseEntity<>(getPlaybookInstallersList(playbookInstallerRepository.findAllByUserId(userId)), HttpStatus.OK);
    }

    @GetMapping(path = "public/{user_id}")
    public ResponseEntity<List<PlaybookUsername>> getPublicAnsibleInstallers(@PathVariable("user_id") String userId) {
        return new ResponseEntity<>(getPlaybookInstallersList(playbookInstallerRepository.findPublicAnsible(userId)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> savePlaybookInstaller(@RequestBody PlaybookInstallerWrapper installerWrapper) {

        PlaybookInstallerEntity playbookInstallerEntity = new PlaybookInstallerEntity();
        playbookInstallerEntity.setName(installerWrapper.getName());
        playbookInstallerEntity.setDescription(installerWrapper.getDescription());
        playbookInstallerEntity.setServiceEntities(installerWrapper.getServices());
        playbookInstallerEntity.setYmlContent(installerWrapper.getYmlContent());
        playbookInstallerEntity.setUserId(installerWrapper.getUserId());
        playbookInstallerEntity.setPrivate(installerWrapper.getIsPrivate());
        playbookInstallerEntity.setCreatedAt(new Timestamp(new Date().getTime()));

        playbookInstallerRepository.save(playbookInstallerEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "{installer_id}")
    public ResponseEntity<Void> deletePlaybookInstaller(@PathVariable("installer_id") Integer installerId) {
        Optional<PlaybookInstallerEntity> optionalAnsibleInstallerEntity = playbookInstallerRepository.findById(installerId);
        if (!optionalAnsibleInstallerEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        PlaybookInstallerEntity installerEntity = optionalAnsibleInstallerEntity.get();
        playbookInstallerRepository.delete(installerEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("deleteservice/{service_id}")
    public ResponseEntity<Void> deletePlaybookService(@PathVariable("service_id") String serviceId){
        playbookInstallerServiceRepository.deleteAllByIdServiceId(serviceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "{installer_id}")
    public ResponseEntity<PlaybookService> getPlaybookInstaller(@PathVariable("installer_id") Integer installerId) {
        Optional<PlaybookInstallerEntity> optionalInstallerEntity =  playbookInstallerRepository.findById(installerId);
        List<ServiceEntity> serviceEntities = new ArrayList<>();
        if (!optionalInstallerEntity.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<PlaybookInstallerServiceEntity> ansibleInstallerServiceEntities = playbookInstallerServiceRepository.findAllByIdInstallerId(optionalInstallerEntity.get().getId());
        for (PlaybookInstallerServiceEntity playbookInstallerServiceEntity : ansibleInstallerServiceEntities) {
            Optional<ServiceEntity> serviceEntity = serviceOps.byId(playbookInstallerServiceEntity.getId().getServiceId());
            if (serviceEntity.isPresent())
                serviceEntities.add(serviceEntity.get());
        }
        PlaybookService playbookService = new PlaybookService(optionalInstallerEntity.get(), serviceEntities);
        return new ResponseEntity<>(playbookService, HttpStatus.OK);

    }

    @PutMapping(path = "{installer_id}")
    public ResponseEntity<Void> updatePlaybookInstaller(@PathVariable("installer_id") Integer installerId,
                                                       @RequestBody PlaybookInstallerWrapper installerWrapper) {
        Optional<PlaybookInstallerEntity> optionalAnsibleInstallerEntity = playbookInstallerRepository.findById(installerId);
        if (!optionalAnsibleInstallerEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PlaybookInstallerEntity installerEntity = optionalAnsibleInstallerEntity.get();
        installerEntity.setName(installerWrapper.getName());
        installerEntity.setDescription(installerWrapper.getDescription());
        installerEntity.setServiceEntities(installerWrapper.getServices());
        installerEntity.setYmlContent(installerWrapper.getYmlContent());
        installerEntity.setUserId(installerWrapper.getUserId());
        installerEntity.setPrivate(installerWrapper.getIsPrivate());
        installerEntity.setUpdatedAt(new Timestamp(new Date().getTime()));

        playbookInstallerRepository.save(installerEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "histories/{installer_id}")
    public ResponseEntity<Set<PlaybookHistoryEntity>> getPlaybookStatus(@PathVariable("installer_id") Integer installerId) {
        Optional<PlaybookInstallerEntity> optionalInstallerEntity = playbookInstallerRepository.findById(installerId);
        if (!optionalInstallerEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PlaybookInstallerEntity installerEntity = optionalInstallerEntity.get();

        return new ResponseEntity<>(installerEntity.getHistoryEntities(), HttpStatus.OK);
    }

    @GetMapping(path = "result/{history_id}")
    public ResponseEntity<PlaybookStatus> getPlaybookHistory(@PathVariable("history_id") Integer historyId) {

        Optional<PlaybookHistoryEntity> optionalHistoryEntity = playbookHistoryRepository.findById(historyId);
        if (!optionalHistoryEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PlaybookHistoryEntity historyEntity = optionalHistoryEntity.get();
        PlaybookInstallerEntity installerEntity = historyEntity.getInstallerEntity();
        if (installerEntity == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String statusUrl = ANSIBLE_SERVER_URL + "/ansible-result.sh?installer_id=" + installerEntity.getId() + "&history_id=" + historyEntity.getId().toString();
        ResponseEntity<PlaybookStatus> response = restTemplate.exchange(statusUrl, HttpMethod.POST, null, PlaybookStatus.class);
        PlaybookStatus status = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || status == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(status, HttpStatus.OK);

    }

    private List<PlaybookUsername> getPlaybookInstallersList(List<PlaybookInstallerEntity> ansibleInstallerEntities){
        List<PlaybookUsername> playbookUsernames = new ArrayList<>();
        List<ServiceEntity> serviceEntities = new ArrayList<>();
        for (PlaybookInstallerEntity playbookInstallerEntity : ansibleInstallerEntities) {
            Optional<UserEntity> byId = usersOps.byId(playbookInstallerEntity.getUserId());
            if (byId.isPresent()) {
                UserEntity userEntity = byId.get();
                List<PlaybookInstallerServiceEntity> ansibleInstallerServiceEntities = playbookInstallerServiceRepository.findAllByIdInstallerId(playbookInstallerEntity.getId());
                for (PlaybookInstallerServiceEntity playbookInstallerServiceEntity : ansibleInstallerServiceEntities) {
                    Optional<ServiceEntity> serviceEntity = serviceOps.byId(playbookInstallerServiceEntity.getId().getServiceId());
                    if (serviceEntity.isPresent())
                        serviceEntities.add(serviceEntity.get());
                }
                PlaybookUsername playbookUsername = new PlaybookUsername(playbookInstallerEntity, serviceEntities, userEntity.getUsername(), userEntity.getFirstName(), userEntity.getLastName());
                playbookUsernames.add(playbookUsername);
                serviceEntities = new ArrayList<>();
            }
        }
        return playbookUsernames;
    }
}