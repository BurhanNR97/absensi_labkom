package com.absensi.labkom.UI.ubahData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absensi.labkom.R;
import com.absensi.labkom.UI.DataJadwal;
import com.absensi.labkom.model.modelDosen;
import com.absensi.labkom.model.modelJadwal;
import com.absensi.labkom.model.modelJadwalDosen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UbahJadwal extends AppCompatActivity {
    ProgressDialog progressDialog;
    EditText jamMulai, jamSelesai, txtMatkul, txtSKS, txtJamMasuk, txtJamKeluar,txtRuang, txtTgl;
    ArrayList<modelJadwal> listJadwal = new ArrayList<>();
    ArrayList<modelDosen> listDosen = new ArrayList<>();
    Spinner spSemester, spNIP;
    List<String> listNIP = new ArrayList<>();
    List<String> mDosen = new ArrayList<>();
    Button btnSimpan, btnKeluar, btnTgl;
    String tanggl = "";
    String jamm = "";
    ArrayList<String> iniNmDosen, iniNIPdosen, iniDOsen;
    int[] tanggal;
    int[] jam;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_jadwal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        dataJadwal();

        jamMulai = findViewById(R.id.Edit_WaktuMulai);
        jamSelesai = findViewById(R.id.Edit_WaktuSelesai);
        spNIP = findViewById(R.id.Edit_NIPjadwal);
        btnSimpan = findViewById(R.id.btnSimpan_UbahJadwal);
        btnKeluar = findViewById(R.id.btnKeluarEditJadwal);
        txtJamMasuk = findViewById(R.id.Edit_WaktuMulai);
        txtJamKeluar = findViewById(R.id.Edit_WaktuSelesai);
        txtMatkul = findViewById(R.id.Edit_NamaMatkul);
        txtRuang = findViewById(R.id.Edit_Ruang);
        spSemester = findViewById(R.id.Edit_Semester);
        txtSKS = findViewById(R.id.Edit_SKS);
        btnTgl = findViewById(R.id.btnEditTglJadwal);
        txtTgl = findViewById(R.id.Edit_TglKuliah);

        iniDOsen = new ArrayList<>();
        iniNIPdosen = new ArrayList<>();
        iniNmDosen = new ArrayList<>();
        DatabaseReference dbDoss = FirebaseDatabase.getInstance().getReference("dosen");
        dbDoss.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                iniDOsen.clear();
                iniNIPdosen.clear();
                iniNmDosen.clear();

                iniDOsen.add("Dosen Pengajar");
                iniNmDosen.add("Nama Dosen");
                iniNIPdosen.add("NIP");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelDosen model = ds.getValue(modelDosen.class);
                    iniDOsen.add(model.getId() +" - "+ model.getNama());
                    iniNmDosen.add(model.getNama());
                    iniNIPdosen.add(model.getId());
                }

                ArrayAdapter<String> adpSpin = new ArrayAdapter<String>(UbahJadwal.this,
                        android.R.layout.simple_spinner_item, iniDOsen);
                adpSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spNIP.setAdapter(adpSpin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploads();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UbahJadwal.this, DataJadwal.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    private void dataJadwal() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("jadwalkuliah");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listJadwal.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelJadwal model = dataSnapshot.getValue(modelJadwal.class);
                    listJadwal.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilData();
    }

    private void uploads() {
        String matkul = txtMatkul.getText().toString().trim();
        char[] smt = spSemester.getSelectedItem().toString().toCharArray();
        String semester = spSemester.getSelectedItem().toString();
        String sks = txtSKS.getText().toString().trim();
        String jam1 = txtJamMasuk.getText().toString();
        String jam2 = txtJamKeluar.getText().toString();
        String ruangan = txtRuang.getText().toString();
        String tgl = txtTgl.getText().toString();
        String teks = tanggl+jamm;

        if (spNIP.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Pilih dosen pengajar", Toast.LENGTH_SHORT).show();
        }
        if (matkul.isEmpty()) {
            txtMatkul.requestFocus();
            txtMatkul.setError("Input mata kuliah");
        } else
        if (spSemester.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Pilih semester", Toast.LENGTH_SHORT).show();
        } else
        if (sks.isEmpty()) {
            txtSKS.requestFocus();
            txtSKS.setError("Input SKS");
        } else
        if (jam1.isEmpty()) {
            txtJamMasuk.setError("Input jam masuk");
        } else
        if (jam2.isEmpty()) {
            txtJamKeluar.setError("Input jam keluar");
        } else
        if (ruangan.isEmpty()) {
            txtRuang.requestFocus();
            txtRuang.setError("Input ruang kuliah");
        } else {
            if (cekData() == true) {
                Toast.makeText(this, "Jadwal pada waktu tersebut sudah ada", Toast.LENGTH_SHORT).show();
            } else
            if (cekData() == false) {
                int posisi = spNIP.getSelectedItemPosition();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("jadwalkuliah");
                modelJadwal model = new modelJadwal();
                model.setKode(teks);
                model.setNama(matkul);
                model.setNip(iniNIPdosen.get(posisi));
                model.setRuang(ruangan);
                model.setSemester(semester);
                model.setTanggal(tgl);
                model.setWaktu(jam1+" ~ "+jam2);
                model.setSks(sks);

                DatabaseReference dbku = FirebaseDatabase.getInstance().getReference("kelas");
                DatabaseReference dbrefku = dbku.child(iniNIPdosen.get(posisi));
                modelJadwalDosen modelku = new modelJadwalDosen();
                modelku.setKode(teks);
                modelku.setNama(matkul);
                modelku.setTanggal(tgl);

                MultiFormatWriter multi = new MultiFormatWriter();
                Bitmap bmp;
                try {
                    BitMatrix matrix = multi.encode(teks, BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    bmp = encoder.createBitmap(matrix);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

                StorageReference storage = FirebaseStorage.getInstance().getReference("QRcode");
                Bitmap bitmap = bmp;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                //kompres bmp -> jpg dgn kualitas 100%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                final String pathImage = teks+".jpg";
                storage.child(pathImage).putBytes(bytes);

                progressDialog = new ProgressDialog(UbahJadwal.this);
                progressDialog.setMessage("Sedang menyimpan data");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                UploadTask ut = storage.child(pathImage).putBytes(bytes);
                ut.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            db.child(teks).setValue(model);
                            dbrefku.child(teks).setValue(modelku);
                            Toast.makeText(UbahJadwal.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            onBackPressed();
                        }
                    }

                }).addOnProgressListener(UbahJadwal.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double proses = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Runnable cekProses = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setProgress((int) proses);
                            }
                        };

                        Thread jalan = new Thread(cekProses);
                        jalan.start();
                    }
                });
            }
        }


    }

    private String makeDateString(int tgl, int bln, int thn) {
        return tgl+" "+getMonthFormat(bln)+" "+thn;
    }

    private String getMonthFormat(int i) {
        switch (i) {
            case 1 :
                return "Januari";
            case 2 :
                return "Februari";
            case 3 :
                return "Maret";
            case 4 :
                return "April";
            case 5 :
                return "Mei";
            case 6 :
                return "Juni";
            case 7 :
                return "Juli";
            case 8 :
                return "Agustus";
            case 9 :
                return "September";
            case 10 :
                return "Oktober";
            case 11 :
                return "Nopember";
            case 12 :
                return "Desember";
        }
        return "Januari";
    }

    private boolean cekData() {
        boolean n = false;
        for (int i=0; i<listJadwal.size(); i++) {
            if (listJadwal.get(i).getKode().equals(tanggl+jamm)) {
                n = true;
            }
        }
        return n;
    }

    private void tampilData() {
        String kode = getIntent().getStringExtra("kode");
        String nama = getIntent().getStringExtra("nama");
        String nip = getIntent().getStringExtra("nip");
        String ruang = getIntent().getStringExtra("ruang");
        String semester = getIntent().getStringExtra("semester");
        String sks = getIntent().getStringExtra("sks");
        String tanggal = getIntent().getStringExtra("tgl");
        String waktu = getIntent().getStringExtra("waktu");

        txtMatkul.setText(nama);
        txtTgl.setText(tanggal);
        txtSKS.setText(sks);
        txtRuang.setText(ruang);
        smt(semester);
        spNIP.setSelection(listNIP.indexOf(nip));

        String[] jam = waktu.split(" ~ ");
        String[] tgl = txtTgl.getText().toString().trim().split(" ");
        txtJamMasuk.setText(jam[0]);
        txtJamKeluar.setText(jam[1]);

        pilihTanggal();

        String[] mulai = jam[0].split(":");
        String[] akhir = jam[1].split(":");
        pilihJamMulai(Integer.parseInt(mulai[0]), Integer.parseInt(mulai[1]));
        pilihJamSelesai(Integer.parseInt(akhir[0]), Integer.parseInt(akhir[1]));
    }

    private void pilihTanggal () {
        Calendar cal = Calendar.getInstance();
        int tahun = cal.get(Calendar.YEAR);
        int bulan = cal.get(Calendar.MONTH);
        int tanggal = cal.get(Calendar.DAY_OF_MONTH);
        btnTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog pickerDialog1;
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int thn, int bln, int tgl) {
                        bln = bln + 1;
                        String date = makeDateString(tgl, bln, thn);
                        txtTgl.setText(date);
                        String yyyy = String.valueOf(thn).substring(2,4);
                        char[] dddd = String.valueOf(tgl).toCharArray();
                        String dd = "";
                        if (dddd.length == 1) {
                            dd = "0"+tgl;
                        } else {
                            dd = ""+tgl;
                        }
                        char[] m = String.valueOf(bln).toCharArray();
                        String mm = "";
                        if (m.length == 1) {
                            mm = "0"+bln;
                        } else {
                            mm = ""+bln;
                        }
                        tanggl = yyyy+mm+dd;
                    }
                };
                pickerDialog1 = new DatePickerDialog(UbahJadwal.this, AlertDialog.THEME_HOLO_LIGHT, onDateSetListener, tahun, bulan, tanggal);
                pickerDialog1.show();
            }
        });
    }

    private void pilihJamSelesai (int h, int m) {
        jamSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog;
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        char[] nH = String.valueOf(hour).toCharArray();
                        char[] nM = String.valueOf(minute).toCharArray();
                        String hh = "";
                        if (nH.length == 1) {
                            hh = "0" + hour + ":";
                        } else
                        if (nH.length == 2) {
                            hh = hour + ":";
                        }
                        if (nM.length == 1) {
                            jamSelesai.setText(hh+"0"+minute);
                        } else
                        if (nM.length == 2) {
                            jamSelesai.setText(hh+minute);
                        }
                    }
                };
                timePickerDialog = new TimePickerDialog(UbahJadwal.this, AlertDialog.THEME_HOLO_LIGHT, timeSetListener,
                        h, m, true);
                timePickerDialog.show();
            }
        });
    }

    private void pilihJamMulai (int h, int m) {
        jamMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog;
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        char[] nH = String.valueOf(hour).toCharArray();
                        char[] nM = String.valueOf(minute).toCharArray();
                        String hh = "";
                        if (nH.length == 1) {
                            hh = "0" + hour + ":";
                        } else
                        if (nH.length == 2) {
                            hh = hour + ":";
                        }
                        if (nM.length == 1) {
                            jamMulai.setText(hh+"0"+minute);
                        } else
                        if (nM.length == 2) {
                            jamMulai.setText(hh+minute);
                        }
                    }
                };
                timePickerDialog = new TimePickerDialog(UbahJadwal.this, AlertDialog.THEME_HOLO_LIGHT, timeSetListener,
                        h, m, true);
                timePickerDialog.show();
            }
        });
    }

    private int nBulan(String bulan) {
        switch (bulan) {
            case "Januari" : return 1;
            case "Februari" : return 2;
            case "Maret" : return 3;
            case "April" : return 4;
            case "Mei" : return 5;
            case "Juni" : return 6;
            case "Juli" : return 7;
            case "Agustus" : return 8;
            case "September" : return 9;
            case "Oktober" : return 10;
            case "Nopember" : return 11;
            case "Desember" : return 12;
        }
        return 0;
    }

    private void smt(String semester) {
        switch (semester) {
            case "I" :
                spSemester.setSelection(1);
                break;
            case "II" :
                spSemester.setSelection(2);
                break;
            case "III" :
                spSemester.setSelection(3);
                break;
            case "IV" :
                spSemester.setSelection(4);
                break;
            case "V" :
                spSemester.setSelection(5);
                break;
            case "VI" :
                spSemester.setSelection(6);
                break;
            case "VII" :
                spSemester.setSelection(7);
                break;
            case "VIII" :
                spSemester.setSelection(8);
                break;
        }
    }
}