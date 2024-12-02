package com.mycreation.astro.data_manipulation_model;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycreation.astro.myutils_pack.DayLightSaver_util;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.NewTaskModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskDatabaseManipulation {

    Application apk;
    MyUtils myUtils;

    Uri fbReturnedUri;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ArrayList<NewTaskModel> tasksArrayList= new ArrayList<>();
    private MutableLiveData<ArrayList<NewTaskModel>> tasksLivedata= new MutableLiveData<>();

    public TaskDatabaseManipulation(Application apkk) {
        this.apk = apkk;
        FirebaseApp.initializeApp(apk.getApplicationContext());
        myUtils= new MyUtils(apk.getApplicationContext());

        firebaseStorage= FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();
    }


    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private CollectionReference tasksCollectionReference=firestore.collection("TasksDatabase");

    private CollectionReference groupChatRef=firestore.collection("GroupChatDatabase");


    // adding new task below
    public void AddNewTask(NewTaskModel newTaskModel){

        tasksCollectionReference.add(newTaskModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                myUtils.MakeShortToast("Success");

                if (newTaskModel.isHighPriority()){

                    long temp=newTaskModel.getSelectedTimeInMillis();
                    if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
                        temp+=3600000;
                    }

                    String msg= "Dear Team,\nNew high priority task has been scheduled by "+newTaskModel.getSchedulerName()+".\nTitle: "+newTaskModel.getTaskTitle()+".\nDescription: "+newTaskModel.getTaskDescription()+".\nScheduled time: "+ myUtils.GetKBDateFormat(temp)+"\n\n"+"Check Dashboard for more details.";

                    GroupChat_model m=new GroupChat_model("Astro",msg, null, System.currentTimeMillis(), null, null, null, "12345");
                    groupChatRef.add(m);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myUtils.MakeShortToast(e.getMessage());
                Log.d("errr",e.getMessage());
            }
        });


        /*String s = new String("12/12/12");
        tasksCollectionReference.document(newTaskModel.getSchedulerName()).collection("leaves").a
*/
    }


    public Uri AddImageToFBStorage(Uri uri){

        if (uri==null){
        myUtils.MakeShortToast("uri is null");}

        StorageReference filePath=storageReference.child("our_images")
                .child("img_"+ Timestamp.now().getSeconds());

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fbReturnedUri=uri;
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                        myUtils.MakeLongToast(e.getMessage());
            }
        });



        return fbReturnedUri;
    }

   //
    public void UploadImg_postMsg_addNewTask(NewTaskModel newTaskModel, Uri localImgUri){

        StorageReference filePath=storageReference.child("our_images")
                .child("img"+ Timestamp.now().getSeconds());

        if (localImgUri!=null) {
            filePath.putFile(localImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newTaskModel.setTaskImageUrl(uri.toString());
                            AddNewTask(newTaskModel);

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    myUtils.MakeShortToast(e.getMessage());
                    Log.d("errr",e.getMessage());
                }
            });
        }else {
            AddNewTask(newTaskModel);
        }

    }

    public MutableLiveData<ArrayList<NewTaskModel>> GetAll_ScheduledTasks(){

        tasksCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value!=null){

                     tasksArrayList.clear();
                    for (QueryDocumentSnapshot qds: value){
                        NewTaskModel model= qds.toObject(NewTaskModel.class);
                        model.setTaskDocId(qds.getId());
                        tasksArrayList.add(model);
                    }
                    tasksLivedata.setValue(tasksArrayList);
                }

            }
        });
        return tasksLivedata;
    }

    public void DeleteScheduledTask(NewTaskModel model){
        tasksCollectionReference.document(model.getTaskDocId()).delete();
        if (model.getTaskImageUrl()!=null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(model.getTaskImageUrl()).delete();
        }
    }

    public void MarkToDeleteScheduledTask(String docId){
        tasksCollectionReference.document(docId).update("markedToDeleteRight", true);
    }


}
