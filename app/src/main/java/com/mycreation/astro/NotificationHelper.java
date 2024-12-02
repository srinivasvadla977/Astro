package com.mycreation.astro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mycreation.astro.fragments.DashboardFragment;
import com.mycreation.astro.fragments.GroupChatFragment;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.NewTaskModel;

import java.util.Objects;

public class NotificationHelper {

    static NotificationHelper notificationHelper;

    PowerManager powerManager;
    Ringtone ringtone;
    MyUtils myUtils;

    PendingIntent pendingIntent;

    public static NotificationHelper getNotificationHelperInstance() {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper();
        }
        return notificationHelper;
    }

    String channelId = "CHANNEL_ID";
    int notificationId = 10;
    int pendingIntentReqCode=11;

    NotificationChannel channel;


    public void CreateChannel(Context c) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channel == null) {

            String channelName = "Upcoming Activities";        // visible to user when enabling notification in settings
            String channelDescription = "notifies scheduled upcoming activities";

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + c.getPackageName() + "/" + R.raw.notif_sound);


            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.setSound(soundUri, null);
            channel.setVibrationPattern(new long[]{0, 150, 100, 150});
            channel.enableVibration(true);

            NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

        }

    }


    public void TriggerNotification(Context c, NewTaskModel model, String curUser) {

        myUtils=new MyUtils(c);

        if (channel == null) {
            CreateChannel(c);
        }

        String title= model.getTaskTitle();

        // Action TO update tracker
        //  TO DO SOME ACTION ON NOTIFICATION BUTTON PRESSED
        Intent intent = new Intent(c, Action_notification.class); //when pressed on notification it triggers to mainActivity
        intent.putExtra("notificationId",notificationId);
        intent.putExtra("taskTitle", title);
        intent.putExtra("curUser", curUser);
        intent.setAction("INTENT_ACTION");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // new task will create new instance of mainActivity while clear task will clear all existing 												activities of the app

        pendingIntent = PendingIntent.getBroadcast(c, pendingIntentReqCode++, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing
        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.small_icon, "mark as done by me!", pendingIntent);

        // Action TO IGNORE
        Intent ignoreIntent= new Intent(c,Action_notification.class);
        ignoreIntent.putExtra("notificationId", notificationId);
        PendingIntent ignorePI= PendingIntent.getBroadcast(c,pendingIntentReqCode++,ignoreIntent,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action ignoreAction= new NotificationCompat.Action(R.drawable.small_icon,"Ignore", ignorePI);

        // INTENT TO OPEN APP
        Intent openingIntent= new Intent(c, MainActivity.class);
       // openingIntent.setAction("TASK_NOTIFICATION");
        openingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        openingIntent.putExtra("taskModel", model);
        openingIntent.putExtra("TaskOverViewFrag", true);

        PendingIntent pi= PendingIntent.getActivity(c,pendingIntentReqCode++,openingIntent,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


        String greetings="";
        if (model.isRepeatingTaskRight()){
            if (model.isDailyRepeatingTaskRight()){
                greetings+="Daily task reminder!";
            }else {
                greetings+="Weekly task reminder!";
            }
        }else {greetings+="One time task reminder!";}


        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, channelId)
                .setContentTitle(greetings)
                .setContentText(model.getTaskTitle())
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.small_icon)
                .setContentIntent(pi)
                ;

                if (model.isWeekEndRight()){
                    builder.addAction(action);
                    builder.addAction(ignoreAction);
                }

        // IF URI IS NULL, SHOWING NOTIFICATION WITHOUT BIG ICON
        if ((Objects.equals(model.getTaskImageUrl(), "") || model.getTaskImageUrl()==null || !myUtils.IsInternetAvailable()) && ActivityCompat.checkSelfPermission(c, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            builder.setContentTitle(model.getTaskTitle()+": "+greetings);
            builder.setContentText(model.getTaskDescription());
            NotificationManagerCompat.from(c).notify(notificationId++, builder.build());
            LightUpAndRing(c);
        }
        // IF URI IS NOT NULL, SHOWING NOTIFICATION WITH BIG ICON
        else {
            Glide.with(c)
                    .asBitmap()
                    .load(model.getTaskImageUrl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            builder.setLargeIcon(resource);
                            builder.setStyle(
                                    new NotificationCompat.BigPictureStyle()
                                            .bigPicture(resource)
                                            .bigLargeIcon((Icon) null)
                                            .setBigContentTitle(model.getTaskTitle())
                                            .setSummaryText(model.getTaskDescription())
                            );
                            if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                NotificationManagerCompat.from(c).notify(notificationId++, builder.build());
                                LightUpAndRing(c);
                            }
                        }
                    });
        }



    }

    private void LightUpAndRing(Context context){

        powerManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,context.getPackageName()+":myLock");
            wl.acquire(10000);
        }

       // Uri soundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ context.getPackageName() +"/"+R.raw.notif_sound);
        Uri soundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ context.getPackageName() +"/"+R.raw.daily_tasks);
        ringtone= RingtoneManager.getRingtone(context,soundUri);
        ringtone.play();

    }

}
