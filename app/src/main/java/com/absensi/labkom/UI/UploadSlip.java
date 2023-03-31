package com.absensi.labkom.UI;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelAbsen;
import com.absensi.labkom.model.modelSlip;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadSlip extends AppCompatActivity {
    ImageView gambar;
    String nim, kode, tgl, nama;
    Button ya, tidak;
    SimpleDateFormat sdf;
    final int kodeGallery = 100, kodeKamera = 99;
    Uri imageUri = null;
    ProgressDialog proses;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_slip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }

        gambar = findViewById(R.id.ImgUploadSlip);
        ya = findViewById(R.id.btnYaa);
        tidak = findViewById(R.id.btnBatall);

        String prefnim = getIntent().getStringExtra("id");
        String prefkode = getIntent().getStringExtra("kode");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("slip").child(prefkode);
        db.child(prefnim).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nim = snapshot.child("nim").getValue().toString();
                kode = snapshot.child("kode").getValue().toString();
                nama = snapshot.child("nama").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, kodeGallery);
            }
        });

        tidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadSlip.this, SlipBayar.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("nama", getIntent().getStringExtra("nama"));
                intent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(intent);
                finish();
            }
        });

        ya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri == null) {
                    Toast.makeText(UploadSlip.this, "Silahkan pilih gambar", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("slip").child(kode);
                    sdf = new SimpleDateFormat("d MMMM yyyy", Locale.US);
                    Date dt = new Date();
                    String tgl = sdf.format(dt);
                    modelSlip model = new modelSlip();
                    model.setKode(kode);
                    model.setNim(nim);
                    model.setNama(nama);
                    model.setKet("Menunggu konfirmasi");
                    model.setTanggal(tgl);
                    ref.child(nim).setValue(model);

                    DatabaseReference refe = FirebaseDatabase.getInstance().getReference("slipku").child(nim);
                    refe.child(kode).setValue(model);

                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("arslip")
                            .child(getIntent().getStringExtra("nip"));
                    dr.child(kode).child(nim).setValue(model);

                    upload();
                    addAbsen(kode, nim);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == kodeGallery && resultCode == RESULT_OK) {
            imageUri = data.getData();
            gambar.setImageURI(imageUri);
        }
    }

    private void addAbsen(String kode, String nim) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mahasiswa").child(nim);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nama = snapshot.child("nama").getValue().toString().trim();
                if (!nama.isEmpty()) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("absen").child(kode);
                    modelAbsen model = new modelAbsen();
                    model.setKode(kode);
                    model.setNama(nama);
                    model.setNim(nim);
                    model.setKet("Tidak hadir");
                    db.child(nim).setValue(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void upload() {
        StorageReference storage = FirebaseStorage.getInstance().getReference("slip").child(kode);
        gambar.setDrawingCacheEnabled(true);
        gambar.buildDrawingCache();
        bitmap = ((BitmapDrawable) gambar.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //kompres bmp -> jpg dgn kualitas 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        final String pathImage = nim+".jpg";
        storage.child(pathImage).putBytes(bytes);

        proses = new ProgressDialog(UploadSlip.this);
        proses.setMessage("Sedang mengupload gambar");
        proses.setCancelable(false);
        proses.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        proses.show();

        UploadTask ut = storage.child(pathImage).putBytes(bytes);
        ut.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    proses.dismiss();
                    Toast.makeText(UploadSlip.this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadSlip.this, SlipBayar.class);
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    intent.putExtra("nama", getIntent().getStringExtra("nama"));
                    intent.putExtra("no", getIntent().getStringExtra("no"));
                    startActivity(intent);
                    finish();
                }
            }

        }).addOnProgressListener(UploadSlip.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double jml = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                Runnable cekProses = new Runnable() {
                    @Override
                    public void run() {
                        proses.setProgress((int) jml);
                    }
                };

                Thread jalan = new Thread(cekProses);
                jalan.start();
            }
        });
    }
}