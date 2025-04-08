package com.h2h.pda.map;

import com.h2h.pda.pojo.ldap.LdapDevice;
import com.h2h.pda.pojo.ldap.LdapDeviceAttributes;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

public class DeviceContextMapper implements ContextMapper<LdapDevice> {

    private final LdapDeviceAttributes ldapDeviceAttributes;

    public DeviceContextMapper(LdapDeviceAttributes ldapDeviceAttributes) {
        this.ldapDeviceAttributes = ldapDeviceAttributes;
    }

    @Override
    public LdapDevice mapFromContext(Object ctx) {

        DirContextAdapter context = (DirContextAdapter)ctx;
        LdapDevice ldapDevice = new LdapDevice();
        ldapDevice.setName(context.getStringAttribute(ldapDeviceAttributes.getName()));
        ldapDevice.setDescription(context.getStringAttribute(ldapDeviceAttributes.getDescription()));
        ldapDevice.setDistinguishedName(context.getStringAttribute(ldapDeviceAttributes.getDistinguishedName()));
        ldapDevice.setRdn(context.getDn().toString());
        return ldapDevice;
    }
}