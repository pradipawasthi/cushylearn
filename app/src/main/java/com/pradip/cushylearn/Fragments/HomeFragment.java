package com.pradip.cushylearn.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pradip.cushylearn.ApplicationConfig.MyApplication;
import com.pradip.cushylearn.Model.SubjectModel;
import com.pradip.cushylearn.Model.UserLocation;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.helper.Constants;
import com.pradip.cushylearn.util.OnInfoWindowElemTouchListener;
import com.pradip.cushylearn.util.SingletonUtil;
import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment implements OnMapReadyCallback,InfoWindowManager.WindowShowListener, GoogleMap.OnMarkerClickListener
{

    private ViewGroup infoWindow;
    private UiSettings mUiSettings;
    private Rating rating;
    private ImageView img;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;


    //for offer values

    List<String> offerStringList = new ArrayList<String>();
    List<SubjectModel> subjectModelsArray = new ArrayList<SubjectModel>();
    ArrayAdapter<String> adapterOfferModel;


    private GoogleMap mGoogleMap;
    public static View view;
    private Context mContext;
    private LocationManager locationManager;
    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    Location location;
    public static double latitude,longitude;
    LatLng currLocation;
    private final String TAG = this.getClass().getSimpleName();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private JSONObject locOfferMap = new JSONObject();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContext = getActivity();

        if (shouldAskPermissions()) {
            askPermissions();
        }
        ImageView iv=(ImageView) getActivity().findViewById(R.id.add_offer);
        //Spinner spinner1=(Spinner)getActivity().findViewById(R.id.spinner_nav);
        iv.setVisibility(View.INVISIBLE);


//        Spinner spinner1=(Spinner)getActivity().findViewById(R.id.spinner_nav);
//        spinner1.setVisibility(View.GONE);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            shortRoidPreferences=new ShortRoidPreferences(getActivity(),"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (status != ConnectionResult.SUCCESS) {

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();
        } else {
            try {

                view = inflater.inflate(R.layout.fragment_home, container, false);
                initializeMap();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public void initializeMap(){
        if (mGoogleMap == null) {

            MapInfoWindowFragment mapFragment = (MapInfoWindowFragment) getChildFragmentManager().findFragmentById(R.id.map);
            infoWindowManager = mapFragment.infoWindowManager();
            infoWindowManager.setHideOnFling(true);
            mapFragment.getMapAsync(this);

        }
    }
    JSONObject userLocationsJson, userlatlng;
    ShortRoidPreferences shortRoidPreferences;

    /**
     * Fetches User locations
     */
    private void fetchUserLocationsAndShowInMap() {
        final FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference databaseReference = database.getReference(Constants.LOCATION_FOLDER_NAME);
        userLocationsJson = new JSONObject();

        try {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("datasnap", dataSnapshot.toString() + databaseReference.toString());
                    Iterator childs = dataSnapshot.getChildren().iterator();


                    while (childs.hasNext()) {
                        DataSnapshot obj = (DataSnapshot) childs.next();

                        Object locKey = obj.getValue();

                        try {
                            UserLocation tmpLoc = obj.getValue(UserLocation.class);

                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userKey", tmpLoc.getAddedByKey());
                            jsonObject.put("latitude", tmpLoc.getLatitude());
                            jsonObject.put("longitude", tmpLoc.getLongitude());
                            jsonObject.put("namePlace", tmpLoc.getNamePlace());
                            jsonObject.put("placeDetails", tmpLoc.getPlaceDetails());
                            jsonObject.put("rating", tmpLoc.getRating());
                            jsonObject.put("category", tmpLoc.getCategory());
                            jsonObject.put("feedback", tmpLoc.getFeedback());
                            jsonObject.put("imageURL", tmpLoc.getImageURL());
                            jsonObject.put("locationId", tmpLoc.getLocationId());

                            final String locId = tmpLoc.getLocationId();

                            database.getReference(Constants.OFFER_FOLDER_NAME)
                                    .orderByChild("locationId")
                                    .equalTo(locId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot offersSnapshot) {
                                            List<JSONObject> offerList = new ArrayList<JSONObject>();
                                            Iterator childOffers = offersSnapshot.getChildren().iterator();

                                            try {
                                                while (childOffers.hasNext()) {
                                                    DataSnapshot childOffer = (DataSnapshot) childOffers.next();
                                                    Log.d(TAG,childOffer.getValue().toString());
                                                    SubjectModel subjectModel = childOffer.getValue(SubjectModel.class);
                                                    JSONObject offerObj = new JSONObject();

                                                    offerObj.put("offername", subjectModel.getSubject_Name());
                                                    offerObj.put("offerid", subjectModel.getSubjectId());
                                                    offerList.add(offerObj);

                                                }
                                                locOfferMap.put(locId,offerList);
                                                jsonObject.put("offers", offerList);
                                                SingletonUtil.getInstance().updateLocation(locId,jsonObject);
                                                SingletonUtil.getInstance().addOfferList(locId,offerList);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
//
                            userLocationsJson.put(tmpLoc.getLocationId(),jsonObject);
                            SingletonUtil.getInstance().setLocationArray(userLocationsJson);
                        } catch (Exception e) {
                            Log.d(TAG, locKey.toString());
                            e.printStackTrace();
                        }
                    }
                    addMarkerToMap(userLocationsJson);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    void addMarkerToMap(JSONObject locationArray)
    {
        try {
            Iterator<String> locIdKeys = locationArray.keys();
//            JSONArray jsonArray=new JSONArray(shortRoidPreferences.getPrefString("pradip"));
            while(locIdKeys.hasNext())
            {
                String locId = locIdKeys.next();
                JSONObject jsonObject=locationArray.getJSONObject(locId);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(jsonObject.getDouble("latitude"),jsonObject.getDouble("longitude")));
                markerOptions.title(jsonObject.getString("namePlace"));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                markerOptions.visible(true);

                Marker marker = mGoogleMap.addMarker(markerOptions);

                final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);

                final InfoWindow.MarkerSpecification markerSpec =
                        new InfoWindow.MarkerSpecification(offsetX, offsetY);

                formWindow = new InfoWindow(marker, markerSpec, new FormFragment());

                marker.setTag(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Request Code "+requestCode);
        switch (requestCode) {

            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }
    private InfoWindow recyclerWindow;
    private InfoWindow formWindow;
    private InfoWindowManager infoWindowManager;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        String locText = "My Current Location";
        //Set UiSettings of the Map
        setUiSettings();
        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            //RB changes
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (!(permissionCheck == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                //RB changes

                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    currLocation = new LatLng(latitude, longitude);
                }
            }


            if(currLocation == null) {
                locText = "APJ Abdul Kalam Block, CSE Department, NIT Agartala";

               // currLocation = new LatLng(12.981483, 77.682753); //nit agartala
                currLocation = new LatLng(20.5937, 78.9629);
            }

            mGoogleMap.addMarker(new MarkerOptions().position(currLocation).title(locText).visible(false)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gplus)));
            fetchUserLocationsAndShowInMap();
            infoWindowManager.setWindowShowListener(this);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(currLocation).zoom(15).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initializeMap();
    }

    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
        final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
        final InfoWindow.MarkerSpecification markerSpec =
                new InfoWindow.MarkerSpecification(offsetX, offsetY);

        JSONObject jsonObject = (JSONObject)marker.getTag();
        Bundle bundl = new Bundle();
        bundl.putString("location", jsonObject.toString());
        FormFragment formFragment = new FormFragment();
        formFragment.setArguments(bundl);

        formWindow = new InfoWindow(marker, markerSpec, formFragment);
        InfoWindow infoWindow = formWindow;
        if (infoWindow != null) {
            infoWindowManager.toggle(infoWindow, true);
        }
        return true;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    private  void setUiSettings() {
        mUiSettings = mGoogleMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
//        mUiSettings.setCompassEnabled(isChecked(R.id.compass_toggle));
        mUiSettings.setMyLocationButtonEnabled(true);
//        mGoogleMap.setMyLocationEnabled(isChecked(R.id.mylocationlayer_toggle));
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(24)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }




}