package com.absensi.labkom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelUser;

import java.util.ArrayList;

public class adpUsers extends BaseAdapter {
    private Context mContext;
    private ArrayList<modelUser> myList=  new ArrayList<>();

    public void setUsersList(ArrayList<modelUser> myList) {
        this.myList = myList;
    }

    public adpUsers(Context mContext) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_akun, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(itemView);
        modelUser model = (modelUser) getItem(i);
        viewHolder.bind(model);
        return itemView;
    }

    private class ViewHolder {
        private ImageView foto;
        TextView id, username, password, level;

        ViewHolder(View view) {
            id = view.findViewById(R.id.akun_id);
            username = view.findViewById(R.id.akun_username);
            password = view.findViewById(R.id.akun_password);
            level = view.findViewById(R.id.akun_level);
        }

        void bind(modelUser model) {
            id.setText(model.getId());
            username.setText(model.getUsername());
            password.setText(model.getPassword());
            level.setText(model.getLevel());
        }
    }
}
