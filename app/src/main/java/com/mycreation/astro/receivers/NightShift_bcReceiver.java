package com.mycreation.astro.receivers;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.storage.FirebaseStorage;
import com.mycreation.astro.TaskReminding_Scheduler;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.DayLightSaver_util;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.NewTaskModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NightShift_bcReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    Context context;

    FirebaseFirestore firestore;
    CollectionReference reference;
    // for calender
    CollectionReference collectionReference;

    ArrayList<NewTaskModel> models= new ArrayList<>();

    String curUser;
    boolean isUserOnline;

    @Override
    public void onReceive(Context contextt, Intent intent) {

      context=contextt;
      FirebaseApp.initializeApp(context);
      //Toast.makeText(context,"NSBC received",Toast.LENGTH_SHORT).show();
      curUser=intent.getStringExtra("curUser");

      sharedPreferences= context.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
      isUserOnline=sharedPreferences.getBoolean("isCurUserConnected",false);


          firestore= FirebaseFirestore.getInstance();
          reference=firestore.collection("TasksDatabase");
          collectionReference= firestore.collection("Activity_tracker");
          LocalScheduler scheduler=new LocalScheduler();

          reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                  models.clear();
                  for (QueryDocumentSnapshot qds: queryDocumentSnapshots){
                      NewTaskModel model=qds.toObject(NewTaskModel.class);
                      model.setTaskDocId(qds.getId());
                      models.add(model);
                  }
                  scheduler.ScheduleTaskNotification();
              }
          });

          // UPDATING NIGHT SHIFT ONLY FOR WEEK DAYS WHEN USER IS CONNECTED
          if (isUserOnline && !CheckForWeekend()){
              UpdateNightShiftTracker();
          }


    }


    int recheck=0;

    boolean dateFound=false;
    String docId=null;
    String performers=null;
    String title="Night Shift";
