package com.pradip.cushylearn.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pradip.cushylearn.Activities.LocationDetailsActivity;
import com.pradip.cushylearn.Fragments.ViewTutorialsFragment;
import com.pradip.cushylearn.R;
import com.pradip.cushylearn.imageutils.ImageLoader;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.ContextCompat.startActivity;
import static java.security.AccessController.getContext;


public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater layoutinflater;
    private Map<String, JSONObject> listStorage;
    private Context context;
    private ImageLoader imageLoader;
    public static final String TAG = ViewTutorialsFragment.class.getSimpleName();
    public CustomAdapter(Context context, Map<String, JSONObject> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }




    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            Context context = parent.getContext();
            imageLoader = new ImageLoader(context);
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.pop_offer_list, parent, false);
            listViewHolder.displayClassesImg = (ImageView)convertView.findViewById(R.id.sub_img_display);
            listViewHolder.displaySubjectName = (TextView)convertView.findViewById(R.id.sub_name);
            listViewHolder.displaySubjectDescription = (TextView)convertView.findViewById(R.id.sub_des);
            listViewHolder.displayLocationName=(TextView)convertView.findViewById(R.id.sub_loc_name);
            listViewHolder.displayLessonName = (TextView)convertView.findViewById(R.id.sub_lesson);
            //  listViewHolder.displayLocationaddrs=(TextView)convertView.findViewById(R.id.offer_shopaddress_display);

            convertView.setTag(listViewHolder);
            convertView.setOnClickListener(this);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        /*********
         //        listViewHolder.displayClassesImg.setImageResource(listStorage.getOfferURI());
         listViewHolder.displaySubjectName.setText("helllo");
         //listViewHolder.displaySubjectDescription.setText("sfsdgdgsdgsdgs");
         //  JSONObject offerMap = listStorage.get(0);


         try {
         listViewHolder.displaySubjectDescription.setText((CharSequence) listStorage.get(position).getJSONObject("offername"));
         } catch (JSONException e) {
         e.printStackTrace();
         }

         *********************/

        try {
            Collection<JSONObject> vaues = listStorage.values();
            Object[] offers = vaues.toArray();
            HashMap profile = (HashMap) offers[position];
            String name = profile.get("subject_Name").toString();
            String des=profile.get("lessonName").toString();
            String imageURI=profile.get("ClassesURI").toString();
//            long validtill= (long) profile.get("validtill");
//            long validfrom= (long) profile.get("validfrom");
            String shopname=profile.get("locationName").toString();



//            String vf1= SingletonUtil.dateTimeFromMilli(validtill);
//            String vf2= SingletonUtil.dateTimeFromMilli(validfrom);

            listViewHolder.displaySubjectName.setText(name);
            listViewHolder.displaySubjectDescription.setText(des);
//            listViewHolder.displayValidtill.setText("validFrom: "+vf2);
//            listViewHolder.displayValidfrom.setText("validTill: "+vf1);
            listViewHolder.displayLocationName.setText(shopname);
            if(imageURI==null){
                listViewHolder.displayClassesImg.setImageResource(R.drawable.default_image);
            }else {
                imageLoader.DisplayImage(imageURI, listViewHolder.displayClassesImg);
            }
            // String imageURL = jsonObject.has("imageURL")?jsonObject.getString("imageURL"):null;
//               listViewHolder.displayClassesImg.setImageResource(imageURI);
        } catch (Exception e) {
            e.printStackTrace();
        }




        return convertView;
    }

    @Override
    public void onClick(View view) {
       // Toast.makeText(getContext(), "View All Subject Offers...", Toast.LENGTH_SHORT).show();
//        Intent intent =new Intent(getContext(),LocationDetailsActivity.class);
        Intent intent= new Intent(context, LocationDetailsActivity.class);
//        intent.putextra("your_extra","your_class_value");
        context.startActivity(intent);

    }

    static class ViewHolder{
        ImageView displayClassesImg;
        TextView displaySubjectName;
        TextView displaySubjectDescription;

        TextView displayLocationName;
        TextView displayLessonName;




    }



}
