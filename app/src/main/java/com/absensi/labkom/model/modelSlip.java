package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelSlip implements Parcelable {
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    private String kode;
    private String nim;
    private String nama;
    private String ket;
    private String tanggal;

    public modelSlip() {}

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
        dest.writeString(this.tanggal);
    }

    protected modelSlip(Parcel in) {
        this.kode = in.readString();
        this.nim = in.readString();
        this.nama = in.readString();
        this.ket = in.readString();
        this.tanggal = in.readString();
    }

    public static final Creator<modelSlip> CREATOR = new Creator<modelSlip>() {
        @Override
        public modelSlip createFromParcel(Parcel source) {
            return new modelSlip(source);
        }

        @Override
        public modelSlip[] newArray(int size) {
            return new modelSlip[size];
        }
    };
}
