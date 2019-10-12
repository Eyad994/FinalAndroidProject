package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;

public class DialogMap extends AppCompatDialogFragment {

    private static final String TAG = "DialogMap";
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    private String apiTitle;
    private EditText editText;
    private ExampleDialogListener listener;
    private int mHour;
    private int mMinute;
    private int mDay;
    private int mMonth;
    private int mYear;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_dialog, null);

        editText = view.findViewById(R.id.alert_dialogg);

        builder.setView(view)
                .setIcon(R.drawable.logo)
                .setTitle(apiTitle)
                .setNegativeButton("cancel", null)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String isEmpty = editText.getText().toString();
                        if (!isEmpty.matches("")) {
                            listener.applyInputs(mYear, mMonth, mDay, mHour, mMinute);
                        } else {
                            Log.d(TAG, "Dialog empty: Null");
                        }
                        /*Intent intent = new Intent(((Dialog) dialog).getContext(), FinalActivity.class);
                        startActivity(intent);*/

                    }
                });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog(v);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public void setApiTitle(String apiTitle) {
        this.apiTitle = apiTitle;
    }

    public interface ExampleDialogListener {
        void applyInputs(int year, int month, int day, int hour, int minute);
    }

    private void DatePickerDialog(View view) {

        final Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), android.R.style.Theme_Holo_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mDay = dayOfMonth;
                        mMonth = monthOfYear + 1;
                        mYear = year;
//                        editText.setText(dayOfMonth + "/"
//                                + (monthOfYear + 1) + "/" + year);
                        timePicker();

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        dialog.show();
    }

    private void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;


                        editText.setText(mDay + "/"
                                + (mMonth) + "/" + mYear + " - " + mHour + ":" + mMinute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}
