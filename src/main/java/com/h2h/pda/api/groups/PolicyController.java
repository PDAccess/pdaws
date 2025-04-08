package com.h2h.pda.api.groups;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.policy.*;
import com.h2h.pda.service.api.*;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/policy")
public class PolicyController {
    private final Logger log = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ProxyPolicyService proxyPolicyEntityPolicyService;

    @Autowired
    SudoPolicyService sudoPolicyEntityPolicyService;

    @Autowired
    ActionPdaService actionPdaService;

    @GetMapping(path = "/proxy")
    public ResponseEntity<List<ProxyPolicyWrapper>> proxyPolicyList() {
        UserEntity userEntity = usersOps.securedUser();

        List<ProxyPolicyEntity> proxyPolicyEntities = proxyPolicyEntityPolicyService.userEffectivePolicyOn(userEntity.getUserId(), "");
        List<ProxyPolicyWrapper> policyEntityWrappers = proxyPolicyEntities.stream()
                .map(ppe -> new ProxyPolicyWrapper().wrap(ppe)).collect(Collectors.toList());

        return new ResponseEntity<>(policyEntityWrappers, HttpStatus.OK);
    }

    @GetMapping(path = "/sudo")
    public ResponseEntity<List<SudoPolicyWrapper>> sudoPolicyList() {
        UserEntity userEntity = usersOps.securedUser();

        List<SudoPolicyEntity> proxyPolicyEntities = sudoPolicyEntityPolicyService.userEffectivePolicyOn(userEntity.getUserId(), "");
        List<SudoPolicyWrapper> policyEntityWrappers = proxyPolicyEntities.stream()
                .map(spe -> new SudoPolicyWrapper().wrap(spe)).collect(Collectors.toList());
        return new ResponseEntity<>(policyEntityWrappers, HttpStatus.OK);
    }

    @GetMapping(path = "/app/{upperId}")
    public ResponseEntity<List<PolicyEntityWrapper>> appPolicies(@PathVariable String upperId) {
        List<PolicyEntity> policyList = proxyPolicyEntityPolicyService.groupPolicies(upperId, PolicyService.PolicyType.PROXY);
        List<PolicyEntityWrapper> sendPolList = new ArrayList<>();
        for (PolicyEntity item : policyList) {
            if (item.getGroup() != null && isNotServiceOrGroup(item) && item.getGroup().getGroupId().equals(upperId)
            ) {
                for (PolicyUserEntity policyUserEntity : item.getPolicyUserEntity()) {
                    Optional<UserEntity> userEntity = usersOps.byId(policyUserEntity.getUserId());
                    userEntity.ifPresent(entity -> policyUserEntity.setUserId(entity.getUsername()));
                }
                sendPolList.add(new PolicyEntityWrapper().wrap(item));
            }
        }

        return new ResponseEntity<>(sendPolList, HttpStatus.OK);
    }

