package com.manish.chushylearn.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.manish.chushylearn.Adapters.MyCustomPagerAdapter;
import com.manish.chushylearn.R;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailsActivity extends AppCompatActivity {

    private static final String TAG =LocationDetailsActivity.class.getSimpleName() ;
    String name,details;
    String rating;
    String category, feedback;
    // String addedBykey;
    //List<String> imageURLList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);


        // code for back arrow button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_already);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home);
        finish();
        return super.onOptionsItemSelected(item);
    }
}