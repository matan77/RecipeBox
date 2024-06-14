package com.example.recipebox.ui.menu.AddRecipe;

import com.example.recipebox.R;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import android.widget.Button;
import android.widget.Toast;

import com.example.recipebox.databinding.FragmentAddRecipeBinding;

import com.example.recipebox.model.Recipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transition.MaterialFadeThrough;


import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.navigation.Navigation;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.stream.Collectors;

public class AddRecipeFragment extends Fragment {

    private FragmentAddRecipeBinding binding;


    // image from phone gallery
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {

        if (uri != null) {
            ImageDecoder.Source source = ImageDecoder.createSource(requireContext().getContentResolver(), uri);
            try {
                binding.imgFood.setImageBitmap(ImageDecoder.decodeBitmap(source));
                recipeImg = ImageDecoder.decodeBitmap(source);
                binding.btnDelImg.setEnabled(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    });


    private Bitmap recipeImg;

    // camera
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private ImageCapture imageCapture;
    // for permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        isCamPermission = isGranted;
    });
    private boolean isCamPermission;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


    public ActivityResultLauncher<String> getRequestPermissionLauncher() {
        return requestPermissionLauncher;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageCapture = null;
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        isCamPermission = false;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        binding.btnCap.shrink();
        binding.btnCap.setOnClickListener(takeImage);

        binding.topAppBar.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigateUp();
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });


        // create a recipe
        binding.btnSave.setOnClickListener(addRecipe);


        binding.btnUpload.setOnClickListener(v ->

        {
            pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        });

        binding.btnDelImg.setOnClickListener(v ->

        {
            binding.imgFood.setImageResource(R.drawable.recipe);
            recipeImg = null;
            binding.btnDelImg.setEnabled(false);

        });
        return binding.getRoot();
    }

    private final View.OnClickListener addRecipe = v -> {
        binding.btnSave.setEnabled(false);
        String title = binding.etTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        List<String> ingredients = Arrays.stream(binding.etIngredients.getText().toString().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        List<String> instructions = Arrays.stream(binding.etInstructions.getText().toString().split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());

        if (title.isEmpty() || description.isEmpty() ||
                binding.etIngredients.getText().toString().isEmpty() || binding.etInstructions.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Recipe recipe = new Recipe(title, description, ingredients, instructions, FirebaseAuth.getInstance().getUid());
        Context context = requireContext();
        DocumentReference ref = db.collection("recipes").document();
        if (recipeImg == null) {
            ref.set(recipe)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(binding.getRoot()).navigateUp();
                        }
                    });
        } else {
            setImageUrl(ref, recipe);
        }


    };

    // Upload image to Firebase Storage
    private void setImageUrl(DocumentReference ref, Recipe recipe) {
        Context context = requireContext();

        String id = ref.getId();

        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("uploads/" + recipe.getCreator() + "/" + id + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        recipeImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, now get download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                recipe.setImageUrl(uri.toString());
                ref.set(recipe)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(binding.getRoot()).navigateUp();
                            }
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show();
        });

    }

    private final View.OnClickListener takeImage = v -> {
        requestCam();
        if (!isCamPermission) {
            return;
        }
        if (imageCapture == null) {

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                    // view camera
                    Preview preview = new Preview.Builder().setTargetRotation(Surface.ROTATION_0).build();
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


                    Matrix matrix = new Matrix();
                    matrix.preRotate(image.getImageInfo().getRotationDegrees());

                    recipeImg = Bitmap.createBitmap(recipeImg, 0, 0, recipeImg.getWidth(), recipeImg.getHeight(), matrix, true);

                    binding.imgFood.setImageBitmap(recipeImg);


                    binding.camView.setVisibility(View.INVISIBLE);
                    binding.btnCap.shrink();
                    binding.btnDelImg.setEnabled(true);
                    imageCapture = null;
                }

            });

    };


    // request camera permissions
    private void requestCam() {
        if (ContextCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            CameraDialog cameraDialog = new CameraDialog();
            cameraDialog.show(getChildFragmentManager(), CameraDialog.TAG);

        } else {
            CameraDialog cameraDialog = new CameraDialog();
            cameraDialog.show(getChildFragmentManager(), CameraDialog.TAG);

        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestCam();

    }

    @Override
    public void onStart() {
        super.onStart();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();

        }

    }


}