private void UpdateNightShiftTracker() {

    Calendar toNightCal = Calendar.getInstance();
    //toNightCal.set(Calendar.DAY_OF_MONTH, toNightCal.get(Calendar.DAY_OF_MONTH - 1));
    toNightCal.add(Calendar.DATE,-1);
    DateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");
    Calendar tempCal = Calendar.getInstance();

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // models.clear();
                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    ActivityTracker_model modelAct = qds.toObject(ActivityTracker_model.class);
                    /*modelAct.setDocId(qds.getId());
                    models.add(modelAct);*/

                    tempCal.setTimeInMillis(modelAct.getTimeStamp());
                    if (dateFormat.format(tempCal.getTime()).equalsIgnoreCase(dateFormat.format(toNightCal.getTime()))) {
                        dateFound = true;
                        docId = qds.getId();
                        performers = modelAct.getPerformer();
                    }
                }

                if (dateFound) {
                    if (!performers.contains(curUser)){
                    performers = performers + "\n" + title + ": " + curUser;
                    collectionReference.document(docId).update("performer", performers);}

                } else {
                    //Toast.makeText(context,"date NOT found",Toast.LENGTH_SHORT).show();
                    performers = title + ": " + curUser;
                    ActivityTracker_model myModel = new ActivityTracker_model(System.currentTimeMillis()-5000000, performers, 3, null);
                    collectionReference.add(myModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
                }

                if (recheck<=2){
                recheck++;
                dateFound=false;
                UpdateNightShiftTracker();}
            }

        });

    }

    private boolean CheckForWeekend(){
        Calendar calendar=Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
    }

    ////////////////////////////////////// FOR SCHEDULING TASK /////////////////////////////////////


    private class LocalScheduler {

        AlarmManager alarmManager;
        Calendar todayCal;
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");

        public void ScheduleTaskNotification() {

            ArrayList<NewTaskModel> taskModels = models;
            alarmManager=(AlarmManager) context.getSystemService(ALARM_SERVICE);

            // CHECKING FOR DAY LIGHT SAVING
            for (NewTaskModel model : taskModels) {
                long millis= model.getSelectedTimeInMillis();
                if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
                    //myUtils.MakeLongToast("found one dls task");
                    millis+=3600000;
                }
                model.setSelectedTimeInMillis(millis);
                // SCHEDULING FOR WEEK END ACT OR USER ONLINE
                if (model.isWeekEndRight() || isUserOnline){
                CheckTypeOfTask(model);
                }
            }
        }

        // CHECKING
        private void CheckTypeOfTask( NewTaskModel model){

            Calendar tempCal= Calendar.getInstance();
            todayCal=Calendar.getInstance();

            if (model.isRepeatingTaskRight()){

                if (model.isDailyRepeatingTaskRight()){

                    // CHECKING IF MARKED TO DELETE. UN SCHEDULING IF TIME NOT PASSED, DELETING IF TIME ALREADY PASSED
                    if (model.isMarkedToDeleteRight()){
                        MarkedToDeleteTask(model);
                    }
                    else {
                        // SETTING UP ALARM BROADCAST FOR DAILY REPEATING TASKS
                        SetUpAlarmBroadcast(model);
                    }

                }
                // FOR WEEKLY TASKS
                else {

                    if (model.isMarkedToDeleteRight()){
                        MarkedToDeleteTask(model);
                    }else {
                        SetUpAlarmBroadcast(model);
                    }

                }


            }
            // FOR ONE TIME TASKS
            else {
                //
                if (model.isMarkedToDeleteRight()){
                    MarkedToDeleteTask(model);
                }
                else {
                    // SETTING UP ALARM BROADCAST FOR ONETIME TASKS FOR THE SAME DATE
                    tempCal.setTimeInMillis(model.getSelectedTimeInMillis());
                    if (dateFormat.format(tempCal.getTime()).equalsIgnoreCase(dateFormat.format(todayCal.getTime()))){

                        SetUpAlarmBroadcast(model);
                    }

                }

            }

        }
        //
        private void MarkedToDeleteTask( NewTaskModel model){

            Intent i=GetIntent(model);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,model.getAlarmReqCode(),i,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE);

            todayCal=Calendar.getInstance();

            if (model.getSelectedTimeInMillis() >= todayCal.getTimeInMillis()){
                // CANCELLING AND DELETING P.I IF ALREADY EXIST
                if (pendingIntent!=null){
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }
            else {
                reference.document(model.getTaskDocId()).delete();
                if (model.getTaskImageUrl()!=null) {
                    FirebaseStorage.getInstance().getReferenceFromUrl(model.getTaskImageUrl()).delete();
                }

            }

        }



        // SCHEDULING
        @SuppressLint("ScheduleExactAlarm")
        private void SetUpAlarmBroadcast( NewTaskModel model) {

            if (model.isRepeatingTaskRight()){
                model.setSelectedTimeInMillis(GetScheduleTimeForRepeatingTask(model));
            }

            todayCal=Calendar.getInstance();

            //CHECKING IF TIME ALREADY PASSED
            if (model.getSelectedTimeInMillis()>=todayCal.getTimeInMillis()) {
                Intent intent = GetIntent(model);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, model.getAlarmReqCode(), intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing

                long earlyTime=0;
                if (!model.isHighPriority()){
                    earlyTime=1000*60*5;
                }else { earlyTime=1000*60*10; }

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, model.getSelectedTimeInMillis()-earlyTime, pendingIntent);
                Toast.makeText(context,"Schedule success",Toast.LENGTH_LONG).show();
            }
            //  TO DELETE TASK IF TIME IS PASSED AND TASK TYPE IS ONE TIME
            else if (!model.isRepeatingTaskRight()){
                reference.document(model.getTaskDocId()).delete();
                if (model.getTaskImageUrl()!=null) {
                    FirebaseStorage.getInstance().getReferenceFromUrl(model.getTaskImageUrl()).delete();
                }
            }

            // myUtils.MakeLongToast("failed");

        }

        // CREATING AND RETURNING INTENT
        private Intent GetIntent( NewTaskModel model){
            Intent intent=new Intent(context, TaskReminding_Receiver.class);
            intent.putExtra("taskModel",model);
            intent.putExtra("curUser",curUser);
            return intent;
        }

        // SETTING REPEATING TASK TIME TO TODAY'S CALENDER
        private long GetScheduleTimeForRepeatingTask(NewTaskModel model){

            todayCal=Calendar.getInstance();
            Calendar tempCal= Calendar.getInstance();
            tempCal.setTimeInMillis(model.getSelectedTimeInMillis());

            // CHECKING IF WEEKLY TASK
            if (model.isRepeatingTaskRight() && !model.isDailyRepeatingTaskRight()){
                // CHECKING IF TASK HAS SAME DAY OF WEEK
                if ((todayCal.get(Calendar.DAY_OF_WEEK)!=(tempCal.get(Calendar.DAY_OF_WEEK)))){
                    return tempCal.getTimeInMillis();
                }
            }

            todayCal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
            todayCal.set(Calendar.MINUTE,tempCal.get(Calendar.MINUTE));
            todayCal.set(Calendar.SECOND,0);

            return  todayCal.getTimeInMillis();

        }


    }


}
