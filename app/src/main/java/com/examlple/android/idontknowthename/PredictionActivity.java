package com.examlple.android.idontknowthename;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PredictionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView datePick;
    private TextView timePick;
    private TextView destPick;
    private TextView sourcePick;
    private Button startBtn;
    private Point originPosition;
    private Point destinationPosition;
    private ContextManager mContextManager;
    private static final String TAG = "PredictionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        datePick = findViewById(R.id.datePicker);
        timePick = findViewById(R.id.timePicker);
        sourcePick = findViewById(R.id.sourcePicker);
        destPick = findViewById(R.id.destPicker);
        startBtn = findViewById(R.id.startButton);

        mContextManager = ContextManager.getInstance(PredictionActivity.this);

        setOnclickListener();
    }

    void setOnclickListener () {
        datePick.setOnClickListener(this);
        timePick.setOnClickListener(this);
        sourcePick.setOnClickListener(this);
        destPick.setOnClickListener(this);
        startBtn.setOnClickListener(this);
    }

    void setOriginPosition () {
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(PredictionActivity.this);
        builder.setTitle("출발지 검색");
        builder.setMessage("원하시는 출발지를 검색하세요.");
        builder.setView(editText);

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String source = editText.getText().toString();

                Geocoder mGeocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> mResultLocation = mGeocoder.getFromLocationName(source, 1);
                    double latitude = mResultLocation.get(0).getLatitude();
                    double longitude = mResultLocation.get(0).getLongitude();

                    originPosition = Point.fromLngLat(longitude, latitude);
                    mContextManager.setSourceInfo(source, latitude, longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.show();
    }

    void setDestinationPosition () {
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

                    mContextManager.setDestinationInfo(destination, latitude, longitude);

                    destinationPosition = Point.fromLngLat(longitude, latitude);
                    if (originPosition != null) {
                        getRoute(originPosition, destinationPosition);

                        startBtn.setEnabled(true);
                        startBtn.setBackgroundResource(R.color.mapboxBlue);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.show();
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder().accessToken(Mapbox.getAccessToken()).origin(origin).destination(destination).build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "No Routes Found,Check right user and accesstoken");
                    return;
                } else if (response.body().routes().size() == 0) {
                    Log.e(TAG, "No Route");
                    return;
                }
                DirectionsRoute currentRoute = response.body().routes().get(0);
//                if (navigationMapRoute != null) {
//                    navigationMapRoute.removeRoute();
//                } else {
//                    navigationMapRoute = new NavigationMapRoute(null, mapView, map);
//                }
//
//                navigationMapRoute.addRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e(TAG, "Error" + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.datePicker: {

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

                        mContextManager.setDateInfo(y, moy + 1, dom);
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

                break;
            }
            case R.id.timePicker: {

                TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        mContextManager.setTimeInfo(hourOfDay, minutes);
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

                break;
            }
            case R.id.sourcePicker: {
                setOriginPosition();
                break;
            }
            case R.id.destPicker: {
                setDestinationPosition();
                break;
            }
            case R.id.startButton: {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .directionsProfile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                        .shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(PredictionActivity.this, options);
                break;
            }
        }
    }
}
