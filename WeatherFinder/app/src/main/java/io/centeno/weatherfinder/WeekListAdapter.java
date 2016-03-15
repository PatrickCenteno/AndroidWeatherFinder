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
        holder.highTemp.setText(info.highTemp);
        holder.lowTemp.setText(info.lowTemp);
        holder.setImageView(info.imageIconCode);
    }


    @Override
    public int getItemCount() {
        return weekList.size();
    }

    public static class WeekViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "WeekViewHolder";

        private TextView day;
        private TextView highTemp;
        private TextView lowTemp;
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


        }

        public void setImageView(String iconCode){
            // TODO
        }

    }
}


