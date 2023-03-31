package com.absensi.labkom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelJadwal;
import com.absensi.labkom.model.modelKuliah;

import java.util.ArrayList;

public class adpKuliah extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelJadwal> myList=  new ArrayList<>();

    public void setKuliahList(ArrayList<modelJadwal> myList) {
        this.myList = myList;
    }

    public adpKuliah(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_jadwal, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelKuliah model = (modelKuliah) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView nmr, kode, dosen, matkul, ruang, jam, tanggal;
        int no = 1;

        ViewHolder(View view) {
            nmr = view.findViewById(R.id.listKuliahNo);
            kode = view.findViewById(R.id.listKuliahKode);
            dosen = view.findViewById(R.id.listKuliahDosen);
            matkul = view.findViewById(R.id.listKuliahMatkul);
            ruang = view.findViewById(R.id.listKuliahRuang);
            jam = view.findViewById(R.id.listKuliahJam);
            tanggal = view.findViewById(R.id.listKuliahTgl);
        }

        void bind(modelKuliah model) {
            nmr.setText(""+no++);
            kode.setText(model.getKode());
            dosen.setText(model.getDosen());
            matkul.setText(model.getMatkul());
            ruang.setText(model.getRuang());
            jam.setText(model.getJam());
            tanggal.setText(model.getTanggal());
        }
    }
}
