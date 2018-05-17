package com.manish.chushylearn.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MOHIT KHAITAN on 15-09-2016.
 */
public class User implements Parcelable {



    private String NAME;
    private String EMAIL;
    private String PHONE;


    private String profileImageURL;


    public User(){}

    protected User(Parcel in) {
        NAME = in.readString();
        EMAIL = in.readString();
        PHONE = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getNAME(){
        return NAME;
    }

    public void setNAME(String NAME){
        this.NAME = NAME;
    }

    public String getEMAIL(){
        return EMAIL;
    }

    public void setEMAIL(String EMAIL){
        this.EMAIL = EMAIL;
    }

    public String getPhone(){
        return PHONE;
    }

    public void setPHONE(String PHONE){
        this.PHONE = PHONE;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NAME);
        dest.writeString(EMAIL);
        dest.writeString(PHONE);
    }


    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getProfileImageURL() {

        return profileImageURL;
    }



}
