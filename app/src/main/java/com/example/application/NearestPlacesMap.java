package com.example.application;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class NearestPlacesMap extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Button btnstartnavigation;
    static List<LatLng> directionList;
    ArrayList<String> myList;
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    private double slat, slon, dlat, dlon,hlat, hlon, clat, clon;
    KesriTripDB ktdb;
    Calendar calendar=Calendar.getInstance();
    LocationManager locationManager;
    Location LocationUp;
    Marker myMarker;
    private String fetchStr = IPsetting.ip + "fetchpetrolpumpandhotel.php";
    static String selectedmarkerid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_places_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        btnstartnavigation=(Button)findViewById(R.id.btnStartNavigationOnMap);
        ktdb=new KesriTripDB(NearestPlacesMap.this,"locationreminder",null,1);
        getLocation();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
/*        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        try {
            Cursor cursor = ktdb.getTripDetails();
            while (cursor.moveToNext()) {
                SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
                String ctime = sd.format(calendar.getTime());
                String scdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String stripdate = cursor.getString(11);
                Date cDateTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(ctime);
                Calendar tempcal=Calendar.getInstance();
                String shotelrating = cursor.getString(15);
                if (scdate.equals(stripdate)) {
                    slat=Double.parseDouble(cursor.getString(3));
                    slon=Double.parseDouble(cursor.getString(4));
                    hlat=Double.parseDouble(cursor.getString(6));
                    hlon=Double.parseDouble(cursor.getString(7));
                    dlat=Double.parseDouble(cursor.getString(9));
                    dlon=Double.parseDouble(cursor.getString(10));
                    LatLng sourceLatLng = new LatLng(slat,slon );
                    mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source Location"));
                    LatLng haltLatLng = new LatLng(hlat,hlon );
                    mMap.addMarker(new MarkerOptions().position(haltLatLng).title("Halt Location"));
                    LatLng destLatLng = new LatLng(dlat,dlon );
                    mMap.addMarker(new MarkerOptions().position(destLatLng).title("Destination Location"));

                    String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin=" + String.valueOf(slat) + "," + String.valueOf(slon) + "&destination=" + String.valueOf(dlat) + "," + String.valueOf(dlon)+"&waypoints="+hlat+","+hlon+"&key=AIzaSyC94Rkax10-74mEZTVj1EVmB1m7osUfbUs";
                    getDirectionFromDirectionApiServer(directionApiPath);

                    String sbreakfasttime = cursor.getString(12);
                    Date breakfastDateF = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(sbreakfasttime);
                    tempcal.setTime(breakfastDateF);
                    tempcal.add(Calendar.HOUR,1);
                    String sbreakfastendtime=sd.format(tempcal.getTime());

                    if (isTimeBetweenTwoTime(sbreakfasttime,sbreakfastendtime , ctime)) {
                        Toast.makeText(NearestPlacesMap.this,"Breakfast time",Toast.LENGTH_SHORT).show();
                        if(ReminderService.clat==0.0 || ReminderService.clon==0.0){
                            Toast.makeText(NearestPlacesMap.this,"System fetching location!!!",Toast.LENGTH_SHORT).show();
                        }else {
                            getlistofhotelsandpetrolpump(shotelrating);
                        }
                    }
                    String slunchtime = cursor.getString(13);
                    Date lunchtimeDateF = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(slunchtime);
                    tempcal.setTime(lunchtimeDateF);
                    tempcal.add(Calendar.HOUR,1);
                    String slunchendtime=sd.format(tempcal.getTime());
                    if (isTimeBetweenTwoTime(slunchtime,slunchendtime , ctime)) {
                        Toast.makeText(NearestPlacesMap.this,"Lunch time",Toast.LENGTH_SHORT).show();
                        if(ReminderService.clat==0.0 || ReminderService.clon==0.0){
                            Toast.makeText(NearestPlacesMap.this,"System fetching location!!!",Toast.LENGTH_SHORT).show();
                        }else {
                            getlistofhotelsandpetrolpump(shotelrating);
                        }
                    }
                    String sdinnertime = cursor.getString(14);
                    Date dinnertimeDateF = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(sdinnertime);
                    tempcal.setTime(dinnertimeDateF);
                    tempcal.add(Calendar.HOUR,1);
                    String sdinnerendtime=sd.format(tempcal.getTime());
                    if (isTimeBetweenTwoTime(sdinnertime,sdinnerendtime , ctime)) {
                        Toast.makeText(NearestPlacesMap.this,"Dinner time",Toast.LENGTH_SHORT).show();
                        if(ReminderService.clat==0.0 || ReminderService.clon==0.0){
                            Toast.makeText(NearestPlacesMap.this,"System fetching location!!!",Toast.LENGTH_SHORT).show();
                        }else {
                            getlistofhotelsandpetrolpump(shotelrating);
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void getDirectionFromDirectionApiServer(String url) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }
    private Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if (response.getStatus().equals("OK")) {
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(mMap, mDirections);
                    } else {
                        Toast.makeText(NearestPlacesMap.this, "Maps server error!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(8).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(18)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        directionList = new ArrayList<LatLng>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline) {
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)
                    && actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:MM format");
        }
    }

    private void getLocation() {
        String context = LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        if (locationManager != null) {
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(provider, (long) 0, (float) 0, this);
            } else {
                if (locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 50, this);

                } else if (locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                } else if (locationManager
                        .isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationUp = location;
        clat = location.getLatitude();
        clon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {   }

    @Override
    public void onProviderEnabled(String s) {   }

    @Override
    public void onProviderDisabled(String s) {    }

    private void getlistofhotelsandpetrolpump(String shrating){
        RequestParams params=new RequestParams();
        params.put("clat",ReminderService.clat);
        params.put("clon",ReminderService.clon);
        params.put("dist","5");
        params.put("shrating",shrating);

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(fetchStr,params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s=new String(responseBody);
                myList= new ArrayList<String>();
//                double lat1=18.648387;
//                double lon1=73.764370;
                try {
                    JSONObject object1=new JSONObject(s);
                    mMap.clear();
                    System.out.println(object1);
                    JSONArray jsonArray=object1.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object2=jsonArray.getJSONObject(i);
                        System.out.println(object2.getDouble("lat")+" "+object2.getDouble("lon"));
                        LatLng latLng1 = new LatLng(object2.getDouble("lat"), object2.getDouble("lon"));
                        mMap.setOnMarkerClickListener(NearestPlacesMap.this);
                        myMarker = mMap.addMarker(new MarkerOptions().position(latLng1).title(object2.getString("name")).snippet(object2.getString("type")));
                        myList.add(object2.getString("name")+"\n"+object2.getString("type"));
                        myMarker.setTag(object2.getString("id"));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng1)
                                .zoom(12)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    }
                    LatLng sourceLatLng = new LatLng(slat,slon );
                    mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source Location"));
                    LatLng haltLatLng = new LatLng(hlat,hlon );
                    mMap.addMarker(new MarkerOptions().position(haltLatLng).title("Halt Location"));
                    LatLng destLatLng = new LatLng(dlat,dlon );
                    mMap.addMarker(new MarkerOptions().position(destLatLng).title("Destination Location"));

                    String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin=" + String.valueOf(slat) + "," + String.valueOf(slon) + "&destination=" + String.valueOf(dlat) + "," + String.valueOf(dlon)+"&waypoints="+hlat+","+hlon+"&key=AIzaSyC94Rkax10-74mEZTVj1EVmB1m7osUfbUs";
                    getDirectionFromDirectionApiServer(directionApiPath);
                    System.out.println(object1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(error);
                Toast.makeText(NearestPlacesMap.this,"Error occured"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedmarkerid = (String) marker.getTag();
        Intent i = new Intent(NearestPlacesMap.this,NearestPlacesMap.class);
        startActivity(i);
        return false;
    }
}