package com.mycreation.astro.myutils_pack;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.mycreation.astro.R;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.KnowledgeBase_model;
import com.mycreation.astro.object_models.NewTaskModel;
import com.mycreation.astro.object_models.User_Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MyUtils {

    Context context;
    Calendar dateRetrieveCal=Calendar.getInstance();
    DateFormat formater= new SimpleDateFormat("hh:mm a EEE (dd-MMM-yyyy)");
    DateFormat kbDateFormat= new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
    DateFormat timeFormat=new SimpleDateFormat("hh:mm a");
    DateFormat getDayName=new SimpleDateFormat("EEE");

    public MyUtils(Context context) {
        this.context = context;
    }

    public void MakeShortToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public void MakeLongToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    // to retrieve date from millis
    public String GetDateFromMillis(long millis){

        dateRetrieveCal.setTimeInMillis(millis);
        return formater.format(dateRetrieveCal.getTime());
    }

    public String GetTimeFromMillis(long m){
        dateRetrieveCal.setTimeInMillis(m);
        return timeFormat.format(dateRetrieveCal.getTime());
    }

    public String GetTimeFromMillis_withDay(long m){
        dateRetrieveCal.setTimeInMillis(m);
        return timeFormat.format(dateRetrieveCal.getTime())+" #"+getDayName.format(dateRetrieveCal.getTime());
    }

    public String GetKBDateFormat(long m){
        dateRetrieveCal.setTimeInMillis(m);
        return kbDateFormat.format(dateRetrieveCal.getTime());
    }

    public String GetUserDeviceNumber(){

        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
         String num=null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)== PackageManager.PERMISSION_GRANTED)
        {        //  Toast.makeText(context,"num:"+telephonyManager.getLine1Number(),Toast.LENGTH_LONG).show();
            num= telephonyManager.getLine1Number();
        }/*else {
            ActivityCompat.requestPermissions((Activity)context, new String[]{"android.permission.READ_PHONE_NUMBERS"},101 );
        }*/
       // Toast.makeText(context,"util"+num,Toast.LENGTH_LONG).show();
        return num;
    }

    public void SortArrayList(ArrayList<GroupChat_model> sortingList){

        Collections.sort(sortingList, new Comparator<GroupChat_model>() {
            @Override
            public int compare(GroupChat_model model, GroupChat_model t1) {

                try{
                    return (int) (model.getTimeStamp() - t1.getTimeStamp());
                }catch (Exception e){
                    MakeShortToast(e.getMessage());
                }

                return 0;
            }
        });

    }

    public void SortArrayListKB(ArrayList<KnowledgeBase_model> sortingList){

        Collections.sort(sortingList, new Comparator<KnowledgeBase_model>() {
            @Override
            public int compare(KnowledgeBase_model model, KnowledgeBase_model t1) {

                try{
                    return (int) (model.getTimeStamp() - t1.getTimeStamp());
                }catch (Exception e){
                    MakeShortToast(e.getMessage());
                }

                return 0;
            }
        });

    }

    public void SortNewTaskModelList(ArrayList<NewTaskModel> ipList){
        Collections.sort(ipList, new Comparator<NewTaskModel>() {
            @Override
            public int compare(NewTaskModel newTaskModel, NewTaskModel t1) {

                try {
                    return (int) (newTaskModel.getSelectedTimeInMillis()-t1.getSelectedTimeInMillis());
                }catch (Exception e){
                    MakeShortToast(e.getMessage());
                }

                return 0;
            }
        });
    }

    public void ReplaceFragment(Fragment fragment, FragmentManager fragmentManager){

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }

    public String GetUserName_from_deviceId(String ipString, ArrayList<User_Model> models){


       // MakeLongToast(ipString+"#"+models.size());

        String opString="";
        StringBuilder finalOP=new StringBuilder();
        char[] ipCharSeq= ipString.toCharArray();
        if (ipString!=null && ipCharSeq.length>=2){
            for (int i=0;i<=ipCharSeq.length-1;i++){
                if (ipCharSeq[i]=='#'){
                    for (int j=i+1;j<=ipCharSeq.length-1;j++){
                        if (ipCharSeq[j]!='#' && j!=ipCharSeq.length-1) {
                            opString += ipCharSeq[j];
                           // MakeShortToast(opString);
                        }
                        else {
                            // to add last digit of number
                            if (ipCharSeq[j]!='#'){
                            opString += ipCharSeq[j];}
                            //MakeShortToast(opString);

                            for (User_Model model:models){

                                if (model.getUserDeviceId().equalsIgnoreCase(opString)){
                                    finalOP.append(model.getUserName()).append("\n");
                                   // MakeLongToast(finalOP.toString()+"$");
                                }
                            }

                            opString="";
                            i=j;
                           // break;
                        }
                    }
                }
            }

        }

       // MakeShortToast(opString);

        /*for (User_Model model:models){
            if (model.getUserDeviceId().equalsIgnoreCase(opString)){
                finalOP.append(model.getUserName()).append("\n");
            }
        }*/
        return finalOP.toString();
    }



    //CHECKING NETWORK STATUS
    public boolean IsInternetAvailable()  {
        String command="ping -c 1 google.com";

        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        }catch (Exception e){
            return false;
        }
    }


    public Snackbar GetSnackBar(CoordinatorLayout coordinatorLayout,String msg, String actionName){

      Snackbar  snackbar= Snackbar.make(coordinatorLayout,msg,Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(actionName, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        //snackbar.setBackgroundTint()
        return snackbar;
    }




}
