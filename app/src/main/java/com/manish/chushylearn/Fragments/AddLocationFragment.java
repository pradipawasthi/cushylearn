package com.manish.chushylearn.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manish.chushylearn.Activities.SelectLocationActivity;
import com.manish.chushylearn.ApplicationConfig.MyApplication;
import com.manish.chushylearn.Model.AddrsModel;
import com.manish.chushylearn.Model.Category;
import com.manish.chushylearn.Model.UserLocation;
import com.manish.chushylearn.R;
import com.manish.chushylearn.helper.Constants;
import com.manish.chushylearn.util.SingletonUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.manish.chushylearn.Fragments.AddTutorialsFragment.RequestPermissionCode;


public class AddLocationFragment extends Fragment implements LocationListener {


    private static final int GALLERY_INTENT_Loc = 22;
//    private int REQUEST_CAMERA = 0, SELECT_FILE = 2;
//    private String userChoosenTask;
//    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
//
//    private int REQUEST_CODE_PICKER = 2000;
    View rootView;
    Context mContext;
    ShortRoidPreferences s;
    CheckBox currentLocation, image_uploaded;
    EditText placeDetail;
    TextView selectLocation;
    EditText inputLocation;
    RatingBar rating;
    TextView selectImg;
    //TextView and image view for gallery;
   TextView chose_img;
    ImageView loc_pic;
    Intent shareIntent;
    ShortRoidPreferences shortRoidPreferences;
    static JSONArray alreadyAddedLocation;


    double distance=0;
    private StorageReference mstorage;
    private ProgressDialog mProgressdialog;
    String generatedImagePath;
    int i = 0;
    // String imageEncoded= new String();
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute


    List<String> catStringList = new ArrayList<String>();
    List<Category> categoriesA = new ArrayList<>();
    ArrayAdapter<String> adapterCat;


    private ArrayAdapter<String> adapter;


    public static final String TAG = AddLocationFragment.class.getSimpleName();


    MultiAutoCompleteTextView mact;

    // datatbase reference
    FirebaseDatabase database = MyApplication.getFireBaseInstance();
    final DatabaseReference databaseReference=database.getReference(Constants.LOCATION_FOLDER_NAME);
    //final DatabaseReference databaseReference=database.getReference("user-locations");
    String LocationId = databaseReference.getRoot().getKey();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();



        getLocation();
        //Fetches categories from Firebase
        initCategories();

        try {
            s=new ShortRoidPreferences(mContext,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        rootView = inflater.inflate(R.layout.fragment_add_location, container, false);
        Toast.makeText(mContext,"Switch on GPS for better performance", Toast.LENGTH_SHORT).show();
        // mact = (MultiAutoCompleteTextView) rootView.findViewById(R.id.category_spinner);
        final EditText placeName=(EditText)rootView.findViewById(R.id.input_name_place);

        placeDetail=(EditText)rootView.findViewById(R.id.input_detail_place);
        currentLocation=(CheckBox)rootView.findViewById(R.id.current_location);
        inputLocation=(EditText)rootView.findViewById(R.id.input_location);
        rating=(RatingBar)rootView.findViewById(R.id.ratingBar1);
        image_uploaded=(CheckBox)rootView.findViewById(R.id.show_uploaded_img);
        mact=(MultiAutoCompleteTextView)rootView.findViewById(R.id.labelMulti);
        final EditText feedback=(EditText)rootView.findViewById(R.id.input_detail_feedback);
        chose_img=(TextView) rootView.findViewById(R.id.text_choose_img);
        loc_pic=(ImageView) rootView.findViewById(R.id.loc_imageVw);


        mProgressdialog=new ProgressDialog(getContext());


        inputLocation.addTextChangedListener(new TextWatcher() {
            String currLat,currLng;

            public void afterTextChanged(Editable s) {
                String currLatLng = s.toString();

                if(currLatLng !=null &&!currLatLng.equals("")){
                    String key = mContext.getString(R.string.mapmyindiawebkey);
                    String pars[]={currLatLng, key};
                    //String pars[]={key, String.valueOf(currLatLng.split(",")), String.valueOf(currLatLng.split(""))};

                    SingletonUtil.getInstance().setLatitude(latitude);
                    SingletonUtil.getInstance().setLongitude(longitude);
//                    SingletonUtil.getInstance().setLat(Double.parseDouble(currLatLng.split(",")[0]));
//                    SingletonUtil.getInstance().setLng(Double.parseDouble(currLatLng.split(",")[1]));
                    new AsyncNetworkTask().execute(pars);

                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.d(TAG,"LatLang changed "+s);

                //@TODO will be removed later
                //  getnearestLocation();







            }

        });


        mact.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        //Arrays.asList()
//        adapterCat=new ArrayAdapter<String>(getContext(),
//            android.R.layout.simple_dropdown_item_1line,str);
        adapterCat = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, catStringList);

        mact.setAdapter(adapterCat);
        final Button addLocation=(Button)rootView.findViewById(R.id.btn_add_location);
        selectLocation=(TextView)rootView.findViewById(R.id.select_location_text);
        selectLocation.setPaintFlags(selectLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        locationFunction();
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(placeDetail.getText().length()>0&&placeName.getText().length()>0&&mact.toString().length()>0&&inputLocation.getText().length()>0&&rating.getRating()>0&&feedback.getText().length()>0) {
                    //Toast.makeText(getContext(), alreadyAddedLocation.toString(), Toast.LENGTH_LONG).show();
                    addLocation(placeDetail.getText().toString(), placeName.getText().toString(), mact.getText().toString(), rating.getRating(), feedback.getText().toString());
                    Toast.makeText(getContext(), "Location added", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(), "Please fill the all required fields", Toast.LENGTH_LONG).show();
                }



            }
        });

