package com.manish.chushylearn.Adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.content.Context;


import com.manish.chushylearn.R;

import java.util.List;

/**
 * Created by rbaisak on 7/2/17.
 */

public class MyCustomPagerAdapter extends PagerAdapter {
    Context context;
List<String > img;
    LayoutInflater layoutInflater;


    public MyCustomPagerAdapter(Context context, List<String> img) {
        this.context = context;
        this.img = img;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return img.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item_slider, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
//        imageView.setImageResource(img.get(position));

        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}


//reference..............
//  http://www.thecrazyprogrammer.com/2016/12/android-image-slider-using-viewpager-example.html