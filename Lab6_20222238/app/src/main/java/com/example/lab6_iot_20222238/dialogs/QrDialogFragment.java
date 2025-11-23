package com.example.lab6_iot_20222238.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lab6_iot_20222238.R;
import com.example.lab6_iot_20222238.models.Vehiculo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrDialogFragment extends DialogFragment {

    public static QrDialogFragment newInstance(Vehiculo vehiculo, int ultimoKilometraje) {
        QrDialogFragment fragment = new QrDialogFragment();
        Bundle args = new Bundle();
        args.putString("placa", vehiculo.getPlaca());
        args.putInt("kilometraje", ultimoKilometraje);
        args.putString("fechaRevision", vehiculo.getFechaRevisionTecnica());
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_qr, null);

        String placa = getArguments().getString("placa");
        int kilometraje = getArguments().getInt("kilometraje");
        String fechaRevision = getArguments().getString("fechaRevision");

        TextView tvInfo = view.findViewById(R.id.tvInfoQr);
        ImageView ivQr = view.findViewById(R.id.ivQr);

        String info = "Placa: " + placa + "\nKilometraje: " + kilometraje + " km\nÚltima revisión: " + fechaRevision;
        tvInfo.setText(info);

        String qrContent = "Placa:" + placa + "|Kilometraje:" + kilometraje + "|Revision:" + fechaRevision;

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);
            ivQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Código QR - Revisión Técnica")
                .setView(view)
                .setPositiveButton("Cerrar", null)
                .create();
    }
}
