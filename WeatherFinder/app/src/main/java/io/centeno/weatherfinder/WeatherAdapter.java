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

import java.util.List;

/**
 * Created by patrickcenteno on 1/26/16.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>  {

    private final String TAG = "WeatherAdapter";
    private List<SelectedLocations> selectedLocations;
    Context context;

    public WeatherAdapter(List<SelectedLocations> selectedLocations, Context context) {
        this.selectedLocations = selectedLocations;
        this.context = context;
    }

    public void addToLocations(SelectedLocations l){
        selectedLocations.add(l);
        notifyDataSetChanged();
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

        // Check whether or not its internation and has a state
        if(info.state.equals("")) {
            weatherViewHolder.location.setText(info.city + ", " + info.state + ", "
                    + info.country);
        }else{
            weatherViewHolder.location.setText(info.city + ", " + info.country);
        }

        weatherViewHolder.ipAddress.setText(info.ipAddress);
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
        protected TextView ipAddress;
        protected TextView delete;
        protected CardView cardView;
        protected List<SelectedLocations> selectedLocations;
        protected WeatherAdapter weatherAdapter;
        protected Context context;


        /**
         *
         * Passing an instance of the weather adapter so we can call
         * notifyDataSetHasChanged() when an a selectedLocation is removed
         */
        public WeatherViewHolder(View itemView, final List<SelectedLocations> selectedLocations,
                                 final Context context, final WeatherAdapter weatherAdapter) {
            super(itemView);
            Log.d(TAG, "Locations list size: " + selectedLocations.size());
            this.selectedLocations = selectedLocations;
            this.context = context;
            this.weatherAdapter = weatherAdapter;

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.weather_card_layout);

            location = (TextView) cardView.findViewById(R.id.location);
            ipAddress = (TextView) cardView.findViewById(R.id.ip_address);
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
            Toast.makeText(context, selectedLocationsInfo.city + " " + selectedLocationsInfo.state + " "
                + selectedLocationsInfo.country, Toast.LENGTH_LONG).show();
        }

    }
}
