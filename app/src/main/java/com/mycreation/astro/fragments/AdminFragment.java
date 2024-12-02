package com.mycreation.astro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.object_models.User_Model;
import com.mycreation.astro.recyclerview_adopters.ExistingUsers_Admin_Adopter;
import com.mycreation.astro.recyclerview_adopters.NewUser_Admin_Adopter;

import java.util.ArrayList;

public class AdminFragment extends Fragment {

    static AdminFragment fragment;
    Context context;

    ArrayList<User_Model> newUsersList=new ArrayList<>();
    ArrayList<User_Model> existingUsersList=new ArrayList<>();

    RecyclerView existingUsers_recView;
    RecyclerView newUsers_recView;

    ExistingUsers_Admin_Adopter existingUsersAdminAdopter;
    NewUser_Admin_Adopter newUserAdminAdopter;

    ViewModel_Custom viewModelCustom;

    User_Model curUser;

    public AdminFragment() {
        // Required empty public constructor
    }

    public AdminFragment(User_Model curUser) {
        this.curUser=curUser;
    }

    public static AdminFragment GetAdminFrag(User_Model curUser){
        if (fragment==null){
            fragment=new AdminFragment(curUser);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        viewModelCustom= new ViewModelProvider(this).get(ViewModel_Custom.class);

        existingUsers_recView= ((Activity)context).findViewById(R.id.existingUsers_recView);
        newUsers_recView= ((Activity)context).findViewById(R.id.newUsers_recView);

        existingUsers_recView.setLayoutManager(new LinearLayoutManager(context));
        newUsers_recView.setLayoutManager(new LinearLayoutManager(context));

        viewModelCustom.GetAllUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> models) {

                existingUsersList.clear();
                newUsersList.clear();

                for (User_Model model:models){
                    if (model.isTeamMemberRight()){
                        existingUsersList.add(model);
                    }
                    else { newUsersList.add(model);}
                }
                UpdateView();
            }
        });





    }

    private void UpdateView(){

        existingUsersAdminAdopter=new ExistingUsers_Admin_Adopter(existingUsersList, context,curUser);
        newUserAdminAdopter=new NewUser_Admin_Adopter(newUsersList,context,curUser);

        existingUsers_recView.setAdapter(existingUsersAdminAdopter);
        newUsers_recView.setAdapter(newUserAdminAdopter);

    }

}