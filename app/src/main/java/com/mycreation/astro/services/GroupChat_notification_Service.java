package com.mycreation.astro.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.NotificationHelper;
import com.mycreation.astro.Notification_groupChat;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.data_manipulation_model.GroupChatDatabase_manipulation;
import com.mycreation.astro.data_manipulation_model.UsersDatabase_Manipulation;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class GroupChat_notification_Service extends LifecycleService {

    private final IBinder myBinder = new MyBinder();
    MyUtils myUtils;
    ViewModel_Custom viewModelCustom;
    SharedPreferences sharedPreferences;
    int chatSize;

    GroupChatDatabase_manipulation groupChatDatabaseManipulation;
    UsersDatabase_Manipulation usersDatabaseManipulation;

    ArrayList<User_Model> tempUserModel=new ArrayList<>();
    boolean isFirstTime=true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Add to do logic here
        //MyHandler();

        myUtils=new MyUtils(getApplicationContext());
        sharedPreferences= getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        chatSize= sharedPreferences.getInt("CHAT_SIZE",0);
       // FirebaseApp.initializeApp(getApplicationContext());

       // myUtils.MakeShortToast("ser started");

        //################################FOR NEW MESSAGE NOTIFICATION##################################
        groupChatDatabaseManipulation=new GroupChatDatabase_manipulation(getApplicationContext());
        groupChatDatabaseManipulation.GetAllChatData().observe(this, new Observer<ArrayList<GroupChat_model>>() {
            @Override
            public void onChanged(ArrayList<GroupChat_model> models) {

                if (models.size()>chatSize) {
                    myUtils.SortArrayList(models);
                    GroupChat_model lastModel = models.get(models.size() - 1);
                    // NotificationHelper.getNotificationHelperInstance().TriggerNotification(getApplicationContext(),models.get(models.size()-1).getName(),models.get(models.size()-1).getMessage(),models.get(models.size()-1).getImageUrl());
                    Notification_groupChat.getNotification_groupChat_Instance().TriggerNotification(getApplicationContext(), lastModel.getName(), lastModel.getMessage(), lastModel.getImageUrl());
                    chatSize= models.size();
                }
            }
        });

        //##################################FOR USER STATE CHANGE NOTIFICATION################################
        usersDatabaseManipulation= new UsersDatabase_Manipulation(getApplicationContext());
        usersDatabaseManipulation.GetAllUsers().observe(this, new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> models) {

               // myUtils.MakeShortToast("change detected");

                if (tempUserModel.size()==models.size()){
                    for (int i=0;i<=tempUserModel.size()-1;i++){
                        for (User_Model m:models){
                            if (tempUserModel.get(i).getUserDeviceId().equalsIgnoreCase(m.getUserDeviceId()) && !tempUserModel.get(i).getUserStatus().equalsIgnoreCase(m.getUserStatus())){
                                // send notification
                                if (m.getUserStatus().equalsIgnoreCase("On Shift")){
                                    Notification_groupChat.getNotification_groupChat_Instance().TriggerNotification(getApplicationContext(), "Hi there..!", m.getUserName()+" is joined shift.", null);
                                }else if(m.getUserStatus().equalsIgnoreCase("Off Shift")){
                                    Notification_groupChat.getNotification_groupChat_Instance().TriggerNotification(getApplicationContext(), "Hello,", m.getUserName()+" is out of shift..!", null);
                                }

                            }
                        }
                    }
                }
                tempUserModel.clear();
                tempUserModel.addAll(models);
            }
        });



        return super.onStartCommand(intent, flags, startId);
    }



    public class MyBinder extends Binder {
        GroupChat_notification_Service getService() {
            return GroupChat_notification_Service.this;
        }
    }

    @Override
    public void onDestroy() {

       // Toast.makeText(getApplicationContext(),"Service is STOPPED",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    //
    public void MyHandler(){

        Handler handler= new Handler();

        Runnable runnableTask= new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Service is running",Toast.LENGTH_SHORT).show();
                handler.postDelayed(this,1000*5);
            }
        };

        handler.post(runnableTask);

    }

}
