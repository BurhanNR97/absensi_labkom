package com.absensi.labkom.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelRekapSlip;

import java.util.List;

public class adapterRekapBayar extends RecyclerView.Adapter {

    private List<modelRekapSlip> dataList;

    public adapterRekapBayar(List<modelRekapSlip> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.tabel_slip, parent, false);

        return new RowViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;

        int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {

            rowViewHolder.txtNo.setBackgroundResource(R.drawable.table_cell);
            rowViewHolder.txtNim.setBackgroundResource(R.drawable.table_cell);
            rowViewHolder.txtNama.setBackgroundResource(R.drawable.table_cell);
            rowViewHolder.txtKet.setBackgroundResource(R.drawable.table_cell);
            rowViewHolder.txtNo.setTypeface(null, Typeface.BOLD);
            rowViewHolder.txtNim.setTypeface(null, Typeface.BOLD);
            rowViewHolder.txtNama.setTypeface(null, Typeface.BOLD);
            rowViewHolder.txtKet.setTypeface(null, Typeface.BOLD);

            rowViewHolder.txtNo.setText("NO");
            rowViewHolder.txtNim.setText("NIM");
            rowViewHolder.txtNama.setText("Nama Mahasiswa");
            rowViewHolder.txtKet.setText("Keterangan");
        } else {
            modelRekapSlip data = dataList.get(rowPos - 1);

            rowViewHolder.txtNo.setBackgroundResource(R.drawable.border_left);
            rowViewHolder.txtNim.setBackgroundResource(R.drawable.border_right_bottom);
            rowViewHolder.txtNama.setBackgroundResource(R.drawable.border_right_bottom);
            rowViewHolder.txtKet.setBackgroundResource(R.drawable.border_right_bottom);

            rowViewHolder.txtNo.setText(data.getNo() + "");
            rowViewHolder.txtNim.setText(data.getNim() + "");
            rowViewHolder.txtNama.setText(data.getNama() + "");
            rowViewHolder.txtNama.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            rowViewHolder.txtKet.setText(data.getKet() + "");

            if (data.getKet() == "Ditolak" || data.getKet() == "Belum bayar") {
                rowViewHolder.txtKet.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        TextView txtNo;
        TextView txtNim;
        TextView txtNama;
        TextView txtKet;

        RowViewHolder(View itemView) {
            super(itemView);
            txtNo = itemView.findViewById(R.id.tb_slipNo);
            txtNim = itemView.findViewById(R.id.tb_slipNIM);
            txtNama = itemView.findViewById(R.id.tb_slipNama);
            txtKet = itemView.findViewById(R.id.tb_slipKet);
        }
    }
}