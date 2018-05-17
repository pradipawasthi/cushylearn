package com.manish.chushylearn.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.manish.chushylearn.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.checked;
import static android.R.attr.data;
import static android.R.attr.name;

/**
 * Created by SID on 07-Dec-16.
 */

public class NotificationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText distance;
    private TextView notify,category,km;
    private Spinner spinner;
    private Button buttonsave;
    private CheckBox status,delet;
    //  Switch aswitch;
    //  private Firebase mRootRef;

    private DatabaseReference databaseReference;
    private String[] categories = {"Transportation", "Car Service", "Professional", "Public", "Shopping", "Services", "Food/Drinks",
            "Entertainment", "Lodging", "Others"};
    private String cat = categories[0];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);
        //  mRootRef=new Firebase("https://ezmap-devi.firebaseio.com/users/-KZ03OVqBWSJ1nfEIrfW");
        buttonsave= (Button) findViewById(R.id.button_save);
        distance=(EditText)findViewById(R.id.input_distance);
        category=(TextView)findViewById(R.id.text_view);
        notify= (TextView) findViewById(R.id.text_view1);
        km= (TextView) findViewById(R.id.text_distance);
        spinner= (Spinner) findViewById(R.id.spinner_category);
        status= (CheckBox) findViewById(R.id.checkBox);
        delet= (CheckBox) findViewById(R.id.checkBox_Delet);
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.e("Count " ,""+snapshot.getChildrenCount());
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            Log.e("Count " ,""+snapshot.getChildrenCount());
                            Iterable<DataSnapshot> dataSnapshot = postSnapshot.getChildren();
                            for(DataSnapshot dataSnapshot1 : dataSnapshot) {
                                if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equalsIgnoreCase(dataSnapshot1.getValue().toString())) {
                                    String userKey = postSnapshot.getKey();
                                    //StringBuffer result = new StringBuffer();
                                    //result.append("ON: ").append(status.isChecked());
                                    String km =distance.getText().toString().trim();
                                    UserNotification userNotification=new UserNotification(km,cat, status.isChecked()?"true":"false");
                                    databaseReference.child(userKey).child("notification").setValue(userNotification);
                                    Toast.makeText(NotificationActivity.this, "Saved succesfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("The read failed: " ,firebaseError.getMessage());
                    }
                });
                //databaseReference.child(notification.getUid()).setValue(userNotification);
            }
        });


 /*       aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {


                    String km =distance.getText().toString().trim();
                    UserNotification userNotification=new UserNotification(km,cat);
                    FirebaseUser notification=FirebaseAuth.getInstance().getCurrentUser();
                    databaseReference.child(notification.getUid()).setValue(userNotification);


                 *//*   Firebase childref=mRootRef.child("Category");
                    childref.setValue(cat);
                    String km =distance.getText().toString().trim();*//*
               *//*     saveUserNotification();*//*

                    //result.setText("ON");
                    //result.setVisibility(View.VISIBLE);

                }
                else{

                    //result.setText("OFF");
                }
            }



        });*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Categories,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //saswitch.setOnClickListener(this);

    }
    private void saveUserNotification(){



        Toast.makeText(this,"Notification ON..",Toast.LENGTH_LONG).show();

    }



    /*public void changeNotificationState(View view){
        boolean checked=((ToggleButton)view).isChecked();
        if (checked)
        {
            result.setText("ON");
            result.setVisibility(View.VISIBLE);
        }
        else{

            result.setText("OFF")
        }
    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //EditText name= (EditText) view;
        //Toast.makeText(this, "You Selected"+ category.getText(), Toast.LENGTH_SHORT).show();
        cat = categories[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
