package edu.northeastern.team21.Foggy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.team21.Foggy.ProfileRV.ProfileAdapter;
import edu.northeastern.team21.R;

public class ProfileItemsEditDialog extends DialogFragment {
    private static final String TAG = "__PROFILEITEMEDITDIALOG__";

    private TextInputEditText textInputEditText;

    private TextView reminderTextView;

    private FirebaseAuth authProfile;

    //private ProgressBar progressBar;


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        // position represents which item has been clicked. [-2, 5], -2 stands for the name. -1 stands for the personal description
        int position = getArguments().getInt("position");
        String content = getArguments().getString("content");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogue_profile_items_edit, null);

        this.reminderTextView = (TextView) view.findViewById(R.id.textView_profile_edit_dialog_reminder);
        this.reminderTextView.setText("Update your " + this.getInfoCata(position) + " below: ");
        this.textInputEditText = (TextInputEditText) view.findViewById(R.id.textInputEditText_profile_edit);
        this.textInputEditText.setText(content);
        Log.d(TAG, "content: " + content);
        AlertDialog dialog = builder.setView(view).
                setPositiveButton("UPDATE", null).
                setNegativeButton("CANCEL", null).
                create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        authProfile = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = authProfile.getCurrentUser();
                        String userID = firebaseUser.getUid();
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("UserProfile");

                        referenceProfile.child(userID).child(getKey(position)).setValue(textInputEditText.getText().toString());
                        //after upgrading the data, close the dialog
                        Log.d(TAG, "profile updating...");
                        Intent goBackIntent = new Intent();
                        goBackIntent.putExtra("newContent", textInputEditText.getText().toString());
                        goBackIntent.putExtra("position", position);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, goBackIntent);
                        dismiss();

                    }
                });

                Button negativeButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { ProfileItemsEditDialog.this.getDialog().cancel(); }
                });
            }
        });


        return dialog;
    }

    @Override
    public void onAttach(@Nullable Context context){
        Log.d(TAG, "attaching");
        super.onAttach(context);
    }




    private String getKey(int position){
        if(position==-2) return "name";
        else if(position==-1) return "personaldescription";
        else if(position==0) return "username";
            // cannot edit explored area (when position==1)
        else if(position==2) return "dob";
        else if(position==3) return "phone";
        else if(position==5) return "ins";
        else return "wrong info";
    }
    private String getInfoCata(int position){
        if(position==-2) return "Name";
        else if(position==-1) return "Personal Description";
        else if(position==0) return "Username";
        // cannot edit explored area (when position==1)
        else if(position==2) return "Birthday";
        else if(position==3) return "Phone Number";
        else if(position==5) return "Instagram Account";
        else return "wrong info";
    }
}
