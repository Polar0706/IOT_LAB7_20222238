package com.example.lab6_iot_20222238.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab6_iot_20222238.R;
import com.example.lab6_iot_20222238.adapters.RegistroAdapter;
import com.example.lab6_iot_20222238.databinding.FragmentRegistrosBinding;
import com.example.lab6_iot_20222238.models.RegistroCombustible;
import com.example.lab6_iot_20222238.models.Vehiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegistrosFragment extends Fragment implements RegistroAdapter.OnRegistroClickListener {

    private FragmentRegistrosBinding binding;
    private FirebaseFirestore db;
    private RegistroAdapter adapter;
    private List<RegistroCombustible> registroList;
    private List<Vehiculo> vehiculoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        registroList = new ArrayList<>();
        vehiculoList = new ArrayList<>();
        adapter = new RegistroAdapter(registroList, requireContext(), this);

        binding.recyclerViewRegistros.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewRegistros.setAdapter(adapter);

        binding.fabAgregarRegistro.setOnClickListener(v -> {
            cargarVehiculos(() -> mostrarDialogoAgregarRegistro(null));
        });

        cargarRegistros();
    }

    private void cargarVehiculos(Runnable callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("vehiculos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vehiculoList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Vehiculo vehiculo = doc.toObject(Vehiculo.class);
                        vehiculo.setId(doc.getId());
                        vehiculoList.add(vehiculo);
                    }
                    if (callback != null) callback.run();
                });
    }

    private void cargarRegistros() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("registros")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    
                    registroList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        RegistroCombustible registro = doc.toObject(RegistroCombustible.class);
                        registro.setId(doc.getId());
                        registroList.add(registro);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoAgregarRegistro(RegistroCombustible registroEditar) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_registro, null);
        
        EditText etIdRegistro = dialogView.findViewById(R.id.etIdRegistro);
        Spinner spinnerVehiculo = dialogView.findViewById(R.id.spinnerVehiculo);
        EditText etFecha = dialogView.findViewById(R.id.etFecha);
        EditText etLitros = dialogView.findViewById(R.id.etLitros);
        EditText etKilometraje = dialogView.findViewById(R.id.etKilometraje);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecio);
        Spinner spinnerTipoCombustible = dialogView.findViewById(R.id.spinnerTipoCombustible);

        List<String> vehiculosNombres = new ArrayList<>();
        for (Vehiculo v : vehiculoList) {
            vehiculosNombres.add(v.getIdVehiculo() + " - " + v.getPlaca());
        }
        ArrayAdapter<String> adapterVehiculos = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, vehiculosNombres);
        adapterVehiculos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehiculo.setAdapter(adapterVehiculos);

        String[] tiposCombustible = {"Gasolina", "GLP", "GNV"};
        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tiposCombustible);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoCombustible.setAdapter(adapterTipos);

        if (registroEditar == null) {
            String idAleatorio = String.format("%05d", new Random().nextInt(100000));
            etIdRegistro.setText(idAleatorio);
        } else {
            etIdRegistro.setText(registroEditar.getIdRegistro());
            etFecha.setText(registroEditar.getFecha());
            etLitros.setText(String.valueOf(registroEditar.getLitrosCargados()));
            etKilometraje.setText(String.valueOf(registroEditar.getKilometrajeActual()));
            etPrecio.setText(String.valueOf(registroEditar.getPrecioTotal()));
            
            for (int i = 0; i < vehiculoList.size(); i++) {
                if (vehiculoList.get(i).getId().equals(registroEditar.getVehiculoId())) {
                    spinnerVehiculo.setSelection(i);
                    break;
                }
            }
            
            for (int i = 0; i < tiposCombustible.length; i++) {
                if (tiposCombustible[i].equals(registroEditar.getTipoCombustible())) {
                    spinnerTipoCombustible.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(registroEditar == null ? "Agregar Registro" : "Editar Registro")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String idRegistro = etIdRegistro.getText().toString();
                    int vehiculoPos = spinnerVehiculo.getSelectedItemPosition();
                    String fecha = etFecha.getText().toString();
                    String litrosStr = etLitros.getText().toString();
                    String kilometrajeStr = etKilometraje.getText().toString();
                    String precioStr = etPrecio.getText().toString();
                    String tipoCombustible = spinnerTipoCombustible.getSelectedItem().toString();

                    if (idRegistro.isEmpty() || fecha.isEmpty() || litrosStr.isEmpty() || kilometrajeStr.isEmpty() || precioStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double litros = Double.parseDouble(litrosStr);
                    int kilometraje = Integer.parseInt(kilometrajeStr);
                    double precio = Double.parseDouble(precioStr);
                    String vehiculoId = vehiculoList.get(vehiculoPos).getId();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // IA utilizada: claude sonnet 4 / Prompt: "necesito validar que el nuevo kilometraje ingresado sea mayor al último kilometraje registrado para ese vehículo específico, pero sin usar orderBy en firestore porque genera errores de índice" / Comentario: La IA diseñó un patrón de validación donde se obtienen todos los registros del usuario, se filtran en memoria por vehiculoId, y se encuentra el máximo kilometraje recorriendo la lista. Esta solución evita queries complejas de Firestore y permite validar antes de guardar, mostrando un Toast de error si el kilometraje es inválido.
                    db.collection("registros")
                            .whereEqualTo("userId", userId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                int ultimoKm = 0;
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    RegistroCombustible reg = doc.toObject(RegistroCombustible.class);
                                    if (reg != null && vehiculoId.equals(reg.getVehiculoId())) {
                                        if (registroEditar == null || !doc.getId().equals(registroEditar.getId())) {
                                            if (reg.getKilometrajeActual() > ultimoKm) {
                                                ultimoKm = reg.getKilometrajeActual();
                                            }
                                        }
                                    }
                                }

                                if (kilometraje <= ultimoKm) {
                                    Toast.makeText(requireContext(), "El kilometraje debe ser mayor a " + ultimoKm, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                RegistroCombustible registro = new RegistroCombustible(idRegistro, vehiculoId, fecha, litros, kilometraje, precio, tipoCombustible, userId);

                                if (registroEditar == null) {
                                    db.collection("registros").add(registro)
                                            .addOnSuccessListener(documentReference -> 
                                                Toast.makeText(requireContext(), "Registro guardado", Toast.LENGTH_SHORT).show());
                                } else {
                                    registro.setId(registroEditar.getId());
                                    db.collection("registros").document(registroEditar.getId()).set(registro)
                                            .addOnSuccessListener(aVoid -> 
                                                Toast.makeText(requireContext(), "Registro actualizado", Toast.LENGTH_SHORT).show());
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEditClick(RegistroCombustible registro) {
        cargarVehiculos(() -> mostrarDialogoAgregarRegistro(registro));
    }

    @Override
    public void onDeleteClick(RegistroCombustible registro) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Registro")
                .setMessage("¿Estás seguro de eliminar este registro?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection("registros").document(registro.getId()).delete()
                            .addOnSuccessListener(aVoid -> 
                                Toast.makeText(requireContext(), "Registro eliminado", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
