package io.centeno.weatherfinder;

/**
 * Created by patrickcenteno on 1/26/16.
 */
public class SelectedLocations {
    protected String latitude;
    protected String longitude;
    protected String address;

    public SelectedLocations(String address, String latitude, String longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
