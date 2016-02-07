package io.centeno.weatherfinder;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by patrickcenteno on 1/29/16.
 */
public class FetchAddressIntentService extends IntentService {

    private final String TAG = "FetchAddressIntent";

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = "No location Data given";
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            errorMessage = "Service not availabe";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException){
            errorMessage = "Invalid Lat and Long used";
            Log.e(TAG, "Latitude: " + location.getLatitude() +
                    " Longitude: " + location.getLongitude());
            Log.e(TAG, errorMessage);
        }

        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }
        else{

            // Sometimes the city is displayed in different places. It can be
            // the locality or SubLocailty. Instantiates the city string based
            // on which one isnt null
            String city = "";
            if (addresses.get(0).getLocality() != null)
                city = addresses.get(0).getLocality();
            else if (addresses.get(0).getSubLocality() != null)
                city = addresses.get(0).getSubLocality();

            // Ensure that state is not null and when doing international
            // addresses
            String state = "";
            Log.d(TAG, addresses.get(0).getAdminArea());
            if(addresses.get(0).getAdminArea() != null){
                state = addresses.get(0).getAdminArea();
                Log.d(TAG, state);
            }
            String country = addresses.get(0).getCountryName();
            String latitude = String.valueOf(addresses.get(0).getLatitude());
            String longitude = String.valueOf(addresses.get(0).getLongitude());

            deliverResultToReceiver(Constants.SUCCESS_RESULT, city + ", " + state + " " + country,
                    latitude, longitude, errorMessage);

        }
    }



    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    /**
     *
     * @param resultCode
     * @param address
     * @param errorMessage
     * Overloaded method that sends the locaation contents to the MainActivity
     * so it can be displayed in the RecyclerView
     */
    private void deliverResultToReceiver(int resultCode, String address,
                                         String latitude, String longitude, String errorMessage){
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);
        bundle.putString(Constants.RESULT_DATA_KEY, errorMessage);
        mReceiver.send(resultCode, bundle);
    }
}
