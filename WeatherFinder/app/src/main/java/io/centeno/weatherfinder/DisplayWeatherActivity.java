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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

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


    // openweathermap.org information
    private final String IMAGE_URL = "http://openweathermap.org/img/w/";
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


        if (!isOnline()){
            setContentView(R.layout.display_weather_no_internet);
        }
        else{
            // Make neccessary API calls and when info is received
            // setContentView to main layout for activity

            // Build the parameters
            buildParams();

            // Hit API endpoint to get weather Info
            getWeather();


//            locationDisplay = (TextView) findViewById(R.id.location_display_weather);
//            weatherDisplay = (TextView) findViewById(R.id.location_display_weather);
//            weatherIcon = (NetworkImageView) findViewById(R.id.weather_icon_display);

            //locationDisplay.setText(city + ", " + state + ", " + country);

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

    private void getWeather() {
        Log.d(TAG, "Making a request");
        url += getParamsGET();
        Log.d(TAG, url);
        JSONObject response;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
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
    private String getParamsGET (){
       return "lat=" + params.get("lat")
               + "&" + "lon=" + params.get("lon")
               + "&" + "appid=" + params.get("appid");
    }

    /**
     * Instantiates the Hashmap and fills it with neccessary
     * Parameters for api call
     */
    private void buildParams(){
        params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", API_KEY);
    }
}
