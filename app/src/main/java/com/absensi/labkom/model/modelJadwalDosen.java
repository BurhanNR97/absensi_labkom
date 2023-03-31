package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelJadwalDosen implements Parcelable {
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

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    private String kode;
    private String nama;
    private String waktu;
    private String tanggal;

    public modelJadwalDosen() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kode);
        dest.writeString(this.nama);
        dest.writeString(this.tanggal);
        dest.writeString(this.waktu);
    }

    protected modelJadwalDosen(Parcel in) {
        this.kode = in.readString();
        this.nama = in.readString();
        this.tanggal = in.readString();
        this.waktu = in.readString();
    }

    public static final Creator<modelJadwalDosen> CREATOR = new Creator<modelJadwalDosen>() {
        @Override
        public modelJadwalDosen createFromParcel(Parcel source) {
            return new modelJadwalDosen(source);
        }

        @Override
        public modelJadwalDosen[] newArray(int size) {
            return new modelJadwalDosen[size];
        }
    };
}
