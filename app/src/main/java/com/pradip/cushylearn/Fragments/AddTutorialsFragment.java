package com.pradip.cushylearn.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pradip.cushylearn.Activities.SelectLocationActivity;
import com.pradip.cushylearn.Adapters.ItemsAdapter;
import com.pradip.cushylearn.ApplicationConfig.MyApplication;

import com.pradip.cushylearn.Model.AddrsModel;
import com.pradip.cushylearn.Model.Category;
import com.pradip.cushylearn.Model.ModuleModel;
import com.pradip.cushylearn.Model.ItemNameList;
import com.pradip.cushylearn.Model.SubjectModel;
import com.pradip.cushylearn.Model.UserLocation;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.helper.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

import static android.app.Activity.RESULT_OK;



/**
 * Created by rbaisak on 15/2/17.
 */


public class AddTutorialsFragment extends Fragment implements View.OnClickListener, ItemsAdapter.OnOptionClickedListener {

    public static final String TAG = AddTutorialsFragment.class.getSimpleName();

    private static final int GALLERY_INTENT_1 = 2;
    private static final int GALLERY_INTENT_2 = 3;
    public static final int RequestPermissionCode = 1;
    private ShortRoidPreferences shortRoidPreferences;

    private SubjectModel subjectModel;
    private ModuleModel moduleModel = null;
    private ItemsAdapter itemsAdapter;
    //private DatePickerDialog fromDatePickerDialog;
    //private DatePickerDialog toDatePickerDialog;
    TextView selectLocation, btn_att;
    Context mContext;
    CheckBox currentloc;
    EditText sub_nametxt, loc_id, adrstext, latlngtext;

    String itemkeyyy;


    FirebaseStorage mstorage;
    private ProgressDialog mprogress;
    public static String generatedFilePath1;
    String generatedFilePath2;


    ItemsAdapter adapter;
    Button btnAdd; //btnDel;
    View v;
    EditText topicNameText, DescriptionText, lessonName, validTillText;
    ListView lv;
    ImageView iv_add, offer_pic;


    //  ImageView offer_pic;


    LocationManager locationManager;
    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    Location location;
    public static double latitude, longitude;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute


    /**
     * Items entered by the user is stored in this ArrayList variable
     */
    ArrayList<ItemNameList> list = new ArrayList<>();

    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");

    /**
     * Declaring an ArrayAdapter to set items to ListView
     */


    // datatbase reference
    FirebaseDatabase database = MyApplication.getFireBaseInstance();
    final DatabaseReference databaseReference = database.getReference(Constants.OFFER_FOLDER_NAME);

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;



    String branchId = databaseReference.getRoot().getKey();


    //spinner for Location category
    Spinner locationSpinner;
    List<UserLocation> LocationStringList = new ArrayList<UserLocation>();

    List<String> LocationIdString = new ArrayList<String>();
    List<UserLocation> LocationArray = new ArrayList<>();
    ArrayAdapter<UserLocation> adapterLocation, adapterId;
    UserLocation usrLoc;
    String s=null;

