package com.examlple.android.idontknowthename;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public class ContextManager {

    private static volatile ContextManager contextManager;
    private static GoogleApiClient client;
    private static Context contextData;
    private static Location userLocation;

    private static DateInfo dateInfo;
    private static TimeInfo timeInfo;
    private static DestinationInfo destinationInfo;
    private static SourceInfo sourceInfo;

    private ContextManager (Context context) {
        contextData = context;
        client = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    /**
     * SingleTon
     * Synchronization
     * DCL(Double Checking Locking)
     * @return ContextManager Instance
     */
    public static ContextManager getInstance (Context context) {
        if (contextManager == null) {
            synchronized (ContextManager.class) {
                contextManager = new ContextManager(context);
            }
        } return contextManager;
    }

    public Location getUserCurrentLocation () {

        userLocation = null;
        if (ActivityCompat.checkSelfPermission(contextData, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Awareness.SnapshotApi.getLocation(client)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (!locationResult.getStatus().isSuccess()) {
                                return;
                            }
                            Location location = locationResult.getLocation();
                            Toast.makeText(contextData, location.toString(), Toast.LENGTH_SHORT).show();
                            userLocation = location;
                        }
                    });
        }
        if (userLocation == null) {
            Toast.makeText(contextData, "Could not find Loca", Toast.LENGTH_LONG).show();
        }
        return userLocation;
    }

    public void setDateInfo (int y, int m, int d) {
        this.dateInfo = new DateInfo(y, m, d);
    }

    public DateInfo getDateInfo() {
        return dateInfo;
    }

    public void setTimeInfo (int h, int m) {
        this.timeInfo = new TimeInfo(h, m);
    }

    public TimeInfo getTimeInfo() {
        return timeInfo;
    }

    public void setDestinationInfo (String dest, double lat, double lon) {
        this.destinationInfo = new DestinationInfo(dest, lat, lon);
    }

    public DestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    public void setSourceInfo (String s, double lat, double lon) {
        this.sourceInfo = new SourceInfo(s, lat, lon);
    }

    public SourceInfo getSourceInfo () {
        return sourceInfo;
    }

    class DateInfo {
        int year;
        int month;
        int date;

        public DateInfo (int y, int mon, int dom) {
            year = y;
            month = mon;
            date = dom;
        }
    }

    class TimeInfo {
        int hour;
        int minutes;

        public TimeInfo (int h, int m) {
            hour = h;
            minutes = m;
        }
    }

    class DestinationInfo {
        String destination;
        double latitude;
        double longitude;

        public DestinationInfo (String dest, double lat, double lon) {
            destination = dest;
            latitude = lat;
            longitude = lon;
        }
    }

    class SourceInfo {
        String source;
        double latitude;
        double longitude;

        public SourceInfo (String s, double lat, double lon) {
            source = s;
            latitude = lat;
            longitude = lon;
        }
    }
}
