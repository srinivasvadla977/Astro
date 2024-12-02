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

import java.util.Objects;

public class Notification_groupChat {

    static Notification_groupChat notification_groupChat;

    PowerManager powerManager;
    Ringtone ringtone;

    public static Notification_groupChat getNotification_groupChat_Instance() {
        if (notification_groupChat == null) {
            notification_groupChat = new Notification_groupChat();
        }
        return notification_groupChat;
    }

    private final String channelId = "CHANNEL_ID_GROUP_CHAT";
    int notificationId = 121;

    private NotificationChannel channel;

    int ringToneType;
    Uri soundUri;
    Uri soundUri2;




    public void CreateChannel(Context c) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channel == null) {

            String channelName = "Group Chat"; // visible to user when enabling notification in settings
            String channelDescription = "notifies incoming messages";

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + c.getPackageName() + "/" + R.raw.notif_sound);

            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(false);


            NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

        }

    }


    public void TriggerNotification(Context c, String sender, String description, String uri) {

        if (channel == null) {
            CreateChannel(c);
        }

        // INTENT TO OPEN APP
        Intent openingIntent= new Intent(c, MainActivity.class);
        openingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pi= PendingIntent.getActivity(c,11,openingIntent,PendingIntent.FLAG_IMMUTABLE);



        // Notification notification =

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, channelId)
                .setContentTitle(sender)
                .setContentText(description)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.small_icon)
                .setContentIntent(pi)
                .setVibrate(new long[]{0});


        // IF URI IS NULL, SHOWING NOTIFICATION WITHOUT BIG ICON
        if (Objects.equals(uri, "") || uri==null && ActivityCompat.checkSelfPermission(c, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(c).notify(notificationId++, builder.build());
            LightUpAndRing(c);
        }
        // IF URI IS NOT NULL, SHOWING NOTIFICATION WITH BIG ICON
        else {
            Glide.with(c)
                    .asBitmap()
                    .load(uri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            builder.setLargeIcon(resource);
                            builder.setStyle(
                                    new NotificationCompat.BigPictureStyle()
                                            .bigPicture(resource)
                                            .bigLargeIcon((Icon) null)
                                            .setBigContentTitle(sender)
                                            .setSummaryText(description)
                            );
                            if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                NotificationManagerCompat.from(c).notify(notificationId++, builder.build());
                                LightUpAndRing(c);
                            }
                        }
                    });
        }

      // 1= leave
      if (sender.equalsIgnoreCase("Astro") && description.contains("Leave tracker")){
          ringToneType=1;
      }else {
          ringToneType=0;
      }

        soundUri=  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ c.getPackageName() +"/"+R.raw.chin_tapak_dum_dum);
        soundUri2= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ c.getPackageName() +"/"+R.raw.message_notif_grpchat);



    }

    private void LightUpAndRing(Context context){

        powerManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,context.getPackageName()+":myLock");
            wl.acquire(50000);
        }

        if(ringToneType==1){
            ringtone= RingtoneManager.getRingtone(context,soundUri);
            ringtone.play();
         }else {
            ringtone= RingtoneManager.getRingtone(context,soundUri2);
            ringtone.play();
         }



    }

}

