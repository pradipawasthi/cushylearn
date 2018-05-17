package com.pradip.cushylearn.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pradip.cushylearn.R;
import com.pradip.cushylearn.helper.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

import static android.content.Context.MODE_PRIVATE;


public class SelectLocationFromMapFragment  extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private UiSettings mUiSettings;
    private static View view;
    Button selectLocationButton, search;

//    public static final String TAG =SelectLocationFromMapFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
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

                view = inflater.inflate(R.layout.select_location_map_fragment, container, false);
                initializeMap();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        selectLocationButton=(Button)view.findViewById(R.id.btn_select_location);
        selectLocationButton.setEnabled(false);
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("latitude",latitude);
                returnIntent.putExtra("longitude",longitude);


                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
            }
        });

        search=(Button) view.findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapSearch(getView());
            }
        });

        return view;
    }

    public void initializeMap(){
        if (mGoogleMap == null) {

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);


        }
    }

    ShortRoidPreferences shortRoidPreferences;



    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) view.findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        if(location.length()>0) {

            List<Address> addressList = null;
            if (location != null || !location.equals("")) {
                Geocoder geocoder = new Geocoder(getContext());
                try {
                    addressList = geocoder.getFromLocationName(location, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
        else
        {
            Toast.makeText(getContext(), "Please enter the location to search", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        LatLng ezmapWorkHouse = new LatLng(Constants.nitlat, Constants.nitlng);

        //Set UiSettings of the Map
        setUiSettings();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ezmapWorkHouse).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mGoogleMap.clear();
                mGoogleMap.addMarker(new MarkerOptions().position(point).title("").visible(true)  .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                selectLocationButton.setEnabled(true);
                latitude=point.latitude;
                longitude=point.longitude;

            }
        });
        mGoogleMap.getUiSettings().setCompassEnabled(true);
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
    @Override
    public void onResume() {
        super.onResume();
        initializeMap();
    }
    double latitude,longitude;




}

