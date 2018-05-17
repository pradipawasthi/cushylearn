package com.pradip.cushylearn.Activities;

import android.widget.CheckBox;

/**
 * Created by SID on 14-Dec-16.
 */


public class UserNotification {
    private String distance;
    private String category;
    private String status;

    public UserNotification(){

    }

    public UserNotification(String distance ,String category, String status) {
        this.distance=distance;
        this.category=category;
        this.status = status;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

