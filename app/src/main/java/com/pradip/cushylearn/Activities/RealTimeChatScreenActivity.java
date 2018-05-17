package com.pradip.cushylearn.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pradip.cushylearn.Adapters.ChatAdapter;
import com.pradip.cushylearn.ApplicationConfig.MyApplication;
import com.pradip.cushylearn.Model.Chat;
import com.pradip.cushylearn.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;


/**
 * Created by JohnConnor on 03-Sep-16.
 */
public class RealTimeChatScreenActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    Button send;
    EditText chatMessage;
    Chat chat;
    List<Chat> chats;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    String key;
    RecyclerView.LayoutManager mLayoutManager;
    ShortRoidPreferences shortRoidPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_time_chat_activity_layout_main);

        toolbar();

        key = getIntent().getStringExtra("key");
        init();

        test();
    }

    void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ezMap");
    }

    void init() {
        send = (Button) findViewById(R.id.send_button);
        chatMessage = (EditText) findViewById(R.id.chat_message);
        chat = new Chat();
        chats = new ArrayList<>();
        try {
            shortRoidPreferences = new ShortRoidPreferences(RealTimeChatScreenActivity.this, "chat", MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_chat);
        mLayoutManager = new LinearLayoutManager(RealTimeChatScreenActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setHasFixedSize(true);
        adapter = new ChatAdapter(RealTimeChatScreenActivity.this, chats);
        recyclerView.setAdapter(adapter);

    }

    void test() {
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference myRef = database.getReference("groups");
        final DatabaseReference my = myRef.child(key).child("messages");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatMessage.getText().length() != 0) {
                    chat.setChatText(chatMessage.getText().length() != 0 ? chatMessage.getText().toString() : "Hello");
                    chat.setName(shortRoidPreferences.getPrefString("name"));
                    chat.setPhone(shortRoidPreferences.getPrefString("phone"));
                    chat.setTimestamp(Calendar.getInstance().getTimeInMillis());
                    my.push().setValue(chat);

                    chatMessage.setText("");
                }
            }
        });

        final HashMap<String, Chat> h = new HashMap<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey() + " " + dataSnapshot.getValue());

                h.put(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class));
                chats.add(h.get(dataSnapshot.getKey()));
                adapter.notifyItemRangeInserted(0, chats.size());
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chats.size() - 1);
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.


                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }
        };

        my.addChildEventListener(childEventListener);
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
      /*  if (id == R.id.action_settings) {
            Intent intent = new Intent(RealTimeChatScreenActivity.this, GroupContactShowActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}

