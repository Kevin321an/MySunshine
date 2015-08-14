package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Change from tool bar to action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
}
