package com.pradip.cushylearn.Model;

/**
 * Created by JohnConnor on 03-Sep-16.
 */
public class Chat {
    private String chatText;
    private long timestamp;
    private String name;
    private String phone;
    public Chat()
    {

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
