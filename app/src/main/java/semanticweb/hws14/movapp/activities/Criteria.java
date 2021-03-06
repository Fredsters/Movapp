package semanticweb.hws14.movapp.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.fragments.ActorCriteria;
import semanticweb.hws14.movapp.fragments.CriteriaPagerAdapter;
import semanticweb.hws14.movapp.fragments.MovieCriteria;

//is a tab view and contains the actor and movie fragment
public class Criteria extends FragmentActivity {

    private Criteria that;
    private LocationManager locMgr;
    private LocationListener locListner;
    private CriteriaPagerAdapter criteriaPagerAdapter;
    private ViewPager mViewPager;
    private int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_criteria);
        that = this;
        initCriteriaView();

        //Get the Tabpager
        criteriaPagerAdapter = new CriteriaPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(criteriaPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
                tabPosition = tab.getPosition();
            }
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };
        //add the two tabs
        actionBar.addTab(actionBar.newTab().setText("Movies").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Actors").setTabListener(tabListener));
    }

    //Show the GPS Button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.criteria, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.gps_location_button) {
            getGpsLocation();
            return true;
        } else if (id == R.id.license_button) {
            Intent intent = new Intent(that, License.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCriteriaView(){
        //Init the location stuff
        locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locListner = new LocationListener() {
            public void onLocationChanged(Location location) {
                useLocationData(location);
            }
            //standard methods
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
    }

    private void useLocationData (Location location) {
        //Receive the location Data
        AlertDialog ad = new AlertDialog.Builder(that).create();
        ad.setCancelable(false); // This blocks the 'BACK' button
        Geocoder geocoder = new Geocoder(that, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                final String strReturnedAdress = returnedAddress.getLocality();
                ad.setMessage("You are in: "+strReturnedAdress+"\nUse this location for the search?");

                ad.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(tabPosition == 0) {
                            MovieCriteria currentFragmet = (MovieCriteria) getFragmentByPosition(tabPosition);
                            currentFragmet.setGPSLocation(strReturnedAdress);
                        } else if(tabPosition == 1){
                            ActorCriteria currentFragmet = (ActorCriteria) getFragmentByPosition(tabPosition);
                            currentFragmet.setGPSLocation(strReturnedAdress);
                        }

                        dialog.dismiss();
                    }
                });

                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            else {
                ad.setMessage("No Address returned!");
                ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            ad.setMessage("Can not get Address!");
            ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        ad.show();
        setProgressBarIndeterminateVisibility(false);
        locMgr.removeUpdates(locListner);
    }

    public void getGpsLocation() {
        setProgressBarIndeterminateVisibility(true);
        locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListner);
    }
    //get the fragmnet
    private Fragment getFragmentByPosition(int pos) {
        String tag = "android:switcher:" + mViewPager.getId() + ":" + pos;
        return getSupportFragmentManager().findFragmentByTag(tag);
    }
}
