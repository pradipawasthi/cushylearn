package com.pradip.cushylearn.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pradip.cushylearn.Adapters.CustomViewOfferPagerAdapter;
import com.pradip.cushylearn.R;


public class ViewTutorialsFragment extends Fragment implements View.OnClickListener {
    //Mandatory Constructor

    ViewPager vp;
    TabLayout tb;

    public ViewTutorialsFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_offer, container, false);

//
//        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
//        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
//
//        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Fragment B"),
//                FragmentB.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Fragment C"),
//                FragmentC.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("fragmentd").setIndicator("Fragment D"),
//                FragmentD.class, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vp = (ViewPager) view.findViewById(R.id.vp_vp);
        tb = (TabLayout) view.findViewById(R.id.tb_tb);

        CustomViewOfferPagerAdapter nt = new CustomViewOfferPagerAdapter(getFragmentManager());
        vp.setAdapter(nt);
        tb.setupWithViewPager(vp);
    }

    @Override
    public void onClick(View view) {

    }
}