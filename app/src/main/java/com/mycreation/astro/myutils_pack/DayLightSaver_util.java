package com.mycreation.astro.myutils_pack;

import java.util.Calendar;

import javax.inject.Singleton;

public class DayLightSaver_util {

    Calendar todayCalender= Calendar.getInstance();
    Calendar tempCalender= Calendar.getInstance();
    static Calendar dayLightSaving_start_cal= Calendar.getInstance();
    static Calendar dayLightSaving_ending_cal= Calendar.getInstance();

    static DayLightSaver_util util;

    @Singleton
    public static DayLightSaver_util getDLUtil(){
        if (util==null){
            util=new DayLightSaver_util();

            dayLightSaving_start_cal.set(Calendar.MONTH,Calendar.OCTOBER);
            dayLightSaving_start_cal.set(Calendar.DAY_OF_MONTH,27);
            dayLightSaving_start_cal.set(Calendar.HOUR_OF_DAY,0);
            dayLightSaving_start_cal.set(Calendar.MINUTE,0);
            dayLightSaving_start_cal.set(Calendar.SECOND,0);

            //dayLightSaving_ending_cal.set(Calendar.MONTH,Calendar.MARCH);
            //dayLightSaving_ending_cal.set(Calendar.DAY_OF_MONTH,31);
            dayLightSaving_ending_cal.set(Calendar.MONTH,Calendar.APRIL);
            dayLightSaving_ending_cal.set(Calendar.DAY_OF_MONTH,1);
            dayLightSaving_ending_cal.set(Calendar.HOUR_OF_DAY,0);
            dayLightSaving_ending_cal.set(Calendar.MINUTE,0);
            dayLightSaving_ending_cal.set(Calendar.SECOND,0);

        }
        return util;
    }

    public boolean CheckIfDayLight_ApplicableToday_for_dashBoard(){

       // todayCalender.set(Calendar.MONTH,7);
        long millis= todayCalender.getTimeInMillis();

        return millis >= dayLightSaving_start_cal.getTimeInMillis() || millis <= dayLightSaving_ending_cal.getTimeInMillis();

    }


}
