package com.h2h.pda.map;

import com.h2h.pda.pojo.ldap.LdapAccount;
import com.h2h.pda.pojo.ldap.LdapAccountAttributes;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

public class AccountContextMapper implements ContextMapper<LdapAccount> {

    private final LdapAccountAttributes ldapAccountAttributes;

    public AccountContextMapper(LdapAccountAttributes ldapAccountAttributes) {
        this.ldapAccountAttributes = ldapAccountAttributes;
    }

    @Override
    public LdapAccount mapFromContext(Object ctx) {

        DirContextAdapter context = (DirContextAdapter)ctx;
        LdapAccount ldapAccount = new LdapAccount();
        ldapAccount.setUsername(context.getStringAttribute(ldapAccountAttributes.getUsername()));
        ldapAccount.setPassword(context.getStringAttribute(ldapAccountAttributes.getPassword()));
        return ldapAccount;
    }
}