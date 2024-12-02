package com.mycreation.astro.data_manipulation_model;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;

import java.util.ArrayList;

public class GroupChatDatabase_manipulation {

    Application apk;
    MyUtils myUtils;

    Context c;

    public GroupChatDatabase_manipulation(Application apk) {
        this.apk = apk;
        myUtils=new MyUtils(apk.getApplicationContext());
        FirebaseApp.initializeApp(apk.getApplicationContext());
    }

    public GroupChatDatabase_manipulation(Context context){
        FirebaseApp.initializeApp(context);
    }

    ArrayList<GroupChat_model> groupChatModelsList= new ArrayList<>();
    MutableLiveData<ArrayList<GroupChat_model>> mutableLiveData= new MutableLiveData<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference groupChatCollectionReference = firestore.collection("GroupChatDatabase");

    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private StorageReference storageReference= storage.getReference();


    // post msg
    public void PostMessage(GroupChat_model model, Uri myUri){

        if (myUri!= null){

            StorageReference filePath=storageReference.child("shared_images")
                    .child("img_"+System.currentTimeMillis());

            filePath.putFile(myUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           model.setImageUrl(uri.toString());
                           SendMessage(model);
                       }
                   });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    myUtils.MakeShortToast("Failed to upload image!");
                }
            });
        }
        else {
                       SendMessage(model);
        }

    }


    // get all messages
    public MutableLiveData<ArrayList<GroupChat_model>> GetAllChatData(){

        groupChatCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value!= null){
                    groupChatModelsList.clear();

                    for (QueryDocumentSnapshot qds: value){
                        GroupChat_model model= qds.toObject(GroupChat_model.class);
                        model.setDocId(qds.getId());

                        groupChatModelsList.add(model);
                    }
                    mutableLiveData.setValue(groupChatModelsList);
                }

            }
        });
        return mutableLiveData;
    }

    // to access likes db
    CollectionReference likesReference= firestore.collection("LikedItemDB");

    // post likes
    public void LikeMessage(GroupChat_model model, int likedIndex){

        groupChatCollectionReference.document(model.getDocId()).update("likes",model.getLikes()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                likesReference.document("pyPj6z06XbWwpf9ilr8u").update("index", likedIndex);
                likesReference.document("pyPj6z06XbWwpf9ilr8u").update("docId",System.currentTimeMillis()+"");

            }
        });

    }


    private void SendMessage(GroupChat_model model){

        groupChatCollectionReference.add(model).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myUtils.MakeShortToast(e.getMessage());
            }
        });



    }



}
