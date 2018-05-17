package com.manish.chushylearn.Model;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserLocation  {
    private double latitude;
    private double longitude;
    private float rating;
    private String category;
    private String namePlace;
    private String feedback;
    private String placeDetails;
    private String addedByKey;
    private String LocationId;
    private String imageURL;
    private String value;
    private  AddrsModel addr;

    public AddrsModel getAddr() {
        return addr;
    }

    public void setAddr(AddrsModel addr) {
        this.addr = addr;
    }

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public UserLocation() {

    }
    public UserLocation(double latitude, double longitude, float rating, String category, String namePlace, String feedback, String placeDetails, String addedByKey, String imageURL, String LocationId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.category = category;
        this.namePlace = namePlace;
        this.feedback = feedback;
        this.placeDetails = placeDetails;
        this.addedByKey = addedByKey;
        this.imageURL = imageURL;
        this.LocationId = LocationId;

    }



    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latobj) {
        String str = latobj.toString();
        this.latitude = Double.parseDouble(str);

    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longObj) {
        String str = longObj.toString();
        this.longitude = Double.parseDouble(str);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNamePlace() {
        return namePlace;
    }

    public void setNamePlace(String namePlace) {
        this.namePlace = namePlace;
    }

    public String getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(String placeDetails) {
        this.placeDetails = placeDetails;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public String getAddedByKey() {
        return addedByKey;
    }

    public void setAddedByKey(String addedByKey) {
        this.addedByKey = addedByKey;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }



    public void setValue(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Exclude
    public Map<String, Object> toMapUserLocation() {
        Map<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("namePlace", namePlace);
//        result.put("address", address);
        result.put("feedback", feedback);
        result.put("rating", rating);
//        result.put("latitude", latitude);
        result.put("placeDetails", placeDetails);
        result.put("imageURL", imageURL);
        result.put("category", category);
        result.put("addedByKey", addedByKey);
        result.put("LocationId", LocationId);
        result.put("Adress", addr);


        return result;
    }

    public String toString(){
        return namePlace;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("rating", rating);
        result.put("category", category);
        result.put("namePlace", namePlace);
        result.put("feedback", feedback);
        result.put("placeDetails", placeDetails);
        result.put("addedByKey", addedByKey);
        result.put("LocationId", LocationId);
        result.put("imageURL", imageURL);
        result.put("value", value);
        result.put("addr", addr.toMap());

        return result;
    }

}
