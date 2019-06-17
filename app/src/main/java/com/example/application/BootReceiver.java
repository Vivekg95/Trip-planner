package com.example.application;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {

    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        this.context = context;
        Intent myService = new Intent(context,ReminderService.class);

        while(!isMyServiceRunning(ReminderService.class)) {
            if("android.intent.action.SCREEN_ON".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.SCREEN_OFF".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.LOCKED_BOOT_COMPLETED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.DREAMING_STOPPED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.DREAMING_STARTED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.ACTION_POWER_DISCONNECTED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.DATE_CHANGED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if("com.example.source.pillreminder.RestartSensor".equals(intent.getAction())){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            }
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                //Toast.makeText(context, "Broadcast received...", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myService);
                } else {
                    context.startService(myService);
                }
            } else {
                if (!isMyServiceRunning(myService.getClass())) {
                    context.stopService(new Intent(context, ReminderService.class));
                }
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
