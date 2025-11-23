package com.example.lab6_iot_20222238.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_iot_20222238.databinding.ItemRegistroBinding;
import com.example.lab6_iot_20222238.models.RegistroCombustible;

import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.ViewHolder> {

    private List<RegistroCombustible> registroList;
    private Context context;
    private OnRegistroClickListener listener;

    public interface OnRegistroClickListener {
        void onEditClick(RegistroCombustible registro);
        void onDeleteClick(RegistroCombustible registro);
    }

    public RegistroAdapter(List<RegistroCombustible> registroList, Context context, OnRegistroClickListener listener) {
        this.registroList = registroList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRegistroBinding binding = ItemRegistroBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroCombustible registro = registroList.get(position);
        holder.binding.tvIdRegistro.setText(registro.getIdRegistro());
        holder.binding.tvFecha.setText(registro.getFecha());
        holder.binding.tvLitros.setText(String.format("%.2f L", registro.getLitrosCargados()));
        holder.binding.tvKilometraje.setText(String.format("%d km", registro.getKilometrajeActual()));
        holder.binding.tvTipoCombustible.setText(registro.getTipoCombustible());
        holder.binding.tvPrecio.setText(String.format("S/ %.2f", registro.getPrecioTotal()));

        holder.binding.btnEditar.setOnClickListener(v -> listener.onEditClick(registro));
        holder.binding.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(registro));
    }

    @Override
    public int getItemCount() {
        return registroList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemRegistroBinding binding;

        ViewHolder(ItemRegistroBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
