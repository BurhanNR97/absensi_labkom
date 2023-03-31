package com.absensi.labkom.UI.tambahData;

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

public class TambahJadwal extends AppCompatActivity {
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


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jadwal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        dataJadwal();

        jamMulai = findViewById(R.id.Tambah_WaktuMulai);
        jamSelesai = findViewById(R.id.Tambah_WaktuSelesai);
        spNIP = findViewById(R.id.Tambah_NIPjadwal);
        btnSimpan = findViewById(R.id.btnSimpan_TambahJadwal);
        btnKeluar = findViewById(R.id.btnKeluarAddJadwal);
        txtJamMasuk = findViewById(R.id.Tambah_WaktuMulai);
        txtJamKeluar = findViewById(R.id.Tambah_WaktuSelesai);
        txtMatkul = findViewById(R.id.Tambah_NamaMatkul);
        txtRuang = findViewById(R.id.Tambah_Ruang);
        spSemester = findViewById(R.id.Tambah_Semester);
        txtSKS = findViewById(R.id.Tambah_SKS);
        btnTgl = findViewById(R.id.btnAddTglJadwal);
        txtTgl = findViewById(R.id.Tambah_TglKuliah);

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

                ArrayAdapter<String> adpSpin = new ArrayAdapter<String>(TambahJadwal.this,
                        android.R.layout.simple_spinner_item, iniDOsen);
                adpSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spNIP.setAdapter(adpSpin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spNIP.setSelection(0);

        btnTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int tahun = cal.get(Calendar.YEAR);
                int bulan = cal.get(Calendar.MONTH);
                int tanggal = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog pickerDialog;
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
                pickerDialog = new DatePickerDialog(TambahJadwal.this, AlertDialog.THEME_HOLO_LIGHT, onDateSetListener, tahun, bulan, tanggal);
                pickerDialog.show();
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        jamMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int jam = cal.get(Calendar.HOUR_OF_DAY);
                int menit = cal.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        char[] nH = String.valueOf(hour).toCharArray();
                        char[] nM = String.valueOf(minute).toCharArray();
                        String hh = "";
                        String mm = "";
                        if (nH.length == 1) {
                            hh = "0" + hour;
                        } else
                        if (nH.length == 2) {
                            hh = ""+hour;
                        }
                        if (nM.length == 1) {
                            mm = "0"+minute;
                        } else
                        if (nM.length == 2) {
                            mm = ""+minute;
                        }
                        jamm = hh+mm;
                        jamMulai.setText(hh + ":" +mm);
                    }
                };
                timePickerDialog = new TimePickerDialog(TambahJadwal.this, AlertDialog.THEME_HOLO_LIGHT, timeSetListener,
                        8, 00, true);
                timePickerDialog.show();
            }
        });

        jamSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int jam = cal.get(Calendar.HOUR_OF_DAY);
                int menit = cal.get(Calendar.MINUTE);
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
                timePickerDialog = new TimePickerDialog(TambahJadwal.this, AlertDialog.THEME_HOLO_LIGHT, timeSetListener,
                        9, 00, true);
                timePickerDialog.show();
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
        Intent intent = new Intent(TambahJadwal.this, DataJadwal.class);
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

                progressDialog = new ProgressDialog(TambahJadwal.this);
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
                            Toast.makeText(TambahJadwal.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            onBackPressed();
                        }
                    }

                }).addOnProgressListener(TambahJadwal.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
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
}