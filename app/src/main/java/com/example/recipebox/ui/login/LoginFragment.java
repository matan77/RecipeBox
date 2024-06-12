package com.example.recipebox.ui.login;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CancellationSignal;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recipebox.R;
import com.example.recipebox.databinding.FragmentLoginBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import com.google.android.material.transition.MaterialFadeThrough;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);


        binding.googleAuthButton.setOnClickListener(v -> {
            binding.googleAuthButton.setEnabled(false);
            signInWithGoogle();
        });

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            navToMenu();
        }
    }

    private void navToMenu() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_navigation_login_to_menuFragment);
    }

    private void signInWithGoogle() {

        GetSignInWithGoogleOption googleIdOption = new GetSignInWithGoogleOption.Builder(getString(R.string.client_id)).build();


        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();

        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {
            binding.googleAuthButton.setEnabled(true);
        });

        CredentialManager.create(requireActivity()).getCredentialAsync(requireActivity(), request, cancellationSignal, ContextCompat.getMainExecutor(requireActivity()), new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

            @Override
            public void onResult(GetCredentialResponse getCredentialResponse) {
                binding.googleAuthButton.setEnabled(true);
                Credential credential = getCredentialResponse.getCredential();

                if (credential instanceof CustomCredential) {
                    CustomCredential customCredential = (CustomCredential) credential;
                    if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {

                        GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                        firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
                    }
                }

            }

            @Override
            public void onError(@NonNull GetCredentialException e) {

                NoAccountDialog dialog = new NoAccountDialog();
                dialog.setCancelable(false);
                dialog.show(getChildFragmentManager(), NoAccountDialog.TAG);
                binding.googleAuthButton.setEnabled(true);

            }
        });


    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Toast toast = Toast.makeText(requireActivity(), "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT);
                        toast.show();
                        navToMenu();
                    }

                } else {
                    new NoAccountDialog().show(getChildFragmentManager(), NoAccountDialog.TAG);
                }
            }
        });
    }


}