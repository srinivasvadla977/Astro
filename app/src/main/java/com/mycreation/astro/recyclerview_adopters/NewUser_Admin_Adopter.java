package com.mycreation.astro.recyclerview_adopters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.databinding.NewUsersListitemBinding;
import com.mycreation.astro.myutils_pack.AlertDialogueUtil;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class NewUser_Admin_Adopter extends RecyclerView.Adapter<NewUser_Admin_Adopter.MyViewHolder> {

    ArrayList<User_Model> usersList=new ArrayList<>();
    static ViewModel_Custom viewModelCustom;
    static User_Model curUser;

    public NewUser_Admin_Adopter(ArrayList<User_Model> usersList, Context context, User_Model curUserr) {
        this.usersList = usersList;
        curUser=curUserr;
        viewModelCustom=new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel_Custom.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NewUsersListitemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.new_users_listitem,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User_Model model= usersList.get(position);
        holder.binding.setNewUsersLI(model);

    }

    @Override
    public int getItemCount() {
        if (usersList!=null){ return usersList.size(); }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        NewUsersListitemBinding binding;

        public MyViewHolder(NewUsersListitemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // FOR MAKING NEW USER AS TEAM MEMBER
            binding.newUserApproveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialogueUtil.getAlertDialogueUtil(itemView.getContext()).ShowAlertDialogue_forAdmin("Do you want to add "+binding.getNewUsersLI().getUserName()+" to team?", itemView.getContext().getResources().getString(R.string.adminPassword), new AlertDialogueUtil.AdminCallBack() {
                        @Override
                        public void onConfirmation(boolean confirm) {
                            if (confirm){
                                binding.getNewUsersLI().setTeamMemberRight(true);
                                viewModelCustom.UpdateUser(binding.getNewUsersLI(),3,null);
                                Toast.makeText(itemView.getContext(), "User has been added", Toast.LENGTH_SHORT).show();
                                GroupChat_model gModel=new GroupChat_model("Astro", "Hi all,\n"+curUser.getUserName()+" added a new user: "+binding.getNewUsersLI().getUserName(),null,System.currentTimeMillis(),null,null,null,"12345");
                                viewModelCustom.PostMessage(gModel,null);
                            }
                        }
                    });

                    //viewModelCustom.UpdateUser(binding.getNewUsersLI(),3,null);
                }
            });

            // FOR REJECTING NEW USER REQUEST
            binding.newUserRejectBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialogueUtil.getAlertDialogueUtil(itemView.getContext()).ShowAlertDialogue_forAdmin("Do you want to reject "+binding.getNewUsersLI().getUserName()+"'s request?", itemView.getContext().getResources().getString(R.string.adminPassword), new AlertDialogueUtil.AdminCallBack() {
                        @Override
                        public void onConfirmation(boolean confirm) {
                            viewModelCustom.DeleteUser(binding.getNewUsersLI().getUserDocId());
                            Toast.makeText(itemView.getContext(), "User has been rejected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //viewModelCustom.DeleteUser(binding.getNewUsersLI().getUserDocId());
                }
            });

        }
    }
}
