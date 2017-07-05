package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;

/**
 * Created by hanapearlman on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Home", "Mentions"};
    private Context context;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    //return total number of fragments
    @Override
    public int getCount() {
        return 2;
    }

    //fragment to use depending on position

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.i("TweetsPagerAdapter", "position = " + position);
            return new HomeTimelineFragment();
        } else if (position == 1){
            return new MentionsTimelineFragment();
        } else {
            return null;
        }
    }

    //fragment title for each tab

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
