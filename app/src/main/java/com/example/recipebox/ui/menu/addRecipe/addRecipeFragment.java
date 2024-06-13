package com.example.recipebox.ui.menu.addRecipe;

import com.example.recipebox.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.lifecycle.ViewModelProvider;


import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;

import com.example.recipebox.databinding.FragmentAddRecipeBinding;
import com.google.android.material.transition.MaterialFadeThrough;


import androidx.camera.lifecycle.ProcessCameraProvider;

import com.google.common.util.concurrent.ListenableFuture;

public class addRecipeFragment extends Fragment {

    private FragmentAddRecipeBinding binding;
    private AddRecipeViewModel viewModel;

    // camera
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private Bitmap recipeImg;

    // for permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            binding.btnCap.setEnabled(false);
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());

        imageCapture = null;
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);

        binding.btnCap.shrink();
        binding.btnCap.setOnClickListener(takeImage);
        binding.btnDelImg.setOnClickListener(v -> {
            binding.imgFood.setImageResource(R.drawable.recipe);
            binding.btnDelImg.setEnabled(false);

        });

        return binding.getRoot();
    }

    private final View.OnClickListener takeImage = v -> {

        if (imageCapture == null) {

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();
                    // view camera
                    Preview preview = new Preview.Builder().setTargetRotation(Surface.ROTATION_0)
                            .build();
                    preview.setSurfaceProvider(binding.camView.getSurfaceProvider());
                    //

                    // img cap
                    imageCapture = new ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build();


                    cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
                    binding.camView.setVisibility(View.VISIBLE);
                    binding.btnCap.extend();
                } catch (Exception ignored) {

                }
            }, requireContext().getMainExecutor());
        }

        if (imageCapture != null)

            imageCapture.takePicture(ContextCompat.getMainExecutor(requireActivity()), new ImageCapture.OnImageCapturedCallback() {
                        @Override
                        public void onCaptureSuccess(@NonNull ImageProxy image) {
                            super.onCaptureSuccess(image);

                            recipeImg = image.toBitmap();
                            try {
                                cameraProviderFuture.get().unbindAll();
                            } catch (Exception ignored) {
                            }

                            binding.imgFood.setImageBitmap(recipeImg);

                            binding.test.setText(" " + image.getImageInfo().getRotationDegrees());
                            binding.imgFood.setRotation(image.getImageInfo().getRotationDegrees());

                            binding.camView.setVisibility(View.INVISIBLE);
                            binding.btnCap.shrink();
                            binding.btnDelImg.setEnabled(true);
                            imageCapture = null;
                        }

                    }
            );

    };


    private void requestCam() {
        if (ContextCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            CameraDialog cameraDialog = new CameraDialog(requestPermissionLauncher);
            cameraDialog.show(getChildFragmentManager(), CameraDialog.TAG);
        } else {
            binding.btnCap.setEnabled(false);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddRecipeViewModel.class);
        requestCam();

    }


}