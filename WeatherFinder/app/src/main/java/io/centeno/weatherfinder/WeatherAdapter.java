package io.centeno.weatherfinder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrickcenteno on 1/26/16.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>  {

    private List<Location> locations;

    public WeatherAdapter(List<Location> locations) {
        this.locations = locations;
    }

    public void addToLocations(Location l){
        locations.add(l);
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View viewItem = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.weather_card,
                viewGroup, false);

        return new WeatherViewHolder(viewItem);
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

    public static class WeatherViewHolder extends RecyclerView.ViewHolder{

        protected TextView location;
        protected TextView ipAddress;
        protected CardView cardView;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            location = (TextView) cardView.findViewById(R.id.location);
            ipAddress = (TextView) cardView.findViewById(R.id.ip_address);
        }
    }
}
