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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject toJson () {
        JSONObject request = new JSONObject();
        JSONObject contextJson = new JSONObject();
        String[] list = {"date", "time", "dest", "src"};

        for (int idx = 0; idx < list.length; ++ idx) {
            switch (idx) {
                case 0: {

                    try {
                        contextJson.put(list[idx], dateInfo.toJson());
                        //jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 1: {

                    try {
                        contextJson.put(list[idx], timeInfo.toJson());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {

                    try {
                        contextJson.put(list[idx], destinationInfo.toJson());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3: {

                    try {
                        contextJson.put(list[idx], sourceInfo.toJson());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default: break;
            }
        }

        try {
            request.put("context", contextJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return request;
    }

    class DateInfo {
        int year;
        int month;
        int day;

        public DateInfo (int y, int mon, int dom) {
            year = y;
            month = mon;
            day = dom;
        }

        public JSONObject toJson () {
            JSONObject dateJson = new JSONObject();

            try {
                dateJson.put("year", year);
                dateJson.put("month", month);
                dateJson.put("day", day);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return dateJson;
        }
    }

    class TimeInfo {
        int hour;
        int minutes;

        public TimeInfo (int h, int m) {
            hour = h;
            minutes = m;
        }

        public JSONObject toJson () {
            JSONObject timeJson = new JSONObject();

            try {
                timeJson.put("hour", hour);
                timeJson.put("minutes", minutes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return timeJson;
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

        public JSONObject toJson () {
            JSONObject destJson = new JSONObject();

            try {
                destJson.put("destination", destination);
                destJson.put("latitude", latitude);
                destJson.put("longitude", longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return destJson;
//            String jsonStr = "";
//
//            jsonStr += "{";
//            jsonStr += "\"destination\"" + ":" + destination + ", ";
//            jsonStr += "\"latitude\"" + ":" + latitude + ", ";
//            jsonStr += "\"longitude\"" + ":" + longitude;
//            jsonStr += "}";
//
//            return jsonStr;
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

        public JSONObject toJson () {
            JSONObject sourceJson = new JSONObject();

            try {
                sourceJson.put("source", source);
                sourceJson.put("latitude", latitude);
                sourceJson.put("longitude", longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return sourceJson;
        }
    }
}
