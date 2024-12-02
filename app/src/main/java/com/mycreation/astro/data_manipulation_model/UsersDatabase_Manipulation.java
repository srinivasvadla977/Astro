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
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class UsersDatabase_Manipulation {

    Application application;
    MyUtils myUtils;

    ArrayList<User_Model> userModels= new ArrayList<>();
    MutableLiveData<ArrayList<User_Model>> mutableLiveData= new MutableLiveData<>();

    public UsersDatabase_Manipulation(Application application) {
        this.application = application;
        FirebaseApp.initializeApp(application.getApplicationContext());
        myUtils=new MyUtils(application.getApplicationContext());
    }

    public UsersDatabase_Manipulation(Context context){
        FirebaseApp.initializeApp(context);
    }

    private FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private CollectionReference usersCollectionReference=firestore.collection("UsersDatabase");

    private FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    private StorageReference storageReference= firebaseStorage.getReference();

    CollectionReference groupChatRef=firestore.collection("GroupChatDatabase");


    public void AddUser(User_Model userModel){

        usersCollectionReference.add(userModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                myUtils.MakeShortToast("Request submitted!");

                GroupChat_model m=new GroupChat_model("Astro","Hi Team,\nNew user, "+userModel.getUserName()+" is requesting for the full access to this application.\nPlease checkout Admin tab.",null,System.currentTimeMillis(),null,null,null,"12345");
                groupChatRef.add(m);

            }
        });

    }


    // for 1.profile pic, 2.name change, 3.registering new user, 4.user status
    public void UpdateUser(User_Model userModel, int updateType, Uri uri){

      //  usersCollectionReference.document(userModel.getUserDocId()).update()

        if (updateType==1){

            StorageReference filePath=storageReference.child("user_profilePic")
                            .child("img_"+userModel.getUserDeviceId());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            usersCollectionReference.document(userModel.getUserDocId()).update("profilePic",uri);
                            myUtils.MakeShortToast("Profile pic has been updated");
                        }
                    });
                }
            });

        } else if (updateType==2) {
            usersCollectionReference.document(userModel.getUserDocId()).update("userName",userModel.getUserName());
            myUtils.MakeShortToast("Your name has been updated");
        } else if (updateType==3) {
            usersCollectionReference.document(userModel.getUserDocId()).update("teamMemberRight",userModel.isTeamMemberRight());
        } else if (updateType==4) {
            usersCollectionReference.document(userModel.getUserDocId()).update("userStatus",userModel.getUserStatus());
        }

    }






    // for removing user or rejecting new user request
    public void DeleteUser(String docId){

        usersCollectionReference.document(docId).delete();

    }






    public MutableLiveData<ArrayList<User_Model>> GetAllUsers(){

        usersCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value!=null){

                    userModels.clear();

                  for (QueryDocumentSnapshot qds: value){
                      User_Model model=qds.toObject(User_Model.class);
                      model.setUserDocId(qds.getId());
                      userModels.add(model);
                  }
                    mutableLiveData.setValue(userModels);
                }
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<ArrayList<User_Model>> GetAllUsers_nonListener(){

        ArrayList<User_Model> models=new ArrayList<>();
        MutableLiveData<ArrayList<User_Model>> ld=new MutableLiveData<>();

        usersCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot qds: queryDocumentSnapshots){
                    User_Model model= qds.toObject(User_Model.class);
                    model.setUserDocId(qds.getId());
                    models.add(model);
                }
                ld.setValue(models);
            }
        });
        return ld;
    }


}
