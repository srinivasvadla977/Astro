package com.mycreation.astro.receivers;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mycreation.astro.services.GroupChat_notification_Service;

public class GroupChat_notification_broadcastReceiver extends BroadcastReceiver {

    Context context;
    int RESCHEDULING_REQ_CODE=101;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;

      //  Toast.makeText(context,"BroadCast RECEIVED",Toast.LENGTH_SHORT).show();
        // rescheduling broadcast for every 30 min
        Reschedule();

        //
        if (!isServiceRunning()){

          //  Toast.makeText(context,"starting service again",Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(context, GroupChat_notification_Service.class);
            context.startService(myIntent);

        }
      /*  else {

            Toast.makeText(context,"service already running",Toast.LENGTH_SHORT).show();
        }*/




    }

    @SuppressLint("ScheduleExactAlarm")
    private void Reschedule(){

        long triggerAt= System.currentTimeMillis()+(1000*60*30); // 30 minutes

        Intent intent=new Intent(context, GroupChat_notification_broadcastReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context,RESCHEDULING_REQ_CODE,intent,PendingIntent.FLAG_MUTABLE); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing

        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,triggerAt,pendingIntent);
       // Toast.makeText(context,"Broad cast reset success",Toast.LENGTH_LONG).show();

    }

    private boolean isServiceRunning() {

        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GroupChat_notification_Service.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }


}
