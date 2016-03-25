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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by patrickcenteno on 3/14/16.
 */
public class WeekListAdapter extends RecyclerView.Adapter<WeekListAdapter.WeekViewHolder> {

    private final String TAG = "WeekListAdaper";
    private ArrayList<WeekCardInfo> weekList;
    private Context context;

    public WeekListAdapter(ArrayList<WeekCardInfo> weekList, Context context) {
        this.weekList = weekList;
        this.context = context;
    }

    @Override
    public WeekListAdapter.WeekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.weeklist_card,
                parent, false);

        return new WeekViewHolder(viewItem, weekList, this, context);
    }

    @Override
    public void onBindViewHolder(WeekListAdapter.WeekViewHolder holder, int position) {
        WeekCardInfo info = weekList.get(position);
        holder.day.setText(info.day);
        holder.highTemp.setText("High: " +info.highTemp);
        holder.lowTemp.setText("Low: " + info.lowTemp);
        holder.description.setText(info.description);
        holder.windSpeed.setText("Wind: " + info.windSpeed);

        if (!info.rain.equals(""))
            holder.rain.setText("Rain: " + info.rain);
        else
            holder.rain.setText("Rain:-");
        if (!info.snow.equals(""))
            holder.snow.setText("Snow: " + info.snow);
        else
            holder.snow.setText("Snow:-");

        holder.setImageView(info.imageIconCode);
    }

    public void setWeekList(ArrayList<WeekCardInfo> list){
        this.weekList = list;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (weekList == null)   return 0;
        else    return weekList.size();
    }

    public static class WeekViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "WeekViewHolder";

        private TextView day;
        private TextView highTemp;
        private TextView lowTemp;
        private TextView description;
        private TextView rain;
        private TextView snow;
        private TextView windSpeed;
        private NetworkImageView icon;
        private ArrayList<WeekCardInfo> weekList;

        private WeekListAdapter weekListAdapter;
        private CardView weekCard;
        private LinearLayout weekCardLayout;
        private Context context;


        public WeekViewHolder(View itemView, final ArrayList<WeekCardInfo> weekList,
                              final WeekListAdapter weekListAdapter, Context context) {
            super(itemView);
            Log.d(TAG, weekList.size() + "");
            this.context = context;
            this.weekList = weekList;
            this.weekListAdapter = weekListAdapter;

            weekCard = (CardView) itemView.findViewById(R.id.week_card);
            weekCardLayout = (LinearLayout) itemView.findViewById(R.id.week_card_layout);

            day = (TextView) weekCard.findViewById(R.id.day);
            highTemp = (TextView) weekCard.findViewById(R.id.high_temp_week);
            lowTemp = (TextView) weekCard.findViewById(R.id.low_temp_week);
            icon = (NetworkImageView) weekCard.findViewById(R.id.week_weather_image);
            description = (TextView) weekCard.findViewById(R.id.weather_description_list);
            rain = (TextView) weekCard.findViewById(R.id.rain_text);
            snow = (TextView) weekCard.findViewById(R.id.snow_text);
            windSpeed = (TextView) weekCard.findViewById(R.id.wind_text);


        }

        public void setImageView(String iconCode){
            ImageLoader imageLoader = APICaller.getInstance(context).getImageLoader();
            icon.setImageUrl("http://openweathermap.org/img/w/" + iconCode + ".png", imageLoader);
        }

    }
}


