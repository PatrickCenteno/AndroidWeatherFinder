package io.centeno.weatherfinder;

import android.app.Activity;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass..
 */
public class WeekFragment extends Fragment {

    public final int NUM_OF_DAYS = 7;
    private final String TAG = "WeekFragment";
    final String DEGREE = "\u00b0";
    private boolean called = false;

    private String latitude;
    private String longitude;
    private String address;
    private Map<String, String> params;

    private RecyclerView weekRecycler;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<WeekCardInfo> weekCardInfoArrayList;
    private WeekListAdapter weekListAdapter;
    private ImageLoader imageLoader;

    // openweathermap.org information
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String url = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private final String API_KEY = "209bb1808f1fca53362d3704d127f45f";

    public WeekFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        JodaTimeAndroid.init(getActivity());
        getFromArguments();
        params = buildParams(latitude, longitude);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_week, container, false);
        Log.d(TAG, "onCreateView called");


        weekRecycler = (RecyclerView) rootView.findViewById(R.id.week_weather_recycler);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        weekRecycler.setLayoutManager(linearLayoutManager);

        Log.d(TAG, params.toString());


        return rootView;
    }

    public void callGetWeather() {
        if (!called) {
            called = true;
            if (params != null) {
                getWeather(url, imageUrl, params);
            }
        }
    }

    private void getWeather(String url, final String imageUrl, final Map<String, String> params) {
        url += getParamsGET(params);
        Log.d(TAG, url);
        Log.d(TAG, params.toString());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Setting param imageURL to newImageUrl for access in innerclass
                            String newImageUrl = imageUrl;
                            weekCardInfoArrayList = parseResponse(response);
                            weekListAdapter = new WeekListAdapter(weekCardInfoArrayList, getActivity());

                            weekRecycler.setAdapter(weekListAdapter);
                        } catch (JSONException e) {
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

    private void getFromArguments() {
        address = getArguments().getString("address");
        latitude = getArguments().getString("latitude");
        longitude = getArguments().getString("longitude");
    }

    /**
     * @return String
     * Appends all the parameters into a string to append
     * on to the Api endpoint URL
     */
    private String getParamsGET(Map<String, String> params) {
        return "lat=" + params.get("lat")
                + "&" + "lon=" + params.get("lon")
                + "&" + "cnt=" + params.get("cnt")
                + "&" + "appid=" + params.get("appid");
    }

    /**
     * @param latitude
     * @param longitude
     * @return
     */
    private Map<String, String> buildParams(String latitude, String longitude) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("cnt", String.valueOf(NUM_OF_DAYS));
        params.put("appid", API_KEY);
        return params;
    }

    // Turns Kelvin temp into farenheit
    private String toFaren(Double temp) {
        return (int) (Math.round
                ((temp - 273.15) * 1.8 + 32))
                + DEGREE + "F";
    }

    private ArrayList<WeekCardInfo> parseResponse(JSONObject response) throws JSONException{
        ArrayList<WeekCardInfo> temp = new ArrayList<>();
        for (int i = 0; i < NUM_OF_DAYS; i++) {
            // Getting the object from iteration of week array
            JSONObject tempObject = new JSONObject(response.getJSONArray("list").getString(i));
            String highTemp = tempObject.getJSONObject("temp").getString("max");
            String lowTemp = tempObject.getJSONObject("temp").getString("min");

            // Obtaining the image icon
            JSONObject iconObject = new JSONObject(tempObject
                    .getJSONArray("weather").getString(0));
            String icon = iconObject.getString("icon");

            //Getting the day for the week to add to arraylist
            LocalDate localDate = LocalDate.now();
            String day = localDate.plusDays(i).toString();
            Log.d(TAG, highTemp + " " + lowTemp + " " + icon);
            temp.add(new WeekCardInfo(icon, day, highTemp, lowTemp));
        }

        return temp;
    }

    private String createImageURL(String icon) {
        return icon + ".png";
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
