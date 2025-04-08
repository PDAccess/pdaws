package com.h2h.pda.map;

import com.h2h.pda.pojo.ldap.*;
import com.h2h.pda.service.impl.LdapServiceImpl;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.NamingException;

public class EntryContextMapper implements ContextMapper<LdapEntry> {

    private final LdapCommonAttributes ldapCommonAttributes;
    private final LdapGroupAttributes ldapGroupAttributes;
    private final LdapUserAttributes ldapUserAttributes;
    private final LdapDeviceAttributes ldapDeviceAttributes;

    public EntryContextMapper(LdapCommonAttributes ldapCommonAttributes, LdapGroupAttributes ldapGroupAttributes, LdapUserAttributes ldapUserAttributes, LdapDeviceAttributes ldapDeviceAttributes) {
        this.ldapCommonAttributes = ldapCommonAttributes;
        this.ldapGroupAttributes = ldapGroupAttributes;
        this.ldapUserAttributes = ldapUserAttributes;
        this.ldapDeviceAttributes = ldapDeviceAttributes;
    }

    @Override
    public LdapEntry mapFromContext(Object ctx) throws NamingException {

        DirContextAdapter context = (DirContextAdapter) ctx;

        LdapEntry entry = new LdapEntry();
        entry.setName(context.getStringAttribute(ldapCommonAttributes.getName()));
        entry.setDistinguishedName(context.getStringAttribute(ldapCommonAttributes.getDistinguishedName()));

        String[] objectClasses = context.getStringAttributes(ldapCommonAttributes.getObjectClass());
        if (containsObjectClass(objectClasses, ldapGroupAttributes.getObjectClass())) {
            entry.setObjectClass(LdapServiceImpl.GROUP_CLASS);
        } else if (containsObjectClass(objectClasses, ldapUserAttributes.getObjectClass())) {
            entry.setObjectClass(LdapServiceImpl.PERSON_CLASS);
        } else if (containsObjectClass(objectClasses, ldapDeviceAttributes.getObjectClass())) {
            entry.setObjectClass(LdapServiceImpl.DEVICE_CLASS);
        }

        return entry;
    }

    private Boolean containsObjectClass(String[] objectClasses, String attributeClass) {
        for (String objectClass:objectClasses) {
            if (objectClass.contains(attributeClass)) {
                return true;
            }
        }
        return false;
    }
}