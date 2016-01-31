package io.centeno.weatherfinder;

/**
 * Created by patrickcenteno on 1/26/16.
 */
public class SelectedLocations {
    protected String city;
    protected String state;
    protected String country;
    protected String ipAddress;
    protected String zipCode;

    public SelectedLocations(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public SelectedLocations(String city, String state, String country) {
        // State can be set to null or "" if international
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public SelectedLocations(String city, String state, String country, String ipAddress, String zipCode) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.ipAddress = ipAddress;
        this.zipCode = zipCode;
    }

    public SelectedLocations(String ipAddress) {
        this.ipAddress = ipAddress;

        // Find location by ip Address
    }

    public SelectedLocations(String city, String state, String country, String ipAddress) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.ipAddress = ipAddress;
    }

    private void findLocation(){
        // TODO
    }
}