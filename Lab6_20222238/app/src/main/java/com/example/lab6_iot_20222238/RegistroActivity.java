package com.example.lab6_iot_20222238;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_iot_20222238.databinding.ActivityRegistroBinding;
import com.example.lab6_iot_20222238.services.AuthService;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authService = new AuthService();

        binding.btnRegistrar.setOnClickListener(v -> registrarUsuario());
        binding.btnVolverLogin.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String nombre = binding.etNombre.getText().toString().trim();
        String dni = binding.etDni.getText().toString().trim();
        String email = binding.etEmailRegistro.getText().toString().trim();
        String password = binding.etPasswordRegistro.getText().toString().trim();

        if (nombre.isEmpty() || dni.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.length() != 8) {
            Toast.makeText(this, "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.registerUser(nombre, dni, email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(RegistroActivity.this, message, Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(RegistroActivity.this, error, Toast.LENGTH_LONG).show());
            }
        });
    }
}
