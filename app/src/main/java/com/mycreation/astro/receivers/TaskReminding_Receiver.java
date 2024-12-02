package com.mycreation.astro.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.Toast;

import com.mycreation.astro.Alert_Task_Activity;
import com.mycreation.astro.NotificationHelper;
import com.mycreation.astro.R;
import com.mycreation.astro.object_models.NewTaskModel;

public class TaskReminding_Receiver extends BroadcastReceiver {

    PowerManager powerManager;
    Ringtone ringtone;
    int REQ_CODE=10;

    @Override
    public void onReceive(Context context, Intent intent) {

       // Toast.makeText(context,"Alarm BroadCast received!", Toast.LENGTH_SHORT).show();

        /*powerManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,context.getPackageName()+":myLock");
            wl.acquire(50000);
        }*/

        /*String title= intent.getStringExtra("title");
        String description=intent.getStringExtra("description");
        String imgUri=intent.getStringExtra("imageUri");*/

        NewTaskModel model= (NewTaskModel) intent.getSerializableExtra("taskModel");
        String curUser= intent.getStringExtra("curUser");
       // Toast.makeText(context,curUser+" :BCR", Toast.LENGTH_SHORT).show();

        if (model.isHighPriority()){

            Intent i= new Intent(context, Alert_Task_Activity.class);
           // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("taskModel",model);
            i.putExtra("curUser",curUser);


            LightUpAndRing(context);
            context.startActivity(i);
        }else {
            NotificationHelper.getNotificationHelperInstance().TriggerNotification(context,model, curUser);
        }



        /*Uri soundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ context.getPackageName() +"/"+R.raw.notif_sound);
        ringtone= RingtoneManager.getRingtone(context,soundUri);
        ringtone.play();*/

    }

    private void LightUpAndRing(Context context){

        powerManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,context.getPackageName()+":myLock");
            wl.acquire(50000);
        }

        Uri soundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ context.getPackageName() +"/"+R.raw.notif_sound);
        ringtone= RingtoneManager.getRingtone(context,soundUri);
        //ringtone.play();

    }

}
