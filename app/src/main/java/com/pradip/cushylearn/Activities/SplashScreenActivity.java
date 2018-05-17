package com.pradip.cushylearn.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.pradip.cushylearn.R;
import com.google.firebase.auth.FirebaseAuth;

import shortroid.com.shortroid.ShortRoidPreferences.FileNameException;
import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1500;
    private ShortRoidPreferences shortRoidPreferences;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        try {
            shortRoidPreferences=new ShortRoidPreferences(SplashScreenActivity.this,"chat",MODE_PRIVATE);
        } catch (FileNameException e) {
            e.printStackTrace();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(shortRoidPreferences.getPrefBoolean("logged_in"))
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                else {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                    finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
