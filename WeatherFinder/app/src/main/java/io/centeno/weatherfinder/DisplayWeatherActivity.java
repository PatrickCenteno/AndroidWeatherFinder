package io.centeno.weatherfinder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DisplayWeatherActivity extends AppCompatActivity {

    private final String TAG = "DisplayWeatherActivity";
    final String DEGREE  = "\u00b0";

    private String latitude;
    private String longitude;
    private String address;
    private Map<String,String> params;

    private Bundle bundle;
    private Toolbar toolbar;

    private TextView locationDisplay;
    private TextView weatherDisplay;
    private TextView weatherDescription;
    private TextView minTemp;
    private TextView maxTemp;
    private NetworkImageView weatherIcon;
    private ImageLoader imageLoader;
    private ProgressBar weatherLoading;

    // openweathermap.org information
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String url = "http://api.openweathermap.org/data/2.5/weather?";
    private final String API_KEY = "209bb1808f1fca53362d3704d127f45f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        getFromBundle();

        toolbar = (Toolbar) findViewById(R.id.include_display);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (!isOnline()){
            setContentView(R.layout.display_weather_no_internet);
        }
        else{
            initMainLayout();
            // Make neccessary API calls and when info is received
            // setContentView to main layout for activity

            // Build the parameters
            params = buildParams(latitude, longitude);

            // Hit API endpoint to get weather Info
            // Also sets main layout
            getWeather(url, imageUrl, params);

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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            resetMainLayout();
            if (params != null) {
                getWeather(url, imageUrl, params);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFromBundle(){
        bundle = getIntent().getExtras();
        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");
        address = bundle.getString("address");
    }

    public boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void getWeather(String url, String imageUrl, Map<String, String> params) {
        Log.d(TAG, "Making a request");
        url += getParamsGET(params);

        // Doing this so we can access passed imageUrl in the inner class
        final String tempUrl = imageUrl;
        Log.d(TAG, url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Setting final String to newImageUrl for access in innerclass
                            String newImageUrl = tempUrl;
                            Log.d(TAG, "Response: " + response.toString());
                            Double mainTemp = Double.valueOf(response.getJSONObject("main").getString("temp"));

                            weatherDisplay.setText(toFaren(mainTemp));
                            JSONObject weatherDescriptionInfo = new JSONObject(
                                    response.getJSONArray("weather").getString(0));
                            weatherDescription.setText(weatherDescriptionInfo.getString("main"));

                            //Have to reset the imageURl before doing this
                            JSONObject iconInfo = new JSONObject(response.getJSONArray("weather").getString(0));
                            //Log.d(TAG, iconInfo.getString("icon"));
                            newImageUrl += createImageURL(iconInfo.getString("icon"));
                            getImageIcon(newImageUrl);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });

        APICaller.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    /**
     *
     * @return String
     * Appends all the parameters into a string to append
     * on to the Api endpoint URL
     */
    private String getParamsGET (Map<String, String> params){
       return "lat=" + params.get("lat")
               + "&" + "lon=" + params.get("lon")
               + "&" + "appid=" + params.get("appid");
    }

    /**
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private Map<String, String> buildParams(String latitude, String longitude){
        Map <String, String> params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", API_KEY);
        return params;
    }

    private void initMainLayout(){
        locationDisplay = (TextView) findViewById(R.id.location_display_weather);
        weatherDisplay = (TextView) findViewById(R.id.temperature_display_weather);
        weatherDescription = (TextView) findViewById(R.id.weather_description);
        weatherIcon = (NetworkImageView) findViewById(R.id.weather_icon_display);
        weatherLoading = (ProgressBar) findViewById(R.id.weather_loading);
        locationDisplay.setText("Weather for: " + address);
    }

    private void showMainLayout(){
        weatherLoading.setVisibility(View.GONE);
        locationDisplay.setVisibility(View.VISIBLE);
        weatherDisplay.setVisibility(View.VISIBLE);
        weatherIcon.setVisibility(View.VISIBLE);
        weatherDescription.setVisibility(View.VISIBLE);
    }

    private void resetMainLayout(){
        address = "";
        weatherLoading.setVisibility(View.VISIBLE);
        locationDisplay.setVisibility(View.GONE);
        weatherDisplay.setVisibility(View.GONE);
        weatherIcon.setVisibility(View.GONE);
        weatherDescription.setVisibility(View.GONE);
    }

    // Turns Kelvin temp into farenheit
    private String toFaren(Double temp){
        return (int)(Math.round
                ((temp - 273.15) * 1.8 + 32))
                + DEGREE + "F";
    }

    private void getImageIcon(String imageUrl){
        imageLoader = APICaller.getInstance(this).getImageLoader();
        weatherIcon.setImageUrl(imageUrl, imageLoader);
        showMainLayout();
    }

    private String createImageURL(String icon){
        return icon + ".png";
    }
}
