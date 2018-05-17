package com.manish.chushylearn.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.manish.chushylearn.ApplicationConfig.MyApplication;
import com.manish.chushylearn.Model.ChatUser;
import com.manish.chushylearn.Model.GroupModel;
import com.manish.chushylearn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;


/**
 * Created by MOHIT KHAITAN on 26-09-2016.
 */

public class CreateGroupActivity extends AppCompatActivity {

    TextView addFriend,groupName,groupMessage,phoneNumber;
    List<String> peopleToAdd;
    RadioGroup radioGroup;
    RadioButton private_radio,public_radio;
    FirebaseDatabase database;
    Button createGroupButton;
    ShortRoidPreferences shortRoidPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_group);
        try {
            shortRoidPreferences=new ShortRoidPreferences(CreateGroupActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
            init(); // for initialization

            operation();//for main operations

            underlineText();

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Add Friend",Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void init()
    {
        peopleToAdd=new ArrayList<>();
        addFriend = (TextView)findViewById(R.id.add_friend);
        groupName=(TextView)findViewById(R.id.input_group_name);
        groupMessage=(TextView)findViewById(R.id.input_group_message);
        phoneNumber=(TextView)findViewById(R.id.input_group_phone);
        radioGroup=(RadioGroup)findViewById(R.id.group_type_radio);
        private_radio=(RadioButton)findViewById(R.id.radio_private);
        public_radio=(RadioButton)findViewById(R.id.radio_public);
        createGroupButton=(Button)findViewById(R.id.create_grp_btn);
        database = MyApplication.getFireBaseInstance();
    }
    //TODO: Make Add Contact functionality and add to peopletoAdd List<String>
    private void operation()
    {
        phoneNumber.setText(shortRoidPreferences.getPrefString("phone"));
        peopleToAdd.add(phoneNumber.getText().toString());

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 boolean b = private_radio.isChecked();
                Log.v("bool",private_radio.isChecked()+"");

                chatQuery(database,groupName.getText().toString(),b,peopleToAdd,groupMessage.getText().toString());

            }
        });

    }
    private void underlineText() {

        SpannableString fp = new SpannableString("Add Friend");
        fp.setSpan(new UnderlineSpan(), 0, fp.length(), 0);
        addFriend.setText(fp);

    }

    void chatQuery(final FirebaseDatabase database, String gName, boolean gMode, final List<String> gMembers,String gMessage)
    {

        final DatabaseReference myRef = database.getReference("groups");
        GroupModel groupModel = new GroupModel();
        groupModel.setgName(gName);
        groupModel.setgMode(gMode);
        groupModel.setgMessage(gMessage);
        groupModel.setgMembers(gMembers);
        myRef.push().setValue(groupModel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                toUser(database,databaseReference.getKey(),gMembers);

            }
        });

    }
    void toUser(FirebaseDatabase database, final String key, List<String> phoneNumbers)
    {
        final DatabaseReference myRef = database.getReference("users");

        for( final String phone : phoneNumbers)
        {   myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for (final DataSnapshot ds : dataSnapshots)
                {
                    ChatUser chatUser=ds.getValue(ChatUser.class);
                    if(chatUser.getPhone().contentEquals(phone))
                    {
                        String userKey=ds.getKey();
                        Log.d("firebase",ds.getKey());
                        //ds.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatUser chatUser=dataSnapshot.getValue(ChatUser.class);
                                List<String> temp=chatUser.getGroups();
                                temp.add(key);
                                Toast.makeText(CreateGroupActivity.this,"Wait",Toast.LENGTH_SHORT).show();
                                Map<String,Object> map=new HashMap<String,Object>();
                                map.put("groups",temp);
                                ds.getRef().updateChildren(map);
                                CreateGroupActivity.this.finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Log.d("firebase",ds.getRef().getKey());
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }
    }
}
