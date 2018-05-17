package com.manish.chushylearn.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by rbaisak on 12/20/16.
 */
public class SingletonUtil {
    private static SingletonUtil ourInstance = null;


    private double latitude;
    private  double longitude;


    public static SingletonUtil getInstance() {
        if(ourInstance == null){
            ourInstance = new SingletonUtil();
        }
        return ourInstance;
    }

    private SingletonUtil() {

    }
/*
    public String getAddressFromLatLang(double lat,double lng){


        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(requrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
**********/
    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }


    public JSONObject getLocationArray() {
        return locationArray;
    }

    public void setLocationArray(JSONObject locationArray) {
        this.locationArray = locationArray;
    }

    public JSONObject locationArray;

    public void updateLocation(String locId,JSONObject jsonObject){
        try {
            locationArray.put(locId,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<JSONObject>> getLocOfferMap() {
        return locOfferMap;
    }

    public void setLocOfferMap(Map locOfferMap) {
        this.locOfferMap = locOfferMap;
    }
    public void addOfferList(String locId, List<JSONObject> offers){
        locOfferMap.put(locId,offers);
    }
    public List<JSONObject> getOffers(String locId){
        return locOfferMap.get(locId);
    }
    private Map<String, List<JSONObject>> locOfferMap = new HashMap<>();

    public boolean checkPermission(String permissionType, Activity activity){
        return (ContextCompat.checkSelfPermission(activity,permissionType)== PackageManager.PERMISSION_GRANTED);
    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String dateTimeFromMilli(long timeinMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeinMillis));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//Here you say to java the initial timezone. This is the secret
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//Will print in UTC
        System.out.println(sdf.format(calendar.getTime()));

//Here you set to your timezone
        sdf.setTimeZone(TimeZone.getDefault());
//Will print on your default Timezone
        System.out.println(sdf.format(calendar.getTime()));

        return sdf.format(calendar.getTime());
    }
}
