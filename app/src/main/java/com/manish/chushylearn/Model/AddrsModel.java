package com.manish.chushylearn.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rbaisak on 21/3/17.
 */

public class AddrsModel {
    String address;
    double latitude;
    double longitude;

    public double getLatitude(double latitude) {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude(double longitude) {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }




    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

}
