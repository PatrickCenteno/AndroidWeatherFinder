package io.centeno.weatherfinder;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrickcenteno on 1/26/16.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>  {

    private final String TAG = "WeatherAdapter";
    private List<Location> locations;
    Context context;

    public WeatherAdapter(List<Location> locations, Context context) {
        this.locations = locations;
        this.context = context;
    }

    public void addToLocations(Location l){
        locations.add(l);
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View viewItem = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.weather_card,
                viewGroup, false);

        return new WeatherViewHolder(viewItem, locations, context);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder weatherViewHolder, int i) {
        Location info = locations.get(i);

        // Check whether or not its internation and has a state
        if(info.state != "") {
            weatherViewHolder.location.setText(info.city + ", " + info.state + ", "
                    + info.country);
        }else{
            weatherViewHolder.location.setText(info.city + ", " + info.country);
        }

        weatherViewHolder.ipAddress.setText(info.ipAddress);
    }

    @Override
    public int getItemCount() {
       return locations.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private final String TAG = "WeatherViewHolder";

        protected LinearLayout cardLayout;
        protected TextView location;
        protected TextView ipAddress;
        protected TextView delete;
        protected CardView cardView;
        protected List<Location> locations;
        protected Context context;

        public WeatherViewHolder(View itemView, List<Location> locations, Context context) {
            super(itemView);
            Log.d(TAG, "Locations list size: " + locations.size());
            this.locations = locations;
            this.context = context;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.weather_card_layout);
            location = (TextView) cardView.findViewById(R.id.location);
            ipAddress = (TextView) cardView.findViewById(R.id.ip_address);
            delete = (TextView) cardView.findViewById(R.id.delete);

            cardLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Entered onClick");
            Log.d(TAG, "position: " + getAdapterPosition());
            Location locationInfo = locations.get(getAdapterPosition());
            Toast.makeText(context, locationInfo.city + " " + locationInfo.state + " "
                + locationInfo.ipAddress, Toast.LENGTH_LONG).show();
        }
    }
}
