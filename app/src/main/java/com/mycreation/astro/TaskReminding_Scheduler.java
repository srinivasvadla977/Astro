package com.mycreation.astro;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mycreation.astro.myutils_pack.DayLightSaver_util;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.NewTaskModel;
import com.mycreation.astro.receivers.TaskReminding_Receiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TaskReminding_Scheduler {

    AlarmManager alarmManager;
    Calendar todayCal;

    Context context;
    ArrayList<NewTaskModel> taskModels=new ArrayList<>();
    MyUtils myUtils;
    String curUser;

    ViewModel_Custom viewModelCustom;
    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");

    public TaskReminding_Scheduler(Context context, String curUser) {
        this.context = context;
        alarmManager=(AlarmManager) context.getSystemService(ALARM_SERVICE);
        this.curUser=curUser;

        viewModelCustom=new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel_Custom.class);
        myUtils=new MyUtils(context);
    }

    // ITERATING
    public void ScheduleTaskNotification(ArrayList<NewTaskModel> taskModels) {

        this.taskModels = taskModels;

        // CHECKING FOR DAY LIGHT SAVING
        for (NewTaskModel model : taskModels) {
            long millis= model.getSelectedTimeInMillis();
            if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
                //myUtils.MakeLongToast("found one dls task");
                millis+=3600000;
            }
            model.setSelectedTimeInMillis(millis);
            CheckTypeOfTask(model);
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
    @SuppressLint("ScheduleExactAlarm")
    private void MarkedToDeleteTask( NewTaskModel model){

        Intent i=GetIntent(model);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,model.getAlarmReqCode(),i,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE);

        todayCal=Calendar.getInstance();

        if (model.getSelectedTimeInMillis() >= todayCal.getTimeInMillis()){
                // CANCELLING AND DELETING P.I IF ALREADY EXIST
            if (pendingIntent!=null){
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                myUtils.MakeShortToast("found one pending intent");
            }
        }
        else {
            viewModelCustom.DeleteScheduledTask(model);
        }

    }



    // SCHEDULING
    @SuppressLint("ScheduleExactAlarm")
    private void SetUpAlarmBroadcast( NewTaskModel model) {

        if (model.isRepeatingTaskRight()){
           model.setSelectedTimeInMillis(GetScheduleTimeForRepeatingTask(model));
        }

        todayCal=Calendar.getInstance();

        // FOR EARLY NOTIFICATIONS
        long earlyTime=0;
        if (!model.isHighPriority()){
            earlyTime=1000*60*5;
        }else { earlyTime=1000*60*10; }


        //CHECKING IF TIME ALREADY PASSED
        if (model.getSelectedTimeInMillis()-earlyTime>=todayCal.getTimeInMillis()) {
            Intent intent = GetIntent(model);
            //PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,requestCode++,intent,PendingIntent.FLAG_MUTABLE); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, model.getAlarmReqCode(), intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing


            alarmManager.setExact(AlarmManager.RTC_WAKEUP, model.getSelectedTimeInMillis()-earlyTime, pendingIntent);
            myUtils.MakeShortToast("schedule success");
        }
        //  TO DELETE TASK IF TIME IS PASSED AND TASK TYPE IS ONE TIME
        else if (!model.isRepeatingTaskRight() && model.getSelectedTimeInMillis()<todayCal.getTimeInMillis()){
            viewModelCustom.DeleteScheduledTask(model);
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

// UN SCHEDULING ALL TASKS
public void UnScheduleAllNotifications(ArrayList<NewTaskModel> taskModels){

    for (NewTaskModel model: taskModels){
        UnSchedule_eachNotification(model);
    }
}

private void UnSchedule_eachNotification(NewTaskModel model){

    Intent intent = GetIntent(model);
    PendingIntent pendingIntent=PendingIntent.getBroadcast(context,model.getAlarmReqCode(),intent,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_NO_CREATE);

    if (pendingIntent!=null){
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        myUtils.MakeShortToast("unscheduled");
    }

}


}
