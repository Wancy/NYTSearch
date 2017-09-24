package com.wancy.nytsearch.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.wancy.nytsearch.R;

import java.util.Calendar;  // do not import java.icu.utils.Calendar

import static android.media.CamcorderProfile.get;


public class DatePickerFragment extends DialogFragment {

    DatePicker datePicker;
    public DatePickerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.date_picker, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBackResult();
                dismiss();
            }
        });
    }


    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {

        // Use the current time as the default values for the picker
        String year = Integer.toString(datePicker.getYear());
        String month = Integer.toString(datePicker.getMonth() + 1);
        String day = Integer.toString(datePicker.getDayOfMonth());

        // Activity needs to implement this interface
        OnDateSetListener listener = (OnDateSetListener) getTargetFragment();
        listener.onDateSet(year, month, day);
    }

    public interface OnDateSetListener {
        void onDateSet(String year, String month, String day);
    }
}
