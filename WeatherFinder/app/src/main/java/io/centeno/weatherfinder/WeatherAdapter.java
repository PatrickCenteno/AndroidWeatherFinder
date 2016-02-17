package io.centeno.weatherfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrickcenteno on 1/26/16.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>  {

    private final String TAG = "WeatherAdapter";
    private ArrayList<SelectedLocations> selectedLocations;
    Context context;

    public WeatherAdapter(ArrayList<SelectedLocations> selectedLocations, Context context) {
        this.selectedLocations = selectedLocations;
        this.context = context;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View viewItem = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.weather_card,
                viewGroup, false);

        return new WeatherViewHolder(viewItem, selectedLocations, context, this);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder weatherViewHolder, int i) {
        SelectedLocations info = selectedLocations.get(i);
        weatherViewHolder.location.setText(info.address);

//        // Check whether or not its internation and has a state
//        // This needs to be checked over better
//        if(!info.address.equals("")){
//            weatherViewHolder.location.setText(info.address);
//        }
//        else if(!info.state.equals("")) {
//            weatherViewHolder.location.setText(info.city + ", " + info.state + ", "
//                    + info.country);
//        }else{
//            weatherViewHolder.location.setText(info.city + ", " + info.country);
//        }

    }

    @Override
    public int getItemCount() {
       return selectedLocations.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private final String TAG = "WeatherViewHolder";

        protected LinearLayout cardLayout;
        protected TextView location;
        protected TextView delete;
        protected CardView cardView;
        protected ArrayList<SelectedLocations> selectedLocations;
        protected WeatherAdapter weatherAdapter;
        protected Context context;


        /**
         *
         * Passing an instance of the weather adapter so we can call
         * notifyDataSetHasChanged() when an a selectedLocation is removed
         */
        public WeatherViewHolder(View itemView, final ArrayList<SelectedLocations> selectedLocations,
                                 final Context context, final WeatherAdapter weatherAdapter) {
            super(itemView);
            Log.d(TAG, "Locations list size: " + selectedLocations.size());
            this.selectedLocations = selectedLocations;
            this.context = context;
            this.weatherAdapter = weatherAdapter;

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.weather_card_layout);

            location = (TextView) cardView.findViewById(R.id.location_display_weather);
            delete = (TextView) cardView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedLocations.remove(getAdapterPosition());
                    weatherAdapter.notifyDataSetChanged();
                }
            });

            cardLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Entered onClick");
            Log.d(TAG, "position: " + getAdapterPosition());
            SelectedLocations selectedLocationsInfo = selectedLocations.get(getAdapterPosition());

            Intent intent = new Intent(context, DisplayWeatherActivity.class);
            // Put the longitude and latitude of of location, as well the address info
            intent.putExtra("latitude", selectedLocationsInfo.latitude);
            intent.putExtra("longitude", selectedLocationsInfo.longitude);
            intent.putExtra("address", selectedLocationsInfo.address);
            context.startActivity(intent);
//            Toast.makeText(context, selectedLocationsInfo.city + " " + selectedLocationsInfo.state + " "
//                + selectedLocationsInfo.country, Toast.LENGTH_LONG).show();
        }

    }
}
