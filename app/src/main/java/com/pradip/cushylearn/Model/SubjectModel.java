package com.pradip.cushylearn.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rbaisak on 27/2/17.
 */

public class SubjectModel {

    String subject_Name;
    String description;

    String currentUserId;
    Double lat;

    Double lng;
    String address;
    String locationName;
    private String createdAt;

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    private String lessonName;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }




    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    private String locationId;

    public AddrsModel getAddrs() {
        return addrs;
    }

    public void setAddrs(AddrsModel addrs) {
        this.addrs = addrs;
    }

    private AddrsModel addrs;                     //select location
//    private double latitude;
//    private double longitude;

    private String ClassesURI;

    private List<ModuleModel> topicslist;



    String subjectId;
                                                                    //select location



    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }




    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }




    //ArrayList<ItemNameList> itemsName=new ArrayList<>();

    public List<ModuleModel> getTopicslist() {
        return topicslist;
    }

    public void setTopicslist(List<ModuleModel> topicslist) {
        this.topicslist = topicslist;
    }




    public void setSubject_Name(String subject_Name) {
        this.subject_Name = subject_Name;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject_Name() {
        return subject_Name;
    }


    public String getDescription() {
        return description;
    }


    public String getClassesURI() {
        return ClassesURI;
    }

    public void setClassesURI(String classesURI) {
        ClassesURI = classesURI;
    }




    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("subject_Name", subject_Name);
        result.put("createdAt", createdAt);
        result.put("address", address);
        result.put("description", description);
        result.put("subjectId", subjectId);
        result.put("lat", lat);
        result.put("lng", lng);
//        result.put("topicslist", topicslist);
        result.put("currentUserId", currentUserId);
        result.put("ClassesURI", ClassesURI);
        result.put("addrs", addrs);                                           //select location
        result.put("locationId", locationId);
        result.put("locationName", locationName);
        result.put("lessonName", lessonName);

        return result;
    }


}
