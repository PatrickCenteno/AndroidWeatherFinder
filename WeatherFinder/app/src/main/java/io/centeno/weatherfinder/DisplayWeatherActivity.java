package io.centeno.weatherfinder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DisplayWeatherActivity extends AppCompatActivity {

    private final String TAG = "DisplayWeatherActivity";
    private final int NUM_OF_TABS = 2;
    Context context;

    private ViewPager weatherPager;
    private SlidingTabLayout slidingTabLayout;
    private WeatherFragmentAdapter adapter;

    private Toolbar toolbar;

    public interface WeatherRequestListenerNow{
        public void callGetWeather();
    }

    public interface WeatherRequestListenerWeek{
        public void callGetWeather();
    }

    WeatherRequestListenerNow weatherRequestListenerNow;
    WeatherRequestListenerWeek weatherRequestListenerWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);

        weatherRequestListenerNow = new NowFragment();
        weatherRequestListenerWeek = new WeekFragment();

        toolbar = (Toolbar) findViewById(R.id.include_display);
        setSupportActionBar(toolbar);
        context = this;


        if (!isOnline()) {
            setContentView(R.layout.display_weather_no_internet);
        } else {
            adapter = new WeatherFragmentAdapter(getSupportFragmentManager(),
                    getIntent().getExtras(), NUM_OF_TABS);
            weatherPager = (ViewPager) findViewById(R.id.weatherpager);
            weatherPager.setAdapter(adapter);
            weatherPager.setOffscreenPageLimit(NUM_OF_TABS);
            weatherPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0){
                        weatherRequestListenerNow.callGetWeather();
                    }else {
                        weatherRequestListenerWeek.callGetWeather();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
            slidingTabLayout.setDistributeEvenly(true);

            slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return ContextCompat.getColor(context, R.color.appwhite);
                }
            });

            slidingTabLayout.setViewPager(weatherPager);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        return super.onOptionsItemSelected(item);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}