        chose_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");

//            new AsyncGalleryTask().execute(i);
//            Uri uri = i.getData();
//            offer_pic.setImageURI(uri);

                startActivityForResult(i, GALLERY_INTENT_Loc);


            }
        });

        mProgressdialog = new ProgressDialog(getContext());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserLocation userLocation = new UserLocation();
       // offerModel.setItem_Name(new ArrayList<ModuleModel>());
        userLocation.setAddr(new AddrsModel());                                           //select location


//        itemsAdapter = new ItemsAdapter(getActivity(), offerModel.getItem_Name());
//
////        Add the listener here to handle Edit or Delete option click
//        itemsAdapter.setOnOptionClickedListener(this);
//
//
//        lv.setAdapter(itemsAdapter);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 0:
                    // onCaptureImageResult(data);
                    break;
//                case 2000:
//                    onCaptureImageResult(data);
//                    break;
                case 1:
                    currentLocation.setChecked(false);
                    latitude = data.getDoubleExtra("latitude", 0);
                    longitude = data.getDoubleExtra("longitude", 0);
                    inputLocation.setText("" + latitude + "," + longitude);
                    //onSelectFromGalleryResult(data);
                    break;

                case 22:
                    mProgressdialog.setMessage("image uploading.........!!");
                    mProgressdialog.show();

                    Uri uri = data.getData();
                    new AddLocationFragment.AsyncGalleryTask().execute(uri);
                    mProgressdialog.dismiss();
                    Toast.makeText(getContext(), "IMAGE SAVE SUCCESSFULLY", Toast.LENGTH_LONG).show();
                    loc_pic.setImageURI(uri);
                    break;

                default:
            }

        }
    }

    void locationFunction()
    {
        currentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    getLocation();
                    inputLocation.setText(""+latitude+","+longitude);
                    String latlng12=latitude+","+longitude;
                    //geo decode address
                    String key = mContext.getString(R.string.mapmyindiawebkey);

                    //String srt = SingletonUtil.getInstance(mContext).getAddressFromLatLang(latitude,longitude);
                    String requrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng12+"&key="+key;

                    String pars[]={latlng12, key};
                    new AsyncNetworkTask().execute(pars);


                    //End

                }
                else
                {
                    inputLocation.setText("");
                    placeDetail.setText("");
                }
            }
        });
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SelectLocationActivity.class);
                String key = mContext.getString(R.string.mapmyindiawebkey);

                startActivityForResult(intent,1);


                //
            }
        });
    }
    public void addLocation(String placeDetail, String placeName, String category, float rating, String feedback)
    {
        final UserLocation userLocation=new UserLocation();
        userLocation.setCategory(category);
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);
        userLocation.setNamePlace(placeName);
        userLocation.setPlaceDetails(placeDetail);
        userLocation.setRating(rating);
        userLocation.setFeedback(feedback);
        userLocation.setImageURL(generatedImagePath);  //  databaseReference.setValue(imageEncoded);
