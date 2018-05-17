package com.manish.chushylearn.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manish.chushylearn.Adapters.GroupShowAdapter;
import com.manish.chushylearn.ApplicationConfig.MyApplication;
import com.manish.chushylearn.Model.ChatUser;
import com.manish.chushylearn.Model.GroupModel;
import com.manish.chushylearn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

/**
 * Created by John Corner on 26-09-2016.
 */

public class ChatActivity extends AppCompatActivity {


    RecyclerView mrecyclerView;
    ImageView defaultImage;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView defaultText;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ShortRoidPreferences shortRoidPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar();
        fab();
        init();
       // query();
        //gTypes.clear();gNames.clear();gKeys.clear();
        swipeListener();

    }
    void init()
    {
        defaultText=(TextView)findViewById(R.id.default_text);
        defaultImage=(ImageView)findViewById(R.id.default_chat_img);
        try {
            shortRoidPreferences=new ShortRoidPreferences(ChatActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe);
        mrecyclerView=(RecyclerView)findViewById(R.id.group_show_recyclerview);
        layoutManager = new LinearLayoutManager(ChatActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            mrecyclerView.setLayoutManager(layoutManager);
        }
        //recyclerView.setHasFixedSize(true);
        //adapter=new ChatAdapter(GroupShowActivity.this,chats);
        //mrecyclerView.setAdapter(adapter);
    }
    void query() {
        try {
            final List<String> gKeys;
            final List<String> gNames;
            final List<Boolean> gTypes;
            gKeys=new ArrayList<>();
            gNames=new ArrayList<>();
            gTypes=new ArrayList<>();
            final FirebaseDatabase database = MyApplication.getFireBaseInstance();
            final DatabaseReference databaseReference = database.getReference("users");
            databaseReference.child(shortRoidPreferences.getPrefString("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
                    for (int j = 1; j < chatUser.getGroups().size(); j++)
                        gKeys.add(chatUser.getGroups().get(j));
                    DatabaseReference dR = database.getReference("groups");
                    for (int i = 0; i < gKeys.size(); i++) {
                        Log.d("key", gKeys.get(i) + gKeys.size());
                        dR.child(gKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GroupModel groupModel = dataSnapshot.getValue(GroupModel.class);
                                gNames.add(groupModel.getgName());
                                gTypes.add(groupModel.isgMode());
                                Log.d("name", groupModel.getgName());
                                if (gKeys.size() == gNames.size()) {
                                    if(gKeys.size()>0) {
                                        defaultImage.setVisibility(View.GONE);
                                        defaultText.setVisibility(View.GONE);
                                        try {
                                            swipeRefreshLayout.setRefreshing(false);
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                        adapter = new GroupShowAdapter(ChatActivity.this, gNames, gKeys, gTypes);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
                                            mrecyclerView.setAdapter(adapter);
                                        }
                                    }
                                    else
                                    {
                                        defaultImage.setVisibility(View.VISIBLE);
                                        defaultText.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            gKeys.clear();gTypes.clear();gNames.clear();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    void swipeListener()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
    }
    private void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat");
    }

    private void fab() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_group_add).color(Color.WHITE).sizeDp(24));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCreateGroup = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(toCreateGroup);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","Called");
        //gTypes.clear();gNames.clear();gKeys.clear();
        query();
       // adapter.notifyDataSetChanged();
    }
}
