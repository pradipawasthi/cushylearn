package com.manish.chushylearn.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.manish.chushylearn.Fragments.ViewTutorials1Fragment;
import com.manish.chushylearn.Fragments.ViewTutorials2Fragment;
import com.manish.chushylearn.Fragments.ViewTutorials3Fragment;

/**
 * Created by rbaisak on 18/4/17.
 */

public class CustomViewOfferPagerAdapter extends FragmentPagerAdapter {

    public CustomViewOfferPagerAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ViewTutorials1Fragment();
            case 1:
                return new ViewTutorials2Fragment();
            case 2:
                return new ViewTutorials3Fragment();
            default:
                return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Offers";
            case 1:
                return "Recent Added";
            case 2:
                return "Near to you";
            default:
                return null;
        }    }

    @Override
    public int getCount() {
        return 3;
    }
}


