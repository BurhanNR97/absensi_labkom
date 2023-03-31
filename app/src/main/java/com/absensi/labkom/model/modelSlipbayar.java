package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelSlipbayar implements Parcelable {
    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    private String kode;
    private String nama;
    private String tanggal;

    public modelSlipbayar() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kode);
        dest.writeString(this.nama);
        dest.writeString(this.tanggal);
    }

    protected modelSlipbayar(Parcel in) {
        this.kode = in.readString();
        this.nama = in.readString();
        this.tanggal = in.readString();
    }

    public static final Creator<modelSlipbayar> CREATOR = new Creator<modelSlipbayar>() {
        @Override
        public modelSlipbayar createFromParcel(Parcel source) {
            return new modelSlipbayar(source);
        }

        @Override
        public modelSlipbayar[] newArray(int size) {
            return new modelSlipbayar[size];
        }
    };
}
