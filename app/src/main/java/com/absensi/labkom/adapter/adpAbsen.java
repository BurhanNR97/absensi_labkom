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
import com.absensi.labkom.model.modelAbsen;

import java.util.ArrayList;

public class adpAbsen extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelAbsen> myList=  new ArrayList<>();

    public void setList(ArrayList<modelAbsen> myList) {
        this.myList = myList;
    }

    public adpAbsen(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_absen, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelAbsen model = (modelAbsen) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView nim, nama, ket, tanggal, jam;
        LinearLayout bg, ly;
        ImageView gbr;

        ViewHolder(View view) {
            jam = view.findViewById(R.id.absen_jam);
            nim = view.findViewById(R.id.absen_nim);
            nama = view.findViewById(R.id.absen_nama);
            ket = view.findViewById(R.id.absen_status);
            tanggal = view.findViewById(R.id.absen_tgl);
            bg = view.findViewById(R.id.absen_bg);
            ly = view.findViewById(R.id.absen_header);
            gbr = view.findViewById(R.id.absen_gbr);
        }

        @SuppressLint("ResourceAsColor")
        void bind(modelAbsen model) {
            String mKet = model.getKet();
            jam.setText(model.getJam());
            nim.setText(model.getNim());
            nama.setText(model.getNama());
            ket.setText(mKet);
            tanggal.setText(model.getTgl());

            if (mKet.equals("Tidak hadir")) {
                ly.setBackgroundResource(R.drawable.bg_statgagal);
                bg.setBackgroundResource(R.color.merah);
                gbr.setBackgroundResource(R.drawable.status_belum);
            } else
            if (mKet.equals("Menunggu konfirmasi")) {
                ly.setBackgroundResource(R.drawable.bg_stattunggu);
                bg.setBackgroundResource(R.color.orange);
                gbr.setBackgroundResource(R.drawable.status_tunggu);
            } else
            if (mKet.equals("Hadir")) {
                ly.setBackgroundResource(R.drawable.bg_statberhasil);
                bg.setBackgroundResource(R.color.hijau);
                gbr.setBackgroundResource(R.drawable.status_selesai);
            }
        }
    }
}
