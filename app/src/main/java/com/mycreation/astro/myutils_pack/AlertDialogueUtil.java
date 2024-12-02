package com.mycreation.astro.myutils_pack;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.mycreation.astro.R;

public class AlertDialogueUtil {

    static Context context;
    static AlertDialogueUtil alertDialogueUtil;

    MutableLiveData<Boolean> confirm=new MutableLiveData<>();
    Vibrator vibrator;



    public AlertDialogueUtil() {}

    public static AlertDialogueUtil getAlertDialogueUtil(Context c){
        context=c;
        if (alertDialogueUtil==null){alertDialogueUtil=new AlertDialogueUtil();}
        return alertDialogueUtil;
    }

    public void ShowDialogue(String title, String message,DialogueCallBack dialogueCallBack){

        //confirm.setValue(false);

        AlertDialog.Builder alertDialogueBuilder= new AlertDialog.Builder(context);
        alertDialogueBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // confirm.setValue(true);
                        dialogueCallBack.onConfirmation(true);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // confirm.setValue(false);
                        dialogueCallBack.onConfirmation(false);
                    }
                });


        AlertDialog dialog= alertDialogueBuilder.create();
        dialog.show();


      //  return confirm;

    }

    public void ShowDialogue(String title, String message){



        AlertDialog.Builder alertDialogueBuilder= new AlertDialog.Builder(context);
        alertDialogueBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            alertDialogueBuilder.setNeutralButton("Developer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(context,"꧁★ ՏɾíղíѵɑՏ ѵɑժӀɑ ★꧂",Toast.LENGTH_LONG).show();
                }
            });


        AlertDialog dialog= alertDialogueBuilder.create();
        dialog.show();

    }

    public interface DialogueCallBack {
        void onConfirmation(boolean confirm);
    }
    ///////////////////////////

    public void ShowAlertDialogue_forAdmin(String title, String password, AdminCallBack adminCallBack){

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View myView = LayoutInflater.from(context).inflate(R.layout.alertdialogue_admin, null);

        vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibPattern={0,100,100,100};

        TextView warningTitleTV= myView.findViewById(R.id.warningNote_tv);
        EditText passwordTV=myView.findViewById(R.id.editTextTextPassword);
        Button yesBtn=myView.findViewById(R.id.warning_yesBtn);
        Button noBtn=myView.findViewById(R.id.warning_noBtn);

        warningTitleTV.setText(title);

        builder.setView(myView);

        AlertDialog dialog= builder.create();
        dialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(passwordTV.getText())) {
                    if (passwordTV.getText().toString().equals(password)) {
                        adminCallBack.onConfirmation(true);
                        dialog.dismiss();
                    }else {
                        Toast.makeText(context,"Please enter correct password",Toast.LENGTH_SHORT).show();
                        vibrator.vibrate(vibPattern,-1);
                    }

                }else {
                    Toast.makeText(context,"Please enter Security code",Toast.LENGTH_SHORT).show();
                }
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // adminCallBack.onConfirmation(false);
                dialog.dismiss();
            }
        });


    }

    public interface AdminCallBack{
        void onConfirmation(boolean confirm);
    }

}
