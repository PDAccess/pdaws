package com.h2h.pda.pojo.policy;

import java.util.List;

public class PolicyDAO {
    private String behavior;
    private List<String> commands;

    public PolicyDAO(String behavior) {
        this.behavior = behavior;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
