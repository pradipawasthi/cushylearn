package com.manish.chushylearn.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manish.chushylearn.Activities.LocationDetailsActivity;
import com.manish.chushylearn.R;
import com.manish.chushylearn.imageutils.ImageLoader;
import com.manish.chushylearn.util.SingletonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;


public class FormFragment extends Fragment implements View.OnClickListener{


    List<JSONObject> offers = null;
    private JSONObject data;
    private ImageLoader imageLoader;
    private final String TAG = this.getClass().getName();
    private ListView listViewOffer ;
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.info_widow_layout, container, false);
        imageLoader = new ImageLoader(getContext());
        setGui(view);
        return view;
    }
    private void setGui(View view){
        Bundle bundle = this.getArguments();
        String locationStr = bundle.getString("location");
        try {
            JSONObject jsonObject = new JSONObject(locationStr);
            String name = jsonObject.getString("namePlace");
            String address = jsonObject.getString("placeDetails");
            String rating = jsonObject.getString("rating");
            String category = jsonObject.getString("category");
            String imageURL = jsonObject.has("imageURL")?jsonObject.getString("imageURL"):null;
            String feedback = jsonObject.getString("feedback");
            String locationId = jsonObject.getString("locationId");

            String offerString = "Tutorials Available (0)";
            if(jsonObject.has("offers")){
                offers = SingletonUtil.getInstance().getOffers
                        (locationId);

                Log.d(TAG,"Size :"+offers.size());
                offerString = "Tutorials Available ("+offers.size()+")>>>";
            }

            listViewOffer = (ListView) view.findViewById(R.id.list_offers);
            

            TextView offersCountText = (TextView)view.findViewById(R.id.offers_count);
            offersCountText.setText(offerString);
            offersCountText.setOnClickListener(this);

            TextView nameText = (TextView)view.findViewById(R.id.nametv);
            nameText.setText(name);
            TextView addrText = (TextView)view.findViewById(R.id.addressTv);
            addrText.setText(address);
            TextView catText = (TextView)view.findViewById(R.id.categoryTv);
            catText.setText(category);
            ImageView imageView = (ImageView)view.findViewById(R.id.addIcon);
            if(imageURL==null){
                imageView.setImageResource(R.drawable.default_image);
            }else {
                imageLoader.DisplayImage(imageURL, imageView);
            }
            //new DownloadImageTask(imageView).execute(imageURL);

            RatingBar simpleRatingBar = (RatingBar) view.findViewById(R.id.ratingValues);
            simpleRatingBar.setRating(Float.parseFloat(rating));

            TextView feedText = (TextView)view.findViewById(R.id.feedbackValue);
            feedText.setText(feedback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Well I don't have any...", Toast.LENGTH_SHORT).show();
            }
        };

//        view.findViewById(R.id.clinicType).setOnClickListener(onClickListener);
        view.findViewById(R.id.timingLabel).setOnClickListener(onClickListener);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==R.id.offers_count){
if(offers.size() >0){
            Toast.makeText(getContext(), "View All Subject Tutorials...", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(getContext(),LocationDetailsActivity.class);
//            offers = SingletonUtil.getInstance().getOffers("topicName");
//            intent.putExtra("Place Name", );
//            intent.putExtra("Place Details",offers.get());
//            intent.putExtra("Rating",detailLists.get(i).get("rating"));
//            intent.putExtra("category",detailLists.get(i).get("category"));
//            intent.putExtra("feedback",detailLists.get(i).get("feedback"));
//            intent.putExtra("adedByKey",detailLists.get(i).get("adedByKey"));
//            intent.putExtra("imageURL",detailLists.get(i).get("imageURL"));
//////////////////  intent.putExtra("placename",detailLists.get(i).get("namePlace"));

            startActivity(intent);
        }
        else
            {
                Toast.makeText(getContext(), "No Subject Tutorials are available...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPreExecute() {
            bmImage.setImageResource(R.drawable.ic_launcher);
        }
        protected void onPostExecute(Bitmap result) {
            if(result==null){
                bmImage.setImageResource(R.drawable.ic_launcher);
            }else {
                bmImage.setImageBitmap(result);
            }
        }

    }
}
