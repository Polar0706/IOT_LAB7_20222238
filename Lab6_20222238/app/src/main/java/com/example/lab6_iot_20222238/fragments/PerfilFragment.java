package com.example.lab6_iot_20222238.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lab6_iot_20222238.databinding.FragmentPerfilBinding;
import com.example.lab6_iot_20222238.services.CloudStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private CloudStorage cloudStorage;
    private FirebaseFirestore db;
    private String userId;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        subirImagen(imageUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cloudStorage = new CloudStorage();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            cargarDatosUsuario();
        }

        binding.btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());

            db.collection("usuarios").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            String dni = documentSnapshot.getString("dni");
                            String fotoUrl = documentSnapshot.getString("fotoUrl");

                            binding.tvNombre.setText(nombre != null ? nombre : "No disponible");
                            binding.tvDni.setText(dni != null ? dni : "No disponible");

                            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(fotoUrl)
                                        .into(binding.ivFotoPerfil);
                            }
                        }
                    });
        }
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void subirImagen(Uri imageUri) {
        cloudStorage.uploadFile(imageUri, userId, new CloudStorage.StorageCallback() {
            @Override
            public void onSuccess(String url) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Imagen subida exitosamente\nURL: " + url, Toast.LENGTH_LONG).show();

                    db.collection("usuarios").document(userId)
                            .update("fotoUrl", url)
                            .addOnSuccessListener(aVoid -> {
                                Glide.with(requireContext())
                                        .load(url)
                                        .into(binding.ivFotoPerfil);
                            });
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
