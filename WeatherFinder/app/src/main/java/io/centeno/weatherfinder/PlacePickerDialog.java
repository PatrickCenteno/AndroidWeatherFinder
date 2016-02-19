package io.centeno.weatherfinder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

/**
 * Created by patrickcenteno on 2/7/16.
 */
public class PlacePickerDialog extends DialogFragment {

    PlaceAutocompleteFragment fragment;
    private final String TAG = "PlacePickerDialog";

    /**
     * Must Overides the method to properly destory
     * PlaceAutocompleteFragment. Other wise a NullPointerException
     * is thrown when another dialog attempts to be inflated
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.autocomplete_location));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Displays a custom layout
        View v = inflater.inflate(R.layout.place_picker_layout, null);
        builder.setView(v);

        fragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_location);

        // Sends the results of the place picker to MainACtivity so it can
        // be put in the WeatherAdapter
        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "Place: " + place.getName() + place.getLatLng() +
                        " " + place.getAddress());
                ((MainActivity)getActivity())
                        .addFromPlacePicker(place.getAddress().toString(), place.getLatLng());
                dismiss();
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "An error has occured: " + status);
                dismiss();
            }
        });



        return builder.create();
    }
}
