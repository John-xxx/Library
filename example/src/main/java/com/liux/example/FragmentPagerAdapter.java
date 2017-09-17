package com.liux.example;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Liux on 2017/8/13.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private Fragment[] mFragments;

    public FragmentPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);

        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}