package com.absensi.labkom.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adapterRekapAbsen;
import com.absensi.labkom.model.modelAbsen;
import com.absensi.labkom.model.modelRekapAbsen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class detailAbsensi extends AppCompatActivity {
    ArrayList<modelAbsen> list = new ArrayList<>();
    TextView txtKode, txtNama, txtMatkul;
    private static int REQ_MEDIA = 10;
    String inKode, inMatkul, inDosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_absensi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        inKode = getIntent().getStringExtra("id");

        Toolbar toolbar = findViewById(R.id.toolbar_detailabsen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Rekap Absensi");

        txtKode = findViewById(R.id.rekap_kode);
        txtNama = findViewById(R.id.rekap_dosen);
        txtMatkul = findViewById(R.id.rekap_nama);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(detailAbsensi.this, RekapAbsen.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_absensi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.print_absen) {
            ActivityCompat.requestPermissions(detailAbsensi.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            PdfDocument document = new PdfDocument();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(620, 900, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setUnderlineText(true);
            paint.setTextSize(16.0f);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText("REKAP ABSENSI MAHASISWA", pageInfo.getPageWidth()/2, 30, paint);

            Paint paint1 = new Paint();
            Canvas canvas1 = page.getCanvas();
            paint1.setTextAlign(Paint.Align.LEFT);
            paint1.setTextSize(14.0f);
            canvas1.drawText("Kode", 30, 80, paint1);
            canvas1.drawText(":", 120, 80, paint1);
            canvas1.drawText(txtKode.getText().toString(), 130, 80, paint1);

            canvas1.drawText("Mata Kuliah", 30, 100, paint1);
            canvas1.drawText(":", 120, 100, paint1);
            canvas1.drawText(txtMatkul.getText().toString(), 130, 100, paint1);

            canvas1.drawText("Dosen", 30, 120, paint1);
            canvas1.drawText(":", 120, 120, paint1);
            canvas1.drawText(txtNama.getText().toString(), 130, 120, paint1);

            Paint paint2 = new Paint();
            Canvas canvas2 = page.getCanvas();
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setStrokeWidth(1);
            canvas2.drawRect(30, 160, pageInfo.getPageWidth()-30, 195, paint2);

            Paint paint3 = new Paint();
            Canvas canvas3 = page.getCanvas();
            canvas3.drawText("NO", 40, 180, paint3);
            canvas3.drawText("NIM", 110, 180, paint3);
            canvas3.drawText("NAMA MAHASISWA", 250, 180, paint3);
            canvas3.drawText("KETERANGAN", 450, 180, paint3);

            int y = 215;
            for (int m=0; m<list.size(); m++) {
                canvas3.drawText(String.valueOf(m+1), 40, y, paint3);
                canvas3.drawText(list.get(m).getNim(), 110, y, paint3);
                canvas3.drawText(list.get(m).getNama(), 250, y, paint3);
                canvas3.drawText(list.get(m).getKet(), 450, y, paint3);
                y = y + 20;
            }

            document.finishPage(page);
            String namaFIle = "RekapAbsensi-" + txtKode.getText().toString().trim() + ".pdf";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), namaFIle);

            try {
                document.writeTo(new FileOutputStream(file));
                Toast.makeText(this, "Berkas berhasil disimpan\n"+ file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            document.close();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<modelRekapAbsen> data = new ArrayList<>();
        final String id = getIntent().getStringExtra("id");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("jadwalkuliah").child(id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nip = snapshot.child("nip").getValue().toString();
                String matkul = snapshot.child("nama").getValue().toString();

                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("dosen");
                dbref.child(nip).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nama = snapshot.child("nama").getValue().toString();
                        txtKode.setText(id);
                        txtNama.setText(nama);
                        txtMatkul.setText(matkul);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("absen").child(nip);
                ref.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        data.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            modelAbsen model = ds.getValue(modelAbsen.class);
                            list.add(model);
                        }

                        for (int n=0; n<list.size(); n++) {
                            data.add(new modelRekapAbsen(n+1, list.get(n).getNim(),
                                    list.get(n).getNama(), list.get(n).getKet()));
                        }

                        RecyclerView rv = findViewById(R.id.rcAbsen);
                        adapterRekapAbsen adp = new adapterRekapAbsen(data);
                        LinearLayoutManager ly = new LinearLayoutManager(detailAbsensi.this);
                        rv.setLayoutManager(ly);
                        rv.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}