package com.pradip.cushylearn.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pradip.cushylearn.ApplicationConfig.MyApplication;
import com.pradip.cushylearn.ConnectionManager.ConnectivityReceiver;
import com.pradip.cushylearn.Fragments.AddLocationFragment;
import com.pradip.cushylearn.Fragments.HomeFragment;
import com.pradip.cushylearn.Fragments.AddTutorialsFragment;
import com.pradip.cushylearn.Fragments.ViewTutorialsFragment;
import com.pradip.cushylearn.Model.ChatUser;
import com.pradip.cushylearn.Model.User;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.helper.Constants;
import com.pradip.cushylearn.imageutils.ImageLoader;
import com.pradip.cushylearn.util.SingletonUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.InputStream;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Toolbar toolbar;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private String mUserId;
    private DatabaseReference mDatabase;
    private Drawer navDrawer;
    boolean doubleBackToExitPressedOnce = false;

    ShortRoidPreferences shortRoidPreferences;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    ImageLoader imgLoader;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar();
        SingletonUtil.verifyStoragePermissions(this);
        initialization();


        checkConnection();

        navigationDrawer(savedInstanceState);

        userSessionCheck();

        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }


    private void toolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
    }

    private void initialization() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        try {
            shortRoidPreferences=new ShortRoidPreferences(MainActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        imgLoader = new ImageLoader(this);
    }

    private void checkConnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected){
        String message;
        int color;

        if(!isConnected){
            message = "Sorry! Not connected to the Internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(toolbar,message,Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }

    private void navigationDrawer(Bundle savedInstanceState) {

        PrimaryDrawerItem item0 = new PrimaryDrawerItem().withIdentifier(0).withName("Home").withIcon(GoogleMaterial.Icon.gmd_home);
     //   PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("View Locations").withIcon(GoogleMaterial.Icon.gmd_show_chart);
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("View Tutorials").withIcon(GoogleMaterial.Icon. gmd_local_offer);
       // PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Car/Cab Pool").withIcon(GoogleMaterial.Icon.gmd_directions_car);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Add Location").withIcon(GoogleMaterial.Icon.gmd_add_location);

        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Chat").withIcon(GoogleMaterial.Icon.gmd_chat);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Add Tutorials").withIcon(GoogleMaterial.Icon. gmd_playlist_add);


        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(8).withName("Contact Us").withIcon(GoogleMaterial.Icon.gmd_contacts).withSelectable(false);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false);
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(6).withName("About Us").withIcon(GoogleMaterial.Icon.gmd_android).withSelectable(false);
        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(7).withName("Privacy Policy").withIcon(GoogleMaterial.Icon.gmd_priority_high).withSelectable(false);



        Gson gson = new Gson();
        String json = shortRoidPreferences.getPrefString(Constants.CHATUSER, "");
        ChatUser chatUser = gson.fromJson(json, ChatUser.class);
        if(chatUser == null)chatUser = new ChatUser();
        String profile_img_url = chatUser.getProfileURL();


            ImageView imageView = new ImageView(this);
            Drawable profileImage = ContextCompat.getDrawable(this, R.drawable.ic_launcher);
            imgLoader.DisplayImage(profile_img_url, imageView);
            //getResources().getDrawable(R.drawable.profile_pic, Theme);
//        Bitmap bitmap = new Bitmap();

        ProfileDrawerItem profileDrawerItem = null;
        if(profile_img_url ==null){
            profileDrawerItem = new ProfileDrawerItem().
                withName(shortRoidPreferences.getPrefString("name")).
                withEmail(shortRoidPreferences.getPrefString("email")).
                withIcon(profileImage).withIdentifier(99);
        }else{
            profileDrawerItem = new ProfileDrawerItem().
                    withName(shortRoidPreferences.getPrefString("name")).
                    withEmail(shortRoidPreferences.getPrefString("email")).
                    withIcon(profile_img_url).withIdentifier(99);
        }

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.img12)
                .addProfiles(profileDrawerItem,
                        new ProfileSettingDrawerItem().withName("Forgot Password").withIdentifier(101).withIcon(GoogleMaterial.Icon.gmd_vpn_key),
                        new ProfileSettingDrawerItem().withName("Edit Profile").withIdentifier(102).withIcon(GoogleMaterial.Icon.gmd_mode_edit),
                        new ProfileSettingDrawerItem().withName("Logout").withIdentifier(103).withIcon(GoogleMaterial.Icon.gmd_keyboard_arrow_right)

                ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

                        if(profile.getIdentifier() == 101){
                            Toast.makeText(getApplicationContext(),"Forgot Password",Toast.LENGTH_SHORT).show();
                        }else if(profile.getIdentifier() == 102){
                            Toast.makeText(getApplicationContext(),"Edit Profile",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,EditProfileActivity.class));
                        }else if(profile.getIdentifier() == 103){
                            Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_SHORT).show();
                            shortRoidPreferences.setPrefBoolean("logged_in",false);
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            MainActivity.this.finish();
                        }

                        return false;
                    }
                })
                .build();
        if(profile_img_url!=null) {
            new DownloadImageTask(accountHeader).execute(profile_img_url);
        }
        navDrawer = new DrawerBuilder()
                .withActivity(this)
                .withSelectedItem(0)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accountHeader, true)
                .withShowDrawerOnFirstLaunch(true)
                .withFireOnInitialOnClick(true)
                .addDrawerItems(
                        item0,
                        item1,
                        item2,
                        item3,
                        item4,
                        new SectionDrawerItem().withName("Information"),
                        item5,
                        item6,
                        item7,
                        item8

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        Intent drawItem;

                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
                        fragment = new Fragment();

                        switch (position) {
                            case 0:
                                fragment = new HomeFragment();
                                toolbar.setTitle("Home");
                                
                                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                fragment = new ViewTutorialsFragment();
                                toolbar.setTitle("View Tutorials");
                                Toast.makeText(getApplicationContext(), "View Tutorials", Toast.LENGTH_SHORT).show();
                                break;

                            case 2:
                                fragment = new AddLocationFragment();
                                toolbar.setTitle("Add Location");
                                Toast.makeText(getApplicationContext(), "Add Location", Toast.LENGTH_SHORT).show();

                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                                break;
                            case 4:
                                fragment = new AddTutorialsFragment();
                                toolbar.setTitle("Add Tutorials");
                                Toast.makeText(getApplicationContext(), "Add Tutorials", Toast.LENGTH_SHORT).show();
                                break;

                            case 6:
                                drawItem = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(drawItem);
                                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                                break;
                            case 7:
                                drawItem = new Intent(getApplicationContext(), AboutUsActivity.class);
                                startActivity(drawItem);
                                Toast.makeText(getApplicationContext(), "About Us", Toast.LENGTH_SHORT).show();
                                break;
                            case 8:
                                drawItem = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                                startActivity(drawItem);
                                Toast.makeText(getApplicationContext(), "Privacy Policy", Toast.LENGTH_SHORT).show();
                                break;
                            case 9:
                                drawItem = new Intent(getApplicationContext(), ContactUsActivity.class);
                                startActivity(drawItem);
                                Toast.makeText(getApplicationContext(), "Contact Us", Toast.LENGTH_SHORT).show();
                                break;

                        }

                        fragmentTransaction.replace(R.id.flFragments, fragment);
                        fragmentTransaction.commit();

                        return false;
                    }
                }).build();

        navDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    private void userSessionCheck() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

                if (user != null) {

                    mUserId = user.getUid();

                    Intent i = getIntent();
                    User userObject = i.getParcelableExtra("USER_OBJECT");

                    Toast.makeText(getApplicationContext(), "User Signed in" + mUserId, Toast.LENGTH_SHORT).show();
                    mDatabase.child("users").child(mUserId).child("items").push().child("title").setValue(userObject);
                    System.out.println("USER INFO - " + userObject);
                    Toast.makeText(getApplicationContext(), "User Saved to DB", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "User Null", Toast.LENGTH_SHORT).show();
                    gotoLoginActivity();
                }

            }
        };

    }

    private void gotoLoginActivity() {

        Intent in = new Intent(this, LoginActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

       // menu.findItem(R.id.action_settings).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings).color(Color.WHITE).actionBar());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_notification) {
            Intent intnt = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(intnt);
            return true;
        }
        if (id == R.id.action_logout) {
            shortRoidPreferences.setPrefBoolean("logged_in", false);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            MainActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }else {
            navDrawer.closeDrawer();

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onResume() {
        MyApplication.getInstance().setConnectivityListener(this);
     /*   FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fuckThatShit = new HomeFragment();
        toolbar.setTitle("Home");
        ft.add(R.id.flFragments,fuckThatShit);
        ft.commit();*/
        super.onResume();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        AccountHeader accountHeader;
        public DownloadImageTask(AccountHeader accountHeader) {
            this.accountHeader = accountHeader;
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
            accountHeader.getActiveProfile().withIcon(result);
//            accountHeader.getHeaderBackgroundView().setImageBitmap(result);

            ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().
                    withName(shortRoidPreferences.getPrefString("name")).
                    withEmail(shortRoidPreferences.getPrefString("email")).
                    withIcon(result).withIdentifier(99);

            accountHeader.updateProfile(profileDrawerItem);
        }
    }

}
