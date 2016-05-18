package com.example.madelenko.app.moviegami;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Objects of this class manage ListFragment instances for a ViewPager.
 */
public class MoviePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public MoviePagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    /**
     * Returns a fragment at the specified position.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return this.mFragments.get(position);
    }

    /**
     * Returns the amount of elements managed by the adapter, or null if it is empty.
     * @return
     */
    @Override
    public int getCount() {
        return mFragments==null? 0 : mFragments.size();
    }

    /**
     * Returns the title of the fragment requested by the position parameter.
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ListFragment.POPULAR:
                return MovieListActivity.POPULAR;
            case ListFragment.HIGHEST_RATED:
                return MovieListActivity.TOP;
            case ListFragment.FAVORITES:
                return MovieListActivity.FAVORITES;
            default:
                throw new IllegalArgumentException("Requesting a fragment that doesn't exist");
        }
    }
}