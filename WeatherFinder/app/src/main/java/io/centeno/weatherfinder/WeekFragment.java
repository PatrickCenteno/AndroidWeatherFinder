package io.centeno.weatherfinder;

import android.app.Activity;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * A simple {@link Fragment} subclass..
 */
public class WeekFragment extends Fragment {

    public final int NUM_OF_DAYS = 7;

    private final String[] DAYS = {
            "null",
            "Mon",
            "Tues",
            "Weds",
            "Thurs",
            "Fri",
            "Sat",
            "Sun"
    };
    private final String TAG = "WeekFragment";
    final String DEGREE = "\u00b0";
    //private boolean called = false;

    private String latitude;
    private String longitude;
    private String address;
    private Map<String, String> params;

    private RecyclerView weekRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;


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

        weekListAdapter = new WeekListAdapter(weekCardInfoArrayList, getActivity());
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_to_refresh_wee);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                callGetWeather();
            }
        });


        weekRecycler.setAdapter(weekListAdapter);

        Log.d(TAG, params.toString());


        return rootView;
    }

    public void callGetWeather() {
        if (params != null) {
            Log.d(TAG, "getting weather");
            getWeather(url, imageUrl, params);
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
                            weekListAdapter.setWeekList(parseResponse(response));
                            swipeRefreshLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error);
                        swipeRefreshLayout.setRefreshing(false);
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

    private String toMPS(Double speed) {
        return (Math.round(speed / .44704) + " mph");
    }

    private ArrayList<WeekCardInfo> parseResponse(JSONObject response) throws JSONException {
        ArrayList<WeekCardInfo> temp = new ArrayList<>();
        for (int i = 0; i < NUM_OF_DAYS; i++) {
            // Setting these to "" in case they dont exists
            String rain = "";
            String snow = "";

            // Getting the object from iteration of week array
            JSONObject tempObject = new JSONObject(response.getJSONArray("list").getString(i));
            Double highTemp = Double.valueOf(tempObject.getJSONObject("temp").getString("max"));
            Double lowTemp = Double.valueOf(tempObject.getJSONObject("temp").getString("min"));
            Double windSpeed = Double.valueOf(tempObject.getString("speed"));
            if (tempObject.has("rain")) rain = tempObject.getString("rain");
            if (tempObject.has("snow")) snow = tempObject.getString("snow");


            // Obtaining the image icon
            JSONObject iconObject = new JSONObject(tempObject
                    .getJSONArray("weather").getString(0));
            String icon = iconObject.getString("icon");
            String description = iconObject.getString("main");

            //Getting the day for the week to add to arraylist
            LocalDate localDate = LocalDate.now();
            String day = localDate.plusDays(i).toString();
            day = day.substring(5);
            day = day.replace('-', '/');
            String fullDate = DAYS[localDate.plusDays(i).getDayOfWeek()] + "\n" + day;

            Log.d(TAG, highTemp + " " + lowTemp + " " + icon);
            temp.add(new WeekCardInfo(icon, fullDate, toFaren(highTemp), toFaren(lowTemp),
                    description, rain, snow, toMPS(windSpeed)));
            swipeRefreshLayout.setRefreshing(false);
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
