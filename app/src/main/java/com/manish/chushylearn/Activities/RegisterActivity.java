package com.manish.chushylearn.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manish.chushylearn.Model.User;
import com.manish.chushylearn.Model.UserLocation;
import com.manish.chushylearn.R;
import com.manish.chushylearn.Model.ChatUser;
import com.manish.chushylearn.ApplicationConfig.MyApplication;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;


public class RegisterActivity extends AppCompatActivity {


    static int RESULT_LOAD_IMAGE = 1;
    private Vibrator vib;
    private ProgressBar progressBar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mUserId;
    private ShortRoidPreferences shortRoidPreferences;
    String NAME, EMAIL, PHN, PASSWORD, ProfileURL;
    String picturePath=null;
    String profileEncoded=null;

    EditText name, email, phone, password, confirmPassword;
    TextInputLayout nameLayout, emailLayout, phoneLayout, passwordLayout, confirmPasswordLayout;
    Button reg;
    TextView skip;
    Animation animShake;
    CheckBox checkBox1;
    CircleImageView profile_img;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_register);

        initialization();

        underlineText();    //Underlining Text in App

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);     //Animation

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);    //Vibration

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();   //Registration Click Listener
            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipRegistrationSection();  //Skip Click Listener
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();   //Registration Click Listener
            }
        });


    }

    private void selectImage() {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);


    }

    private void initialization() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //shortroid init
        try {
            shortRoidPreferences=new ShortRoidPreferences(RegisterActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }

        name = (EditText) findViewById(R.id.input_name);
        email = (EditText) findViewById(R.id.input_email);
        phone = (EditText) findViewById(R.id.input_phone);
        password = (EditText) findViewById(R.id.input_password);
        confirmPassword = (EditText) findViewById(R.id.input_confirm_password);

        nameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        emailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        phoneLayout = (TextInputLayout) findViewById(R.id.input_layout_phone);
        passwordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);

        reg = (Button) findViewById(R.id.btn_register);
        skip = (TextView) findViewById(R.id.btn_skip);

        progressBar = (ProgressBar) findViewById(R.id.register_progress_bar);

        checkBox1=(CheckBox) findViewById(R.id.checkBox_show_img);
        profile_img=(CircleImageView) findViewById(R.id.reg_profile_img);

    }




    private void underlineText() {

        SpannableString ss = new SpannableString("Already registered ");
        ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
        skip.setText(ss);

    }
    //use ShortRoidPreferences for SharedPreferences
    private void runAtInstallation() {

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {

            Intent act = new Intent(getApplicationContext(), MainActivity.class);
            act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(act);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }
    }

    private void skipRegistrationSection() {

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);

    }

    private void submitForm() {

        if (!checkName()) {
            name.setAnimation(animShake);
            name.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }
        if (!checkEmail()) {
            email.setAnimation(animShake);
            email.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }
        if (!checkPhone()) {
            phone.setAnimation(animShake);
            phone.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }
        if (!checkPassword()) {
            password.setAnimation(animShake);
            password.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }
        if (!checkConfirmPassword()) {
            confirmPassword.setAnimation(animShake);
            confirmPassword.startAnimation(animShake);
            vib.vibrate(60);
            return;
        }


        nameLayout.setErrorEnabled(false);
        emailLayout.setErrorEnabled(false);
        phoneLayout.setErrorEnabled(false);
        passwordLayout.setErrorEnabled(false);
        confirmPasswordLayout.setErrorEnabled(false);


        NAME = name.getText().toString().trim();
        EMAIL = email.getText().toString().trim();
        PHN = phone.getText().toString().trim();
        PASSWORD = password.getText().toString();


        //ProfileURL=().toString();


        progressBar.setVisibility(View.VISIBLE);

        authUser();     //authenticating  User via Email & Password

    }

    private void authUser() {

        mFirebaseAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage(task.getException().getMessage())
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    progressBar.setVisibility(View.GONE);
//
                    User userObj = new User();
                    userObj.setNAME(NAME);
                    userObj.setEMAIL(EMAIL);
                    userObj.setPHONE(PHN);
                    userObj.setProfileImageURL(profileEncoded);
                   // userObj.setProfileImageURL(p);
                    shortRoidPreferences.setPrefString("fname",NAME);
                    shortRoidPreferences.setPrefString("email",EMAIL);
                    shortRoidPreferences.setPrefString("phone",PHN);
                    shortRoidPreferences.setPrefString("profile_img",profileEncoded);
                    Log.d("login",shortRoidPreferences.getPrefString("name")+shortRoidPreferences.getPrefString("email")
                            +shortRoidPreferences.getPrefString("phone")+shortRoidPreferences.getPrefString("profile_img"));
                    Intent i = new Intent();
                    i.putExtra("USER_OBJECT",userObj);
                    FirebaseDatabase database = MyApplication.getFireBaseInstance();
                    final DatabaseReference myRef = database.getReference("users");
                    ChatUser chatUser=new ChatUser();
                    chatUser.setPhone(shortRoidPreferences.getPrefString("phone"));
                    chatUser.setName(shortRoidPreferences.getPrefString("name"));
                    chatUser.setEmail(shortRoidPreferences.getPrefString("email"));
                    chatUser.setProfileURL(shortRoidPreferences.getPrefString("profile_img"));
                    List<UserLocation> userLocations=new ArrayList<UserLocation>();
                    userLocations.add(new UserLocation());
                    chatUser.setUserLocations(userLocations);
                    List<String> d=new ArrayList<>();
                    d.add("");
                    chatUser.setGroups(d);
                    myRef.push().setValue(chatUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            shortRoidPreferences.setPrefBoolean("logged_in",true);
                            shortRoidPreferences.setPrefString("key",databaseReference.getKey());
                            Log.d("Key",shortRoidPreferences.getPrefString("key"));

                        }
                    });
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

        });
    }

    private boolean checkConfirmPassword() {
        if (confirmPassword.getText().toString().isEmpty()) {
            confirmPasswordLayout.setErrorEnabled(true);
            confirmPasswordLayout.setError("Please Confirm Password");
            return false;
        }

        if (!confirmPassword.getText().toString().equals(password.getText().toString())) {
            confirmPasswordLayout.setErrorEnabled(true);
            confirmPasswordLayout.setError("Password did not match, Re-enter Password!");
            return false;
        }

        confirmPasswordLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {
        if (password.getText().toString().isEmpty()) {

            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("Please Enter a Password");
            return false;
        }

        if (password.getText().toString().length() < 6) {
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("Password too short, enter minimum 6 character!");
            return false;
        }

        passwordLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkName() {
        if (name.getText().toString().trim().isEmpty()) {

            nameLayout.setErrorEnabled(true);
            nameLayout.setError("Please Enter a Name");
            name.setError("Valid Input Required");
            return false;
        }
        nameLayout.setErrorEnabled(false);
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

    private boolean checkPhone() {
        if (phone.getText().toString().trim().isEmpty()) {

            phoneLayout.setError("Enter a Mobile Number");
            requestFocus(phone);
            return false;
        }
        phoneLayout.setErrorEnabled(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.reg_profile_img);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(picturePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
            // byte[] b = baos.toByteArray();
            profileEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);


        }
        if( picturePath!=null)
            checkBox1.setChecked(true);
        else
            Toast.makeText(getApplicationContext(),"Switch on GPS for better performance",Toast.LENGTH_SHORT).show();


    }



}