package com.example.lab6_iot_20222238.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab6_iot_20222238.R;
import com.example.lab6_iot_20222238.adapters.VehiculoAdapter;
import com.example.lab6_iot_20222238.databinding.FragmentMisVehiculosBinding;
import com.example.lab6_iot_20222238.dialogs.QrDialogFragment;
import com.example.lab6_iot_20222238.models.RegistroCombustible;
import com.example.lab6_iot_20222238.models.Vehiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisVehiculosFragment extends Fragment implements VehiculoAdapter.OnVehiculoClickListener {

    private FragmentMisVehiculosBinding binding;
    private FirebaseFirestore db;
    private VehiculoAdapter adapter;
    private List<Vehiculo> vehiculoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMisVehiculosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        vehiculoList = new ArrayList<>();
        adapter = new VehiculoAdapter(vehiculoList, requireContext(), this);

        binding.recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewVehiculos.setAdapter(adapter);

        binding.fabAgregarVehiculo.setOnClickListener(v -> mostrarDialogoAgregarVehiculo(null));

        cargarVehiculos();
    }

    private void cargarVehiculos() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("vehiculos")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    
                    vehiculoList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Vehiculo vehiculo = doc.toObject(Vehiculo.class);
                        vehiculo.setId(doc.getId());
                        vehiculoList.add(vehiculo);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoAgregarVehiculo(Vehiculo vehiculoEditar) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vehiculo, null);
        EditText etIdVehiculo = dialogView.findViewById(R.id.etIdVehiculo);
        EditText etPlaca = dialogView.findViewById(R.id.etPlaca);
        EditText etMarcaModelo = dialogView.findViewById(R.id.etMarcaModelo);
        EditText etAnio = dialogView.findViewById(R.id.etAnio);
        EditText etFechaRevision = dialogView.findViewById(R.id.etFechaRevision);

        if (vehiculoEditar != null) {
            etIdVehiculo.setText(vehiculoEditar.getIdVehiculo());
            etPlaca.setText(vehiculoEditar.getPlaca());
            etMarcaModelo.setText(vehiculoEditar.getMarcaModelo());
            etAnio.setText(String.valueOf(vehiculoEditar.getAnioFabricacion()));
            etFechaRevision.setText(vehiculoEditar.getFechaRevisionTecnica());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(vehiculoEditar == null ? "Agregar Vehículo" : "Editar Vehículo")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String idVehiculo = etIdVehiculo.getText().toString();
                    String placa = etPlaca.getText().toString();
                    String marcaModelo = etMarcaModelo.getText().toString();
                    String anioStr = etAnio.getText().toString();
                    String fechaRevision = etFechaRevision.getText().toString();

                    if (idVehiculo.isEmpty() || placa.isEmpty() || marcaModelo.isEmpty() || anioStr.isEmpty() || fechaRevision.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int anio = Integer.parseInt(anioStr);
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Vehiculo vehiculo = new Vehiculo(idVehiculo, placa, marcaModelo, anio, fechaRevision, userId);

                    if (vehiculoEditar == null) {
                        db.collection("vehiculos").add(vehiculo)
                                .addOnSuccessListener(documentReference -> 
                                    Toast.makeText(requireContext(), "Vehículo guardado", Toast.LENGTH_SHORT).show());
                    } else {
                        vehiculo.setId(vehiculoEditar.getId());
                        db.collection("vehiculos").document(vehiculoEditar.getId()).set(vehiculo)
                                .addOnSuccessListener(aVoid -> 
                                    Toast.makeText(requireContext(), "Vehículo actualizado", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEditClick(Vehiculo vehiculo) {
        mostrarDialogoAgregarVehiculo(vehiculo);
    }

    @Override
    public void onDeleteClick(Vehiculo vehiculo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Vehículo")
                .setMessage("¿Estás seguro de eliminar este vehículo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection("vehiculos").document(vehiculo.getId()).delete()
                            .addOnSuccessListener(aVoid -> 
                                Toast.makeText(requireContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onQrClick(Vehiculo vehiculo) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        // IA utilizada: claude sonnet 4 / Prompt: "necesito obtener el último kilometraje registrado de un vehículo específico para mostrarlo en un código QR, pero firestore me da error de índice compuesto al usar whereEqualTo con orderBy" / Comentario: La IA sugirió hacer el filtrado en memoria en lugar de usar queries complejas de Firestore. Se obtienen todos los registros del usuario con una query simple y luego se filtra por vehiculoId en el cliente, evitando la necesidad de crear índices compuestos en Firestore que tardan varios minutos en propagarse.
        db.collection("registros")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int ultimoKm = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        RegistroCombustible registro = doc.toObject(RegistroCombustible.class);
                        if (registro != null && vehiculo.getId().equals(registro.getVehiculoId())) {
                            if (registro.getKilometrajeActual() > ultimoKm) {
                                ultimoKm = registro.getKilometrajeActual();
                            }
                        }
                    }
                    QrDialogFragment.newInstance(vehiculo, ultimoKm).show(getParentFragmentManager(), "qr");
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
