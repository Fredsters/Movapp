package semanticweb.hws14.movapp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.fragments.CriteriaPagerAdapter;


public class Criteria extends FragmentActivity {

    Activity that = this;

    LocationManager locMgr;
    LocationListener locListner;

    CriteriaPagerAdapter criteriaPagerAdapter;
    ViewPager mViewPager;
    Fragment currentFragment;
    android.support.v4.app.FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_criteria);
        initCriteriaView();



        criteriaPagerAdapter = new CriteriaPagerAdapter(getSupportFragmentManager());
        fragmentManager = getSupportFragmentManager();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(criteriaPagerAdapter);

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
                currentFragment = fragmentManager.findFragmentById(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        actionBar.addTab(actionBar.newTab().setText("Movies").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Actors").setTabListener(tabListener));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.criteria, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.gps_location_button) {
            getGpsLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCriteriaView(){

        //Init geo location

        locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locListner = new LocationListener() {

            public void onProviderEnabled(String provider) {
                Log.d("onProviderEnabled", provider.toString());

            }

            public void onProviderDisabled(String provider) {
                Log.d("onProviderDisabled", provider.toString());

            }

            public void onLocationChanged(Location location) {

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

                                String[] cityArray = getResources().getStringArray(R.array.city_array);
                                int position = 0;
                                for(int i = 0 ; i < cityArray.length; i++) {
                                    if(strReturnedAdress.equals(cityArray[i])) {
                                        position = i;
                                        break;
                                    }
                                }
                                //TODO get Fragment
                         //       swCity.setChecked(true);
                         //       spCity.setSelection(position);
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
                    ad.setMessage("Canont get Address!");
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

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.d("onStatusChanged", provider.toString());
                Log.d("onStatusChanged", ""+status);
                Log.d("onStatusChanged", extras.toString());

            }
        };
    }
    public void getGpsLocation() {
        setProgressBarIndeterminateVisibility(true);
        locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListner);
    }
}
