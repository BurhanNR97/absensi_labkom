package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelKuliah implements Parcelable {

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

    public String getDosen() {
        return dosen;
    }

    public void setDosen(String dosen) {
        this.dosen = dosen;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    private String kode;
    private String nim;
    private String dosen;
    private String matkul;
    private String ruang;
    private String jam;
    private String tanggal;

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    private String ket;

    public modelKuliah() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kode);
        dest.writeString(this.nim);
        dest.writeString(this.dosen);
        dest.writeString(this.matkul);
        dest.writeString(this.ruang);
        dest.writeString(this.jam);
        dest.writeString(this.tanggal);
        dest.writeString(this.ket);
    }

    protected modelKuliah(Parcel in) {
        this.kode = in.readString();
        this.nim = in.readString();
        this.dosen = in.readString();
        this.matkul = in.readString();
        this.ruang = in.readString();
        this.jam = in.readString();
        this.tanggal = in.readString();
        this.ket = in.readString();
    }

    public static final Creator<modelKuliah> CREATOR = new Creator<modelKuliah>() {
        @Override
        public modelKuliah createFromParcel(Parcel source) {
            return new modelKuliah(source);
        }

        @Override
        public modelKuliah[] newArray(int size) {
            return new modelKuliah[size];
        }
    };
}
