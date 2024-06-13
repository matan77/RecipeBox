package com.example.recipebox.ui.menu.addRecipe;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.recipebox.R;

public class CameraDialog extends DialogFragment {
    public static String TAG = "CameraDialog";
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    public CameraDialog(ActivityResultLauncher<String> requestPermissionLauncher) {
        this.requestPermissionLauncher = requestPermissionLauncher;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Use your camera to take a photo of the food")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();
    }


}