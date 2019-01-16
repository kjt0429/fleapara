package kmu.ac.kr.fleapara;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by HP on 2018-04-06.
 */

public class BannerFragmentAdapter extends FragmentPagerAdapter {

    private final int ITEM_COUNT = 5;

    public BannerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return BannerFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}
