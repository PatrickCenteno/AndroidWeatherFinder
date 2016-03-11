package io.centeno.weatherfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by patrickcenteno on 3/11/16.
 */
public class WeatherFragmentAdapter extends FragmentStatePagerAdapter {
    CharSequence titles [] = {"Right Now", "Week"};
    Bundle extras;
    int numOfTabs;

    public WeatherFragmentAdapter(FragmentManager fm, Bundle extras, int numOfTabs) {
        super(fm);
        this.extras = extras;
        this.numOfTabs = numOfTabs;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            NowFragment nowFragment = new NowFragment();
            nowFragment.setArguments(extras);
            return nowFragment;
        }
        else {
            WeekFragment weekFragment = new WeekFragment();
            weekFragment.setArguments(extras);
            return weekFragment;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
