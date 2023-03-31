package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adpJadwalDosen;
import com.absensi.labkom.model.modelJadwalDosen;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class JadwalDosen extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<modelJadwalDosen> list = new ArrayList<>();
    adpJadwalDosen adapter;
    ListView layout;
    TextView teks;
    DatabaseReference db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_dosen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_jadwalDosen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Jadwal Mengajar");

        db = FirebaseDatabase.getInstance().getReference("kelas").child(getIntent().getStringExtra("id"));

        adapter = new adpJadwalDosen(this);
        layout = findViewById(R.id.rvJadwalDosen);
        teks = findViewById(R.id.kosongJadwaldosen);
        layout.setOnItemClickListener(this);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelJadwalDosen model = ds.getValue(modelJadwalDosen.class);
                    list.add(model);
                }

                adapter.setJadwalList(list);
                layout.setAdapter(adapter);

                if (list.isEmpty()) {
                    layout.setVisibility(View.GONE);
                    teks.setVisibility(View.VISIBLE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    teks.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(JadwalDosen.this, MainDosen.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = view.findViewById(R.id.jd_kode);
        String id = getId.getText().toString().trim();
        String pathFoto = id + ".jpg";

        StorageReference db = FirebaseStorage.getInstance().getReference("QRcode");
        db.child(pathFoto).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onSuccess(Uri uri) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JadwalDosen.this, R.style.AlertDialogTheme);
                builder.setCancelable(true);
                View v = LayoutInflater.from(JadwalDosen.this).inflate(R.layout.tampil_slip, (ConstraintLayout) findViewById(R.id.layoutTampilGambarSlip));
                builder.setView(v);
                ImageView foto = v.findViewById(R.id.imgTampilSlip);

                final AlertDialog alertDialog = builder.create();
                String pathName = uri.toString();
                Glide.with(v.getContext()).load(pathName).into(foto);

                if (alertDialog.getWindow()!=null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });
    }
}