package com.h2h.pda.api.user;

import com.h2h.pda.entity.TenantEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.TenantDTO;
import com.h2h.pda.pojo.TenantEntityWrapper;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.repository.TenantRepository;
import com.h2h.pda.service.api.ActionPdaService;
import com.h2h.pda.service.api.UsersOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tenant")
public class TenantController {

    @Autowired
    TenantRepository tenantRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    ActionPdaService actionPdaService;

    @GetMapping(path = "/all")
    public List<TenantEntity> getTenants() {
        return new ArrayList<>(tenantRepository.findByNotDeleted());
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PostMapping()
    public ResponseEntity<String> addTenant(@RequestBody TenantDTO dto) {
        TenantEntity tenantEntity = new TenantEntity();
        String id = UUID.randomUUID().toString();
        tenantEntity.setTenantId(id);

        if (dto.getCompanyName() == null || dto.getCompanyName().equals("")) {
            return new ResponseEntity<>("Company name is cannot be null!", HttpStatus.BAD_REQUEST);
        }

        if (dto.getCountry() == null || dto.getCountry().equals("")) {
            return new ResponseEntity<>("Country is cannot be null!", HttpStatus.BAD_REQUEST);
        }

        tenantEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tenantEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        tenantEntity.setCompanyName(dto.getCompanyName());
        tenantEntity.setCountry(dto.getCountry());

        tenantRepository.save(tenantEntity);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable String id) {
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(id);
        if (tenantEntity.isPresent()) {
            TenantEntity tenant = tenantEntity.get();
            if (tenant.getDeletedAt() != null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            tenant.setDeletedAt(new Timestamp(System.currentTimeMillis()));
            tenantRepository.save(tenant);
            actionPdaService.saveAction(String.format("%s tenant is deleted", tenant.getCompanyName()));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @PutMapping()
    public ResponseEntity<String> updateTenant(@RequestBody TenantDTO dto) {
        Optional<TenantEntity> tenantEntity1 = tenantRepository.findById(dto.getTenantId());
        if (tenantEntity1.isPresent()) {
            TenantEntity tenant = tenantEntity1.get();

            if (dto.getCompanyName() == null || dto.getCompanyName().equals("")) {
                return new ResponseEntity<>("Company name is cannot be null!", HttpStatus.BAD_REQUEST);
            }

            if (dto.getCountry() == null || dto.getCountry().equals("")) {
                return new ResponseEntity<>("Country is cannot be null!", HttpStatus.BAD_REQUEST);
            }

            tenant.setCompanyName(dto.getCompanyName());
            tenant.setCountry(dto.getCountry());
            tenant.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            tenantRepository.save(tenant);

            actionPdaService.saveAction(String.format("%s tenant is edited", dto.getCompanyName()));

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    //@PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/user")
    public List<TenantEntityWrapper> getTenantWithUsers() {

        List<TenantEntityWrapper> tenantlist = new ArrayList<>();

        for (TenantEntity tenantEntity : tenantRepository.findByNotDeleted()) {
            List<UserEntity> users = usersOps.findUsersByExternal(null);
            TenantEntityWrapper tenantEntityWrapper = new TenantEntityWrapper(tenantEntity);
            tenantEntityWrapper.setUsers(users.stream().map(u -> new UserDTO(u)).collect(Collectors.toList()));

            tenantlist.add(tenantEntityWrapper);
        }

        return tenantlist;
    }

    @PreAuthorize("@securityService.hasAdmin(authentication)")
    @GetMapping(path = "/{id}")
    public ResponseEntity<TenantEntityWrapper> getTenant(@PathVariable("id") String tenantId) {
        Optional<TenantEntity> optionalTenantEntity = tenantRepository.findById(tenantId);
        if (optionalTenantEntity.isPresent()) {
            TenantEntityWrapper tenantEntityWrapper = new TenantEntityWrapper(optionalTenantEntity.get());
            return new ResponseEntity<>(tenantEntityWrapper, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}