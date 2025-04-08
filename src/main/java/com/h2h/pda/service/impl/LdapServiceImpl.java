package com.h2h.pda.service.impl;

import com.h2h.pda.config.LdapTemplateWrapper;
import com.h2h.pda.entity.*;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.map.*;
import com.h2h.pda.pojo.group.GroupCategory;
import com.h2h.pda.pojo.group.GroupMembership;
import com.h2h.pda.pojo.ldap.*;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.repository.ActionRepository;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.LdapService;
import com.h2h.pda.service.api.ServiceOps;
import com.h2h.pda.service.api.UsersOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.directory.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapServiceImpl implements LdapService {

    private Logger log = LoggerFactory.getLogger(LdapServiceImpl.class);

    public static final String GROUP_CLASS = "group";
    public static final String PERSON_CLASS = "person";
    public static final String DEVICE_CLASS = "device";
    public static final String FILTER_REGEX = "*%s*";

    @Autowired
    LdapTemplateWrapper templateWrapper;

    @Autowired
    LdapUtil ldapUtil;

    @Autowired
    LdapGroupAttributes ldapGroupAttributes;

    @Autowired
    LdapUserAttributes ldapUserAttributes;

    @Autowired
    LdapCommonAttributes ldapCommonAttributes;

    @Autowired
    LdapDeviceAttributes ldapDeviceAttributes;

    @Autowired
    LdapAccountAttributes ldapAccountAttributes;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    UsersOps usersOps;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ServiceOps serviceOps;

    @Override
    public void saveLdapSettings(LdapBaseSetting ldapBaseSetting) throws Exception {
        templateWrapper.setLdapSettings(ldapBaseSetting.getUrl(), ldapBaseSetting.getBase(), ldapBaseSetting.getUserDn(), ldapBaseSetting.getPassword(), ldapBaseSetting.getStartTLS(), ldapBaseSetting.getInsecureTLS());
    }

    @Override
    public boolean checkLdapCredentials(LdapCredential ldapCredential) throws NamingException {

        LdapContextSource sourceLdapCtx = new LdapContextSource();
        sourceLdapCtx.setUrl((ldapCredential.getSsl() ? "ldaps://" : "ldap://") + ldapCredential.getHost() + ":" + ldapCredential.getPort());
        sourceLdapCtx.setUserDn(ldapCredential.getBindDN());
        sourceLdapCtx.setPassword(ldapCredential.getBindPass());
        sourceLdapCtx.setDirObjectFactory(DefaultDirObjectFactory.class);

        if (ldapCredential.getStartTLS()) {
            DefaultTlsDirContextAuthenticationStrategy authenticationStrategy = new DefaultTlsDirContextAuthenticationStrategy();
            authenticationStrategy.setShutdownTlsGracefully(true);
            authenticationStrategy.setHostnameVerifier((hostname, session) -> true);
            if (ldapCredential.getInsecureTLS()) {
                authenticationStrategy.setSslSocketFactory(ldapUtil.trustSelfSignedSSL());
            }
            sourceLdapCtx.setAuthenticationStrategy(authenticationStrategy);
        }

        sourceLdapCtx.afterPropertiesSet();
        LdapTemplate template = new LdapTemplate(sourceLdapCtx);
        template.getContextSource().getContext(ldapCredential.getBindDN(), ldapCredential.getBindPass());

        return true;
    }

    @Override
    public List<LdapGroup> getLdapGroups() throws Exception {
        List<LdapGroup> ldapGroups = templateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where("objectclass").is(ldapGroupAttributes.getObjectClass()),
                new GroupContextMapper(ldapGroupAttributes));;
        return ldapGroups;
    }

    @Override
    public String saveLdapGroup(LdapGroup ldapGroup) throws Exception {
        UserEntity user = usersOps.securedUser();

        DirContextAdapter context;
        context = (DirContextAdapter) templateWrapper.getLdapTemplate().lookup(ldapGroup.getRdn());

        Attributes attributes =  context.getAttributes();
        Attribute attribute = attributes.get(ldapGroupAttributes.getMemberAttribute());

        GroupsEntity groupsEntity = new GroupsEntity();

        groupsEntity.setGroupName(ldapGroup.getName());
        groupsEntity.setDescription(ldapGroup.getDescription());
        groupsEntity.setGroupCategory(GroupCategory.LDAP);
        groupsEntity.setLdapRdn(ldapGroup.getRdn());

        if (StringUtils.hasText(ldapGroup.getParentGroup())) {
            Optional<GroupsEntity> optionalParentGroup = groupOps.byId(ldapGroup.getParentGroup());
            optionalParentGroup.ifPresent(groupsEntity::setParent);
        }

        String groupId = groupOps.newGroup(groupsEntity, user);

        StringBuilder createdUsers = new StringBuilder();
        StringBuilder addedUsers = new StringBuilder();

        if (attribute != null) {
            for (int i = 0; i < attribute.size(); i++) {

                try {
                    String rdn = ldapUtil.getRdn((String) attribute.get(i));

                    DirContextAdapter userContext = (DirContextAdapter) templateWrapper.getLdapTemplate().lookup(rdn);
                    Attributes userAttributes = userContext.getAttributes();
                    Optional<UserEntity> byName = usersOps.byName((String) userAttributes.get(ldapUserAttributes.getUsername()).get());
                    UserEntity userEntity;
                    if (!byName.isPresent()) {
                        userEntity = new UserEntity();
                        userEntity.setFirstName(ldapUtil.getLdapAttributes(userAttributes, ldapUserAttributes.getFirstName()));
                        userEntity.setLastName(ldapUtil.getLdapAttributes(userAttributes, ldapUserAttributes.getLastName()));
                        userEntity.setUsername(ldapUtil.getLdapAttributes(userAttributes, ldapUserAttributes.getUsername()));
                        userEntity.setEmail(ldapUtil.getLdapAttributes(userAttributes, ldapUserAttributes.getMail()));
                        userEntity.setPhone(ldapUtil.getLdapAttributes(userAttributes, ldapUserAttributes.getTelephoneNumber()));
                        userEntity.setExternal(true);
                        userEntity.setRole(UserRole.USER);
                        userEntity.setTwofactorauth(false);
                        userEntity.setLdapDn(ldapUtil.getLdapAttributes(userAttributes, ldapCommonAttributes.getDistinguishedName()));
                        userEntity = usersOps.newUser(userEntity);
                        createdUsers.append(userEntity.getUsername()).append(",");
                    } else {
                        userEntity = byName.get();
                    }

                    groupOps.addUsersTo(groupId, Collections.singletonList(userEntity.getUserId()), GroupMembership.LDAP);
                    addedUsers.append(userEntity.getUsername()).append(",");

                } catch (Exception exception) {
                    log.error("LDAP job error", exception);
                }
            }


            // TODO: this logging logic should be made in jms
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();
            if (createdUsers.length() != 0) {
                ActionEntity actionEntity = new ActionEntity();
                actionEntity.setProxyAction(createdUsers.append(" ldap users are created").toString());
                actionEntity.setActionTime(Timestamp.valueOf(LocalDateTime.now()));
                actionEntity.setSessionId(0);
                actionRepository.save(actionEntity);
            }

            if (addedUsers.length() != 0) {
                ActionEntity actionEntity = new ActionEntity();
                actionEntity.setProxyAction(addedUsers.append(" users are added to ").append(groupsEntity.getGroupName()).append(" ldap group").toString());
                actionEntity.setActionTime(Timestamp.valueOf(LocalDateTime.now()));
                actionEntity.setSessionId(0);
                actionRepository.save(actionEntity);
            }
            // end of TODO:

            return groupId;
        } else {
            return groupId;
        }
    }

    @Override
    public List<LdapEntry> getLdapEntries(String objectClass, String filter) throws Exception {
        if (templateWrapper == null || templateWrapper.getLdapTemplate() == null) {
            log.warn("There is no ldap configuration");
            return null;
        }

        List<LdapEntry> ldapEntries = templateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).like(String.format(FILTER_REGEX, objectClass)).and(ldapCommonAttributes.getName()).like(String.format(FILTER_REGEX, filter)),
                new EntryContextMapper(ldapCommonAttributes, ldapGroupAttributes, ldapUserAttributes, ldapDeviceAttributes));
        return ldapEntries;
    }

    @Override
    public void saveLdapEntries(List<LdapEntry> ldapEntries) {
        UserEntity user = usersOps.securedUser();

        for (LdapEntry ldapEntry:ldapEntries) {
            switch (ldapEntry.getObjectClass()) {
                case GROUP_CLASS:
                    List<LdapGroup> ldapGroups = new ArrayList<>();
                    try {
                        ldapGroups = templateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapGroupAttributes.getObjectClass()).and(ldapCommonAttributes.getDistinguishedName()).is(ldapEntry.getDistinguishedName()),
                                new GroupContextMapper(ldapGroupAttributes));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    for (LdapGroup ldapGroup:ldapGroups) {
                        GroupsEntity groupsEntity = new GroupsEntity();

                        groupsEntity.setGroupName(ldapGroup.getName());
                        groupsEntity.setDescription(ldapGroup.getDescription());
                        groupsEntity.setLdapDn(ldapGroup.getDistinguishedName());
                        groupsEntity.setGroupCategory(GroupCategory.LDAP);

                        groupOps.newGroup(groupsEntity, user);
                        //groupOps.addUsersTo(groupsEntity.getGroupId(), Collections.singletonList(user.getUserId()), GroupMembership.LDAP);
                    }
                    break;
                case PERSON_CLASS:
                    List<LdapUser> ldapUsers = new ArrayList<>();
                    try {
                        ldapUsers = templateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapUserAttributes.getObjectClass()).and(ldapCommonAttributes.getDistinguishedName()).is(ldapEntry.getDistinguishedName()),
                                new UserContextMapper(ldapUserAttributes));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    for (LdapUser ldapUser:ldapUsers) {
                        UserEntity userEntity = new UserEntity();
                        userEntity.setUsername(ldapUser.getUsername());
                        userEntity.setFirstName(ldapUser.getFirstName());
                        userEntity.setLastName(ldapUser.getLastName());
                        userEntity.setEmail(ldapUser.getMail());
                        userEntity.setPhone(ldapUser.getTelephoneNumber());
                        userEntity.setLdapDn(ldapUser.getDistinguishedName());
                        userEntity.setTwofactorauth(false);
                        usersOps.newUser(userEntity);
                    }
                    break;
                case DEVICE_CLASS:
                    List<LdapDevice> ldapDevices = new ArrayList<>();
                    try {
                        ldapDevices = templateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapDeviceAttributes.getObjectClass()).and(ldapCommonAttributes.getDistinguishedName()).is(ldapEntry.getDistinguishedName()),
                                new DeviceContextMapper(ldapDeviceAttributes));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    for (LdapDevice ldapDevice:ldapDevices) {
                        ServiceEntity serviceEntity = new ServiceEntity();
                        serviceEntity.setInventoryId(UUID.randomUUID().toString());
                        serviceEntity.setName(ldapDevice.getName());
                        serviceEntity.setDescription(ldapDevice.getDescription());
                        serviceEntity.setLdapDn(ldapDevice.getDistinguishedName());
                        serviceEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        serviceEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        serviceEntity.setWhoCreate(user.getUserId());
                        serviceEntity.setWhoUpdate(user.getUserId());
                        serviceOps.createOrUpdate(serviceEntity);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<LdapAccount> getLdapAccounts(LdapBaseSetting baseSetting) throws Exception {
        LdapTemplateWrapper ldapTemplateWrapper = new LdapTemplateWrapper();
        ldapTemplateWrapper.setLdapSettings(baseSetting.getUrl(), baseSetting.getBase(), baseSetting.getUserDn(), baseSetting.getPassword(), baseSetting.getStartTLS(), baseSetting.getInsecureTLS());
        return ldapTemplateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapUserAttributes.getObjectClass()), new AccountContextMapper(ldapAccountAttributes));
    }

    @Override
    public LdapTemplateWrapper getTemplateWrapper() {
        return templateWrapper;
    }

    @Override
    public void syncLdapUser(UserEntity userEntity) throws Exception {
        if (userEntity != null && userEntity.isExternal()) {
            List<LdapUser> ldapUsers = templateWrapper.getLdapTemplate().search(query().searchScope(SearchScope.SUBTREE).where(ldapCommonAttributes.getObjectClass()).is(ldapUserAttributes.getObjectClass()).and(query().where(ldapCommonAttributes.getDistinguishedName()).is(userEntity.getLdapDn()).or(ldapUserAttributes.getUsername()).is(userEntity.getUsername())), new UserContextMapper(ldapUserAttributes));
            if (!ldapUsers.isEmpty()) {
                LdapUser ldapUser = ldapUsers.get(0);
                userEntity.setUsername(ldapUser.getUsername());
                userEntity.setFirstName(ldapUser.getFirstName());
                userEntity.setLastName(ldapUser.getLastName());
                userEntity.setEmail(ldapUser.getMail());
                userEntity.setPhone(ldapUser.getTelephoneNumber());
                userEntity.setLdapDn(ldapUser.getDistinguishedName());
                usersOps.update(userEntity);
            }
        }
    }

}
