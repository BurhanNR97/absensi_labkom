package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelAbsen implements Parcelable {
    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    private String kode;
    private String nim;
    private String nama;
    private String ket;

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    private String tgl;
    private String jam;

    public modelAbsen() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kode);
        dest.writeString(this.nim);
        dest.writeString(this.nama);
        dest.writeString(this.ket);
        dest.writeString(this.tgl);
        dest.writeString(this.jam);
    }

    protected modelAbsen(Parcel in) {
        this.kode = in.readString();
        this.nim = in.readString();
        this.nama = in.readString();
        this.ket = in.readString();
        this.tgl = in.readString();
        this.jam = in.readString();
    }

    public static final Creator<modelAbsen> CREATOR = new Creator<modelAbsen>() {
        @Override
        public modelAbsen createFromParcel(Parcel source) {
            return new modelAbsen(source);
        }

        @Override
        public modelAbsen[] newArray(int size) {
            return new modelAbsen[size];
        }
    };
}
