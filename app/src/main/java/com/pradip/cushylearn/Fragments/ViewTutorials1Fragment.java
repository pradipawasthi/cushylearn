package com.pradip.cushylearn.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.pradip.cushylearn.Adapters.CustomAdapter;
import com.pradip.cushylearn.ApplicationConfig.MyApplication;
import com.pradip.cushylearn.Model.ModuleModel;
import com.pradip.cushylearn.Model.SubjectModel;
import com.pradip.cushylearn.Model.UserLocation;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.helper.Constants;
import com.pradip.cushylearn.imageutils.ImageLoader;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pradip.cushylearn.util.SingletonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import shortroid.com.shortroid.ShortRoidPreferences.ShortRoidPreferences;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class ViewTutorials1Fragment extends Fragment {

    //multiautocomplete text for category

    Spinner locationSpinner;
    List<UserLocation> LocationStringList = new ArrayList<UserLocation>();

    List<String> LocationIdString = new ArrayList<String>();
    List<UserLocation> LocationArray = new ArrayList<>();
    ArrayAdapter<UserLocation> adapterLocation;
    UserLocation usrLoc;
    String s=null;


    List<SubjectModel> offerArray = new ArrayList<>();
    ArrayAdapter<SubjectModel> adapterOffer;
    ArrayAdapter<UserLocation> adapterId;
    Map items;
    JSONObject OfferJson;
    private JSONObject locOfferMap2 = new JSONObject();
    Map<String, JSONObject> offerList;
    GridView gridview;
    private ImageLoader imageLoader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_fragment_b, container, false);
        View view = inflater.inflate(R.layout.fragment_pop_offer, container, false);

        gridview = (GridView)view.findViewById(R.id.gridView1);

        final FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference databaseReference = database.getReference(Constants.OFFER_FOLDER_NAME);
        OfferJson = new JSONObject();
        imageLoader = new ImageLoader(getContext());

        databaseReference .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot offersSnapshot) {
                offerList = (Map<String, JSONObject>) offersSnapshot.getValue();

                Iterator childOffers = offersSnapshot.getChildren().iterator();
                try {
                    while (childOffers.hasNext()) {
                        DataSnapshot childOffer = (DataSnapshot) childOffers.next();
                        Log.d(TAG,childOffer.getValue().toString());
                        SubjectModel offerModel = childOffer.getValue(SubjectModel.class);
                        JSONObject offerObj = new JSONObject();
                        offerObj.put("offername",offerModel.getSubject_Name());
                        offerObj.put("offerid",offerModel.getSubjectId());
                        offerList.get(offerObj);
//                                                    offerList2.add(offerList.keySet());

                    }

                    locOfferMap2.put("offers", offerList);
                    //Map<String,OfferModel> allItems = offerList;
                    CustomAdapter customAdapter = new CustomAdapter(getActivity(), offerList);
                    gridview.setAdapter(customAdapter);
//                                                SingletonUtil.getInstance().updateLocation(locId,jsonObject);
//                                                SingletonUtil.getInstance().addOfferList(subId,offerList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










        //  fetchOffersAndShowInMap();
        // todo();





        return view;
//        return v;
    }

    private void todo() {

        System.out.println(offerList);
        // List<ItemObject> allItems = getAllItemObject();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), offerList);
        gridview.setAdapter(customAdapter);

    }

    private void fetchOffersAndShowInMap() {
        final FirebaseDatabase database = MyApplication.getFireBaseInstance();
        final DatabaseReference databaseReference = database.getReference(Constants.OFFER_FOLDER_NAME);
        OfferJson = new JSONObject();
        databaseReference .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot offersSnapshot) {
                offerList = (Map<String, JSONObject>) offersSnapshot.getValue();

                Iterator childOffers = offersSnapshot.getChildren().iterator();
                try {
                    while (childOffers.hasNext()) {
                        DataSnapshot childOffer = (DataSnapshot) childOffers.next();
                        Log.d(TAG,childOffer.getValue().toString());
                        SubjectModel offerModel = childOffer.getValue(SubjectModel.class);
                        JSONObject offerObj = new JSONObject();
                        offerObj.put("offername",offerModel.getSubject_Name());
                        offerObj.put("offerid",offerModel.getSubjectId());
                        offerList.get(offerObj);
//                                                    offerList2.add(offerList.keySet());

                    }

                    locOfferMap2.put("offers", offerList);
//                                                SingletonUtil.getInstance().updateLocation(locId,jsonObject);
//                                                SingletonUtil.getInstance().addOfferList(locId,offerList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

