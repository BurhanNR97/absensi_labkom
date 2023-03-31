package com.absensi.labkom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelSlipbayar;

import java.util.ArrayList;

public class adpSlipawal extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelSlipbayar> myList=  new ArrayList<>();

    public void setList(ArrayList<modelSlipbayar> myList) {
        this.myList = myList;
    }

    public adpSlipawal(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_slip, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelSlipbayar model = (modelSlipbayar) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {

        TextView kode, nama, ket, tanggal;

        ViewHolder(View view) {
            kode = view.findViewById(R.id.slipkode);
            nama = view.findViewById(R.id.slipmatkul);
            tanggal = view.findViewById(R.id.sliptgl);
        }

        @SuppressLint("ResourceAsColor")
        void bind(modelSlipbayar model) {
            kode.setText(model.getKode());
            nama.setText(model.getNama());
            tanggal.setText(model.getTanggal());
        }
    }
}
