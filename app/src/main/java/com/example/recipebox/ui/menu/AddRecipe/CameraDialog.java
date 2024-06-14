package com.example.recipebox.ui.menu.AddRecipe;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.recipebox.R;

public class CameraDialog extends DialogFragment {
    public static String TAG = "CameraDialog";

    public CameraDialog() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AddRecipeFragment fragment = (AddRecipeFragment) getParentFragment();
        setCancelable(false);
        return new AlertDialog.Builder(requireContext())
                .setMessage("Use your camera to take a photo of the food")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {


                    if (fragment != null) {
                        fragment.getRequestPermissionLauncher().launch(Manifest.permission.CAMERA);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    dialog.cancel();
                })
                .create();

    }


}