    //multiautocomplete text for category
    AutoCompleteTextView actv;
    List<String> catStringList = new ArrayList<String>();
    List<Category> categoriesA = new ArrayList<Category>();
    ArrayAdapter<String> adapterCat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add_offer, container, false);
        mContext = getActivity();

        try {
            shortRoidPreferences=new ShortRoidPreferences(getActivity(),"chat",getContext().MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        if(user != null){
            uid = user.getUid();
        }else{
            uid = shortRoidPreferences.getPrefString("fbuid");
        }
        //  offer_pic=(ImageView) getActivity().findViewById(R.id.imageVw);


        iv_add = (ImageView) getActivity().findViewById(R.id.add_offer);
        //Spinner spinner1=(Spinner)getActivity().findViewById(R.id.spinner_nav);
        iv_add.setVisibility(View.VISIBLE);
        iv_add.setOnClickListener(this);


        offer_pic = (ImageView) v.findViewById(R.id.imageVw);
        offer_pic.setOnClickListener(this);

        findViewsById();

        setField();
      //  locationFunction();                                           //select location
        EnableRuntimePermission();
        locationF();
        Locationtodo();


        mprogress = new ProgressDialog(getContext());
        // firebase storage
        mstorage = FirebaseStorage.getInstance();


 //Select Location

//        latlngtext.addTextChangedListener(new TextWatcher() {
//            String currLat, currLng;
//
//            public void afterTextChanged(Editable s) {
//                String currLatLng = s.toString();
//
//                if (currLatLng != null && !currLatLng.equals("")) {
//                    String key = mContext.getString(R.string.mapmyindiawebkey);
//                    String pars[] = {currLatLng, key};
//                    //String pars[]={key, String.valueOf(currLatLng.split(",")), String.valueOf(currLatLng.split(""))};
//
//                    SingletonUtil.getInstance().setLat(latitude);
//                    SingletonUtil.getInstance().setLng(longitude);
////                    SingletonUtil.getInstance().setLat(Double.parseDouble(currLatLng.split(",")[0]));
////                    SingletonUtil.getInstance().setLng(Double.parseDouble(currLatLng.split(",")[1]));
//                    new AsyncNetworkTask().execute(pars);
//
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//                Log.d(TAG, "LatLang changed " + s);
//
//                //@TODO will be removed later
//                // getnearestLocation();
//
//
//            }
//
//        });


        lv.setAdapter(adapter);
        return v;
    }



    private void findViewsById() {

        /** Reference to the add button of the layout main.xml */
        btnAdd = (Button) v.findViewById(R.id.btnAdd);
        lv = (ListView) v.findViewById(R.id.topics);
        topicNameText = (EditText) v.findViewById(R.id.txtItem);
        lessonName = (EditText) v.findViewById(R.id.input_lessonName);


//        currentloc = (CheckBox) v.findViewById(R.id.current_shop_location);
//        selectLocation = (TextView) v.findViewById(R.id.select_location_text);
//        latlngtext = (EditText) v.findViewById(R.id.input_loc);
//        adrstext = (EditText) v.findViewById(R.id.input_address);
        DescriptionText = (EditText) v.findViewById(R.id.input_description);
        sub_nametxt = (EditText) v.findViewById(R.id.input_oname);
        btn_att = (TextView) v.findViewById(R.id.button_attG);
        locationSpinner = (Spinner) v.findViewById(R.id.location_spinner);

//        adrstext.setInputType(InputType.TYPE_NULL);


    }

    private void setField() {
//        lessonName.setOnClickListener(this);
//        validTillText.setOnClickListener(this);
        //selectLocation.setOnClickListener(this);                    //select location
        btnAdd.setOnClickListener(this);
        btn_att.setOnClickListener(this);


//        btn_att.setOnClickListener(this);

        // currentloc.setOnClickListener(this);

        //  dt1.setText(dnt);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subjectModel = new SubjectModel();
        subjectModel.setTopicslist(new ArrayList<ModuleModel>());
        subjectModel.setAddrs(new AddrsModel());                                           //select location


        itemsAdapter = new ItemsAdapter(getActivity(), subjectModel.getTopicslist());

        //Add the listener here to handle Edit or Delete option click
        itemsAdapter.setOnOptionClickedListener(this);


        lv.setAdapter(itemsAdapter);
    }

    //select location
/*****************
/// Don't touch................................................................

    void locationFunction() {
        currentloc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getLocation();
                    latlngtext.setText("" + latitude + "," + longitude);
                    String latlng12 = latitude + "," + longitude;
                    //geo decode address
                    String key = mContext.getString(R.string.mapmyindiawebkey);

                    //String srt = SingletonUtil.getInstance(mContext).getAddressFromLatLang(latitude,longitude);
                    String requrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlng12 + "&key=" + key;

                    String pars[] = {latlng12, key};
                    new AsyncNetworkTask().execute(pars);


                    //End

                } else {
                    latlngtext.setText("");
                    adrstext.setText("");
                }
            }
        });
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
                String key = mContext.getString(R.string.mapmyindiawebkey);

                startActivityForResult(intent, 1);


                //
            }
        });
    }

    //    LocationManager locationManager;
//    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
//    Location location;
//    public static double latitude,longitude;
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
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            1, (LocationListener) this);
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
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

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

    private class AsyncNetworkTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private String resStr;

        protected String doInBackground(String... urls) {

            // String requrl = "http://apis.mapmyindia.com/advancedmaps/v1/"+urls[0]+"/rev_geocode?lat="+urls[1]+"&lng="+urls[2];
            String requrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + urls[0] + "" + "&key=" + urls[1];
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            resStr = result.toString();
            // Log.d(TAG,"sdffffffffff"+      resStr);


            return resStr;


        }

        protected void onPostExecute(String jsonresp) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                JSONObject jsonObj = new JSONObject(resStr);
                if (jsonObj.getJSONArray("results").length() > 0) {
                    adrstext.setText(jsonObj.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                } else {
                    adrstext.setText("Please enter address");
                }

            } catch (JSONException e) {
                adrstext.setText("Error fetching address. Please enter Address");
                e.printStackTrace();
            }

        }
    }
*********************************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                currentloc.setChecked(false);
//                latitude = data.getDoubleExtra("latitude", 0);
//                longitude = data.getDoubleExtra("longitude", 0);
//                latlngtext.setText("" + latitude + "," + longitude);
//
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//
//
//            }
//        } else
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle MBuddle = data.getExtras();
                Object result = MBuddle.get("result");
                Log.d(TAG, "Result " + result);

                ItemNameList inl = (ItemNameList) result;
                list.add(inl);

            }
        } else if (requestCode == GALLERY_INTENT_1 && resultCode == RESULT_OK) {
            mprogress.setMessage("image uploading.........!!");
            mprogress.show();

            Uri uri = data.getData();
            new AsyncGalleryTask1().execute(uri);
            mprogress.dismiss();
            Toast.makeText(getContext(), "IMAGE SAVE SUCCESSFULLY", Toast.LENGTH_LONG).show();
            offer_pic.setImageURI(uri);


        } else if (requestCode == GALLERY_INTENT_2 && resultCode == RESULT_OK) {
            mprogress.setMessage("image uploading.........!!");
            mprogress.show();

            Uri uri = data.getData();
            new AsyncGalleryTask2().execute(uri);
            mprogress.dismiss();
            Toast.makeText(getContext(), "ITEM IMAGE SAVE SUCCESSFULLY", Toast.LENGTH_LONG).show();
            offer_pic.setImageURI(uri);

        }

    }




    @Override
    public void onClick(View v) {
        if (v == iv_add) {
            if (sub_nametxt.getText().length() > 0 && lessonName.toString().length() > 0 && DescriptionText.getText().length() > 0) { // && adrstext.getText().length() > 0) {
                //Toast.makeText(getContext(), alreadyAddedLocation.toString(), Toast.LENGTH_LONG).show();

                //for current time of adding notes
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
                String currenttimeformat = simpleDateFormat.format(new Date());
                Date currentDt=null;
                // Log.d("MainActivity", "Current Timestamp: " + format);
                try {
                    currentDt = simpleDateFormat.parse(currenttimeformat);
                    //dttill = df.parse(tillStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long currentDatelong=currentDt.getTime();


                //TODO save the SubjectModel object here
                UserLocation ul= new UserLocation();
                ul.setLocationId(locationSpinner.getSelectedItem().toString());
                ul.setLocationId(s);

                subjectModel.setLocationName(locationSpinner.getSelectedItem().toString());
                subjectModel.setSubject_Name(sub_nametxt.getText().toString());
                subjectModel.setDescription(DescriptionText.getText().toString());
                subjectModel.setClassesURI(generatedFilePath1);
                subjectModel.setLessonName(lessonName.getText().toString());

//                subjectModel.setAddress(adrstext.getText().toString());
//                subjectModel.setLat(latitude);
//                subjectModel.setLng(longitude);
                subjectModel.setCurrentUserId(uid);
                subjectModel.setSubjectId(branchId);
                subjectModel.setCreatedAt(currenttimeformat);


                List<ModuleModel> itemList = subjectModel.getTopicslist();
                String key = databaseReference.child(Constants.OFFER_FOLDER_NAME).push().getKey();
                subjectModel.setSubjectId(key);
                Map<String, Object> itemMap = new HashMap<String, Object>();
                for (ModuleModel item : itemList) {
                    String itemKey = databaseReference.child(Constants.OFFER_FOLDER_NAME).push().getKey();
                    item.setTopiclistId(itemKey);
                    item.setSubjectId(key);


                    itemMap.put(itemKey, item.toMap());
                }

                Map<String, Object> onlValues = subjectModel.toMap();
                onlValues.put("TopicList", itemMap);


                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + key, onlValues);

                databaseReference.updateChildren(childUpdates);
                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_LONG).show();
            } else {
                //e.printStackTrace();
                Toast.makeText(getContext(), "Please fill the all required fields", Toast.LENGTH_LONG).show();
            }


        } else if (v == btnAdd) {
            showDialog(-1);
        } else if (v == btn_att || v.getId()==R.id.imageVw) {

            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");

//            new AsyncGalleryTask().execute(i);
//            Uri uri = i.getData();
//            offer_pic.setImageURI(uri);

            startActivityForResult(i, GALLERY_INTENT_1);


        } else if (v == selectLocation) {

            Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
            //  String key = mContext.getString(R.string.mapmyindiawebkey);

            startActivityForResult(intent, 1);

        }
//        else  if( v == locationSpinner){
//            locationF();
//            Locationtodo();
//
//        }


    }


    private void Locationtodo() {

        adapterLocation = new ArrayAdapter<UserLocation>(getContext(),
                android.R.layout.simple_dropdown_item_1line,LocationStringList );
//    adapterCat = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, categoriesA.toArray(new String[categoriesA.size()]));

        //   actv.setThreshold(1);

        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapterLocation);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG,"Item Selected"+i);
                UserLocation usrLoc = LocationStringList.get(i);

               // UserLocation usrLoc1 = adapterLocation.getItem(i);
            subjectModel.setLocationId(usrLoc.getLocationId());
                subjectModel.setLat(usrLoc.getLatitude());
                subjectModel.setLng(usrLoc.getLongitude());
                subjectModel.setAddress(usrLoc.getPlaceDetails());

                Double lat1= usrLoc.getLatitude();
                Double lng1= usrLoc.getLongitude();
                String add1 = usrLoc.getPlaceDetails();
               // subjectModel.setAddrs();
//                subjectModel.setAddrs(usrLoc.getPlaceDetails());
//                subjectModel.setAddrs(usrLoc.setLng(longitude));
//                subjectModel.setAddrs(usrLoc.setLat(latitude));
//                subjectModel.getAddrs().setAddress(adrstext.getText().toString());



                AddrsModel addrm = new AddrsModel();
                addrm.setAddress(add1);
                addrm.setLatitude(lat1);
//                 att.setText(generatedFilePath);
                addrm.setLongitude(lng1);


                //Add the newly added item to Item list                                   //select Lcation
                subjectModel.getAddrs().setAddress(add1);
                subjectModel.getAddrs().setLatitude(lat1);
                subjectModel.getAddrs().setLongitude(lng1);


              //  Log.d(TAG,"Item Selected"+usrLoc.getNamePlace()+":"+usrLoc.getLocationId());
                Log.d(TAG,"Item Selected"+usrLoc.getNamePlace()+":"+usrLoc.getLocationId());


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        locationSpinner.setSelection(0);
//        loc_id.setSelection(0);



    }
    private void locationF() {
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference dbRef = database.getReference(Constants.LOCATION_FOLDER_NAME);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.print("There are " + dataSnapshot.getChildrenCount() + " pradip");
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (final DataSnapshot ds : dataSnapshots) {
                    UserLocation userlocation = ds.getValue(UserLocation.class);
//                    Log.d("firebase", ds.getKey()+" : "+category.getCat_name());
                    LocationArray.add(userlocation);
                    // adapterCat.add(category.getCat_name());
                    adapterLocation.add(userlocation);
//                    LocationIdString.add(userlocation.getLocationId());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error occurred "+databaseError.getDetails());
            }
        });

    }


    private void showDialog(final int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_note_edit);

        final EditText questionET = (EditText) dialog.findViewById(R.id.ques_et);
       // final EditText chapterET = (EditText) dialog.findViewById(R.id.autotext1);
        final EditText answerET = (EditText) dialog.findViewById(R.id.note_text_input4);
        final Button attchment_dialog = (Button) dialog.findViewById(R.id.button);

        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.ll);


      actv  =(AutoCompleteTextView) dialog.findViewById(R.id.autotext1);
        initCategories();
        todo();


        // onOffSwitch.isChecked()


//        int selectedId = rg.getCheckedRadioButtonId();
//        final RadioButton rb =(RadioButton) dialog.findViewById(selectedId);

        //final String itemId = databaseReference.push().getKey();


/*****************************************/
//         final ImageButton camerabtn_dialog=(ImageButton)dialog.findViewById(R.id.button_cam);

        Button actionCancel = (Button) dialog.findViewById(R.id.actionCancel);
        Button actionOk = (Button) dialog.findViewById(R.id.actionOk);

        //position == -1 means new Item
        if (position == -1)
            dialog.setTitle("Add Item");

            //Editing existing item
        else {
            dialog.setTitle("Edit Item");


            //Pre fill title and quantity fields
            questionET.setText(subjectModel.getTopicslist().get(position).getQuestion());
           // chapterET.setText(subjectModel.getTopicslist().get(position).getQuestion());
            answerET.setText(subjectModel.getTopicslist().get(position).getAnswer());
            attchment_dialog.setText(subjectModel.getTopicslist().get(position).getUrl());
            actv.setText(subjectModel.getTopicslist().get(position).getChapterName());
//            rb.setChecked(subjectModel.getItem_List().get(position).getAttachmentType());

            String offerType = subjectModel.getTopicslist().get(position).getAttachmentType();
            if (offerType.equalsIgnoreCase("kg")) {

                RadioButton rb = (RadioButton) dialog.findViewById(R.id.switch2);
                rb.setChecked(true);

            } else if (offerType.equalsIgnoreCase("pieces")) {

                RadioButton rb = (RadioButton) dialog.findViewById(R.id.switch3);
                rb.setChecked(true);

            } else if (offerType.equalsIgnoreCase("Dozens")) {

                RadioButton rb = (RadioButton) dialog.findViewById(R.id.switch4);
                rb.setChecked(true);

            }




        }


        attchment_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setType("image/*");


                startActivityForResult(intent, GALLERY_INTENT_2);

            }
        });
        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        actionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ModuleModel moduleModel = null;
                //position == -1 means new Item
                if (position == -1) {
                    moduleModel = new ModuleModel();
                    moduleModel.setTopicName(topicNameText.getText().toString());
                    moduleModel.setUrl(generatedFilePath2);
                    // att.setText(generatedFilePath);
                    // moduleModel.setTopiclistId(itemkeyyy);

                    //Add the newly added item to Item list
                    subjectModel.getTopicslist().add(moduleModel);
                    // moduleModel.setAttachmentType(rb.getText().toString());

                    topicNameText.setText("");
                }
                //Editing existing item

                else {
                    moduleModel = subjectModel.getTopicslist().get(position);
                }

                moduleModel.setQuestion(questionET.getText().toString());
                //moduleModel.setQuestion(chapterET.getText().toString());
                moduleModel.setAnswer(answerET.getText().toString());
                moduleModel.setUrl(generatedFilePath2);
                moduleModel.setTopiclistId(itemkeyyy);
                moduleModel.setChapterName(actv.getText().toString());
                int typeId = rg.getCheckedRadioButtonId();
                switch (typeId) {
                    case R.id.switch2:
                        moduleModel.setAttachmentType("Image");
                        break;
//                    case R.id.switch3:
//                        moduleModel.setAttachmentType("Pieces");
//                        break;
//                    case R.id.switch4:
//                        moduleModel.setAttachmentType("Dozens");
//                        break;
                }

