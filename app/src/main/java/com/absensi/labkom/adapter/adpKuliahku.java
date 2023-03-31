package com.absensi.labkom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelKuliah;

import java.util.ArrayList;

public class adpKuliahku extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelKuliah> myList=  new ArrayList<>();

    public void setList(ArrayList<modelKuliah> myList) {
        this.myList = myList;
    }

    public adpKuliahku(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int i) {
        return myList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;

        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_kuliahku, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelKuliah model = (modelKuliah) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView kode, dosen, matkul, ruang, jam, tanggal;
        LinearLayout bg, header;
        ImageView img;

        ViewHolder(View view) {
            bg = view.findViewById(R.id.listKuliahkuBg);
            header = view.findViewById(R.id.listKuliahkuHeader);
            img = view.findViewById(R.id.listKuliahkuGbr);
            kode = view.findViewById(R.id.listKuliahkuKode);
            dosen = view.findViewById(R.id.listKuliahkuDosen);
            matkul = view.findViewById(R.id.listKuliahkuMatkul);
            ruang = view.findViewById(R.id.listKuliahkuRuang);
            jam = view.findViewById(R.id.listKuliahkuJam);
            tanggal = view.findViewById(R.id.listKuliahkuTgl);
        }

        @SuppressLint("ResourceAsColor")
        void bind(modelKuliah model) {
            String ket = model.getKet().trim();
            if (ket.equals("Hadir")) {
                header.setBackgroundResource(R.drawable.bg_statberhasil);
                img.setBackgroundResource(R.drawable.status_selesai);
                bg.setBackgroundResource(R.color.hijau);
            } else if (ket.equals("Menunggu konfirmasi")) {
                header.setBackgroundResource(R.drawable.bg_stattunggu);
                img.setBackgroundResource(R.drawable.status_tunggu);
                bg.setBackgroundResource(R.color.orange);
            } else if (ket.equals("Tidak hadir")) {
                header.setBackgroundResource(R.drawable.bg_statgagal);
                img.setBackgroundResource(R.drawable.status_belum);
                bg.setBackgroundResource(R.color.merah);
            }

            kode.setText(model.getKode());
            dosen.setText(model.getDosen());
            matkul.setText(model.getMatkul());
            ruang.setText(model.getRuang());
            jam.setText(model.getJam());
            tanggal.setText(model.getTanggal());
        }
    }
}
