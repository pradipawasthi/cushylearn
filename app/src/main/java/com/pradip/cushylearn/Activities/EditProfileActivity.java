package com.pradip.cushylearn.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pradip.cushylearn.Model.ChatUser;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.helper.Constants;
import com.pradip.cushylearn.imageutils.ImageLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private StorageReference mstorage;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    private ShortRoidPreferences shortRoidPreferences;
    private final String TAG = this.getClass().getName();

    private ImageLoader imgLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        **/


        mFirebaseAuth = FirebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        imgLoader = new ImageLoader(this);
        try {
            shortRoidPreferences=new ShortRoidPreferences(EditProfileActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        this.setupGui();

    }
    private void setupGui(){
        boolean logged_in = shortRoidPreferences.getPrefBoolean("logged_in",false);

        Log.d(TAG,"Logged In :"+logged_in);

        Gson gson = new Gson();
        String json = shortRoidPreferences.getPrefString(Constants.CHATUSER, "");
        ChatUser chatUser = gson.fromJson(json, ChatUser.class);
        updateProfileUI();
        String profile_img_url = chatUser.getProfileURL();

        ImageView editProfile = (ImageView) findViewById(R.id.profile_edit_img);
        editProfile.setOnClickListener(this);
        imgLoader.DisplayImage(profile_img_url, editProfile);

        ImageView editProfileTick = (ImageView)findViewById(R.id.edit_profile);
        editProfileTick.setOnClickListener(this);

//        editProfile.setImageURI(Uri.parse(profile_img_url));
//
//        if(profile_img_url!=null) {
//            new DownloadImageTask(editProfile).execute(profile_img_url);
//        }


        Button changaProfileBtn = (Button) findViewById(R.id.changeOffrImg);
        changaProfileBtn.setOnClickListener(this);
        ImageView selectLocProfile = (ImageView) findViewById(R.id.ivContactItem5);
        selectLocProfile.setOnClickListener(this);

        Button mapSepBtn = (Button) findViewById(R.id.addrFromMap);
        mapSepBtn.setOnClickListener(this);
    }
    private void updateProfileUI(){
        Gson gson = new Gson();
        String json = shortRoidPreferences.getPrefString(Constants.CHATUSER, "");
        ChatUser chatUser = gson.fromJson(json, ChatUser.class);
        EditText nameText = (EditText)findViewById(R.id.fullname);
        nameText.setText(chatUser.getName());

        EditText mobText = (EditText)findViewById(R.id.mobile_no);
        mobText.setText(chatUser.getPhone());

        EditText emailText = (EditText)findViewById(R.id.email);
        emailText.setText(chatUser.getEmail());

        EditText addressText = (EditText)findViewById(R.id.address);
        addressText.setText(chatUser.getAddress());
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab || v.getId() == R.id.edit_profile){
            Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //Update profile
            final Gson gson = new Gson();
            String json = shortRoidPreferences.getPrefString(Constants.CHATUSER, "");
            final ChatUser chatUser = gson.fromJson(json, ChatUser.class);

            EditText nameText = (EditText)findViewById(R.id.fullname);
            chatUser.setName(nameText.getText().toString());

            EditText mobText = (EditText)findViewById(R.id.mobile_no);
            chatUser.setPhone(mobText.getText().toString());

            EditText emailText = (EditText)findViewById(R.id.email);
            chatUser.setEmail(emailText.getText().toString());

            EditText addressText = (EditText)findViewById(R.id.address);
            chatUser.setAddress(addressText.getText().toString());

            boolean logged_in = shortRoidPreferences.getPrefBoolean("logged_in", false);
            String user_key = shortRoidPreferences.getPrefString("key");
            if (logged_in) {
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + user_key, chatUser);

                final LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
                linlaHeaderProgress.setVisibility(View.VISIBLE);

                mDatabase.updateChildren(childUpdates,new DatabaseReference.CompletionListener(){

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        String json = gson.toJson(chatUser);
                        shortRoidPreferences.setPrefString("name",chatUser.getName());
                        shortRoidPreferences.setPrefString("phone",chatUser.getPhone());
                        shortRoidPreferences.setPrefString(Constants.CHATUSER,json);
                        updateProfileUI();
                        linlaHeaderProgress.setVisibility(View.GONE);
                    }
                });
            }

            //Intent myIntent2 = new Intent(getApplicationContext(), EditProfileActivity.class);
            EditProfileActivity.this.finish();
        }else if(v.getId() == R.id.changeOffrImg){
            if(!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                askPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }else {
                changeProfileImage();
            }
        }else if(v.getId() == R.id.addrFromMap){
            //Select address from Map

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                Intent placeIntent = builder.build(this);
                startActivityForResult(placeIntent, Constants.PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
    private void changeProfileImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String userkey = shortRoidPreferences.getPrefString("key");
            //Delete previously stored images for the User
            mstorage.child("images/"+userkey).delete().addOnSuccessListener(new OnSuccessListener(){
                @Override
                public void onSuccess(Object o) {
                    Log.d(TAG,"Deleted All previous images of profile");
                }
            });
            StorageReference riversRef = mstorage.child("images/"+userkey+"/"+uri.getLastPathSegment());
            try {

                UploadTask uploadTask = riversRef.putFile(uri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getApplicationContext(), "Image Saved Successfully", Toast.LENGTH_LONG).show();
                        CircleImageView profileImage = (CircleImageView) findViewById(R.id.profile_edit_img);
                        //profileImage.setImageURI(downloadUrl);
                        imgLoader.DisplayImage(downloadUrl.toString(),profileImage);
                        boolean logged_in = shortRoidPreferences.getPrefBoolean("logged_in", false);
                        String user_key = shortRoidPreferences.getPrefString("key");
                        if (logged_in) {
                            Gson gson = new Gson();
                            String json = shortRoidPreferences.getPrefString(Constants.CHATUSER, "");
                            ChatUser chatUser = gson.fromJson(json, ChatUser.class);
                            chatUser.setProfileURL(downloadUrl.toString());

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/users/" + user_key, chatUser);

                            mDatabase.updateChildren(childUpdates);
                            //mstorage.child("users").child(user_key);

                        }
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }

        }else if (requestCode == Constants.PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this,data);
            String placeName = (String) place.getAddress();
            String toastMsg = String.format("Place: %s", placeName);
            EditText nameText = (EditText)findViewById(R.id.address);
            nameText.setText(placeName);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkPermission(String permissionType){
        return (ContextCompat.checkSelfPermission(this,permissionType)== PackageManager.PERMISSION_GRANTED);
    }
    public void askPermission(String permissionType){
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permissionType)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType},
                    Constants.REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    changeProfileImage();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "User has denined permission to change Profile Image", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
