package com.mycreation.astro.data_manipulation_model;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mycreation.astro.object_models.LikedItemIndex_model;

public class LikedItem_DBM {

    Application apk;
    LikedItemIndex_model model=null;
    MutableLiveData<LikedItemIndex_model> liveData= new MutableLiveData<>();

    public LikedItem_DBM(Application apk) {
        this.apk = apk;
        FirebaseApp.initializeApp(apk.getApplicationContext());
    }

    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    CollectionReference reference= firestore.collection("LikedItemDB");

    public MutableLiveData<LikedItemIndex_model> GetLikedItemIndex(){

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){
                    for (QueryDocumentSnapshot qds: value){
                        model=qds.toObject(LikedItemIndex_model.class);
                        model.setDocId(qds.getId());
                    }
                    liveData.setValue(model);
                }
            }
        });
        return liveData;
    }

    public void PostLikedItemIndex(int index){
       /* if (model!=null){
            reference.document(model.getDocId()).update("index", index);
        }
        else {
            reference.add(new LikedItemIndex_model(null,index));
        }*/
        reference.document("pyPj6z06XbWwpf9ilr8u").update("index", index);
        reference.document("pyPj6z06XbWwpf9ilr8u").update("docId",System.currentTimeMillis()+"");
    }



}
