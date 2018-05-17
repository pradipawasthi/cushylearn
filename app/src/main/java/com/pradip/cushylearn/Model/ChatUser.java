package com.pradip.cushylearn.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JohnConnor on 05-Sep-16.
 */
@IgnoreExtraProperties
public class ChatUser {
    private String name;
    private String phone;
    private String email;
    private String profileURL;
    private List<String> groups;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    private List<UserLocation> userLocations = new ArrayList<UserLocation>();

    public List<UserLocation> getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(List<UserLocation> userLocations) {
        this.userLocations = userLocations;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getProfileURL() {
        return this.profileURL;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("phone", phone);
        result.put("email", email);
        result.put("profileURL", profileURL);
        result.put("groups", groups);
        result.put("userLocations", userLocations);

        return result;
    }


}
