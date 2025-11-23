package com.example.lab6_iot_20222238;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.lab6_iot_20222238.databinding.ActivityMainBinding;
import com.example.lab6_iot_20222238.fragments.MisVehiculosFragment;
import com.example.lab6_iot_20222238.fragments.RegistrosFragment;
import com.example.lab6_iot_20222238.fragments.ResumenFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToLogin();
            return;
        }

        loadFragment(new MisVehiculosFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_vehiculos) {
                fragment = new MisVehiculosFragment();
            } else if (itemId == R.id.nav_registros) {
                fragment = new RegistrosFragment();
            } else if (itemId == R.id.nav_resumen) {
                fragment = new ResumenFragment();
            } else if (itemId == R.id.nav_cerrar_sesion) {
                cerrarSesion();
                return true;
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void cerrarSesion() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    goToLogin();
                });
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}