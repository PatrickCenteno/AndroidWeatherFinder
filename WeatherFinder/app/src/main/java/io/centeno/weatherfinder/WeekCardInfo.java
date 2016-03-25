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
    public String rain;
    public String snow;
    public String windSpeed;

    public WeekCardInfo(String imageIconCode, String day, String highTemp,
                        String lowTemp, String description, String rain,
                        String snow, String windSpeed) {
        this.imageIconCode = imageIconCode;
        this.day = day;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.description = description;
        this.rain = rain;
        this.snow = snow;
        this.windSpeed = windSpeed;
    }
}
