package com.absensi.labkom.model;

public class modelRekapSlip {

    public modelRekapSlip(int no, String nim, String nama, String ket) {
        this.no = no;
        this.nim = nim;
        this.nama = nama;
        this.ket = ket;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
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

    private int no;
    private String nim;
    private String nama;
    private String ket;
}