//                moduleModel.setAttachmentType(rb.getText().toString());


                itemsAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    public void initCategories() {
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference df = database.getReference("PradipCat");

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.print("There are " + dataSnapshot.getChildrenCount() + " PradipCat");


                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (final DataSnapshot ds : dataSnapshots) {
                    Category category = ds.getValue(Category.class);
//                    Log.d("firebase", ds.getKey()+" : "+category.getCat_name());
                    categoriesA.add(category);
                    catStringList.add(category.getCat_name());
                    //adapterCat.add(category.getCat_name());
//          adapterCat.add(category.getCat_name());
//          adapterCat.notifyDataSetChanged();

                }


                /**
                 for(Category cat:categoriesA){
                 String catName = cat.getCat_name();
                 adapterCat.add(catName);

                 }
                 **/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error occurred " + databaseError.getDetails());
            }
        });
    }
    public void todo() {
       // actv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        //Arrays.asList()
//        adapterCat=new ArrayAdapter<String>(getContext(),
//            android.R.layout.simple_dropdown_item_1line,str);
        adapterCat = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, catStringList);
//    adapterCat = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, categoriesA.toArray(new String[categoriesA.size()]));

        //   actv.setThreshold(1);
        actv.setAdapter(adapterCat);
    }


    @Override
    public void onEditOptionClicked(int position) {
        showDialog(position);
    }

    @Override
    public void onDeleteOptionClicked(int position) {
        subjectModel.getTopicslist().remove(position);
        itemsAdapter.notifyDataSetChanged();
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

    class AsyncGalleryTask1 extends AsyncTask<Uri, Void, String> {


    Uri downloadUri;


    @Override
    protected String doInBackground(Uri... uris) {
        mstorage = FirebaseStorage.getInstance();

        StorageReference storageRef = mstorage.getReferenceFromUrl("gs://chushylearn2017.appspot.com");
        StorageReference path = storageRef.child("PhotoAndroid").child(uris[0].getLastPathSegment());


        path.putFile(uris[0]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                generatedFilePath1 = downloadUri.toString(); /// Th

            }
        });
        return generatedFilePath1;


    }

//    @Override
//    protected void onPostExecute(String result) {
//
//        super.onPostExecute(result);
//        AddTutorialsFragment.generatedFilePath1 = result;
//
//    }
}
    class AsyncGalleryTask2 extends AsyncTask<Uri, Void, String> {


        Uri downloadUri;


        @Override
        protected String doInBackground(Uri... uris) {
            mstorage = FirebaseStorage.getInstance();
            mstorage = FirebaseStorage.getInstance();

            StorageReference storageRef = mstorage.getReferenceFromUrl("gs://chushylearn2017.appspot.com");


            StorageReference path = storageRef.child("PhotoAndroid").child("Item").child(uris[0].getLastPathSegment());
//            StorageReference path = mstorage.child("PhotoAndroid").child(uris[0].getLastPathSegment());


            path.putFile(uris[0]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    generatedFilePath2 = downloadUri.toString(); /// Th

                }
            });
            return generatedFilePath2;


        }


    }
}







