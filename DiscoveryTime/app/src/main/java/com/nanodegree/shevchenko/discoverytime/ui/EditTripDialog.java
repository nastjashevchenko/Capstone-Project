package com.nanodegree.shevchenko.discoverytime.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.Util;
import com.nanodegree.shevchenko.discoverytime.model.Poi;
import com.nanodegree.shevchenko.discoverytime.model.Trip;

import java.util.Calendar;

public class EditTripDialog  extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener{
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;

    EditTripDialogListener mListener;

    private boolean newTrip;
    private boolean start;
    private long startDate;
    private long endDate;
    private Trip mTrip;

    EditText mTitle;
    EditText mStartDate;
    EditText mEndDate;

    public static EditTripDialog newInstance(Long id) {
        EditTripDialog dialog = new EditTripDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(Trip.EXTRA_ID_NAME, id);
        dialog.setArguments(bundle);
        return dialog;
    }

    public static EditTripDialog newInstance() {
        return new EditTripDialog();
    }

    public interface EditTripDialogListener {
        void onTitleChanged(DialogFragment dialog);
        void onDatesChanged(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EditTripDialogListener)
            mListener = (EditTripDialogListener) activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        newTrip = (getArguments() == null);
        mTrip = newTrip ? new Trip() : Trip.getById(getArguments().getLong(Trip.EXTRA_ID_NAME));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_trip_edit, null);

        builder.setView(dialogView);
        builder.setTitle(newTrip ? "Add new trip" : mTrip.getTitle());

        mTitle = (EditText) dialogView.findViewById(R.id.destination);
        mStartDate = (EditText) dialogView.findViewById(R.id.start_date);
        mEndDate = (EditText) dialogView.findViewById(R.id.end_date);

        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);

        if (!newTrip) {
            mTitle.setText(mTrip.getTitle());
            mTitle.setHint(R.string.title);
            startDate = mTrip.getStartDate();
            endDate = mTrip.getEndDate();
            if (startDate != 0L && endDate != 0L) {
                mStartDate.setText(mTrip.getStartDateStr());
                mEndDate.setText(mTrip.getEndDateStr());
            }
        } else {
            mTitle.setOnClickListener(this);
            mTitle.setFocusable(false);
        }

        builder.setPositiveButton(R.string.save_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing here, because onClick is overriden in onStart
                        // Don't need to cancel dialog if validation is not passed
                    }
                })
                .setNegativeButton(R.string.cancel_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog d = (AlertDialog) getDialog();
        if(d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(saveAndExit())
                        d.dismiss();
                }
            });
        }
    }

    public void showDatePickerDialog(View view) {
        start = (view.getId() == R.id.start_date);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), this, year, month, day);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        long date = c.getTimeInMillis();
        // Saving dates to local variables and assign to mTrip after validations
        if (start) {
            startDate = date;
            mStartDate.setText(Util.longDateToString(date));
        } else {
            endDate = date;
            mEndDate.setText(Util.longDateToString(date));
        }
    }

    private void showError(String error) {
        Toast toast = Toast.makeText(getContext(), error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public boolean saveAndExit() {
        // Validate destination is set
        if (mTitle.getText() == null || mTitle.getText().length() <= 0) {
            int error = (newTrip) ? R.string.destination_empty : R.string.title_empty;
            showError(getResources().getString(error));
            return false;
        }

        // Validate dates
        if ((startDate == 0L && endDate > 0L) || (startDate > 0L && endDate == 0L)) {
            showError(getResources().getString(R.string.one_date_set));
            return false;
        }

        if (startDate != 0L && endDate != 0L && startDate > endDate) {
            showError(getResources().getString(R.string.start_after_end));
            return false;
        }
        // Update UI and places if some fields were changed
        //String newTitle = mTitle.getText().toString();
        boolean titleChanged = (!mTrip.getTitle().equals(mTitle.getText().toString()));
        boolean datesChanged = (startDate != mTrip.getStartDate() || endDate != mTrip.getEndDate());
        mTrip.setTitle(mTitle.getText().toString());
        mTrip.setStartDate(startDate);
        mTrip.setEndDate(endDate);
        mTrip.save();

        if (!newTrip && titleChanged) {
            mListener.onTitleChanged(EditTripDialog.this);
        }
        if (!newTrip && datesChanged) {
            long newDuration = (endDate - startDate) / (24 * 60 * 60 * 1000) + 1;
            // If duration became shorter, some places could have days out of new range
            // This places will be not assigned to any day in this case
            for (Poi poi : mTrip.getPois()) {
                if (poi.getDay() > newDuration) {
                    poi.setDay(0);
                    poi.save();
                }
            }
            mListener.onDatesChanged(EditTripDialog.this);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.destination) {
            addTripDest();
        } else {
            showDatePickerDialog(v);
        }
    }

    public void addTripDest() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                mTrip.setPlaceId(place.getId());
                if (mTitle.getText().length() <= 0) mTitle.setText(place.getName().toString());
            }
        }
    }
}
