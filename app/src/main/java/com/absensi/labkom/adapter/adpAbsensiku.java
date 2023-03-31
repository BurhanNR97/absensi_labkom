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

import androidx.annotation.NonNull;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelAbsen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adpAbsensiku extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelAbsen> myList=  new ArrayList<>();

    public void setList(ArrayList<modelAbsen> myList) {
        this.myList = myList;
    }

    public adpAbsensiku(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_absensiku, viewGroup, false);
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
            jam = view.findViewById(R.id.absensiku_jam);
            nim = view.findViewById(R.id.absensiku_nim);
            nama = view.findViewById(R.id.absensiku_nama);
            ket = view.findViewById(R.id.absensiku_status);
            tanggal = view.findViewById(R.id.absensiku_tgl);
            bg = view.findViewById(R.id.absensiku_bg);
            ly = view.findViewById(R.id.absensiku_header);
            gbr = view.findViewById(R.id.absensiku_gbr);
        }

        @SuppressLint("ResourceAsColor")
        void bind(modelAbsen model) {
            String mKet = model.getKet();
            jam.setText(model.getJam());
            nim.setText(model.getKode());
            ket.setText(mKet);
            tanggal.setText(model.getTgl());

            DatabaseReference db = FirebaseDatabase.getInstance().getReference("jadwalkuliah");
            db.child(model.getKode()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String matkul = snapshot.child("nama").getValue().toString();
                    nama.setText(matkul);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
