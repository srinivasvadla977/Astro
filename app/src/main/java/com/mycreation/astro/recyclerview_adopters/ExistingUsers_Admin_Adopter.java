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
import com.mycreation.astro.databinding.ExistingUsersListitemBinding;
import com.mycreation.astro.myutils_pack.AlertDialogueUtil;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class ExistingUsers_Admin_Adopter extends RecyclerView.Adapter<ExistingUsers_Admin_Adopter.MyViewHolder> {

    ArrayList<User_Model> usersList=new ArrayList<>();
    static ViewModel_Custom viewModelCustom;
    static User_Model curUser;

    public ExistingUsers_Admin_Adopter(ArrayList<User_Model> usersList, Context context, User_Model curUserr) {
        this.usersList = usersList;
        curUser=curUserr;
        viewModelCustom=new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel_Custom.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ExistingUsersListitemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.existing_users_listitem, parent,false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User_Model model= usersList.get(position);
        holder.binding.setExistingUsersLI(model);
    }

    @Override
    public int getItemCount() {
        if (usersList!=null){ return usersList.size(); }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ExistingUsersListitemBinding binding;

        public MyViewHolder(ExistingUsersListitemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            //
            binding.oldUserRemoveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialogueUtil.getAlertDialogueUtil(itemView.getContext()).ShowAlertDialogue_forAdmin("Do you want to remove "+binding.getExistingUsersLI().getUserName()+" ?", itemView.getContext().getResources().getString(R.string.adminPassword), new AlertDialogueUtil.AdminCallBack() {
                        @Override
                        public void onConfirmation(boolean confirm) {
                            if (confirm){
                                binding.getExistingUsersLI().setTeamMemberRight(false);
                                viewModelCustom.UpdateUser(binding.getExistingUsersLI(),3,null);
                                Toast.makeText(itemView.getContext(), "User has been removed",Toast.LENGTH_SHORT).show();
                                GroupChat_model gModel=new GroupChat_model("Astro", curUser.getUserName()+" removed the user: "+binding.getExistingUsersLI().getUserName(),null,System.currentTimeMillis(),null,null,null,"12345");
                                viewModelCustom.PostMessage(gModel,null);
                            }
                        }
                    });
                }
            });

        }
    }

}
