package com.mycreation.astro;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mycreation.astro.receivers.NightShift_bcReceiver;

import java.util.Calendar;

import javax.inject.Singleton;

public class AutoScheduler_nightShift {

    Calendar todayCal= Calendar.getInstance();
    static AutoScheduler_nightShift autoSchedulerNightShift;
    static Context context;
    static String curUser;

    int REQ_CODE=7;
    AlarmManager alarmManager;

    @Singleton
    static AutoScheduler_nightShift getInstance(Context c, String cu){
        if (autoSchedulerNightShift==null){
            autoSchedulerNightShift=new AutoScheduler_nightShift();
        }
        context=c;
        curUser=cu;
        return autoSchedulerNightShift;
    }


    @SuppressLint("ScheduleExactAlarm")
    public void ScheduleForNightShift(){

        todayCal.set(Calendar.DAY_OF_MONTH, todayCal.get(Calendar.DAY_OF_MONTH)+1);
        todayCal.set(Calendar.HOUR_OF_DAY,0);
        todayCal.set(Calendar.MINUTE,1);

        alarmManager=(AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent= new Intent(context, NightShift_bcReceiver.class);
        intent.putExtra("curUser", curUser);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,REQ_CODE,intent,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent==null){
            pendingIntent=PendingIntent.getBroadcast(context,REQ_CODE,intent,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, todayCal.getTimeInMillis(), pendingIntent);

        }

    }

}
