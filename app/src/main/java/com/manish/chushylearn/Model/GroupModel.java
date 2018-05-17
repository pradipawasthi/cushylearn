package com.manish.chushylearn.Model;

import java.util.List;


public class GroupModel {
    private String gName;
    private boolean gMode;
    private String gMessage;
    List<String> gMembers;
    Chat gMessages;

    public String getgMessage() {
        return gMessage;
    }

    public void setgMessage(String gMessage) {
        this.gMessage = gMessage;
    }

    public Chat getgMessages() {
        return gMessages;
    }

    public void setgMessages(Chat gMessages) {
        this.gMessages = gMessages;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public boolean isgMode() {
        return gMode;
    }

    public void setgMode(boolean gMode) {
        this.gMode = gMode;
    }

    public List<String> getgMembers() {
        return gMembers;
    }

    public void setgMembers(List<String> gMembers) {
        this.gMembers = gMembers;
    }
}
