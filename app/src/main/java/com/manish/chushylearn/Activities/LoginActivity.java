package com.manish.chushylearn.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manish.chushylearn.R;
import com.manish.chushylearn.Model.ChatUser;
import com.manish.chushylearn.ApplicationConfig.MyApplication;
import com.manish.chushylearn.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;


public class LoginActivity extends AppCompatActivity {

    private Vibrator vib;
    private Animation animShake;

    private FirebaseAuth mFirebaseAuth;
    private ShortRoidPreferences shortRoidPreferences;

    String EMAIL, PASSWORD;

    EditText email, password;
    TextInputLayout emailLayout, passwordLayout;
    TextView forgotPassword, loginRegisterView;
    ProgressBar mProgressBar;
    Button loginBtn;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //

        //

        initialization();

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);     //Animation

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);    //Vibration

        underlineText();    //Underlining Text in App

        loginRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { transferToRegister(); }
        }); //Directing to Register Activity

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(getApplicationContext(),"Forgot Password Clicked",Toast.LENGTH_SHORT).show();     }
        });

    }

    private void initialization(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        //intitalizing preferences
        try {
            shortRoidPreferences=new ShortRoidPreferences(LoginActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        email = (EditText)findViewById(R.id.input_login_email);
        password = (EditText)findViewById(R.id.input_login_password);

        emailLayout = (TextInputLayout)findViewById(R.id.input_layout_login_email);
        passwordLayout = (TextInputLayout)findViewById(R.id.input_layout_login_password);

        forgotPassword = (TextView)findViewById(R.id.btn_forgot_password);
        loginRegisterView = (TextView)findViewById(R.id.login_activity_register);

        mProgressBar = (ProgressBar)findViewById(R.id.login_pregress_bar);

        loginBtn = (Button)findViewById(R.id.btn_login);
    }

    private void loginUser() {

        if(!checkEmail()){
            email.setAnimation(animShake);
            email.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }

        if(!checkPassword()){
            password.setAnimation(animShake);
            password.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }

        emailLayout.setErrorEnabled(false);
        passwordLayout.setErrorEnabled(false);

        mProgressBar.setVisibility(View.VISIBLE);

        EMAIL = email.getText().toString().trim();
        PASSWORD = password.getText().toString().trim();

        signInFirebase();   //Logging User Via Firebase


    }

    private void getFromDatabase(final String email, final TempInterface tempInterface)
    {   FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference myRef = database.getReference("users");


           myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for (final DataSnapshot ds : dataSnapshots)
                {

                    ChatUser chatUser=new ChatUser();
                    Object usrObj = new Object();
                    try {
                        usrObj = ds.getValue();
                        if(usrObj != null && (usrObj.getClass() == ArrayList.class)){
                            Map objMap = (HashMap)((ArrayList)usrObj).get(0);
                            Log.d("email",objMap.toString());
                        }else{
                            chatUser = ds.getValue(ChatUser.class);
                        }

                    }catch(Exception e){
                        Log.d("ChatUser",e.getLocalizedMessage());
                    }
                    Log.d("email",email);
                    if(chatUser != null && chatUser.getEmail()!=null && chatUser.getEmail().contentEquals(email))
                    {
                        String userKey=ds.getKey();
                        shortRoidPreferences.setPrefString("key",userKey);
                        Log.d("firebase",ds.getKey());
                        //ds.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatUser chatUser=dataSnapshot.getValue(ChatUser.class);
                                shortRoidPreferences.setPrefString("name",chatUser.getName());
                                shortRoidPreferences.setPrefString("phone",chatUser.getPhone());


                                Gson gson = new Gson();
                                String json = gson.toJson(chatUser);
                                shortRoidPreferences.setPrefString(Constants.CHATUSER,json);

                                tempInterface.queryDone();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Log.d("firebase",ds.getRef().getKey());

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("firebase",databaseError.getDetails());
            }
        });


    }
    private void signInFirebase() {

        mFirebaseAuth.signInWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    shortRoidPreferences.setPrefBoolean("logged_in",true);
                    shortRoidPreferences.setPrefString("email",task.getResult().getUser().getEmail());
                    getFromDatabase(shortRoidPreferences.getPrefString("email"), new TempInterface() {
                        @Override
                        public void queryDone() {
                            Toast.makeText(getApplicationContext(),"User Logged In",Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            Log.d("phone",shortRoidPreferences.getPrefString("phone"));
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });

                }else{
                    mProgressBar.setVisibility(View.GONE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(task.getException().getMessage())
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    private boolean checkPassword() {

        if(password.getText().toString().isEmpty()){

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("Please Enter a Password");
            return false;
        }

        passwordLayout.setErrorEnabled(false);
        return true;

    }

    private boolean checkEmail() {

        String email_input = email.getText().toString().trim();
        if (email_input.isEmpty() || !isValidEmail(email_input)) {

            emailLayout.setErrorEnabled(true);
            emailLayout.setError("Please Enter a Valid Email");
            email.setError("Valid Input Required");
            requestFocus(email);
            return false;
        }
        emailLayout.setErrorEnabled(false);
        return true;

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void transferToRegister() {

        Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(i);

    }

    private void underlineText() {

        SpannableString fp = new SpannableString("Forgot Password");
        fp.setSpan(new UnderlineSpan(), 0, fp.length(), 0);
        forgotPassword.setText(fp);

        SpannableString lr = new SpannableString("Register!!");
        lr.setSpan(new UnderlineSpan(), 0, lr.length(), 0);
        loginRegisterView.setText(lr);

    }

    @Override
    protected void onResume() {
        super.onResume();
       mProgressBar.setVisibility(View.GONE);
    }
    interface TempInterface
    {
        void queryDone();
    }
}
