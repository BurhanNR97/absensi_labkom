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
import com.absensi.labkom.model.modelSlip;

import java.util.ArrayList;

public class adpSlip extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelSlip> myList=  new ArrayList<>();

    public void setList(ArrayList<modelSlip> myList) {
        this.myList = myList;
    }

    public adpSlip(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_slipselesai, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelSlip model = (modelSlip) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView kode, nim, nama, ket, tanggal;
        LinearLayout bg, ly;
        ImageView gbr;

        ViewHolder(View view) {
            kode = view.findViewById(R.id.listSlipKode);
            nim = view.findViewById(R.id.listSlipNIM);
            nama = view.findViewById(R.id.listSlipNama);
            ket = view.findViewById(R.id.listSlipStatus);
            tanggal = view.findViewById(R.id.listSlipTanggal);
            bg = view.findViewById(R.id.listSlipBg);
            ly = view.findViewById(R.id.listSlipHeader);
            gbr = view.findViewById(R.id.listSlipGbr);
        }

        @SuppressLint("ResourceAsColor")
        void bind(modelSlip model) {
            String mKet = model.getKet();
            kode.setText(model.getKode());
            nim.setText(model.getNim());
            nama.setText(model.getNama());
            ket.setText(mKet);
            tanggal.setText(model.getTanggal());

            if (mKet.equals("Ditolak")) {
                ly.setBackgroundResource(R.drawable.bg_statgagal);
                bg.setBackgroundResource(R.color.merah);
                gbr.setBackgroundResource(R.drawable.status_belum);
            } else
            if (mKet.equals("Belum bayar")) {
                ly.setBackgroundResource(R.drawable.bg_statgagal);
                bg.setBackgroundResource(R.color.merah);
                gbr.setBackgroundResource(R.drawable.status_belum);
            } else
            if (mKet.equals("Menunggu konfirmasi")) {
                ly.setBackgroundResource(R.drawable.bg_stattunggu);
                bg.setBackgroundResource(R.color.orange);
                gbr.setBackgroundResource(R.drawable.status_tunggu);
            } else
            if (mKet.equals("Lunas")) {
                ly.setBackgroundResource(R.drawable.bg_statberhasil);
                bg.setBackgroundResource(R.color.hijau);
                gbr.setBackgroundResource(R.drawable.status_selesai);
            }
        }
    }
}
