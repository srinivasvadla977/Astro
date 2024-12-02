package com.mycreation.astro;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.fragments.NewTaskOverViewFragment;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.NewTaskModel;
import com.ortiz.touchview.TouchImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Alert_Task_Activity extends AppCompatActivity {

    PowerManager powerManager;
    PowerManager.WakeLock wl;

    TouchImageView imageView;
    TextView titleTV,descTV;
    TextView closeBTN, markBTN;
    ImageButton openBtn;

    MyUtils myUtils;
    // for updating tracker
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

    FragmentManager fragmentManager;
    Ringtone ringtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alert_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Initializer();
        powerManager=(PowerManager) this.getSystemService(Context.POWER_SERVICE);


        Intent intent=getIntent();
        NewTaskModel taskModel= (NewTaskModel) intent.getSerializableExtra("taskModel");
        curUser= intent.getStringExtra("curUser");
        title= taskModel.getTaskTitle();

        if (taskModel.isWeekEndRight()){
            markBTN.setVisibility(View.VISIBLE);
        }else {
            markBTN.setVisibility(View.GONE);
        }

        if (taskModel.getTaskImageUrl()!=null){
            imageView.setVisibility(View.VISIBLE);
        Glide.with(Alert_Task_Activity.this).load(taskModel.getTaskImageUrl()).into(imageView);}
        else {
            imageView.setVisibility(View.GONE);
        }

        titleTV.setText(taskModel.getTaskTitle());
        String desc= taskModel.getTaskDescription()+"\n\n"+"Set by: "+taskModel.getSchedulerName()+"\n"+"Scheduled at: "+myUtils.GetKBDateFormat(taskModel.getSelectedTimeInMillis());
        descTV.setText(desc);

        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        markBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateTaskDB(Alert_Task_Activity.this);
            }
        });

        LightUpAndRing(this);


        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Alert_Task_Activity.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("TaskOverViewFrag",true);
                i.putExtra("taskModel",taskModel);
                startActivity(i);
                finish();
            }
        });

    }


    // using power manager wake lock for 10 seconds to show activity on lock screen
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED ,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    private void Initializer(){

        imageView=findViewById(R.id.imp_IMG);
        titleTV=findViewById(R.id.imp_taskOverviewTitleTV);
        descTV=findViewById(R.id.imp_taskOverviewDescripTV);
        closeBTN=findViewById(R.id.imp_closeBTN);
        markBTN=findViewById(R.id.imp_markBTN);
        openBtn=findViewById(R.id.openButton);

        myUtils=new MyUtils(this);
        descTV.setMovementMethod(new ScrollingMovementMethod());

        FirebaseApp.initializeApp(Alert_Task_Activity.this);

        firestore= FirebaseFirestore.getInstance();
        collectionReference=firestore.collection("Activity_tracker");

        fragmentManager=getSupportFragmentManager();

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
                finish();
            }
        });

    }

    private void LightUpAndRing(Context context){

        powerManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,context.getPackageName()+":myLock");
            wl.acquire(10000);
        }

        Uri soundUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://"+ context.getPackageName() +"/"+R.raw.lets_toast);
        ringtone= RingtoneManager.getRingtone(context,soundUri);
        ringtone.play();

    }

}