//        userLocation.setImageURL(imageEncoded);
        userLocation.setLocationId(LocationId);
//        offerModel.getAddrs().setAddress(adrstext.getText().toString())
//        userLocation.setAddr(latitude);



        AddrsModel addrm = new AddrsModel();
                //addrm.setAddress(adrstext.getText().toString());
                addrm.setLatitude(latitude);
//         att.setText(generatedFilePath);
                addrm.setLongitude(longitude);
//
//        Add the newly added item to Item list                                   //select Lcation
//        userLocation.getAddr().getLongitude(longitude);
//        userLocation.getAddr().getLatitude(latitude);
       // userLocation.getAddr().setAddress(adrstext.getText().toString());


        userLocation.setAddedByKey(s.getPrefString("key"));     // change to user-locations
        String key=databaseReference.child(Constants.LOCATION_FOLDER_NAME).push().getKey();
        userLocation.setLocationId(key);
        Map<String, Object> onlValues = userLocation.toMapUserLocation();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, onlValues);
        databaseReference.updateChildren(childUpdates);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.flFragments, new HomeFragment());
        Toast.makeText(getActivity(),"Location Added", Toast.LENGTH_LONG).show();
        fragmentTransaction.commit();


/*******
        databaseReference.push().setValue(userLocation,new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {



                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.flFragments, new HomeFragment());
                Toast.makeText(getActivity(),"Location Added",Toast.LENGTH_LONG).show();
                fragmentTransaction.commit();

            }
        });
*******************/

    }
    LocationManager locationManager;
    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    Location location;
    public static double latitude,longitude;
    public Location getLocation() {

        try {


            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            1, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            //    return TODO;
                        }
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        //RB changes
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //RB changes
                        /**locationManager.requestLocationUpdates(
                         LocationManager.GPS_PROVIDER,
                         10,
                         1, this);
                         **/
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }





    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    /**
     * Fetches location from Firebase
     */
    public void initCategories(){
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference dbRef = database.getReference("BranchCategory");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.print("There are " + dataSnapshot.getChildrenCount() + " BranchCategory");
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (final DataSnapshot ds : dataSnapshots) {
                    Category category = ds.getValue(Category.class);
//                    Log.d("firebase", ds.getKey()+" : "+category.getCat_name());
                    categoriesA.add(category);
                   // adapterCat.add(category.getCat_name());
                    catStringList.add(category.getCat_name());

                }

                //final MultiSpinner spinner = (MultiSpinner) rootView.findViewById(R.id.category_spinner);
                //adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, categoriesArray.toArray());
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error occurred "+databaseError.getDetails());
            }
        });
    }

    private class AsyncNetworkTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private String resStr;

        protected String doInBackground(String... urls) {

            // String requrl = "http://apis.mapmyindia.com/advancedmaps/v1/"+urls[0]+"/rev_geocode?lat="+urls[1]+"&lng="+urls[2];
            String requrl= "https://maps.googleapis.com/maps/api/geocode/json?latlng="+urls[0]+""+"&key="+urls[1];
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
            resStr = result.toString();
            Log.d(TAG,"sdffffffffff     "+resStr);

            return resStr;



        }

        protected void onPostExecute(String jsonresp) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                placeDetail.setText(jsonObj.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
            } catch (JSONException e) {
                placeDetail.setText("Error fetching address. Please enter Address");
                e.printStackTrace();
            }

        }
    }


    class AsyncGalleryTask extends AsyncTask<Uri, Void, String> {


        Uri downloadUri;


        @Override
        protected String doInBackground(Uri... uris) {
            mstorage = FirebaseStorage.getInstance().getReference();

            StorageReference path = mstorage.child("PhotoAndroid").child("LocationAndroid").child(uris[0].getLastPathSegment());
//            StorageReference path = mstorage.child("PhotoAndroid").child(uris[0].getLastPathSegment());


            path.putFile(uris[0]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    generatedImagePath = downloadUri.toString(); /// Th

                }
            });
            return generatedImagePath;


        }


    }
    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(getContext(), "READ_EXTERNAL_STORAGE permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getContext(), "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getContext(), "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }





}