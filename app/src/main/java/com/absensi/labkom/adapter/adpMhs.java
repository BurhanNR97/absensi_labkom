package com.absensi.labkom.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelMhs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class adpMhs extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelMhs> myList=  new ArrayList<>();

    public void setMhsList(ArrayList<modelMhs> myList) {
        this.myList = myList;
    }

    public adpMhs(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_mahasiswa, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelMhs model = (modelMhs) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {
        private ImageView foto;
        TextView id, nama, alamat, hp, tgl_lahir, jk;

        ViewHolder(View view) {
            foto = view.findViewById(R.id.listFotoMhs);
            id = view.findViewById(R.id.listIDmhs);
            nama = view.findViewById(R.id.listNamaMhs);
            jk = view.findViewById(R.id.listJKmhs);
            tgl_lahir = view.findViewById(R.id.listTglMhs);
            hp = view.findViewById(R.id.listHPmhs);
            alamat = view.findViewById(R.id.listAlamatMhs);
        }

        void bind(modelMhs model) {
            id.setText(model.getId());
            nama.setText(model.getNama());
            jk.setText(model.getJK());
            tgl_lahir.setText(model.getTgl_lahir());
            hp.setText(model.getHP());
            alamat.setText(model.getAlamat());

            String path = model.getId()+".jpg";
            StorageReference db = FirebaseStorage.getInstance().getReference("mahasiswa");
            db.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String pathName = uri.toString();
                    Glide.with(mContext).load(pathName).into(foto);
                }
            });
        }
    }
}
