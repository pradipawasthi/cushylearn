package com.manish.chushylearn.ApplicationConfig;

import android.app.Application;

import com.manish.chushylearn.ConnectionManager.ConnectivityReceiver;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;


public class MyApplication extends Application {
    private static FirebaseDatabase firebaseDatabase;
    public static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        if (!FirebaseApp.getApps(this).isEmpty())
        {   firebaseDatabase=FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);

        }
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       // FirebaseOptions options=FirebaseOptions.fromResource(this);
        //FirebaseDatabase.getInstance(FirebaseApp.initializeApp(this,options)).setPersistenceEnabled(true);
    }

    public static synchronized MyApplication getInstance(){
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }


    public static FirebaseDatabase getFireBaseInstance()
    {
        return firebaseDatabase;
    }
}
