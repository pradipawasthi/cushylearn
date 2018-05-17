package com.manish.chushylearn.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.manish.chushylearn.ApplicationConfig.MyApplication;
import com.manish.chushylearn.Model.ChatUser;
import com.manish.chushylearn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;



public class GroupContactShowActivity extends AppCompatActivity {
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    FloatingActionButton floatingActionButton;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_group_contacts_show);

        toolbar();

        key = getIntent().getStringExtra("key");
        Log.d("key", key);

        init();
        getContacts();
        addContact();
    }

    void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");
    }

    void init() {
        listView = (ListView) findViewById(R.id.list_contacts);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabs);
    }

    void getContacts() {
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference databaseReference = database.getReference("groups");
        databaseReference.child(key).child("gMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("datasnap", dataSnapshot.toString() + databaseReference.toString());
                List<String> temp = (List<String>) dataSnapshot.getValue();
                String contacts[] = new String[temp.size()];
                for (int i = 0; i < temp.size(); i++)
                    contacts[i] = temp.get(i);
                arrayAdapter = new ArrayAdapter<String>(GroupContactShowActivity.this, android.R.layout.simple_list_item_1, contacts);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ShortRoidPreferences shortRoidPreferences = null;
    void addContact() {
        floatingActionButton.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_add).color(Color.WHITE).sizeDp(24));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(GroupContactShowActivity.this, AddContactActivity.class);
//                intent.putExtra("key", key);
//                startActivity(intent);

                LayoutInflater layoutInflater = LayoutInflater.from(GroupContactShowActivity.this);
                View promptView = layoutInflater.inflate(R.layout.custom_alert_dialog, null);



                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GroupContactShowActivity.this, R.style.CustomDialog);

                alertDialogBuilder.setView(promptView);

                final EditText addFriend = (EditText)promptView.findViewById(R.id.add_contact_edit_text);

                try {
                    shortRoidPreferences=new ShortRoidPreferences(GroupContactShowActivity.this,"chat",MODE_PRIVATE);
                } catch (FileNameException e) {
                    e.printStackTrace();
                }
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();


                                String edit_text_data = addFriend.getText().toString().trim();

                                if (edit_text_data.length() == 10) {
                                    addContactOperation();
                                }
                                else if(edit_text_data.contentEquals(shortRoidPreferences.getPrefString("phone")))
                                {
                                    Toast.makeText(getApplicationContext(), "Don't Enter your own number", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Invalid Phone No", Toast.LENGTH_SHORT).show();
                                }
                            }

                            void addContactOperation() {
                                final FirebaseDatabase database = MyApplication.getFireBaseInstance();
                                final DatabaseReference myRef = database.getReference("users");


                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                                        for (final DataSnapshot ds : dataSnapshots) {
                                            ChatUser chatUser = ds.getValue(ChatUser.class);
                                            if (chatUser.getPhone().contentEquals(addFriend.getText().toString().trim())) {
                                                String userKey = ds.getKey();
                                                Log.d("firebase", ds.getKey());
                                                ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
                                                        List<String> temp = chatUser.getGroups();
                                                        temp.add(key);
                                                        Map<String, Object> map = new HashMap<String, Object>();
                                                        map.put("groups", temp);
                                                        ds.getRef().updateChildren(map);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                                Log.d("firebase", ds.getRef().getKey());
                                                break;
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                final DatabaseReference databaseReference = database.getReference("groups");
                                databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("datasnap", dataSnapshot.toString() + databaseReference.toString());
                                        List<String> temp = (List<String>) dataSnapshot.child("gMembers").getValue();
                                        temp.add(addFriend.getText().toString().trim());
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("gMembers", temp);
                                        dataSnapshot.getRef().updateChildren(map);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }



                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }


}
