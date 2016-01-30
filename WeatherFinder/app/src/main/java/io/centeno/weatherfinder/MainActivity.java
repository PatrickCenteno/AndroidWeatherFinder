package io.centeno.weatherfinder;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        ,LocationChooserDialog.LocationDialogListener{

    private final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TextView listEmpty;
    private TextView myPlaces;
    private RecyclerView recyclerView;
    private List<SelectedLocations> selectedLocations;
    private WeatherAdapter weatherAdapter;
    private LinearLayoutManager llm;
    private GoogleApiClient apiClient;
    private Location lastLocation;
    private AddressResultReceiver addressResultReceiver;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if(isOnline()) {
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            initList();

            recyclerView = (RecyclerView) findViewById(R.id.weather_list);
            llm = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(llm);

            weatherAdapter = new WeatherAdapter(selectedLocations, this);
            recyclerView.setAdapter(weatherAdapter);

            listEmpty = (TextView) findViewById(R.id.listIsEmpty);
            myPlaces = (TextView) findViewById(R.id.my_places);
            if (selectedLocations.size() > 0){
                listEmpty.setVisibility(View.GONE);
                myPlaces.setVisibility(View.VISIBLE);
            }
            addressResultReceiver = new AddressResultReceiver(new Handler());

        }else {
            Toast.makeText(this, "Check your Internet Connection before starting.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Currently not being used

        int id = item.getItemId();

        if (id == R.id.add_location){
            showDiaglog();
        }

        return super.onOptionsItemSelected(item);
    }

    // Simply for testing
    private void initList(){
        selectedLocations = new ArrayList<>();
        // Pull from shared preferences and load the list
////         Some dummy locations just for testin
//        selectedLocations.add(new SelectedLocations("Brooklyn", "New York", "US", "11.1.1.1."));
//        selectedLocations.add(new SelectedLocations("Los Angles", "California", "US", "11.1.1.1.434"));
//        selectedLocations.add(new SelectedLocations("London", "", "England", "4.3.4122..2"));
//        selectedLocations.add(new SelectedLocations("Blakdf", "sdfsdf", "sdfsdfs", "747474747474"));
//        selectedLocations.add(new SelectedLocations("Brooklyn", "New York", "US", "11.1.1.1."));
//        selectedLocations.add(new SelectedLocations("Brooklyn", "New York", "US", "11.1.1.1."));
    }

    public boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showDiaglog(){
        DialogFragment dialog = new LocationChooserDialog();
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onfindLocationClick() {
        //Toast.makeText(this, "Latitude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_LONG).show();
        startIntentService();

    }

    @Override
    public void onSetLocationClick() {

    }

    @Override
    protected void onStop() {
        if(isOnline())  apiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(isOnline())  apiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume(){
        if(isOnline())  apiClient.connect();
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            lastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(apiClient);
            if (lastLocation != null){
                latitude = String.valueOf(lastLocation.getLatitude());
                longitude = String.valueOf(lastLocation.getLongitude());
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void startIntentService(){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            Log.d(TAG, resultData.getString(Constants.RESULT_DATA_KEY));


            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getApplicationContext(), "Address Found", Toast.LENGTH_LONG).show();
            }
            String city = resultData.getString("city");
            String state = resultData.getString("state");
            String country = resultData.getString("country");
            selectedLocations.add(new SelectedLocations(city, state, country));
            weatherAdapter.notifyDataSetChanged();
            if (selectedLocations.size() > 0){
                listEmpty.setVisibility(View.GONE);
                myPlaces.setVisibility(View.VISIBLE);
            }

        }
    }

}
