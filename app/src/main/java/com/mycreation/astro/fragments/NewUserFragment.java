package com.mycreation.astro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;
import java.util.Objects;

public class NewUserFragment extends Fragment {

Context context;

TextView noteTV;
EditText nameET;
TextView deviceIdTV;
Button submitBTN;

ViewModel_Custom viewModelCustom;
MyUtils myUtils;

static ArrayList<User_Model> usersList=new ArrayList<>();
static NewUserFragment newUserFragment;

    public NewUserFragment() {
        // Required empty public constructor
    }

    public static NewUserFragment getInstance(ArrayList<User_Model> usersListt){
        usersList=usersListt;
        if (newUserFragment==null){
            return new NewUserFragment();
        }
        else return newUserFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_user, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        InitializeUiFields(view);
        myUtils=new MyUtils(context);

        viewModelCustom=new ViewModelProvider(NewUserFragment.this).get(ViewModel_Custom.class);

        // getting user device id below
       deviceIdTV.setText(myUtils.GetUserDeviceNumber());
       deviceIdTV.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               deviceIdTV.setText(myUtils.GetUserDeviceNumber());
               if (CheckIfAlreadyRequested()!=null){
                   submitBTN.setEnabled(false);
               }
               String curUser=CheckIfAlreadyRequested();
               if (curUser!=null){
                   submitBTN.setEnabled(false);
                   noteTV.setText("Hi "+curUser+", your request for full access is waiting for Admin approval..!");
                   nameET.setText(curUser);
               }
           }
       });

        viewModelCustom.GetAllUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> userModels) {
                usersList.addAll(userModels);
            }
        });

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(nameET.getText())){
                    if (!TextUtils.isEmpty(deviceIdTV.getText())){
                        User_Model model= new User_Model(nameET.getText().toString(),null,"Off Shift",false,deviceIdTV.getText().toString(),null);
                        viewModelCustom.AddNewUser(model);
                        noteTV.setText("Hi "+nameET.getText()+", your request for full access is waiting for Admin approval..!");


                    }
                }

            }
        });

        // disabling submit button if already submitted a request
        String curUser=CheckIfAlreadyRequested();
        if (curUser!=null){
            submitBTN.setEnabled(false);
            noteTV.setText("Hi "+curUser+", your request for full access is waiting for Admin approval..!");
            nameET.setText(curUser);
        }


    }


    private void InitializeUiFields(View activity){

        noteTV = activity.findViewById(R.id.noteTV);
        nameET= activity.findViewById(R.id.newUserNameET);
        deviceIdTV= activity.findViewById(R.id.newUserDeviceIdTV);
        submitBTN= activity.findViewById(R.id.newUserSubmitBTN);

    }

    MutableLiveData<User_Model> liveData=new MutableLiveData<>();
    private String CheckIfAlreadyRequested(){

        for (User_Model model: usersList){
            if (model.getUserDeviceId().equalsIgnoreCase(deviceIdTV.getText().toString())){
                return model.getUserName();
            }
        }
        return null;
    }



}