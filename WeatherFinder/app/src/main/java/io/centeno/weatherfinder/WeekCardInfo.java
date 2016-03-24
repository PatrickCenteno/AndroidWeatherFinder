package io.centeno.weatherfinder;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by patrickcenteno on 3/14/16.
 */
public class WeekCardInfo {

    public String imageIconCode;
    public String day;
    public String highTemp;
    public String lowTemp;
    public String description;

    public WeekCardInfo(String imageIconCode, String day,
                        String highTemp, String lowTemp, String description) {
        this.imageIconCode = imageIconCode;
        this.day = day;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.description = description;
    }
}
