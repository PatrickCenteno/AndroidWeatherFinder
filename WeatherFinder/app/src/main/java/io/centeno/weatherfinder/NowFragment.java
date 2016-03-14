package io.centeno.weatherfinder;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class NowFragment extends Fragment {

    private final String TAG = "NowFragment";
    final String DEGREE  = "\u00b0";

    private String latitude;
    private String longitude;
    private String address;
    private Map<String,String> params;

    private TextView locationDisplay;
    private TextView weatherDisplay;
    private TextView weatherDescription;
    private NetworkImageView weatherIcon;
    private ImageLoader imageLoader;
    private ProgressBar weatherLoading;

    // openweathermap.org information
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String url = "http://api.openweathermap.org/data/2.5/weather?";
    private final String API_KEY = "209bb1808f1fca53362d3704d127f45f";

    public NowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.now_display, container, false);

        // Get all Location data from mainActivity
        getFromArguments();
        initMainLayout(rootView);
        // Build Params for Api Call
        params = buildParams(latitude, longitude);
        // Retrieve the weather and display it
        getWeather(url, imageUrl, params);


        // TODO: add a pulldown to refresh widget

        return rootView;
    }

    /**
     *
     * @param url
     * @param imageUrl
     * @param params
     * Accepts api url, url for weather icon and a map of http request params
     * Retreives weather information JSON and weather icon, parses and display
     * as in proper views. Disables progressbar and displays main layout.
     * showMainLayout() called in getImageIcon(String newImageUrl).
     */
    private void getWeather(String url, final String imageUrl, Map<String, String> params) {
        Log.d(TAG, "Making a request");
        url += getParamsGET(params);

        Log.d(TAG, url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Setting param imageURL to newImageUrl for access in innerclass
                            String newImageUrl = imageUrl;
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

        APICaller.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private void getFromArguments(){
        address = getArguments().getString("address");
        latitude = getArguments().getString("latitude");
        longitude = getArguments().getString("longitude");
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

    private void initMainLayout(View rootView) {
        locationDisplay = (TextView) rootView.findViewById(R.id.location_display_weather);
        weatherDisplay = (TextView) rootView.findViewById(R.id.temperature_display_weather);
        weatherDescription = (TextView) rootView.findViewById(R.id.weather_description);
        weatherIcon = (NetworkImageView) rootView.findViewById(R.id.weather_icon_display);
        weatherLoading = (ProgressBar) rootView.findViewById(R.id.weather_loading);
        locationDisplay.setText("Weather for: " + address);
    }

    private void showMainLayout() {
        weatherLoading.setVisibility(View.GONE);
        locationDisplay.setVisibility(View.VISIBLE);
        weatherDisplay.setVisibility(View.VISIBLE);
        weatherIcon.setVisibility(View.VISIBLE);
        weatherDescription.setVisibility(View.VISIBLE);
    }

    private void resetMainLayout() {
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

    // showMainLayout() called in here so that displays when everything is done
    private void getImageIcon(String imageUrl){
        imageLoader = APICaller.getInstance(getActivity()).getImageLoader();
        weatherIcon.setImageUrl(imageUrl, imageLoader);
        showMainLayout();
    }

    private String createImageURL(String icon){
        return icon + ".png";
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
