package io.centeno.weatherfinder;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass..
 */
public class WeekFragment extends Fragment {

    public final int NUM_OF_DAYS = 7;

    private RecyclerView weekRecycler;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<WeekCardInfo> weekCardInfoArrayList;
    private WeekListAdapter weekListAdapter;

    // openweathermap.org information
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String url = "http://api.openweathermap.org/data/2.5/weather?";
    private final String API_KEY = "209bb1808f1fca53362d3704d127f45f";

    public WeekFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_week, container, false);

        weekRecycler = (RecyclerView) rootView.findViewById(R.id.week_weather_recycler);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        weekRecycler.setLayoutManager(linearLayoutManager);

        weekCardInfoArrayList = new ArrayList<>();
        weekListAdapter = new WeekListAdapter(weekCardInfoArrayList, getActivity());


        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
