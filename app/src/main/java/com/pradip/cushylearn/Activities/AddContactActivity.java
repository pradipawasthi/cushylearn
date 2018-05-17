package com.pradip.cushylearn.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pradip.cushylearn.ApplicationConfig.MyApplication;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.Model.ChatUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by JohnConnor on 07-Sep-16.
 */
public class AddContactActivity extends AppCompatActivity {

    EditText editText;
    Button add;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_add_contact);
        key = getIntent().getStringExtra("key");
        init();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().length()==10)
                    addOperation();
                else
                    Toast.makeText(getApplicationContext(),"Invalid Phone No",Toast.LENGTH_SHORT).show();
            }
        });

    }
    void init()
    {
        editText=(EditText)findViewById(R.id.phone_contact);
        add=(Button)findViewById(R.id.add_contact);
    }
    void addOperation() {
        FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference myRef = database.getReference("users");



            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                    for (final DataSnapshot ds : dataSnapshots) {
                        ChatUser chatUser = ds.getValue(ChatUser.class);
                        if (chatUser.getPhone().contentEquals(editText.getText().toString())) {
                            String userKey = ds.getKey();
                            Log.d("firebase", ds.getKey());
                            //ds.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
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

        final DatabaseReference databaseReference=database.getReference("groups");
        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("datasnap",dataSnapshot.toString() + databaseReference.toString());
                List<String> temp=(List<String>)dataSnapshot.child("gMembers").getValue();
                temp.add(editText.getText().toString());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("gMembers", temp);
                dataSnapshot.getRef().updateChildren(map);
                AddContactActivity.this.finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
