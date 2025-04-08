package com.h2h.pda.pojo.group;

public class GroupsService {
    private String groupName;
    private String serviceName;
    private Id id;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public static class Id {
        private String groupid;
        private String serviceid;

        public Id() {
        }

        public Id(String groupid, String serviceid) {
            this.groupid = groupid;
            this.serviceid = serviceid;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getServiceid() {
            return serviceid;
        }

        public void setServiceid(String serviceid) {
            this.serviceid = serviceid;
        }
    }
}

