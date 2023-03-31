package com.absensi.labkom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelJadwal;

import java.util.ArrayList;

public class adpJadwal extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelJadwal> myList=  new ArrayList<>();

    public void setJadwalList(ArrayList<modelJadwal> myList) {
        this.myList = myList;
    }

    public adpJadwal(Context mContext) {
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
        modelJadwal model = (modelJadwal) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView kode, nama, nip, semester, waktu, ruang, nmr, tanggal;
        int n = 0;

        ViewHolder(View view) {
            nmr = view.findViewById(R.id.listNoJadwal);
            kode = view.findViewById(R.id.listKodeJadwal);
            nama = view.findViewById(R.id.listMatkulJadwal);
            nip = view.findViewById(R.id.listNIPJadwal);
            semester = view.findViewById(R.id.listSemesterJadwal);
            waktu = view.findViewById(R.id.listWaktuJadwal);
            ruang = view.findViewById(R.id.listRuangJadwal);
            tanggal = view.findViewById(R.id.listTglJadwal);
        }

        void bind(modelJadwal model) {
            n++;
            nmr.setText(""+n);
            kode.setText(model.getKode());
            nama.setText(model.getNama());
            nip.setText(model.getNip());
            semester.setText(model.getSemester()+" ("+model.getSks()+")");
            waktu.setText(model.getWaktu());
            ruang.setText(model.getRuang());
            tanggal.setText(model.getTanggal());
        }
    }
}
