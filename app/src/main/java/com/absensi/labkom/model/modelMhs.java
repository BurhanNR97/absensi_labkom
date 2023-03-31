package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelMhs implements Parcelable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJK() {
        return jk;
    }

    public void setJK(String jk) {
        this.jk = jk;
    }

    public String getHP() {
        return hp;
    }

    public void setHP(String hp) {
        this.hp = hp;
    }

    private String id;
    private String nama;
    private String tgl_lahir;
    private String alamat;
    private String jk;
    private String hp;

    public modelMhs() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.tgl_lahir);
        dest.writeString(this.alamat);
        dest.writeString(this.jk);
        dest.writeString(this.hp);
    }

    protected modelMhs(Parcel in) {
        this.id = in.readString();
        this.nama = in.readString();
        this.tgl_lahir = in.readString();
        this.alamat = in.readString();
        this.jk = in.readString();
        this.hp = in.readString();
    }

    public static final Creator<modelMhs> CREATOR = new Creator<modelMhs>() {
        @Override
        public modelMhs createFromParcel(Parcel source) {
            return new modelMhs(source);
        }

        @Override
        public modelMhs[] newArray(int size) {
            return new modelMhs[size];
        }
    };
}
