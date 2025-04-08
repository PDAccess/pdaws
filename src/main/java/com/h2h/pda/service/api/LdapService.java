package com.h2h.pda.service.api;

import com.h2h.pda.config.LdapTemplateWrapper;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.ldap.*;
import org.springframework.ldap.NamingException;

import java.util.List;

public interface LdapService {

    void saveLdapSettings(LdapBaseSetting ldapBaseSetting) throws Exception;

    boolean checkLdapCredentials(LdapCredential ldapCredential) throws NamingException;

    List<LdapGroup> getLdapGroups() throws Exception;

    String saveLdapGroup(LdapGroup ldapGroup) throws Exception;

    List<LdapEntry> getLdapEntries(String objectClass, String filter) throws Exception;

    void saveLdapEntries(List<LdapEntry> ldapEntries);

    List<LdapAccount> getLdapAccounts(LdapBaseSetting baseSetting) throws Exception;

    LdapTemplateWrapper getTemplateWrapper();

    void syncLdapUser(UserEntity userEntity) throws Exception;

}
