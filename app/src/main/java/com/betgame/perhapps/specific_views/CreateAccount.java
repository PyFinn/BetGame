package com.betgame.perhapps.specific_views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.betgame.perhapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class CreateAccount extends DialogFragment {

    private TextView mEmail;
    private TextView mPassword;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.create_account, null);
        mEmail = (EditText) view.findViewById(R.id.et_email);
        mPassword = (EditText) view.findViewById(R.id.et_password);

        builder.setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String textEmail = mEmail.getText().toString();
                        final String textPassword = mPassword.getText().toString();
                        if (!textEmail.isEmpty() && !textPassword.isEmpty()){
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(textEmail, textPassword)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(textEmail, textPassword);
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Failed to create Account", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
