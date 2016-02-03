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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DisplayWeatherActivity extends AppCompatActivity {

    private final String TAG = "DisplayWeatherActivity";

    private String latitude;
    private String longitude;
    private String city;
    private String state;
    private String country;
    private Map<String,String> params;

    private Bundle bundle;
    private Toolbar toolbar;

    private TextView locationDisplay;
    private TextView weatherDisplay;
    private NetworkImageView weatherIcon;
    private ImageLoader imageLoader;
    private DecimalFormat df;


    // openweathermap.org information
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String url = "http://api.openweathermap.org/data/2.5/weather?";
    private final String API_KEY = "209bb1808f1fca53362d3704d127f45f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_weather_loading);
        getFromBundle();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);


        if (!isOnline()){
            setContentView(R.layout.display_weather_no_internet);
        }
        else{
            // Make neccessary API calls and when info is received
            // setContentView to main layout for activity

            // Build the parameters
            params = buildParams(latitude, longitude);

            // Hit API endpoint to get weather Info
            // Also sets main layout
            getWeather(url, API_KEY, params);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFromBundle(){
        bundle = getIntent().getExtras();
        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");
        city = bundle.getString("city");
        state = bundle.getString("state");
        country = bundle.getString("country");
    }

    public boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void getWeather(String url, String API_KEY, Map<String, String> params) {
        Log.d(TAG, "Making a request");
        url += getParamsGET(params);
        Log.d(TAG, url);
        JSONObject response;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Response: " + response.toString());
                            initMainLayout();
                            Double temp = Double.valueOf(response.getJSONObject("main").getString("temp"));
                            weatherDisplay.setText(toFaren(temp));

                            //Have to reset the imageURl before doing this
                            JSONObject iconInfo = new JSONObject(response.getJSONArray("weather").getString(0));
                            //Log.d(TAG, iconInfo.getString("icon"));
                            imageUrl += createImageURL(iconInfo.getString("icon"));
                            getImageIcon();
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
        setContentView(R.layout.activity_display_weather);
        locationDisplay = (TextView) findViewById(R.id.location_display_weather);
        weatherDisplay = (TextView) findViewById(R.id.temperature_display_weather);
        weatherIcon = (NetworkImageView) findViewById(R.id.weather_icon_display);

        if (state.equals("")) {
            locationDisplay.setText("Weather for:\n" + city + ", " + country);
        }else{
            locationDisplay.setText("Weather for:\n" + city + ", " + state);
        }

    }

    // Turns Kelvin temp into farenheit
    private String toFaren(Double temp){
        return df.format((temp - 273.15) * 1.8 + 32);
    }

    private void getImageIcon(){
        imageLoader = APICaller.getInstance(this).getImageLoader();
        weatherIcon.setImageUrl(imageUrl, imageLoader);
//        float initHeight = weatherIcon.getHeight();
//        float initWidth = weatherIcon.getWidth();
//
//        Log.d(TAG, initHeight + " " + initWidth + " ");
//
//        initHeight *= 1.4;
//        initWidth *= 1.4;
//
//        Log.d(TAG, initHeight + " " + initWidth + "");
//
//        weatherIcon.setMinimumHeight((int) initHeight);
//        weatherIcon.setMinimumWidth((int) initWidth);
//
//        Log.d(TAG, weatherIcon.getHeight() + " " + weatherIcon.getWidth() + "");
//        //weatherIcon.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    private String createImageURL(String icon){
        return icon + ".png";
    }
}
