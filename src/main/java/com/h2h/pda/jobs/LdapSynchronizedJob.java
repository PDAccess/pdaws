package com.h2h.pda.jobs;

import com.h2h.pda.config.LdapTemplateWrapper;
import com.h2h.pda.entity.*;
import com.h2h.pda.map.AccountContextMapper;
import com.h2h.pda.pojo.Credential;
import com.h2h.pda.pojo.group.GroupCategory;
import com.h2h.pda.pojo.group.GroupMembership;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.ldap.*;
import com.h2h.pda.pojo.service.ServiceProperties;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.ConnectionUserRepository;
import com.h2h.pda.repository.LdapSynchronizationLogRepository;
import com.h2h.pda.repository.SystemTokenRepository;
import com.h2h.pda.service.api.*;
import com.h2h.pda.service.impl.LdapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.vault.VaultException;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Configuration
@EnableScheduling
public class LdapSynchronizedJob {
    private Logger log = LoggerFactory.getLogger(LdapSynchronizedJob.class);

    private static final String LDAP = "ldap";
    private static final String SYNCHRONIZED_GROUP_MEMBERS_JOB = "Synchronized group members job";
    private static final String SYNCHRONIZED_SERVICE_ACCOUNTS_JOB = "Synchronized service accounts job";

    @Autowired
    LdapTemplateWrapper templateWrapper;

    @Autowired
    LdapGroupAttributes ldapGroupAttributes;

    @Autowired
    LdapUserAttributes ldapUserAttributes;

    @Autowired
    LdapCommonAttributes ldapCommonAttributes;

    @Autowired
    LdapAccountAttributes ldapAccountAttributes;

    @Autowired
    ConnectionUserRepository connectionUserRepository;

    @Autowired
    LdapSynchronizationLogRepository ldapSynchronizationLogRepository;

    @Autowired
    VaultService vaultService;

    @Autowired
    LdapUtil ldapUtil;

    @Autowired
    SystemTokenRepository systemTokenRepository;

    @Autowired
    JobService jobService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    LdapService ldapService;

