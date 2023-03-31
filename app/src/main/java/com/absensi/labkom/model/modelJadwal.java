package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelJadwal implements Parcelable {
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

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSks() {
        return sks;
    }

    public void setSks(String sks) {
        this.sks = sks;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    private String kode;
    private String nama;
    private String nip;
    private String semester;
    private String sks;
    private String waktu;
    private String ruang;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    private String tanggal;

    public modelJadwal() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kode);
        dest.writeString(this.nama);
        dest.writeString(this.nip);
        dest.writeString(this.semester);
        dest.writeString(this.sks);
        dest.writeString(this.tanggal);
        dest.writeString(this.waktu);
        dest.writeString(this.ruang);
    }

    protected modelJadwal(Parcel in) {
        this.kode = in.readString();
        this.nama = in.readString();
        this.nip = in.readString();
        this.semester = in.readString();
        this.sks = in.readString();
        this.tanggal = in.readString();
        this.waktu = in.readString();
        this.ruang = in.readString();
    }

    public static final Creator<modelJadwal> CREATOR = new Creator<modelJadwal>() {
        @Override
        public modelJadwal createFromParcel(Parcel source) {
            return new modelJadwal(source);
        }

        @Override
        public modelJadwal[] newArray(int size) {
            return new modelJadwal[size];
        }
    };
}
