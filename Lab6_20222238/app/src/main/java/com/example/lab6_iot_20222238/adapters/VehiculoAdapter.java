package com.example.lab6_iot_20222238.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_iot_20222238.databinding.ItemVehiculoBinding;
import com.example.lab6_iot_20222238.models.Vehiculo;

import java.util.List;

public class VehiculoAdapter extends RecyclerView.Adapter<VehiculoAdapter.ViewHolder> {

    private List<Vehiculo> vehiculoList;
    private Context context;
    private OnVehiculoClickListener listener;

    public interface OnVehiculoClickListener {
        void onEditClick(Vehiculo vehiculo);
        void onDeleteClick(Vehiculo vehiculo);
        void onQrClick(Vehiculo vehiculo);
    }

    public VehiculoAdapter(List<Vehiculo> vehiculoList, Context context, OnVehiculoClickListener listener) {
        this.vehiculoList = vehiculoList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVehiculoBinding binding = ItemVehiculoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehiculo vehiculo = vehiculoList.get(position);
        holder.binding.tvIdVehiculo.setText(vehiculo.getIdVehiculo());
        holder.binding.tvPlaca.setText(vehiculo.getPlaca());
        holder.binding.tvMarcaModelo.setText(vehiculo.getMarcaModelo());
        holder.binding.tvAnio.setText(String.valueOf(vehiculo.getAnioFabricacion()));

        holder.binding.btnEditar.setOnClickListener(v -> listener.onEditClick(vehiculo));
        holder.binding.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(vehiculo));
        holder.binding.btnQr.setOnClickListener(v -> listener.onQrClick(vehiculo));
    }

    @Override
    public int getItemCount() {
        return vehiculoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemVehiculoBinding binding;

        ViewHolder(ItemVehiculoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
