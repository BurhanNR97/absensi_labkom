package com.absensi.labkom.UI.ubahData;

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
import com.absensi.labkom.UI.DataDosen;
import com.absensi.labkom.UI.DataMahasiswa;
import com.absensi.labkom.model.modelMhs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class UbahMahasiswa extends AppCompatActivity implements View.OnClickListener {
    final int kodeGallery = 100, kodeKamera = 99;
    Uri imageUri;
    ImageView tFoto;
    EditText tNim, tNama, tAlamat, tHp, tTgl;
    Spinner tJk;
    modelMhs mMhs;
    Button simpan, keluar, tanggall;
    int tahun, tanggal, bulan;
    ArrayList<modelMhs> myList = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_mahasiswa);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        tNim = findViewById(R.id.Ubah_NIM);
        tNama = findViewById(R.id.Ubah_NamaMhs);
        tAlamat = findViewById(R.id.Ubah_AlamatMhs);
        tHp = findViewById(R.id.Ubah_HPmhs);
        tJk = findViewById(R.id.Ubah_JKmhs);
        tFoto = findViewById(R.id.Ubah_FotoMhs);
        tTgl = findViewById(R.id.Ubah_TglMhs);
        simpan = findViewById(R.id.btnSimpan_EditMhs);
        keluar = findViewById(R.id.btnKeluarEditMhs);
        tanggall = findViewById(R.id.btnEditTglMhs);
        mMhs =  new modelMhs();

        tFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, kodeGallery);
            }
        });

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tanggall.setOnClickListener(this);
        ambilDaftar();
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("mahasiswa");
                if (tNama.getText().toString().isEmpty()) {
                    tNama.requestFocus();
                    tNama.setError("Input Nama Mahasiswa");
                } else
                if (tJk.getSelectedItemPosition() == 0) {
                    Toast.makeText(UbahMahasiswa.this, "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
                } else
                if (tTgl.getText().toString().isEmpty()) {
                    tTgl.setError("Input tanggal lahir");
                } else
                if (tHp.getText().toString().isEmpty()) {
                    tHp.requestFocus();
                    tHp.setError("Input Telepon Dosen");
                } else {
                    upload();
                    Intent intent = new Intent(UbahMahasiswa.this, DataMahasiswa.class);
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    intent.putExtra("nip", getIntent().getStringExtra("nip"));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UbahMahasiswa.this, DataMahasiswa.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    private void ambilDaftar() {
        tNim.setText(getIntent().getStringExtra("nim"));
        tNama.setText(getIntent().getStringExtra("nama"));
        tAlamat.setText(getIntent().getStringExtra("alamat"));
        tHp.setText(getIntent().getStringExtra("hp"));
        tTgl.setText(getIntent().getStringExtra("tgl"));
        tampil();
    }

    private void tampil() {
        String jk = getIntent().getStringExtra("jk");
        if (jk.equals("Laki-Laki")) {
            tJk.setSelection(1);
        } else
        if (jk.equals("Perempuan")) {
            tJk.setSelection(2);
        }
        ambilFoto(tNim.getText().toString().trim());
    }

    private void ambilFoto(String a) {
        String path = a + ".jpg";
        StorageReference reference = FirebaseStorage.getInstance().getReference("mahasiswa");
        reference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String pathName = uri.toString();
                Glide.with(UbahMahasiswa.this).load(pathName).into(tFoto);
            }
        });
    }

    private String makeDateString(int tgl, int bln, int thn) {
        return tgl+" "+getMonthFormat(bln)+" "+thn;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == kodeGallery && resultCode == RESULT_OK) {
            imageUri = data.getData();
            tFoto.setImageURI(imageUri);
        }
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

        progressDialog = new ProgressDialog(UbahMahasiswa.this);
        progressDialog.setMessage("Sedang menyimpan data");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        UploadTask ut = storage.child(pathImage).putBytes(bytes);
        ut.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UbahMahasiswa.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                    simpan();
                    progressDialog.dismiss();
                    Intent intent = new Intent(UbahMahasiswa.this, DataDosen.class);
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnProgressListener(UbahMahasiswa.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
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

        db.child(nim).setValue(mMhs);
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