    @Scheduled(fixedDelay = 300000)
    public void synchronizeGroupMembers() {

        JobHistoryEntity jobHistoryEntity = new JobHistoryEntity();
        jobHistoryEntity.setName(SYNCHRONIZED_GROUP_MEMBERS_JOB);
        jobHistoryEntity.setStartedAt(Timestamp.valueOf(LocalDateTime.now()));
        jobHistoryEntity = jobService.saveJob(jobHistoryEntity);

        try {
            for (GroupsEntity group : groupOps.searchBy(GroupCategory.LDAP)) {

                StringBuilder createdUsers = new StringBuilder();
                StringBuilder addedUsers = new StringBuilder();
                StringBuilder deletedUsers = new StringBuilder();

                if (group.getLdapRdn() != null && !group.getLdapRdn().isEmpty()) {
                    DirContextAdapter groupContext = (DirContextAdapter) templateWrapper.getLdapTemplate().lookup(group.getLdapRdn());
                    if (groupContext != null) {

                        Attributes attributes = groupContext.getAttributes();
                        Attribute attribute = attributes.get(ldapGroupAttributes.getMemberAttribute());

                            for (GroupUserEntity groupUserEntity : groupOps.effectiveMembers(group.getGroupId())) {
                                if (!GroupRole.ADMIN.equals(groupUserEntity.getMembershipRole())) {
                                    UserEntity userEntity = groupUserEntity.getUser();
                                    boolean userExist = false;
                                    if (attribute != null) {
                                        for (int i = 0; i < attribute.size(); i++) {

                                            try {
                                                String rdn = ldapUtil.getRdn((String) attribute.get(i));

                                                DirContextAdapter memberContext = (DirContextAdapter) templateWrapper.getLdapTemplate().lookup(rdn);
                                                Attributes memberAttributes = memberContext.getAttributes();

                                                if (userEntity.getUsername().equals(memberAttributes.get(ldapUserAttributes.getUsername()).get())) {
                                                    userExist = true;
                                                    break;
                                                }

                                            } catch (Exception exception) {
                                                log.error("Member checks error in LDAP group sync: {}", exception.getMessage());
                                            }
                                        }
                                    }

                                    if (!userExist) {
                                        groupOps.removeUsersFrom(group.getGroupId(), Collections.singletonList(userEntity.getUserId()));
                                        deletedUsers.append(userEntity.getUsername()).append(",");
                                    }
                                }
                            }

                            if (attribute != null) {
                                for (int i = 0; i < attribute.size(); i++) {

                                    try {

                                        String rdn = ldapUtil.getRdn((String) attribute.get(i));

                                        DirContextAdapter memberContext = (DirContextAdapter) templateWrapper.getLdapTemplate().lookup(rdn);
                                        Attributes memberAttributes = memberContext.getAttributes();

                                        boolean userExist = false;
                                        for (GroupUserEntity groupUserEntity : groupOps.effectiveMembers(group.getGroupId())) {
                                            UserEntity userEntity = groupUserEntity.getUser();
                                            if (memberAttributes.get(ldapUserAttributes.getUsername()).get().equals(userEntity.getUsername())) {
                                                userExist = true;
                                                break;
                                            }
                                        }

                                        if (!userExist) {
                                            Optional<UserEntity> byName = usersOps.byName((String) memberAttributes.get(ldapUserAttributes.getUsername()).get());
                                            UserEntity userEntity;

                                            if (!byName.isPresent()) {
                                                userEntity = new UserEntity();
                                                userEntity.setFirstName(ldapUtil.getLdapAttributes(memberAttributes, ldapUserAttributes.getFirstName()));
                                                userEntity.setLastName(ldapUtil.getLdapAttributes(memberAttributes, ldapUserAttributes.getLastName()));
                                                userEntity.setUsername(ldapUtil.getLdapAttributes(memberAttributes, ldapUserAttributes.getUsername()));
                                                userEntity.setEmail(ldapUtil.getLdapAttributes(memberAttributes, ldapUserAttributes.getMail()));
                                                userEntity.setPhone(ldapUtil.getLdapAttributes(memberAttributes, ldapUserAttributes.getTelephoneNumber()));
                                                userEntity.setExternal(true);
                                                userEntity.setRole(UserRole.USER);
                                                userEntity.setTwofactorauth(false);
                                                userEntity.setLdapDn(ldapUtil.getLdapAttributes(memberAttributes, ldapCommonAttributes.getDistinguishedName()));
                                                userEntity = usersOps.newUser(userEntity);
                                                createdUsers.append(userEntity.getUsername()).append(",");
                                            } else {
                                                userEntity = byName.get();
                                            }

                                            GroupUserEntity groupUserEntity = new GroupUserEntity();
                                            groupUserEntity.setId(new GroupUserPK(group.getGroupId(), userEntity.getUserId()));
                                            groupUserEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                            groupUserEntity.setMembershipType(GroupMembership.LDAP);

                                            groupOps.addUsersTo(group.getGroupId(), Collections.singletonList(userEntity.getUserId()), GroupRole.USER);

                                            addedUsers.append(userEntity.getUsername()).append(",");
                                        }
                                    } catch (Exception exception) {
                                        log.error("Error syncing LDAP groups: {}", exception.getMessage());
                                    }
                                }
                            }

                        LdapSynchronizationLogEntity synchronizationLogEntity = new LdapSynchronizationLogEntity();
                        synchronizationLogEntity.setGroupId(group.getGroupId());
                        synchronizationLogEntity.setCreatedUsers(createdUsers.toString());
                        synchronizationLogEntity.setAddedUsers(addedUsers.toString());
                        synchronizationLogEntity.setDeletedUsers(deletedUsers.toString());
                        synchronizationLogEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        synchronizationLogEntity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        ldapSynchronizationLogRepository.save(synchronizationLogEntity);

                    }
                }

            }
            jobHistoryEntity.setSuccess(true);
        } catch (Exception exception) {
            jobHistoryEntity.setDescription(exception.getMessage());
            jobHistoryEntity.setSuccess(false);
        } finally {
            jobHistoryEntity.setFinishedAt(Timestamp.valueOf(LocalDateTime.now()));
            jobService.saveJob(jobHistoryEntity);
        }

    }

