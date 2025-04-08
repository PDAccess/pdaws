package com.h2h.pda.map;

import com.h2h.pda.pojo.ldap.LdapUser;
import com.h2h.pda.pojo.ldap.LdapUserAttributes;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

public class UserContextMapper implements ContextMapper<LdapUser> {

    private final LdapUserAttributes ldapUserAttributes;

    public UserContextMapper(LdapUserAttributes ldapUserAttributes) {
        this.ldapUserAttributes = ldapUserAttributes;
    }

    @Override
    public LdapUser mapFromContext(Object ctx) {

        DirContextAdapter context = (DirContextAdapter)ctx;
        LdapUser ldapUser = new LdapUser();
        ldapUser.setUsername(context.getStringAttribute(ldapUserAttributes.getUsername()));
        ldapUser.setFirstName(context.getStringAttribute(ldapUserAttributes.getFirstName()));
        ldapUser.setLastName(context.getStringAttribute(ldapUserAttributes.getLastName()));
        ldapUser.setMail(context.getStringAttribute(ldapUserAttributes.getMail()));
        ldapUser.setTelephoneNumber(context.getStringAttribute(ldapUserAttributes.getTelephoneNumber()));
        ldapUser.setDistinguishedName(context.getStringAttribute(ldapUserAttributes.getDistinguishedName()));
        return ldapUser;
    }
}