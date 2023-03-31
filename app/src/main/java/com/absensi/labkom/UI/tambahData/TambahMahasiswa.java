package com.absensi.labkom.UI.tambahData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absensi.labkom.R;
import com.absensi.labkom.UI.DataMahasiswa;
import com.absensi.labkom.model.modelMhs;
import com.absensi.labkom.model.modelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class TambahMahasiswa extends AppCompatActivity implements View.OnClickListener {
    final int kodeGallery = 100, kodeKamera = 99;
    Uri imageUri;
    ImageView tFoto;
    EditText tNim, tNama, tAlamat, tHp, tTgl;
    Spinner tJk;
    modelMhs mMhs;
    Button simpan, tanggall;
    int tahun, tanggal, bulan;
    modelUser mUser;
    ArrayList<modelUser> listUser = new ArrayList<>();
    ArrayList<modelMhs> listMhs = new ArrayList<>();
    DatabaseReference dbUser;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TambahMahasiswa.this, DataMahasiswa.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_mahasiswa);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        FirebaseApp.initializeApp(this);
        dbUser = FirebaseDatabase.getInstance().getReference("users");
        tNim = findViewById(R.id.Tambah_NIM);
        tNama = findViewById(R.id.Tambah_NamaMhs);
        tAlamat = findViewById(R.id.Tambah_AlamatMhs);
        tHp = findViewById(R.id.Tambah_HPmhs);
        tJk = findViewById(R.id.Tambah_JKmhs);
        tFoto = findViewById(R.id.Tambah_FotoMhs);
        tTgl = findViewById(R.id.Tambah_TglMhs);
        simpan = findViewById(R.id.btnSimpan_TambahMhs);
        tanggall = findViewById(R.id.btnAddTglMhs);
        mUser  = new modelUser();
        mMhs = new modelMhs();

        Button keluar = findViewById(R.id.btnKeluarAddMhs);
        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("mahasiswa");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMhs.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelMhs model = dataSnapshot.getValue(modelMhs.class);
                    listMhs.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TambahMahasiswa.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelUser model = ds.getValue(modelUser.class);
                    listUser.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, kodeGallery);
            }
        });

        tanggall.setOnClickListener(this);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nNim = tNim.getText().toString();
                TextInputLayout txtNim = findViewById(R.id.textInputNIM);

                if (tNim.getText().toString().isEmpty()) {
                    tNim.requestFocus();
                    tNim.setError("Input No Identitas");
                } else if (nNim.length() < 11) {
                    txtNim.setError("Minimal 11 angka");
                    tNim.requestFocus();
                } else
                if (tNama.getText().toString().isEmpty()) {
                    tNama.requestFocus();
                    tNama.setError("Input Nama Dosen");
                } else
                if (tJk.getSelectedItemPosition() == 0) {
                    Toast.makeText(TambahMahasiswa.this, "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
                } else
                if (tTgl.getText().toString().isEmpty()) {
                    tTgl.setError("Input tanggal lahir");
                } else
                if (tHp.getText().toString().isEmpty()) {
                    tHp.requestFocus();
                    tHp.setError("Input Telepon Dosen");
                } else
                if (tAlamat.getText().toString().isEmpty()) {
                    tAlamat.requestFocus();
                    tAlamat.setError("Input alamat Dosen");
                } else {
                    simpan.setClickable(false);
                    simpan.setEnabled(false);
                    if (listMhs.size() == 0) {
                        upload();
                    } else {
                        for (int i = 0; i < listMhs.size(); i++) {
                            if (listMhs.get(i).equals(tNim.getText().toString().trim())) {
                                tNim.requestFocus();
                                tNim.setError("NIM sudah digunakan");
                                tNim.selectAll();
                            } else {
                                upload();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Calendar cal = Calendar.getInstance();
        tahun = cal.get(Calendar.YEAR);
        bulan = cal.get(Calendar.MONTH);
        tanggal = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog;
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int thn, int bln, int tgl) {
                bln = bln + 1;
                String date = makeDateString(tgl, bln, thn);
                tTgl.setText(date);
            }
        };
        pickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, onDateSetListener, tahun, bulan, tanggal);
        pickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == kodeGallery && resultCode == RESULT_OK) {
            imageUri = data.getData();
            tFoto.setImageURI(imageUri);
        }
    }

    private void upload() {
        StorageReference storage = FirebaseStorage.getInstance().getReference("mahasiswa");
        String nip = tNim.getText().toString();
        tFoto.setDrawingCacheEnabled(true);
        tFoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) tFoto.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //kompres bmp -> jpg dgn kualitas 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        final String pathImage = nip+".jpg";
        storage.child(pathImage).putBytes(bytes);

        progressDialog = new ProgressDialog(TambahMahasiswa.this);
        progressDialog.setMessage("Sedang menyimpan data");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        UploadTask ut = storage.child(pathImage).putBytes(bytes);
        ut.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    simpan();
                    progressDialog.dismiss();
                }
            }

        }).addOnProgressListener(TambahMahasiswa.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
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

    private void simpan() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("mahasiswa");
        mMhs =  new modelMhs();
        String nim = tNim.getText().toString();
        String nama = tNama.getText().toString();
        String alamat = tAlamat.getText().toString();
        String hp = tHp.getText().toString();
        String jk = tJk.getSelectedItem().toString();
        String tgl = tTgl.getText().toString();

        mMhs.setId(nim);
        mMhs.setNama(nama);
        mMhs.setJK(jk);
        mMhs.setTgl_lahir(tgl);
        mMhs.setAlamat(alamat);
        mMhs.setHP(hp);
        db.child(nim).setValue(mMhs).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(TambahMahasiswa.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        int n = tTgl.getText().length();
        String tglku = tTgl.getText().toString().substring(n-4,n);
        simpanUser(nim, tglku);
        Intent intent = new Intent(TambahMahasiswa.this, DataMahasiswa.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    private void simpanUser (String username, String password) {
        String id = cekID();
        mUser.setId(id);
        mUser.setUsername(username);
        mUser.setPassword(password);
        mUser.setLevel("mahasiswa");
        dbUser.child(id).setValue(mUser);
    }

    private String cekID () {
        String id = "";
        if (listUser.size() > 0) {
            int a = Integer.parseInt(listUser.get(listUser.size()-1).getId().substring(3));
            int b = a + 1;
            char[] c = String.valueOf(b).toCharArray();
            if (c.length == 1) {
                id = "ID-0000"+b;
            } else
            if (c.length == 2) {
                id = "ID-000"+b;
            } else
            if (c.length == 3) {
                id = "ID-00"+b;
            } else
            if (c.length == 4) {
                id = "ID-0"+b;
            } else
            if (c.length == 5) {
                id = "ID-"+b;
            }
        } else {
            id = "ID-00001";
        }
        return id;
    }

    public void keluarTambahDosen(View v) {
        Intent intent = new Intent(TambahMahasiswa.this, DataMahasiswa.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        finish();
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
}