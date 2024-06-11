package com.example.recipebox.ui.login;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.recipebox.R;

public class NoAccountDialog extends DialogFragment {
    public static String TAG = "NoAccountDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.NoAccount))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                })
                .create();
    }


}