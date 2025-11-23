package com.example.lab6_iot_20222238.services;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CloudStorage {
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public CloudStorage() {
        initializeStorage();
    }

    public void initializeStorage() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    /*
     * IA utilizada: Claude Sonnet 4
     * Prompt: Necesito un método que suba una imagen del perfil del usuario a Firebase
     * Storage y me devuelva la URL de descarga. La imagen debe guardarse en una carpeta
     * profile_images con un nombre único que incluya el userId y un timestamp.
     * Comentario: Le agregué mejor manejo de errores con callbacks separados para success
     * y error. También cambié la estructura para que primero suba el archivo y después
     * obtenga la URL, ya que así es más fácil manejar los errores en cada paso.
     */
    public void uploadFile(Uri fileUri, String userId, StorageCallback callback) {
        String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child("profile_images/" + fileName);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        callback.onSuccess(uri.toString());
                    }).addOnFailureListener(e -> {
                        callback.onError("Error al obtener URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error al subir archivo: " + e.getMessage());
                });
    }

    public void getDownloadUrl(String path, StorageCallback callback) {
        StorageReference fileRef = storageRef.child(path);
        fileRef.getDownloadUrl()
                .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                .addOnFailureListener(e -> callback.onError("Error: " + e.getMessage()));
    }

    public interface StorageCallback {
        void onSuccess(String url);
        void onError(String error);
    }
}
