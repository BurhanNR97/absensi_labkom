package com.absensi.labkom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelJadwalDosen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adpJadwalDosen extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelJadwalDosen> myList=  new ArrayList<>();

    public void setJadwalList(ArrayList<modelJadwalDosen> myList) {
        this.myList = myList;
    }

    public adpJadwalDosen(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_jadwaldosen, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelJadwalDosen model = (modelJadwalDosen) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView kode, nama, waktu, tanggal;
        DatabaseReference db;

        ViewHolder(View view) {
            kode = view.findViewById(R.id.jd_kode);
            nama = view.findViewById(R.id.jd_matkul);
            tanggal = view.findViewById(R.id.jd_tanggal);
            waktu = view.findViewById(R.id.jd_waktu);
        }

        void bind(modelJadwalDosen model) {
            kode.setText(model.getKode());
            nama.setText(model.getNama());
            tanggal.setText(model.getTanggal());

            db = FirebaseDatabase.getInstance().getReference("jadwalkuliah").child(kode.getText().toString().trim());
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String jam = snapshot.child("waktu").getValue().toString().trim();
                    waktu.setText(jam);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
