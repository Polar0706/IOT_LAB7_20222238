package com.example.lab6_iot_20222238.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private OkHttpClient client;

    public AuthService() {
        initializeAuth();
        client = new OkHttpClient();
    }

    public void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void signIn(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Inicio de sesión exitoso");
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Error desconocido");
                    }
                });
    }

    public void resetPassword(String email, AuthCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Correo de recuperación enviado");
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Error al enviar correo");
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
    }

    /*
     * IA utilizada: Claude Sonnet 4
     * Prompt: Necesito crear un método de registro que primero valide el DNI y correo
     * contra un microservicio antes de crear el usuario en Firebase. El microservicio
     * está en localhost:8080/registro y recibe un JSON con dni y correo. Solo si el
     * microservicio responde exitosamente, se debe crear el usuario en Firebase Auth
     * y guardar sus datos en Firestore.
     * Comentario: Ajusté la URL para usar 10.0.2.2 en lugar de localhost porque estoy
     * usando el emulador de Android. También modifiqué los mensajes de error para que
     * sean más claros cuando falla la validación del microservicio.
     */
    public void registerUser(String nombre, String dni, String email, String password, AuthCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("dni", dni);
            json.put("correo", email);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/registro")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Error de conexión: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        Map<String, Object> usuario = new HashMap<>();
                                        usuario.put("nombre", nombre);
                                        usuario.put("dni", dni);
                                        usuario.put("email", email);

                                        db.collection("usuarios").document(userId)
                                                .set(usuario)
                                                .addOnSuccessListener(aVoid -> callback.onSuccess("Usuario registrado exitosamente"))
                                                .addOnFailureListener(e -> callback.onError("Error al guardar datos: " + e.getMessage()));
                                    } else {
                                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Error al crear usuario");
                                    }
                                });
                    } else {
                        String errorMsg = response.body() != null ? response.body().string() : "Error de validación";
                        callback.onError(errorMsg);
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("Error: " + e.getMessage());
        }
    }

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
