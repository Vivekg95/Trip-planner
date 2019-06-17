package com.example.application;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;

public class ReminderService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    Cursor cur;
    static double alpha,beta,earth_radius=6372.795477598,a,b,dist,clat=0.0,clon=0.0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    LocationListener loc;
    LocationManager locationManager;
    protected static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location locationUp;
    KesriTripDB ktdb;
    NotificationManager nManager;
    NotificationChannel notificationChannel;
    String channelid="com.example.locationbasetaskreminder";

    public ReminderService() { }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            TimeReceiver tm = new TimeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            getBaseContext().registerReceiver(tm,filter);
        }catch (Exception e){
            e.printStackTrace();
            System.out.print(e);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(90000);
        //mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient.connect();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            notificationChannel = new NotificationChannel(channelid, "Trip planning", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(this, notificationChannel.getId())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Trip planning service running...")
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Trip planning service running...")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);
        }

        return START_STICKY;
    }

    public void checklocation(){
        try {
            ktdb = new KesriTripDB(getApplicationContext(), "kesritripsqlitedb", null, 1);
            cur = ktdb.getTripDetails();
            while (cur.moveToNext()) {
                Double dblat = Double.parseDouble(cur.getString(6));
                Double dblon = Double.parseDouble(cur.getString(7));

                alpha = Math.abs(clat - dblat);
                beta = Math.abs(clon - dblon);

                alpha = alpha / 2;
                System.out.println("Value of alpha :  " + alpha);
                beta = beta / 2;
                System.out.println("Value of beta :  " + beta);

                a = Math.sin(Math.toRadians(alpha)) * Math.sin(Math.toRadians(alpha)) + Math.cos(Math.toRadians(clat)) * Math.cos(Math.toRadians(clat)) * Math.sin(Math.toRadians(beta)) * Math.sin(Math.toRadians(beta));
                System.out.println("Value of a :  " + a);
                b = Math.asin(Math.min(1, Math.sqrt(a)));
                System.out.println("Value of b :  " + b);
                dist = 2 * earth_radius * b;
                dist = Math.round(dist);
                dist = dist * 1000;
                if (dist < 500) {

                    Intent editIntent = new Intent(getApplicationContext(), UserHome.class);
                    editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent mClick = PendingIntent.getActivity(getApplicationContext(), 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setSmallIcon(R.drawable.ic_today_black_24dp)
                            .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                            .setTicker(cur.getString(4))
                            .setContentText("You are near to halt location")
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                            .setContentIntent(mClick)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(false);
                    nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nManager.notify(0, mBuilder.build());

/*                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    notificationChannel = new NotificationChannel(channelid, "Mobile Tracker", NotificationManager.IMPORTANCE_HIGH);

                    Notification.Builder builder = new Notification.Builder(this, notificationChannel.getId())
                            .setContentTitle(getString(R.string.app_name))
                            .setTicker(cur.getString(4))
                            .setContentText(cur.getString(5))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                            .setContentIntent(mClick)
                            .setAutoCancel(true);

                    Notification notification = builder.build();
                    startForeground(1, notification);

                } else {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setContentTitle(getString(R.string.app_name))
                            .setTicker(cur.getString(4))
                            .setContentText(cur.getString(5))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                            .setContentIntent(mClick)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);

                    Notification notification = builder.build();
                    startForeground(1, notification);
                }*/
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationUp=location;
        clat=locationUp.getLatitude();
        clon=locationUp.getLongitude();
        checklocation();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent i = new Intent("com.example.application.RestartSensor");
        sendBroadcast(i);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Intent i = new Intent("com.example.application.RestartSensor");
        sendBroadcast(i);
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}