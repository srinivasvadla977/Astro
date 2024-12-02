package com.mycreation.astro;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.NewTaskModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Action_notification extends BroadcastReceiver {

    Calendar todayCal=Calendar.getInstance();
    DateFormat dateFormat= new SimpleDateFormat("dd:MM:yyyy");
    DateFormat timeFormat= new SimpleDateFormat("hh:mm:ss a");
    Calendar tempCal=Calendar.getInstance();

    boolean dateFound=false;
    String docId=null;
    String performers=null;
    String curUser;
    String title;

    CollectionReference collectionReference;
    FirebaseFirestore firestore;

    NotificationManager notificationManager;
    int  notificationId;


    @Override
    public void onReceive(Context context, Intent intent) {

        notificationId=intent.getIntExtra("notificationId",-1);
        notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        title=intent.getStringExtra("taskTitle");
        curUser=intent.getStringExtra("curUser");

    //    Toast.makeText(context,title+"****TTT",Toast.LENGTH_SHORT).show();
        FirebaseApp.initializeApp(context);

        firestore= FirebaseFirestore.getInstance();
        collectionReference=firestore.collection("Activity_tracker");

        try {
            String actionType= intent.getAction();
            if (actionType.equalsIgnoreCase("INTENT_ACTION")){
                UpdateTaskDB(context);
            }

        }catch (Exception e){
            //Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            notificationManager.cancel(notificationId);
        }


    }



    private void UpdateTaskDB(Context context){


        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot qds: queryDocumentSnapshots){
                    ActivityTracker_model modelAct= qds.toObject(ActivityTracker_model.class);

                    tempCal.setTimeInMillis(modelAct.getTimeStamp());
                    if (dateFormat.format(tempCal.getTime()).equalsIgnoreCase(dateFormat.format(todayCal.getTime()))){
                        dateFound=true;
                        docId= qds.getId();
                        performers=modelAct.getPerformer();
                    }
                }

                if (dateFound){
                    performers = performers +"\n"+ title+": "+ curUser+" @"+timeFormat.format(todayCal.getTime());
                    collectionReference.document(docId).update("performer", performers);

                }else {
                    performers=title+": "+curUser+" @"+timeFormat.format(todayCal.getTime());
                    ActivityTracker_model myModel= new ActivityTracker_model(System.currentTimeMillis(),performers,2,null);
                    collectionReference.add(myModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
                }
                Toast.makeText(context,"Tracker updated!",Toast.LENGTH_SHORT).show();
                notificationManager.cancel(notificationId);
            }
        });

    }


}
