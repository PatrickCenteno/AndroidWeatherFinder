package io.centeno.weatherfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by patrickcenteno on 1/27/16.
 */
public class LocationChooserDialog extends DialogFragment {

    RelativeLayout findLocationLayout;
    RelativeLayout setLocationLayout;

    public LocationChooserDialog() {
        super();
    }

    // Interface to pass events back to main activity
    public interface LocationDialogListener{
        public void onfindLocationClick();
        public void onSetLocationClick();
    }

    LocationDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            // This instantiate this dialog listener
            listener = (LocationDialogListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()
                + " must implement LocationDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.select_location_type_dialog, null);
        builder.setView(v);

        findLocationLayout = (RelativeLayout) v.findViewById(R.id.find_location_layout);
        setLocationLayout = (RelativeLayout) v.findViewById(R.id.select_location_layout);

        findLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onfindLocationClick();
            }
        });

        setLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSetLocationClick();
            }
        });

        return builder.create();
    }
}
