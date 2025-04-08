package com.h2h.pda.api.services;

import com.h2h.pda.entity.SnippetEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.SnippetUsername;
import com.h2h.pda.pojo.SnippetWrapper;
import com.h2h.pda.repository.SnippetRepository;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/snippet")
public class SnippetController {

    @Autowired
    SnippetRepository snippetRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ActionPdaService actionPdaService;

    @GetMapping(path = "/{userid}")
    public ResponseEntity<List<SnippetUsername>> getSnippets(@PathVariable String userid) {
        List<SnippetEntity> snippetEntities = snippetRepository.findByNotDeleted(userid);
        return getSnippetUsernameList(snippetEntities);
    }


    private ResponseEntity<List<SnippetUsername>> getSnippetUsernameList(List<SnippetEntity> snippetEntities) {
        List<SnippetUsername> snippetUsernames = new ArrayList<>();
        for (SnippetEntity snippetEntity:snippetEntities) {
            Optional<UserEntity> byName = usersOps.byId(snippetEntity.getUserId());
            if (byName.isPresent()) {
                UserEntity userEntity = byName.get();
                SnippetUsername snippetUsername = new SnippetUsername(snippetEntity, userEntity.getUsername(), userEntity.getFirstName(), userEntity.getLastName());
                snippetUsernames.add(snippetUsername);
            }
        }
        return ResponseEntity.ok(snippetUsernames);
    }


    @PostMapping(path = "/{sort}/{userid}")
    public ResponseEntity<List<SnippetUsername>> getSnippets(@PathVariable String userid, @PathVariable String sort) {
        List<SnippetEntity> snippetEntities = null;
        switch (sort) {
            case "name":
                snippetEntities = snippetRepository.findByNotDeletedOrderName(userid);
                break;
            case "namedesc":
                snippetEntities = snippetRepository.findByNotDeletedOrderNameDesc(userid);
                break;
            case "create":
                snippetEntities = snippetRepository.findByNotDeletedOrderCreatedTime(userid);
                break;
            case "createdesc":
            case "undefined":
                snippetEntities = snippetRepository.findByNotDeletedOrderCreatedTimeDesc(userid);
                break;
            default:
                break;
        }
        if (snippetEntities == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return getSnippetUsernameList(snippetEntities);
    }

    @GetMapping(path = "/public/{userid}")
    public ResponseEntity<List<SnippetUsername>> getAllSnippets(@PathVariable String userid) {
        List<SnippetEntity> snippetEntities = snippetRepository.findAllNotDeletedNotUserid(userid);
        return getSnippetUsernameList(snippetEntities);
    }

    @PutMapping()
    public ResponseEntity<String> addSnippet(@RequestBody SnippetWrapper data) {
        SnippetEntity snippetEntity = new SnippetEntity();
        snippetEntity.setUserId(data.getUserid());
        snippetEntity.setSnippetId(UUID.randomUUID().toString());
        snippetEntity.setDescription(data.getDescription());
        snippetEntity.setTitle(data.getTitle());
        snippetEntity.setInfo(data.getInfo());
        snippetEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        snippetEntity.setOperatingSystemId(data.getOperatingSystemId());
        snippetEntity.setServiceTypeId(data.getServiceTypeId());
        snippetRepository.save(snippetEntity);
        actionPdaService.saveAction(String.format("%s snippet is created", data.getTitle()));
        return new ResponseEntity<>(snippetEntity.getSnippetId(),HttpStatus.OK);
    }

    @PutMapping(path = "/edit")
    public ResponseEntity<Void> editSnippet(@RequestBody SnippetUsername data) {
        SnippetEntity snippetEntity = snippetRepository.findSnippetbyId(data.getSnippetEntity().getSnippetId());
        if (snippetEntity == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        snippetEntity.setUserId(data.getSnippetEntity().getUserId());
        snippetEntity.setDescription(data.getSnippetEntity().getDescription());
        snippetEntity.setTitle(data.getSnippetEntity().getTitle());
        snippetEntity.setInfo(data.getSnippetEntity().getInfo());
        snippetEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        snippetEntity.setOperatingSystemId(data.getSnippetEntity().getOperatingSystemId());
        snippetEntity.setServiceTypeId(data.getSnippetEntity().getServiceTypeId());
        snippetRepository.save(snippetEntity);
        actionPdaService.saveAction(String.format("%s snippet is edited", snippetEntity.getTitle()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/info/{snippetid}")
    public ResponseEntity<SnippetUsername> getSnippetInfo(@PathVariable String snippetid) {
        SnippetEntity snippetEntity = snippetRepository.findSnippetbyId(snippetid);
        Optional<UserEntity> userEntity = usersOps.byId(snippetEntity.getUserId());
        if (!userEntity.isPresent()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SnippetUsername snippetUsername = new SnippetUsername(snippetEntity, userEntity.get().getUsername());
        return ResponseEntity.ok(snippetUsername);
    }

    @DeleteMapping(path = "/{snippetid}")
    public ResponseEntity<Void> softDeleteSnippet(@PathVariable String snippetid){
        Optional<SnippetEntity> snippetEntity = snippetRepository.findById(snippetid);
        if (snippetEntity.isPresent()) {
            SnippetEntity snippet = snippetEntity.get();
            snippet.setDeletedAt(new Timestamp(System.currentTimeMillis()));
            snippetRepository.save(snippet);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}