package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    /**
     * Add a constant for a fragment tag called FORECASTFRAGMENT_TAG.
     * A fragment tag is a constant String  to tag a fragment within the fragment manager so it can easily look it up later.
     * When we add the fragment, we add the parameter for the FORECASTFRAGMENT_TAG.
     * Fragments can only be tagged during the fragment transaction.
     * This means that the same fragment code can be reused multiple times with different tags.
     */
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    //GMC checking  interval
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final String PROJECT_NUMBER = "618015748787";
    //     store  current known location.
    private String mLocation;
    private boolean mTwoPane;
    private GoogleCloudMessaging mGcm;
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                            .commit();
        }


    }
    */
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Change from tool bar to action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            //his will get rid of an unnecessary shadow below the action bar for smaller screen devices like phones.
            // Then the action bar and Today item will appear to be on the same plane
            // (as opposed to two different planes, where one casts a shadow on the other).
            getSupportActionBar().setElevation(0f);

        }
        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this); //initialize the Sync adapter

       /* if (checkPlayServices()) {
            //Todo: ask user to install lastest version of Google Play Service
            mGcm = GoogleCloudMessaging.getInstance(this);
            String regId = getRegistrationId(this);
            Log.d("GCM", regId);

            *//*if (PROJECT_NUMBER.equals("618015748787")) {
                new AlertDialog.Builder(this)
                        .setTitle("Needs Project Number")
                        .setMessage("GCM will not function in Sunshine until you set the Project Number to the one from the Google Developers Console.")
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
            } else*//* if (regId.isEmpty()) {
                registerInBackground(this);
            }
        } else {
            Log.i(LOG_TAG, "No valid Google Play Services APK. Weather alerts will be disabled.");
            // Store regID as null
            storeRegistrationId(this, null);
        }*/




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //check whether the location has changed by comparing whatever is stored in the settings
    // (ie Utility.getPreferredLocation(this);) with mLocation. If it has changed:
    @Override
    protected void onResume() {
        super.onResume();
        /*if (!checkPlayServices()) {
            //Todo: If Google Play Services is not available, GCM weather alerts wont available
            // Store regID as null

        }*/
        String location = Utility.getPreferredLocation(this);
//        telling the DetailFragment to restart its’ loader if the location changed.
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            //Get the ForecastFragment using the tag FORECASTFRAGMENT_TAG by using:
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != ff) {
                ff.onLocationChanged();
            }
//            this part added for support 2in1 interface
            DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    //this method designed  for ForecastFragment callback
    @Override
    public void onItemSelected(Uri contentUri,ForecastAdapter.ForecastAdapterViewHolder vh) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            //Inserts a Parcelable value into the mapping of this Bundle,
            // replacing any existing value for the given key.
            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            //startActivity(intent);

            //call transition animation to start the detail activity

            ActivityOptionsCompat activityOptions =ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    //@Pair: The View and its name in xml file
                    new Pair<View, String>(vh.mIconView, getString(R.string.detail_icon_transition_name)));

            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        try{
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        }
        catch (Exception e ){
            Log.d(LOG_TAG, "GCM is not available");
            return false;
        }
        return true;
    }
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(LOG_TAG, "GCM Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // Sunshine persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you. Just make sure
        // that it is private!
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Should never happen. WHAT DID YOU DO?!?!
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                        Log.d("GCM", mGcm.toString());
                    }
                    String regId = mGcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regId;
                    Log.d("GCM", msg);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // TODO: If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}
