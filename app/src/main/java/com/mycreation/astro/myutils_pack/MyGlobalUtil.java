package com.mycreation.astro.myutils_pack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.mycreation.astro.MainActivity;

import java.util.Calendar;

import javax.inject.Singleton;

public class MyGlobalUtil {

    Context context;
    static MyGlobalUtil myGlobalUtil;
    static Context statCon;

    @Singleton
    public static MyGlobalUtil GetGlobalUtilInstance(Context context){
        if(myGlobalUtil==null){
            myGlobalUtil=new MyGlobalUtil(context);
        }
        statCon=context;
        return myGlobalUtil;
    }

    TimePickerDialog timePickerDialog;
    TimePicker myTimePicker=null;

    DatePickerDialog datePickerDialog;
    DatePicker myDatePicker=null;

    MutableLiveData<String> timeStringLiveData=new MutableLiveData<>();
    MutableLiveData<String> dateStringLiveData=new MutableLiveData<>();

    String timeTv,dateTv;
    Calendar calendar, todayCalenderInstance;

    public MyGlobalUtil(Context context) {
        this.context = context;

        calendar= Calendar.getInstance();
        todayCalenderInstance=Calendar.getInstance();

        InitiateTimePicker();
        dateStringLiveData.setValue(InitializeDatePicker());
    }


    public void ShowTimePicker(Context c){
        try {
            timePickerDialog.show();
        }catch (Exception e){
            context=c;
            InitiateTimePicker();
            timePickerDialog.show();
        }
    }

    public void ShowDatePicker(Context c){
        try {
            datePickerDialog.show();
        }catch (Exception e){
            context= c;
            InitializeDatePicker();
            datePickerDialog.show();
        }
    }

    public MutableLiveData<String> getTimeLiveData(){
        return timeStringLiveData;
    }

    public MutableLiveData<String> getDateLiveData(){
        return dateStringLiveData;
    }

    public String InitiateTimePicker() {


        timePickerDialog= new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                myTimePicker=timePicker;

                if(i>12){
                    timeTv=(i-12)+":"+i1+":00 PM";}
                else if(i==12){
                    timeTv=(i+":"+i1+":00 PM");
                }
                else{
                    timeTv=(i+":"+i1+":00 AM");}

                timeStringLiveData.setValue(timeTv);
            }
        }, 12, 0, false  );

        timePickerDialog.setTitle("Select a time:");



        return timeTv;
    }


    public String InitializeDatePicker(){

        datePickerDialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myDatePicker=datePicker;
                dateTv=i+"/"+(i1+1)+"/"+i2;
                dateStringLiveData.setValue(dateTv);
            }

        },todayCalenderInstance.get(Calendar.YEAR),todayCalenderInstance.get(Calendar.MONTH),todayCalenderInstance.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setTitle("Select a date");

        dateTv=(todayCalenderInstance.get(Calendar.YEAR)+"/"+(todayCalenderInstance.get(Calendar.MONTH)+1)+"/"+todayCalenderInstance.get(Calendar.DAY_OF_MONTH));

        return dateTv;
    }

    public long GetTriggerTime() {

        if (myTimePicker!=null){

            calendar.set(Calendar.HOUR_OF_DAY,myTimePicker.getHour());
            calendar.set(Calendar.MINUTE,myTimePicker.getMinute());
            calendar.set(Calendar.SECOND,0);

            if (myDatePicker!=null){
                calendar.clear();
                calendar.set(myDatePicker.getYear(),myDatePicker.getMonth(),myDatePicker.getDayOfMonth(),myTimePicker.getHour(),myTimePicker.getMinute(),0);
                //Toast.makeText(context,myDatePicker.getYear()+"/"+(myDatePicker.getMonth()+1)+"/"+myDatePicker.getDayOfMonth()+"  "+myTimePicker.getHour()+":"+myTimePicker.getMinute(),Toast.LENGTH_LONG).show();

            }else {
               // Toast.makeText(context,"datePicker is empty",Toast.LENGTH_LONG).show();
            }

            return calendar.getTimeInMillis();
        }
        return 0;
    }

    public boolean CheckForWeekend(){
        if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){ return true; }
        return false;
    }

}
