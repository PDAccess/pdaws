package com.h2h.pda.api.admin;

import com.h2h.pda.entity.Oauth2Entity;
import com.h2h.pda.pojo.Oauth2EditWrapper;
import com.h2h.pda.pojo.Oauth2Wrapper;
import com.h2h.pda.repository.Oauth2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/oauth2")
public class Oauth2Controller {
    private static final String URL_REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    @Autowired
    Oauth2Repository repo;

    boolean isMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }

    }

    private String addOauthDataControl(Oauth2Wrapper data, List<String> scope){
        if(data.getName() == null || data.getName().equals("")){
            return "Name is cannot to be null!";
        }

        if(data.getScopes() != null && !data.getScopes().equals("")){
            List<String> scopes = Arrays.asList(data.getScopes().split(","));
            for (String s : scopes) {
                if(!scope.contains(s)){
                    return "Wrong scope parameters!";
                }
            }
        }

        if(!isMatch(data.getCallbackUrl(), URL_REGEX)){
            return "Callback url is wrong!";
        }

        if(data.getTrusted() != 0 && data.getTrusted() != 1){
            return "Trusted data is wrong!";
        }
        return null;
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping
    public ResponseEntity<String> addOauth2(@RequestBody Oauth2Wrapper data) {
        Oauth2Entity oauth2Entity = new Oauth2Entity();
        List<String> scope = new ArrayList<>();
        scope.add("email");
        scope.add("api");
        scope.add("read_user");
        scope.add("sudo");
        scope.add("profile");
        oauth2Entity.setId(UUID.randomUUID().toString());
        oauth2Entity.setAppid(UUID.randomUUID().toString());
        oauth2Entity.setSecret(UUID.randomUUID().toString());
        oauth2Entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        String addOauthDataErrorMessage = addOauthDataControl(data, scope);
        if(addOauthDataErrorMessage != null){
            return new ResponseEntity<>(addOauthDataErrorMessage,HttpStatus.BAD_REQUEST);
        }

        oauth2Entity.setTrusted(data.getTrusted());
        oauth2Entity.setCallbackUrl(data.getCallbackUrl());
        oauth2Entity.setName(data.getName());
        oauth2Entity.setClients(0);
        oauth2Entity.setScopes(data.getScopes());
        repo.save(oauth2Entity);
        return new ResponseEntity<>(oauth2Entity.getId(), HttpStatus.OK);
    }

    // TODO: Entity Fix
    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/id/{id}")
    public ResponseEntity<Oauth2Entity> getOauth2App(@PathVariable String id) {
        Optional<Oauth2Entity> oauth2Entity = repo.findById(id);
        if(oauth2Entity.isPresent()){
            if (oauth2Entity.get().getDeletedAt() != null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(oauth2Entity.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: Entity Fix
    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping
    public ResponseEntity<List<Oauth2Entity>> getOauth2List() {
        List<Oauth2Entity> oauth2Entities = repo.findByNotDeleted();
        return new ResponseEntity<>(oauth2Entities, HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PutMapping
    public ResponseEntity<String> addOauth2Edit(@RequestBody Oauth2EditWrapper data) {
        Optional<Oauth2Entity> oauth2Entity = repo.findById(data.getId());
        if (oauth2Entity.isPresent()) {
            Oauth2Entity oauth2 = oauth2Entity.get();
            if (oauth2.getDeletedAt() != null) {
                return new ResponseEntity<>("Oauth is deleted!", HttpStatus.BAD_REQUEST);
            }
            oauth2.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            List<String> scope = new ArrayList<>();
            scope.add("email");
            scope.add("api");
            scope.add("read_user");
            scope.add("sudo");
            scope.add("profile");

            String addOauthDataErrorMessage = addOauthDataControl(data, scope);
            if(addOauthDataErrorMessage != null){
                return new ResponseEntity<>(addOauthDataErrorMessage,HttpStatus.BAD_REQUEST);
            }
            oauth2.setCallbackUrl(data.getCallbackUrl());
            oauth2.setName(data.getName());
            oauth2.setTrusted(data.getTrusted());
            oauth2.setScopes(data.getScopes());
            repo.save(oauth2);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteOauth(@PathVariable String id) {
        Optional<Oauth2Entity> oauth2Entity = repo.findById(id);
        if (oauth2Entity.isPresent()) {
            Oauth2Entity oauth2 = oauth2Entity.get();
            oauth2.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
            repo.save(oauth2);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}