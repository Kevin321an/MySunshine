package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        /*//Change from tool bar to action bar --move to XML layout
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            //Inserts a Parcelable value into the mapping of this Bundle,
            // replacing any existing value for the given key. Either key or value may be null.
            //getIntent().getData() is Bundle[{URI=content://com.example.android.sunshine.app/weather/94043/1436241600000}] in this case
            arguments.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());
            DetailActivityFragment fragment = new DetailActivityFragment();
            //Supply the construction arguments for this fragment.
            fragment.setArguments(arguments);//mArguments=Bundle[{URI=content://com.example.android.sunshine.app/weather/94043/1436241600000}]
            getSupportFragmentManager().beginTransaction()//Start a series of edit operations on the Fragments associated with this FragmentManager.
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }
}


