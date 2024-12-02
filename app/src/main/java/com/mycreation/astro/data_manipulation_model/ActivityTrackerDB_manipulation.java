package com.mycreation.astro.data_manipulation_model;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.ActivityTracker_model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityTrackerDB_manipulation {

    Application apk;
    MyUtils myUtils;

    ArrayList<ActivityTracker_model> arrayList=new ArrayList<>();
    MutableLiveData<ArrayList<ActivityTracker_model>> liveData=new MutableLiveData<>();

    Calendar todayCal=Calendar.getInstance();
    DateFormat dateFormat= new SimpleDateFormat("dd:MM:yyyy");
    DateFormat timeFormat= new SimpleDateFormat("hh:mm:ss a");
    Calendar tempCal=Calendar.getInstance();




    public ActivityTrackerDB_manipulation(Application apk) {
        this.apk = apk;
        myUtils=new MyUtils(apk.getApplicationContext());
        FirebaseApp.initializeApp(apk.getApplicationContext());
    }

    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=firestore.collection("Activity_tracker");


    public MutableLiveData<ArrayList<ActivityTracker_model>> GetActivityTrack_list(){
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot qds:queryDocumentSnapshots){
                ActivityTracker_model model= qds.toObject(ActivityTracker_model.class);
                arrayList.add(model);
                    //myUtils.MakeLongToast(arrayList.size()+"");
                }
                liveData.setValue(arrayList);
            }
        });
      //  myUtils.MakeLongToast(arrayList.size()+"");
        return liveData;
    }



    boolean dateFound=false;
    String docId,performers;
    public void AddActivityPerformer(ActivityTracker_model model, String curUser,String title){

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
                myUtils.MakeShortToast("Tracker Updated");
            }
        });



    }

}
