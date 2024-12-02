package com.mycreation.astro.data_manipulation_model;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.ReqCode_model;

import java.util.ArrayList;

public class ReqCodeTokenGenerator {

    Application apk;
    MyUtils myUtils;

    ArrayList<ReqCode_model> reqCodeModelArrayList=new ArrayList<>();
    ReqCode_model reqCodeModel=null;

    public ReqCodeTokenGenerator(Application apk) {
        this.apk = apk;
        FirebaseApp.initializeApp(apk.getApplicationContext());
        myUtils=new MyUtils(apk.getApplicationContext());

     //   tokenCollectionReference.add(new ReqCode_model(0,null));
    }

    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private CollectionReference tokenCollectionReference=firestore.collection("ReqCodeToken");



    public void GetReqToken(DataCallBack dataCallBack){

            tokenCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot qds: queryDocumentSnapshots) {

                            reqCodeModel = qds.toObject(ReqCode_model.class);
                            reqCodeModel.setDocId(qds.getId());

                      //  reqCodeModelArrayList.add(reqCodeModel);
                     //   myUtils.MakeLongToast(reqCodeModelArrayList.get(0).getDocId()+"###"+reqCodeModelArrayList.get(0).getToken()); // returning values here
                    }

                    if (reqCodeModel!=null){
                        dataCallBack.onDataReceived(reqCodeModel);
                        reqCodeModel=null;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    myUtils.MakeShortToast(e.getMessage());
                }
            });
      //  myUtils.MakeLongToast(reqCodeModelArrayList.get(0).getDocId()+"###"+reqCodeModelArrayList.get(0).getToken()); // returning null here

    }

    public void InsertToken(ReqCode_model token){

        DocumentReference documentReference= tokenCollectionReference.document(token.getDocId());

        documentReference.update("token",token.getToken()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myUtils.MakeShortToast("error while inserting Req token");
            }
        });
    }

    public interface DataCallBack{
            void onDataReceived(ReqCode_model reqCodeModel);
    }

}
