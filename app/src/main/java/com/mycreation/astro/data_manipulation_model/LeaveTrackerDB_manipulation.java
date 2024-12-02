package com.mycreation.astro.data_manipulation_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.LeaveTracker_model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LeaveTrackerDB_manipulation {

    Application apk;
    MyUtils myUtils;

    Calendar calendar=Calendar.getInstance();
    DateFormat dateFormat= new SimpleDateFormat("dd-MMM-yyyy (EEE)");
    String tempDate;

    ArrayList<LeaveTracker_model> arrayList=new ArrayList<>();
    MutableLiveData<ArrayList<LeaveTracker_model>> liveData=new MutableLiveData<>();

    public LeaveTrackerDB_manipulation(Application apk) {
        this.apk = apk;
        FirebaseApp.initializeApp(apk.getApplicationContext());
        myUtils=new MyUtils(apk.getApplicationContext());
    }

    FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    CollectionReference reference= firestore.collection("Leave_trackerDB");

    CollectionReference groupChatRef=firestore.collection("GroupChatDatabase");

    public MutableLiveData<ArrayList<LeaveTracker_model>> GetAllLeaveMarks(){

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value!=null){
                    arrayList.clear();

                    for (QueryDocumentSnapshot qds: value){
                        LeaveTracker_model model= qds.toObject(LeaveTracker_model.class);
                        model.setDocId(qds.getId());
                        arrayList.add(model);
                    }
                    liveData.setValue(arrayList);
                }
            }
        });
        return liveData;
    }

    // 1= new model, 2= update existing one, 3=delete
    public void AddOrUpdateTracker(LeaveTracker_model model, int type){

       // myUtils.MakeLongToast(type+"#"+model.getPersonsOnLeave());

        if (type==1){
            reference.add(model).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //
                }
            });
        } else if (type==2) {
            reference.document(model.getDocId()).update("personsOnLeave", model.getPersonsOnLeave()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //
                }
            });

        } else{

            reference.document(model.getDocId()).delete();
        }

        calendar.setTimeInMillis(model.getTimeStamp());
        String date= dateFormat.format(calendar.getTime());

        if (tempDate==null || !tempDate.equalsIgnoreCase(date)) {
            groupChatRef.add(new GroupChat_model("Astro", "Hi Guys!\nLeave tracker has been updated for the date " + date, null, System.currentTimeMillis(), null, null, null, "12345"));
            tempDate=date;
        }

    }



}

