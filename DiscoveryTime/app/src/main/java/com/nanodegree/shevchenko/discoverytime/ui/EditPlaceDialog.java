package com.nanodegree.shevchenko.discoverytime.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.nanodegree.shevchenko.discoverytime.R;
import com.nanodegree.shevchenko.discoverytime.Util;
import com.nanodegree.shevchenko.discoverytime.model.Trip;
import com.nanodegree.shevchenko.discoverytime.model.TripPlace;

public class EditPlaceDialog extends DialogFragment {
    EditPlaceDialogListener mListener;

    public static EditPlaceDialog newInstance(TripPlace place, long startDate, long endDate) {
        EditPlaceDialog dialog = new EditPlaceDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TripPlace.TRIP_PLACE, place);
        bundle.putLong(Trip.START_DATE, startDate);
        bundle.putLong(Trip.END_DATE, endDate);
        dialog.setArguments(bundle);
        return dialog;
    }

    public interface EditPlaceDialogListener {
        void onSaveClick(DialogFragment dialog, TripPlace place);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EditPlaceDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement EditPlaceDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String notPlanned = getString(R.string.not_planned_header);
        String dayDateTmpl = getString(R.string.day_and_date_tmpl);

        final TripPlace tripPlace = getArguments().getParcelable(TripPlace.TRIP_PLACE);
        long startDate = getArguments().getLong(Trip.START_DATE);
        long endDate = getArguments().getLong(Trip.END_DATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_place_edit, null);
        builder.setView(dialogView);
        builder.setTitle(tripPlace.getName());

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.days_spinner);
        final EditText note = (EditText) dialogView.findViewById(R.id.note);

        ArrayAdapter adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                Util.getAllDates(startDate, endDate, notPlanned, dayDateTmpl));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(tripPlace.getDay());
        note.setText(tripPlace.getNote());

        builder.setPositiveButton(R.string.save_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (spinner.getSelectedItemPosition() != tripPlace.getDay()
                                        || ! note.getText().toString().equals(tripPlace.getNote())) {
                                    tripPlace.setDay(spinner.getSelectedItemPosition());
                                    tripPlace.setNote(note.getText().toString());
                                    tripPlace.save(getContext().getContentResolver());
                                    mListener.onSaveClick(EditPlaceDialog.this, tripPlace);
                                }
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
}