    @GetMapping(path = "/sudo/{upperId}")
    public ResponseEntity<List<PolicyEntityWrapper>> groupSudoPolicies(@PathVariable String upperId) {
        List<PolicyEntity> policyEntities = sudoPolicyEntityPolicyService.groupPolicies(upperId, PolicyService.PolicyType.SUDO);

        return new ResponseEntity<>(policyEntities.stream().map(p -> new PolicyEntityWrapper().wrap(p)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(path = "/agent/{upperId}")
    public ResponseEntity<List<PolicyEntityWrapper>> agentPolicies(@PathVariable String upperId) {
        List<PolicyEntity> policyList = sudoPolicyEntityPolicyService.groupPolicies(upperId, PolicyService.PolicyType.SUDO);
        List<PolicyEntityWrapper> sendPolList = new ArrayList<>();
        for (PolicyEntity item : policyList) {
            if (item.getGroup() != null && isNotServiceOrGroup(item) && item.getGroup().getGroupId().equals(upperId)) {
                Set<PolicyUserEntity> policyUserEntities = item.getPolicyUserEntity();
                for (PolicyUserEntity policyUserEntity : policyUserEntities) {
                    Optional<UserEntity> userEntity = usersOps.byId(policyUserEntity.getUserId());
                    userEntity.ifPresent(entity -> policyUserEntity.setUserId(entity.getUsername()));
                }

                item.setPolicyUserEntity(policyUserEntities);

                sendPolList.add(new PolicyEntityWrapper().wrap(item));
            }
        }

        return new ResponseEntity<>(sendPolList, HttpStatus.OK);
    }

    @GetMapping(path = "/id/{policyId}")
    public ResponseEntity<PolicyEntityWrapper> getPolicy(@PathVariable String policyId) {
        Optional<ProxyPolicyEntity> optionalPolicyEntity = proxyPolicyEntityPolicyService.findPolicy(policyId);
        return optionalPolicyEntity.map(policyEntity ->
                new ResponseEntity<>(new PolicyEntityWrapper().wrap(policyEntity), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/sudo/id/{policyId}")
    public ResponseEntity<SudoPolicyEntityWrapper> getSudoPolicy(@PathVariable String policyId) {
        Optional<SudoPolicyEntity> optionalPolicyEntity = sudoPolicyEntityPolicyService.findPolicy(policyId);
        return optionalPolicyEntity.map(sudoPolicyEntity ->
                new ResponseEntity<>(new SudoPolicyEntityWrapper().wrap(sudoPolicyEntity), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/regex/{upperId}")
    public ResponseEntity<PolicyDAO> policyRegexes(@PathVariable String upperId) {
        UserEntity userEntity = usersOps.securedUser();

        PolicyDAO policy = new PolicyDAO("W");

        List<ProxyPolicyEntity> policyEntities = proxyPolicyEntityPolicyService.userEffectivePolicyOn(userEntity.getUserId(), upperId);

        ArrayList<String> commands = new ArrayList<>();
        for (PolicyEntity policyEntity : policyEntities) {

            if (!policyEntity.hasUser(userEntity.getUserId())) {
                continue;
            }

            for (PolicyRegexEntity regexEntity : policyEntity.getPolicyRegexEntity()) {
                commands.add(regexEntity.getRegex());
            }
        }

        policy.setCommands(commands);

        return ResponseEntity.ok(policy);
    }

    @PutMapping(value = "/sudo")
    public ResponseEntity<String> putSudoPolicy(@RequestBody SudoPolicyCreateParam param) {
        UserEntity user = usersOps.securedUser();
        SudoPolicyEntity policy = new SudoPolicyEntity();
        List<PolicyRegexEntity> list = new ArrayList<>();

        SudoPolicyEntityWrapper policyEntity = param.getPolicyEntity();
        String groupId = policyEntity.getId();
        if (StringUtil.isEmpty(groupId)) {
            return new ResponseEntity<>("Group list is cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (param.getRegexList() == null || param.getRegexList().size() == 0) {
            return new ResponseEntity<>("Regex list is cannot be null", HttpStatus.BAD_REQUEST);
        }

        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (!optionalGroupsEntity.isPresent()) {
            return new ResponseEntity<>("Group not found", HttpStatus.BAD_REQUEST);
        }

        List<PolicyUserEntity> policyUserEntities = new ArrayList<>();
        if (param.getUserList() != null) {
            for (String userId : param.getUserList()) {
                policyUserEntities.add(new PolicyUserEntity(userId));
            }
        }

        for (int i = 0; i < param.getRegexList().size(); i++) {
            PolicyRegexEntity policyregex = new PolicyRegexEntity(param.getRegexList().get(i));
            list.add(policyregex);
        }

        if (policyEntity.getName() == null || policyEntity.getName().equals("")) {
            return new ResponseEntity<>("Name is cannot be null", HttpStatus.BAD_REQUEST);
        }

        policy.setCreatedAt(Timestamp.from(Instant.now()));
        policy.setWhoCreate(user);
        policy.setName(policyEntity.getName());
        policy.setPolicyRegexEntity(new HashSet<>(list));
        policy.setPolicyUserEntity(new HashSet<>(policyUserEntities));
        policy.setGroup(optionalGroupsEntity.get());
        policy.setBehavior("W".equals(policyEntity.getBehavior()) ? PolicyBehavior.WHITE : PolicyBehavior.BLACK);
        policy.setSudoRunAsUser(param.getPolicyEntity().getRunAsUser());

        String policyId = sudoPolicyEntityPolicyService.newPolicy(policy);

        return new ResponseEntity<>(policyId, HttpStatus.OK);
    }

    @PutMapping(value = "/proxy")
    public ResponseEntity<String> putProxyPolicy(@RequestBody PolicyCreateParam param) {
        String id = UUID.randomUUID().toString();

        UserEntity user = usersOps.securedUser();
        ProxyPolicyEntity policy = new ProxyPolicyEntity();
        List<PolicyRegexEntity> list = new ArrayList<>();

        String errorMessage = isValid(param);

        if ((param.getUserList() == null || param.getUserList().size() == 0) && (param.getGroupList() == null || param.getGroupList().isEmpty())) {
            return new ResponseEntity<>("User and Group list is cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (param.getRegexList() == null || param.getRegexList().size() == 0) {
            return new ResponseEntity<>("Regex list is cannot be null", HttpStatus.BAD_REQUEST);
        }

        PolicyEntityWrapper policyEntity = param.getPolicyEntity();

        String groupId = policyEntity.getId();
        Optional<GroupsEntity> optionalGroupsEntity = groupOps.byId(groupId);
        if (!optionalGroupsEntity.isPresent()) {
            return new ResponseEntity<>("Group not found", HttpStatus.BAD_REQUEST);
        }

        List<PolicyUserEntity> policyUserEntities = new ArrayList<>();
        if (param.getUserList() != null) {
            for (String userId : param.getUserList()) {
                policyUserEntities.add(new PolicyUserEntity(userId));
            }
        }

        for (int i = 0; i < param.getRegexList().size(); i++) {
            PolicyRegexEntity policyregex = new PolicyRegexEntity(param.getRegexList().get(i));
            list.add(policyregex);
        }

        if (policyEntity.getName() == null || policyEntity.getName().equals("")) {
            return new ResponseEntity<>("Name is cannot be null", HttpStatus.BAD_REQUEST);
        }


        policy.setCreatedAt(Timestamp.from(Instant.now()));
        policy.setWhoCreate(user);
        policy.setName(policyEntity.getName());
        policy.setPolicyRegexEntity(new HashSet<>(list));
        policy.setPolicyUserEntity(new HashSet<>(policyUserEntities));
        policy.setBehavior("W".equals(policyEntity.getBehavior()) ? PolicyBehavior.WHITE : PolicyBehavior.BLACK);
        policy.setGroup(optionalGroupsEntity.get());

        proxyPolicyEntityPolicyService.newPolicy(policy);

        actionPdaService.saveAction(String.format("%s policy is created", policy.getName()));

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> updatePolicy(@RequestBody PolicyCreateParam param) {
        Optional<ProxyPolicyEntity> policyEntity = proxyPolicyEntityPolicyService.findPolicy(param.getPolicyEntity().getId());
        if (policyEntity.isPresent()) {
            List<PolicyRegexEntity> list = new ArrayList<>();

            if (param.getPolicyEntity() == null) {
                return new ResponseEntity<>("Policy entity is cannot be null", HttpStatus.BAD_REQUEST);
            }

            if ((param.getUserList() == null || param.getUserList().size() == 0) && (param.getGroupList() == null || param.getGroupList().isEmpty())) {
                return new ResponseEntity<>("User or Group list is cannot be null", HttpStatus.BAD_REQUEST);
            }

            if (param.getRegexList() == null || param.getRegexList().size() == 0) {
                return new ResponseEntity<>("Regex list is cannot be null", HttpStatus.BAD_REQUEST);
            }

            PolicyEntityWrapper policyEntity1 = param.getPolicyEntity();

            if (policyEntity1.getId() == null || policyEntity1.getId().equals("")) {
                return new ResponseEntity<>("ID is cannot be null", HttpStatus.BAD_REQUEST);
            }

            for (int i = 0; i < param.getRegexList().size(); i++) {
                PolicyRegexEntity policyregex = new PolicyRegexEntity(param.getRegexList().get(i));
                list.add(policyregex);
            }


            if (policyEntity1.getName() == null || policyEntity1.getName().equals("")) {
                return new ResponseEntity<>("Name is cannot be null", HttpStatus.BAD_REQUEST);
            }

            ProxyPolicyEntity policy = (ProxyPolicyEntity) policyEntity.get();
            policy.setName(param.getPolicyEntity().getName());
            policy.setCreatedAt(param.getPolicyEntity().getCreatedAt());


            List<PolicyUserEntity> policyUserEntities = new ArrayList<>();
            if (param.getUserList() != null) {
                for (String userId : param.getUserList()) {
                    policyUserEntities.add(new PolicyUserEntity(userId));
                }
            }

            policy.setPolicyUserEntity(new HashSet<>(policyUserEntities));
            policy.setPolicyRegexEntity(new HashSet<>(list));
            policy.setBehavior("W".equals(param.getPolicyEntity().getBehavior()) ? PolicyBehavior.WHITE : PolicyBehavior.BLACK);
            proxyPolicyEntityPolicyService.updatePolicy(policy);
            actionPdaService.saveAction(String.format("%s policy is updated", policy.getName()));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/sudo/update")
    public ResponseEntity<String> updateSudoPolicy(@RequestBody PolicyCreateParam param) {
        Optional<SudoPolicyEntity> policyEntity = sudoPolicyEntityPolicyService.findPolicy(param.getPolicyEntity().getId());
        if (policyEntity.isPresent()) {
            List<PolicyRegexEntity> list = new ArrayList<>();

            if (param.getPolicyEntity() == null) {
                return new ResponseEntity<>("Policy entity is cannot be null", HttpStatus.BAD_REQUEST);
            }

            PolicyEntityWrapper policyEntity1 = param.getPolicyEntity();

            if (StringUtil.isEmpty(policyEntity1.getId())) {
                return new ResponseEntity<>("Group list is cannot be null", HttpStatus.BAD_REQUEST);
            }

            if (param.getRegexList() == null || param.getRegexList().size() == 0) {
                return new ResponseEntity<>("Regex list is cannot be null", HttpStatus.BAD_REQUEST);
            }


            if (policyEntity1.getId() == null || policyEntity1.getId().equals("")) {
                return new ResponseEntity<>("ID is cannot be null", HttpStatus.BAD_REQUEST);
            }

            for (int i = 0; i < param.getRegexList().size(); i++) {
                PolicyRegexEntity policyregex = new PolicyRegexEntity(param.getRegexList().get(i));
                list.add(policyregex);
            }


            if (policyEntity1.getName() == null || policyEntity1.getName().equals("")) {
                return new ResponseEntity<>("Name is cannot be null", HttpStatus.BAD_REQUEST);
            }

            SudoPolicyEntity policy = (SudoPolicyEntity) policyEntity.get();
            policy.setName(param.getPolicyEntity().getName());
            policy.setCreatedAt(param.getPolicyEntity().getCreatedAt());


            List<PolicyUserEntity> policyUserEntities = new ArrayList<>();
            if (param.getUserList() != null) {
                for (String userId : param.getUserList()) {
                    policyUserEntities.add(new PolicyUserEntity(userId));
                }
            }

            policy.setPolicyUserEntity(new HashSet<>(policyUserEntities));
            policy.setPolicyRegexEntity(new HashSet<>(list));
            policy.setBehavior("W".equals(param.getPolicyEntity().getBehavior()) ? PolicyBehavior.WHITE : PolicyBehavior.BLACK);
            policy.setSudoRunAsUser(param.getPolicyEntity().getRunAsUser());
            sudoPolicyEntityPolicyService.updatePolicy(policy);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        proxyPolicyEntityPolicyService.removePolicy(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/user/{userid}")
    public ResponseEntity<List<ProxyPolicyWrapper>> getUserPolicy(@PathVariable String userid, @RequestBody Pagination param) {
        List<ProxyPolicyEntity> policy = proxyPolicyEntityPolicyService.userEffectivePolicyOn(userid, null);

        if (policy != null) {
            return ResponseEntity.ok(policy.stream().map(p -> new ProxyPolicyWrapper().wrap(p)).collect(Collectors.toList()));
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(path = "/users/{policyId}")
    public ResponseEntity<List<UserDTO>> policyUser(@PathVariable String policyId) {
        List<UserDTO> userEntityList = new ArrayList<>();
        Optional<ProxyPolicyEntity> policy = proxyPolicyEntityPolicyService.findPolicy(policyId);

        if (!policy.isPresent())
            return new ResponseEntity<>(userEntityList, HttpStatus.OK);

        for (PolicyUserEntity policyUserEntity : policy.get().getPolicyUserEntity()) {
            Optional<UserEntity> user = usersOps.byId(policyUserEntity.getUserId());
            if (user.isPresent())
                userEntityList.add(new UserDTO().wrap(user.get()));
        }
        return new ResponseEntity<>(userEntityList, HttpStatus.OK);
    }

    private Boolean isNotServiceOrGroup(PolicyEntity item) {
        //return (item.getIdtype().compareTo(SERVICE) == 0 || item.getIdtype().compareTo(GROUP) == 0);
        return true;
    }

    private String isValid(PolicyCreateParam param) {
        if (param.getPolicyEntity() == null)
            return "Policy entity is cannot be null";

        if (isNullOrEmpty(param.getUserList()))
            return "User list is cannot be null";

        if (isNullOrEmpty(param.getRegexList())) {
            return "Regex list is cannot be null";
        }

        if (param.getPolicyEntity().getId() == null) {
            return "ID is cannot be null";
        }
        return null;
    }

    private Boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}