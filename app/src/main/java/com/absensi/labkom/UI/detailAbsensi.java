package com.absensi.labkom.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

public class detailAbsensi extends AppCompatActivity {
    ArrayList<modelAbsen> list = new ArrayList<>();
    TextView txtKode, txtNama, txtMatkul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_absensi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        String id = getIntent().getStringExtra("id");

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