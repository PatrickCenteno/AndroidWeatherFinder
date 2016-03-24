package io.centeno.weatherfinder;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationChooserDialog.LocationDialogListener {

    private final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TextView listEmpty;
    private TextView myPlaces;
    private RecyclerView recyclerView;
    public ArrayList<SelectedLocations> selectedLocations;
    private WeatherAdapter weatherAdapter;
    private LinearLayoutManager llm;
    private GoogleApiClient apiClient;
    private Location lastLocation;
    private AddressResultReceiver addressResultReceiver;
    private LocationsDBHelper dbHelper;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        JodaTimeAndroid.init(this);
        Log.d(TAG, "onCreate() called in main activity");

        // If no internet connection is present, dont give the user
        // An option to try to obtain a location or weather
        if (isOnline()) {
            // Connect to google API first
            if (apiClient == null) {
                apiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(this, this)
                        .build();
            }
            // Read in values from DB and initalize the arraylist
            setContentView(R.layout.activity_main);
            dbHelper = new LocationsDBHelper(this);

            toolbar = (Toolbar) findViewById(R.id.include_main);
            setSupportActionBar(toolbar);

            //Initialize arraylist from db query
            selectedLocations = new ArrayList<>();
            initList();

            recyclerView = (RecyclerView) findViewById(R.id.weather_list);
            llm = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(llm);

            weatherAdapter = new WeatherAdapter(selectedLocations, this);
            recyclerView.setAdapter(weatherAdapter);

            listEmpty = (TextView) findViewById(R.id.listIsEmpty);
            myPlaces = (TextView) findViewById(R.id.my_places);

            displayList();
            addressResultReceiver = new AddressResultReceiver(new Handler());


        } else {
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

        if (id == R.id.add_location) {
            showDiaglog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (isOnline()) {
            apiClient.disconnect();
            Log.d(TAG, "onStop() is called");
            new WriteToDB().execute(dbHelper);
            super.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (isOnline()) apiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (isOnline()) apiClient.connect();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() is called");
        // Initalize arraslist from db call
        initList();
    }

    /********************************
     * LocationChooserDialog Event methods
     */
    @Override
    public void onfindLocationClick() {
        startIntentService();
    }

    @Override
    public void onSetLocationClick() {
        //Toast.makeText(this, "Feature not available yet", Toast.LENGTH_LONG).show();
        showPlacePickerDialog();
    }


    /********************************
     * Google Api Location Methods
     */

    @Override
    public void onConnected(Bundle bundle) {
        try {
            lastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(apiClient);
            if (lastLocation != null) {
                latitude = String.valueOf(lastLocation.getLatitude());
                longitude = String.valueOf(lastLocation.getLongitude());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showDiaglog() {
        DialogFragment dialog = new LocationChooserDialog();
        dialog.show(getFragmentManager(), TAG);
    }

    public void showPlacePickerDialog() {
        DialogFragment dialogFragment = new PlacePickerDialog();
        dialogFragment.show(getFragmentManager(), TAG);
    }

    private void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }

    private void displayList() {
        if (selectedLocations.size() > 0) {
            listEmpty.setVisibility(View.GONE);
            myPlaces.setVisibility(View.VISIBLE);
        }
    }

    private void initList() {
        new ReadFromDB().execute(dbHelper);
    }

    public void addFromPlacePicker(String address, LatLng location) {
        selectedLocations.add(new SelectedLocations(address,
                Double.toString(location.latitude), Double.toString(location.longitude)));
        weatherAdapter.notifyDataSetChanged();
        displayList();
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            Log.d(TAG, resultData.getString(Constants.RESULT_DATA_KEY));


            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getApplicationContext(), "Address Found", Toast.LENGTH_LONG).show();
            }
            String address = resultData.getString("address");
            String latitude = resultData.getString("latitude");
            String longitude = resultData.getString("longitude");
            selectedLocations.add(new SelectedLocations(address
                    , latitude, longitude));
            weatherAdapter.notifyDataSetChanged();
            displayList();

        }
    }

    /**
     * Accepts the SQLlite Database helper class so that
     * It can write contents of Weather adapter to SQlite Databse
     */
    class WriteToDB extends AsyncTask<LocationsDBHelper, Void, Void> {

        @Override
        protected Void doInBackground(LocationsDBHelper... dbHelpers) {
            SQLiteDatabase db = dbHelpers[0].getWritableDatabase();
            ContentValues values;
            long newRowid;

            // Clears and resets the table everytime
            db.execSQL(LocationsDB.RESET_TABLE);
            db.execSQL(LocationsDB.CREATE_TABLE);
            // Only make db query if there are items in the  RecyclerView
            if (!selectedLocations.isEmpty()) {
                // Start from a blank table
                Log.d(TAG, "Writing to db now");
                for (SelectedLocations s : selectedLocations) {
                    values = new ContentValues();
                    values.put(LocationsDB.COLUMN_ADDRESS, s.address);
                    values.put(LocationsDB.COLUMN_LAT, s.latitude);
                    values.put(LocationsDB.COLUMN_LON, s.longitude);

                    // Insert values map into db
                    newRowid = db.insert(
                            LocationsDB.TABLE_NAME,
                            null,
                            values);
                    Log.d(TAG, Long.toString(newRowid));
                }
            }
            return null;
        }
    }

    /**
     * Accepts the SQLlite Database helper class so that
     * It can read the contents of the locattions table
     * and store them in the weatherAdapter
     */
    class ReadFromDB extends AsyncTask<LocationsDBHelper, Void, Void> {
        @Override
        protected Void doInBackground(LocationsDBHelper... dbHelpers) {
            SQLiteDatabase db = dbHelpers[0].getReadableDatabase();

            // query to get all locations
            String query = "SELECT " + LocationsDB.COLUMN_ADDRESS + ", "
                    + LocationsDB.COLUMN_LAT + ", "
                    + LocationsDB.COLUMN_LON + " FROM " + LocationsDB.TABLE_NAME;

            // Runs the query and returns a cursor, which allows
            // for row by row access of the result (in the case the whole table)
            Cursor cursor = db.rawQuery(query, null);

            SelectedLocations sl = null;
            if (cursor.moveToNext()) {
                selectedLocations.clear();
                do {
                    String address = cursor.getString(0);
                    String lat = cursor.getString(1);
                    String lon = cursor.getString(2);
                    Log.d(TAG, address + " " + lat + " " + lon);
                    sl = new SelectedLocations(address, lat, lon);
                    selectedLocations.add(sl);
                } while (cursor.moveToNext());
            }

            if (selectedLocations.size() > 0){
               displayList();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (weatherAdapter != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "On UI thread and weather adapter is being updated");
                        weatherAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

}
