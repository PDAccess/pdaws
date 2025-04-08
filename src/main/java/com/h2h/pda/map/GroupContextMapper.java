package com.h2h.pda.map;

import com.h2h.pda.pojo.ldap.LdapGroup;
import com.h2h.pda.pojo.ldap.LdapGroupAttributes;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

public class GroupContextMapper implements ContextMapper<LdapGroup> {

    private final LdapGroupAttributes ldapGroupAttributes;

    public GroupContextMapper(LdapGroupAttributes ldapGroupAttributes) {
        this.ldapGroupAttributes = ldapGroupAttributes;
    }

    @Override
    public LdapGroup mapFromContext(Object ctx) {

        DirContextAdapter context = (DirContextAdapter)ctx;
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(context.getStringAttribute(ldapGroupAttributes.getName()));
        ldapGroup.setDescription(context.getStringAttribute(ldapGroupAttributes.getDescription()));
        ldapGroup.setRdn(context.getDn().toString());
        ldapGroup.setDistinguishedName(context.getStringAttribute(ldapGroupAttributes.getDistinguishedName()));
        return ldapGroup;
    }
}