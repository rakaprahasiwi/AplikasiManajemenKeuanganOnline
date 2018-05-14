package net.prahasiwi.laporankeuangan.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.prahasiwi.laporankeuangan.fragment.HomeFragment;
import net.prahasiwi.laporankeuangan.fragment.ReportFragment;

/**
 * Created by PRAHASIWI on 14/12/2017.
 */

public class FragPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles = {"HOME", "LAPORAN"};

    public FragPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if (position == 0) {
            frag = new HomeFragment();
        } else if (position == 1) {
            frag = new ReportFragment();
        }
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return titles[position];
    }
}
