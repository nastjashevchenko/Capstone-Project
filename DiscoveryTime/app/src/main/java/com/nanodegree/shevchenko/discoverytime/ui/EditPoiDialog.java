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
import com.nanodegree.shevchenko.discoverytime.model.Poi;

public class EditPoiDialog extends DialogFragment {
    EditPoiDialogListener mListener;

    public static EditPoiDialog newInstance(Long id) {
        EditPoiDialog dialog = new EditPoiDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(Poi.POI_ID, id);
        dialog.setArguments(bundle);
        return dialog;
    }

    public interface EditPoiDialogListener {
        void onSaveClick(DialogFragment dialog, Long poiId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EditPoiDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement EditPoiDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String notPlanned = getString(R.string.not_planned_header);
        String dayDateTmpl = getString(R.string.day_and_date_tmpl);

        final Long poiId = getArguments().getLong(Poi.POI_ID);
        final Poi poi = Poi.getById(poiId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_poi_edit, null);
        builder.setView(dialogView);
        builder.setTitle(poi.getName());

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.days_spinner);
        final EditText note = (EditText) dialogView.findViewById(R.id.note);

        ArrayAdapter adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, poi.getTrip()
                .getAllDates(notPlanned, dayDateTmpl));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(poi.getDay());
        note.setText(poi.getNote());

        builder.setPositiveButton(R.string.save_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (spinner.getSelectedItemPosition() != poi.getDay()
                                        || ! note.getText().toString().equals(poi.getNote())) {
                                    poi.setDay(spinner.getSelectedItemPosition());
                                    poi.setNote(note.getText().toString());
                                    poi.save();
                                    mListener.onSaveClick(EditPoiDialog.this, poi.getId());
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