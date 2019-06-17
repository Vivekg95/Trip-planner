package com.example.application;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeReceiver extends BroadcastReceiver {

    PendingIntent pendingIntent;
    NotificationManager nManager;
    SimpleDateFormat sdf;
    Calendar calendar=Calendar.getInstance();
    Context context;
    KesriTripDB ktdb;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        this.context=context;
        try {
            ktdb=new KesriTripDB(context,"locationreminder",null,1);
            Cursor cursor=ktdb.getTripDetails();
            while(cursor.moveToNext()) {
                calendar = Calendar.getInstance();

                SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
                String ctime = sd.format(calendar.getTime());
                String scdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String ptime = cursor.getString(14);
                    String stripdate=cursor.getString(11);
                Date cDateTime= new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(ctime);
                Date pDateTime= new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(ptime);

                    String[] pt = ptime.split(":");
                    String[] ct = ctime.split(":");
                    if(scdate.equals(stripdate)) {
                        if (ptime.equals(ctime) || cDateTime.after(pDateTime)) {
                            Intent editIntent = new Intent(context, UserHome.class);
                            //change this intent with user task list
                            editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent mClick = PendingIntent.getActivity(context, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            // Create Notification
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                    .setSmallIcon(R.drawable.ic_today_black_24dp)
                                    .setContentTitle(context.getResources().getString(R.string.app_name))
                                    .setTicker(cursor.getString(4))
                                    .setContentText(cursor.getString(5))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                                    .setContentIntent(mClick)
                                    .setAutoCancel(true)
                                    .setOnlyAlertOnce(false);

/*                        MediaPlayer ring = MediaPlayer.create(context, R.raw.plucky);
                        ring.start();*/

                            nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            nManager.notify(3, mBuilder.build());
                        }
                    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}