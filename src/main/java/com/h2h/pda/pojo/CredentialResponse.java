package com.h2h.pda.pojo;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.PermissionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CredentialResponse implements EntityToDTO<CredentialResponse, CredentialEntity> {

    private CredentialDetails credential;
    private List<BreakTheGlassParams> breakTheGlassList;
    private List<PermissionParams> permissionList;
    private List<BreakTheGlassShareParams> shareList;
    private AutoCredentialSettingsParams autoCredentialSettingsParams;
    private PermissionEntity permissionEntity;

    public CredentialDetails getCredential() {
        return credential;
    }

    public void setCredential(CredentialDetails credential) {
        this.credential = credential;
    }

    public List<BreakTheGlassParams> getBreakTheGlassList() {
        return breakTheGlassList;
    }

    public void setBreakTheGlassList(List<BreakTheGlassParams> breakTheGlassList) {
        this.breakTheGlassList = breakTheGlassList;
    }

    public List<PermissionParams> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<PermissionParams> permissionList) {
        this.permissionList = permissionList;
    }

    public List<BreakTheGlassShareParams> getShareList() {
        return shareList;
    }

    public void setShareList(List<BreakTheGlassShareParams> shareList) {
        this.shareList = shareList;
    }

    public AutoCredentialSettingsParams getAutoCredentialSettingsParams() {
        return autoCredentialSettingsParams;
    }

    public void setAutoCredentialSettingsParams(AutoCredentialSettingsParams autoCredentialSettingsParams) {
        this.autoCredentialSettingsParams = autoCredentialSettingsParams;
    }

    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public void setPermissionEntity(PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }

    @Override
    public CredentialResponse wrap(CredentialEntity entity) {
        setCredential(new CredentialDetails().wrap(entity));
        setBreakTheGlassList(entity.getBreakTheGlassEntities().stream().map(b -> new BreakTheGlassParams().wrap(b)).collect(Collectors.toList()));
        setPermissionList(entity.getPermissionEntities().stream().map(p -> new PermissionParams().wrap(p)).collect(Collectors.toList()));
        setAutoCredentialSettingsParams(new AutoCredentialSettingsParams().wrap(entity.getAutoCredantialSettingsEntity()));
        if (!entity.isCheckStatus()) {
            setShareList(entity.getBreakTheGlassShareEntities().stream().map(s -> new BreakTheGlassShareParams().wrap(s)).collect(Collectors.toList()));
        }
        return this;
    }

    @Override
    public CredentialEntity unWrap() {
        return null;
    }
}
