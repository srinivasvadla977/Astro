package com.mycreation.astro.data_manipulation_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.KnowledgeBase_model;

import java.util.ArrayList;

public class KnowledgeBaseDB_manipulation {

    Application apk;
    MyUtils myUtils;

    ArrayList<KnowledgeBase_model> arrayList= new ArrayList<>();
    MutableLiveData<ArrayList<KnowledgeBase_model>> liveData=new MutableLiveData<>();

    public KnowledgeBaseDB_manipulation(Application apk) {
        this.apk = apk;
        myUtils=new MyUtils(apk.getApplicationContext());
        FirebaseApp.initializeApp(apk.getApplicationContext());
    }

    FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    CollectionReference knowledgeBaseReference= firestore.collection("KnowledgeBaseDB");

    public void AddNewAppKnowledge(KnowledgeBase_model model){

        knowledgeBaseReference.add(model).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myUtils.MakeShortToast(e.getMessage());
            }
        });
    }

    public void UpdateAppKnowledge(KnowledgeBase_model model, String oldDocId){

        knowledgeBaseReference.add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                    knowledgeBaseReference.document(oldDocId).delete();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myUtils.MakeShortToast(e.getMessage());
            }
        });
    }

    public MutableLiveData<ArrayList<KnowledgeBase_model>> GetAllApkKnowledge(){

        knowledgeBaseReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                arrayList.clear();
                if (value!=null) {
                    for (QueryDocumentSnapshot qds : value) {
                        KnowledgeBase_model model = qds.toObject(KnowledgeBase_model.class);
                        model.setDocId(qds.getId());
                        arrayList.add(model);
                    }
                    liveData.setValue(arrayList);
                }
            }
        });
        return  liveData;
    }


}
