package com.example.application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class StartTripMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btndone;
    static List<LatLng> directionList;
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    private double slat, slon, dlat, dlon,hlat, hlon;
    String strsrclocname,strsrclat,strsrclon,strhaltlocname,strhaltlat,strhaltlon,strdestlocname,strdestlat,strdestlon,strstartdate,strbreakfasttime,strlunchtime,strdinnertime,strhotelrating;

    SharedPreferences sharedpref;
    ProgressDialog pDialog;
    String addtripdetailsin2dbURL=IPsetting.ip+"addtripdetailsin2db.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btndone= (Button)findViewById(R.id.btnStartNavigationOnMap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        sharedpref = getSharedPreferences("addnewtrip", Context.MODE_PRIVATE);
        slat=Double.parseDouble(sharedpref.getString("srclat","18.0"));
        slon=Double.parseDouble(sharedpref.getString("srclon","18.0"));
        dlat=Double.parseDouble(sharedpref.getString("destlat","18.0"));
        dlon=Double.parseDouble(sharedpref.getString("destlon","18.0"));
        hlat=Double.parseDouble(sharedpref.getString("haltlat","18.0"));
        hlon=Double.parseDouble(sharedpref.getString("haltlon","18.0"));
        strsrclocname=sharedpref.getString("srclocname","defaultval");
        strsrclat =sharedpref.getString("srclat","defaultval");
        strsrclon=sharedpref.getString("srclon","defaultval");
        strhaltlocname=sharedpref.getString("haltlocname","defaultval");
        strhaltlat=sharedpref.getString("haltlat","defaultval");
        strhaltlon=sharedpref.getString("haltlon","defaultval");
        strdestlocname=sharedpref.getString("destlocname","defaultval");
        strdestlat=sharedpref.getString("destlat","defaultval");
        strdestlon=sharedpref.getString("destlon","defaultval");
        strstartdate=sharedpref.getString("startdate","defaultval");
        strbreakfasttime=sharedpref.getString("breakfasttime","defaultval");
        strlunchtime=sharedpref.getString("lunchtime","defaultval");
        strdinnertime=sharedpref.getString("dinnertime","defaultval");
        strhotelrating=sharedpref.getString("hotelrating","defaultval");

        LatLng source1 = new LatLng(slat, slon);
        LatLng dest1 = new LatLng(dlat, dlon);
        LatLng halt1 = new LatLng(hlat, hlon);

        mMap.addMarker(new MarkerOptions().position(dest1).title("destination location"));
        mMap.addMarker(new MarkerOptions().position(halt1).title("halt location"));
        mMap.addMarker(new MarkerOptions().position(source1).title("source location"));
        String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin=" + String.valueOf(slat) + "," + String.valueOf(slon) + "&destination=" + String.valueOf(dlat) + "," + String.valueOf(dlon)+"&waypoints="+hlat+","+hlon+"&key=AIzaSyC94Rkax10-74mEZTVj1EVmB1m7osUfbUs";

        getDirectionFromDirectionApiServer(directionApiPath);

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmtripdetails();
            }
        });
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
                        Toast.makeText(StartTripMap.this, "Maps server error!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
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
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
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
    public void confirmtripdetails(){
        RequestParams params= new RequestParams();
        params.put("uid", IPsetting.uid);
        params.put("strsrclocname", strsrclocname);
        params.put("strsrclat", strsrclat);
        params.put("strsrclon", strsrclon);
        params.put("strhaltlocname", strhaltlocname);
        params.put("strhaltlat", strhaltlat);
        params.put("strhaltlon", strhaltlon);
        params.put("strdestlocname", strdestlocname);
        params.put("strdestlat", strdestlat);
        params.put("strdestlon", strdestlon);
        params.put("strstartdate", strstartdate);
        params.put("strbreakfasttime", strbreakfasttime);
        params.put("strlunchtime", strlunchtime);
        params.put("strdinnertime", strdinnertime);
        params.put("strhotelrating", strhotelrating);
        params.put("json",String.valueOf(VisitingLocationList.json));

        pDialog = new ProgressDialog(StartTripMap.this);
        pDialog.setMessage("Processing..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient client= new AsyncHttpClient();
        client.post(addtripdetailsin2dbURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response =new String(responseBody);
                System.out.print(response);
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    if(obj.getString("success").equals("200"))
                    {
                        //IPsetting.uname=obj.getString("name");
                        Intent inte=new Intent(StartTripMap.this,UserHome.class);
                        startActivity(inte);
                        finish();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(StartTripMap.this, "Exception: "+e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(StartTripMap.this, "Connection Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }
}