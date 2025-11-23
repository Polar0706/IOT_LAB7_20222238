package com.example.lab6_iot_20222238.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab6_iot_20222238.databinding.FragmentResumenBinding;
import com.example.lab6_iot_20222238.models.RegistroCombustible;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumenFragment extends Fragment {

    private FragmentResumenBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResumenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        cargarDatosGraficos();
    }

    private void cargarDatosGraficos() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("registros")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<RegistroCombustible> registros = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        RegistroCombustible registro = doc.toObject(RegistroCombustible.class);
                        registros.add(registro);
                    }
                    configurarGraficoBarras(registros);
                    configurarGraficoTorta(registros);
                });
    }

    // IA utilizada: claude sonnet 4 / Prompt: "cómo puedo implementar un gráfico de barras con MPAndroidChart que muestre los litros de combustible cargados agrupados por mes a partir de una lista de registros con fechas en formato yyyy-MM-dd" / Comentario: La IA proporcionó la lógica completa para extraer el mes de la fecha usando substring, agrupar los datos en un HashMap usando getOrDefault para acumular litros, y convertir todo al formato BarEntry que requiere MPAndroidChart. También sugirió manejar el caso cuando no hay datos agregando una entrada dummy para evitar crashes.
    private void configurarGraficoBarras(List<RegistroCombustible> registros) {
        Map<Integer, Float> litrosPorMes = new HashMap<>();
        
        for (RegistroCombustible registro : registros) {
            String fecha = registro.getFecha();
            if (fecha != null && fecha.length() >= 7) {
                try {
                    String mesStr = fecha.substring(5, 7);
                    int mes = Integer.parseInt(mesStr);
                    float litros = (float) registro.getLitrosCargados();
                    litrosPorMes.put(mes, litrosPorMes.getOrDefault(mes, 0f) + litros);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Float> entry : litrosPorMes.entrySet()) {
            entries.add(new BarEntry(entry.getKey(), entry.getValue()));
        }

        if (entries.isEmpty()) {
            entries.add(new BarEntry(1, 0));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Litros por Mes");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);

        binding.barChart.setData(barData);
        binding.barChart.getDescription().setText("Consumo mensual");
        binding.barChart.invalidate();
    }

    private void configurarGraficoTorta(List<RegistroCombustible> registros) {
        Map<String, Float> litrosPorTipo = new HashMap<>();
        
        for (RegistroCombustible registro : registros) {
            String tipo = registro.getTipoCombustible();
            if (tipo != null) {
                float litros = (float) registro.getLitrosCargados();
                litrosPorTipo.put(tipo, litrosPorTipo.getOrDefault(tipo, 0f) + litros);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : litrosPorTipo.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Sin datos"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);

        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setText("Consumo por tipo");
        binding.pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
