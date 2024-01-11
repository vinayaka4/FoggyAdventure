package edu.northeastern.team21.StickIt;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import edu.northeastern.team21.R;

public class SendStickerDialog extends DialogFragment {
    private static final String TAG = "__SENDSTICKERDIALOG_DIALOG__";
    SendStickerDialogListener listener;


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        int stickerResourceId = getArguments().getInt("stickerResourceId");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogue_send_sticker, null);

        // bind component
        EditText receiver = view.findViewById(R.id.dialogueTextInput);
        ImageView sticker = view.findViewById(R.id.dialogueImage);

        sticker.setImageResource(stickerResourceId);

        AlertDialog dialog = builder.setView(view)
                .setPositiveButton("SEND", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dialog_icon_send, 0);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onDialogPositiveClick(receiver.getText().toString());
                        SendStickerDialog.this.getDialog().cancel();
                    }
                });
                Button negativeButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dialog_icon_cancel, 0);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SendStickerDialog.this.getDialog().cancel();
                    }
                });

            }
        });
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SendStickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement SendStickerDialogListener");
        }
    }

    public interface SendStickerDialogListener {
        void onDialogPositiveClick(String receiver);
    }
}
