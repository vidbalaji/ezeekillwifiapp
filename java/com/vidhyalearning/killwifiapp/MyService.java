package com.vidhyalearning.killwifiapp;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.format.Time;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MyService extends Service {
    public MyService() {
    }
int disableInMinutes,coolingInMinutes;
    public long NOTIFY_INTERVAL = 30 * 1000; // 30 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    Calendar coolingTime = Calendar.getInstance();
    Calendar disableTimeReached = Calendar.getInstance();

    Boolean coolingTimeReached= false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        disableInMinutes = intent.getIntExtra("disableTimer", 0);
        coolingInMinutes = intent.getIntExtra("coolingTimer", 0);
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        disableTimeReached.add(Calendar.MINUTE, disableInMinutes);

        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }
    catch(IllegalStateException ex){
        Toast.makeText(this,"Please Stop service and try.\n Error: " +ex.getMessage(),Toast.LENGTH_SHORT).show();
       throw ex;
    }
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        // cancel if already existed
    }
    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {

                    killProcess();
                }

            });
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void killProcess(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      Calendar now = Calendar.getInstance();
     if(now.after(disableTimeReached) && wifiManager.isWifiEnabled() && !coolingTimeReached) {

         wifiManager.setWifiEnabled(false);
         disableTimeReached  = Calendar.getInstance();
         disableTimeReached.add(Calendar.MINUTE, disableInMinutes);
         coolingTime =  Calendar.getInstance();
         coolingTime.add(Calendar.MINUTE, coolingInMinutes);
         coolingTimeReached = true;
         Toast.makeText(this,"Disabling Wifi as time limit reached",Toast.LENGTH_LONG).show();
     }
     else if(coolingTimeReached &&  now.before(coolingTime) && wifiManager.isWifiEnabled()){
         wifiManager.setWifiEnabled(false);
         Toast.makeText(this,"Disabling Wifi as cooling time limit not yet reached",Toast.LENGTH_LONG).show();

     }
     else if(coolingTimeReached &&  Calendar.getInstance().after(coolingTime)){
         coolingTimeReached = false;
         disableTimeReached  = Calendar.getInstance();
         disableTimeReached.add(Calendar.MINUTE, disableInMinutes);
     }
     else if(now.after(disableTimeReached) && wifiManager.isWifiEnabled()==false )
     {
         disableTimeReached  = Calendar.getInstance();
         disableTimeReached.add(Calendar.MINUTE, disableInMinutes);
     }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mHandler=null;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
