package io.centeno.weatherfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Location> locations;
    private WeatherAdapter weatherAdapter;
    private LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initList();

        recyclerView = (RecyclerView) findViewById(R.id.weather_list);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        weatherAdapter = new WeatherAdapter(locations);
        recyclerView.setAdapter(weatherAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Currently not being used

        return super.onOptionsItemSelected(item);
    }

    // Simply for testing
    private void initList(){
        locations = new ArrayList<>();
        locations.add(new Location("Brooklyn", "New York", "US", "11.1.1.1."));
        locations.add(new Location("Los Angles", "California", "US", "11.1.1.1.434"));
        locations.add(new Location("London", "", "England", "4.3.4122..2"));
        locations.add(new Location("Blakdf", "sdfsdf", "sdfsdfs", "747474747474"));
        locations.add(new Location("Brooklyn", "New York", "US", "11.1.1.1."));
        locations.add(new Location("Brooklyn", "New York", "US", "11.1.1.1."));
    }
}
