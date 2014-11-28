package semanticweb.hws14.movapp.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by Frederik on 18.11.2014.
 */
public class CriteriaPagerAdapter extends FragmentPagerAdapter {
    public CriteriaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0: {
                Fragment fragment = new MovieCriteria();
                return fragment;
            }
            case 1: {
                Fragment fragment = new ActorCriteria();

                return fragment;
            }default : {
                Log.d("tabs", "probleme mit den tabs");
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return "Movies";
        } else if(position == 1) {
            return "Actors";
        }
        return "dummy";
    }
}