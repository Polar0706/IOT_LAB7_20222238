package com.example.lab6_iot_20222238;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_iot_20222238.databinding.ActivityLoginBinding;
import com.example.lab6_iot_20222238.services.AuthService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthService authService;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authService = new AuthService();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            goToMain();
            return;
        }

        binding.btnIniciarSesion.setOnClickListener(v -> iniciarSesionConEmail());
        binding.btnIniciarGoogle.setOnClickListener(v -> iniciarSesionConGoogle());
        binding.tvRecuperarPassword.setOnClickListener(v -> recuperarPassword());
        binding.tvRegistrarse.setOnClickListener(v -> irARegistro());
    }

    private void iniciarSesionConEmail() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.signIn(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    goToMain();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void iniciarSesionConGoogle() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                ))
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.Theme_Lab6_IOT_20222238)
                .setIsSmartLockEnabled(false)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Toast.makeText(this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                goToMain();
            }
        } else {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show();
        }
    }

    private void recuperarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");

        final TextInputEditText input = new TextInputEditText(this);
        input.setHint("Correo electrónico");
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) {
                authService.resetPassword(email, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void irARegistro() {
        startActivity(new Intent(this, RegistroActivity.class));
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
