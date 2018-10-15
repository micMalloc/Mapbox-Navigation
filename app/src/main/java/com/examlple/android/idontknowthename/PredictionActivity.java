package com.examlple.android.idontknowthename;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.mapbox.geojson.Point;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class PredictionActivity extends AppCompatActivity  {

    private TextView datePick;
    private TextView timePick;
    private TextView destPick;
    private Point originPosition;
    private Point destinationPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        datePick = findViewById(R.id.datePicker);
        timePick = findViewById(R.id.timePicker);
        destPick = findViewById(R.id.destPicker);

        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int moy, int dom) {
                        String strDate = String.valueOf(y) + "년 ";
                        strDate += String.valueOf(moy + 1) + "월 ";
                        strDate += String.valueOf(dom) + "일";

                        Toast.makeText(PredictionActivity.this, strDate, Toast.LENGTH_SHORT).show();
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PredictionActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDateSetListener,
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        timePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        Toast.makeText(getApplicationContext(), hourOfDay + ":" + minutes, Toast.LENGTH_SHORT).show();
                    }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        PredictionActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        mTimeSetListener,
                        0,0, false
                );

                timePickerDialog.show();
            }
        });

        destPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });
    }

    void show () {
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(PredictionActivity.this);
        builder.setTitle("목적지 검색");
        builder.setMessage("원하시는 목적지를 검색하세요.");
        builder.setView(editText);

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String destination = editText.getText().toString();

                Geocoder mGeocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> mResultLocation = mGeocoder.getFromLocationName(destination, 1);
                    double latitude = mResultLocation.get(0).getLatitude();
                    double longitude = mResultLocation.get(0).getLongitude();
//
//                    destinationPosition = Point.fromLngLat(longitude, latitude);
//                    originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
//                    getRoute(originPosition, destinationPosition);
//
//                    startButton.setEnabled(true);
//                    startButton.setBackgroundResource(R.color.mapboxBlue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.show();
    }

}