    @Scheduled(fixedDelay = 60000)
    public void synchronizeServiceAccounts() throws Exception {

        JobHistoryEntity jobHistoryEntity = new JobHistoryEntity();
        jobHistoryEntity.setName(SYNCHRONIZED_SERVICE_ACCOUNTS_JOB);
        jobHistoryEntity.setStartedAt(Timestamp.valueOf(LocalDateTime.now()));
        jobHistoryEntity = jobService.saveJob(jobHistoryEntity);

        try {
            for (ServiceEntity service : serviceOps.search(ServiceType.LDAP)) {
                Set<ServiceProperty> properties = service.getProperties();

                String ldapUrl = properties.stream().filter(p -> p.getKey() == ServiceProperties.LDAP_URL).findFirst().get().getValue();
                String baseDn = properties.stream().filter(p -> p.getKey() == ServiceProperties.LDAP_BASE_DN).findFirst().get().getValue();
                String bindDn = properties.stream().filter(p -> p.getKey() == ServiceProperties.LDAP_BIND_DN).findFirst().get().getValue();
                String bindPassword = properties.stream().filter(p -> p.getKey() == ServiceProperties.LDAP_BIND_PASSWORD).findFirst().get().getValue();
                boolean startTLS = properties.stream().filter(p -> p.getKey() == ServiceProperties.LDAP_START_TLS).findFirst().get().getValue().equals("true");
                boolean insecureTLS = properties.stream().filter(p -> p.getKey() == ServiceProperties.LDAP_INSECURE_TLS).findFirst().get().getValue().equals("true");

                LdapTemplateWrapper ldapTemplateWrapper = new LdapTemplateWrapper();
                ldapTemplateWrapper.setLdapSettings(ldapUrl, baseDn, bindDn, bindPassword, startTLS, insecureTLS);
                List<LdapAccount> ldapAccounts = ldapTemplateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapUserAttributes.getObjectClass()), new AccountContextMapper(ldapAccountAttributes));

                for (ConnectionUserEntity connectionUserEntity : connectionUserRepository.findByServiceId(service.getInventoryId())) {
                    boolean isUserExist = false;
                    for (LdapAccount ldapAccount : ldapAccounts) {
                        if (ldapAccount.getUsername().equals(connectionUserEntity.getUsername())) {
                            isUserExist = true;
                            break;
                        }
                    }
                    if (!isUserExist) {
                        int accountId = connectionUserEntity.getId();
                        connectionUserRepository.delete(connectionUserEntity);

                        if (vaultService.isVaultEnabled()) {
                            try {
                                vaultService.doWithVaultUsingRootTokenAndTemplate(template -> {
                                    template.delete(SECRET_INVENTORY + service.getInventoryId() + "/" + accountId);
                                    return null;
                                });

                            } catch (VaultException ve) {
                            }
                        }
                    }
                }

                for (LdapAccount ldapAccount : ldapAccounts) {
                    boolean isUserExist = false;
                    for (ConnectionUserEntity connectionUser : connectionUserRepository.findByServiceId(service.getInventoryId())) {
                        if (connectionUser.getUsername().equals(ldapAccount.getUsername())) {
                            isUserExist = true;
                            break;
                        }
                    }
                    if (!isUserExist) {
                        //restUtil.call("/api/service/account/" + service.getInventoryId(), HttpMethod.POST, ldapAccount, String.class);

                        ConnectionUserEntity connectionUserEntity = new ConnectionUserEntity();
                        connectionUserEntity.setUsername(ldapAccount.getUsername());
                        connectionUserEntity.setServiceEntity(service);
                        connectionUserEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        connectionUserEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        connectionUserEntity.setAdmin(false);
                        connectionUserEntity = connectionUserRepository.save(connectionUserEntity);

                        if (vaultService.isVaultEnabled()) {
                            try {
                                final Credential credential = new Credential();
                                credential.setId(connectionUserEntity.getId());
                                credential.setUsername(ldapAccount.getUsername());
                                credential.setPassword(ldapAccount.getPassword());

                                vaultService.doWithVaultUsingRootTokenAndTemplate(template -> {
                                    template.write(SECRET_INVENTORY + service.getInventoryId() + "/" + credential.getId(), (Object) credential);

                                    return credential;
                                });
                            } catch (VaultException ve) {
                            }
                        }

                    }
                }
            }
            jobHistoryEntity.setSuccess(true);
        } catch (Exception exception) {
            jobHistoryEntity.setDescription(exception.getMessage());
            jobHistoryEntity.setSuccess(false);
        } finally {
            jobHistoryEntity.setFinishedAt(Timestamp.valueOf(LocalDateTime.now()));
            jobService.saveJob(jobHistoryEntity);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void synchronizeLdapUsers() {
        List<UserEntity> userEntities = usersOps.findUsersByExternal(true);
        for (UserEntity userEntity : userEntities) {
            try {
                ldapService.syncLdapUser(userEntity);
            } catch (Exception exception) {
                log.error("Syncing ldap user error: {}", exception.getMessage());
            }
        }
    }

}
