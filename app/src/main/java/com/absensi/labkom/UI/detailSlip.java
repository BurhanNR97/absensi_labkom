package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
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
import com.absensi.labkom.adapter.adapterRekapBayar;
import com.absensi.labkom.model.modelRekapSlip;
import com.absensi.labkom.model.modelSlip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class detailSlip extends AppCompatActivity {
    TextView txtKode, txtNama, txtMatkul;
    ArrayList<modelSlip> list = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_slip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        String id = getIntent().getStringExtra("id");

        Toolbar toolbar = findViewById(R.id.toolbar_detailslip);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Rekap Pembayaran");

        txtKode = findViewById(R.id.struk_kode);
        txtNama = findViewById(R.id.struk_dosen);
        txtMatkul = findViewById(R.id.struk_nama);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(detailSlip.this, RekapSlip.class));
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

        final String id = getIntent().getStringExtra("id");
        List<modelRekapSlip> data = new ArrayList<>();

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

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("slip");
                ref.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        data.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            modelSlip model = ds.getValue(modelSlip.class);
                            list.add(model);
                        }

                        for (int n=0; n<list.size(); n++) {
                            data.add(new modelRekapSlip(n+1, list.get(n).getNim(),
                                    list.get(n).getNama(), list.get(n).getKet()));
                        }

                        RecyclerView rv = findViewById(R.id.rcSlip);
                        adapterRekapBayar adp = new adapterRekapBayar(data);
                        LinearLayoutManager ly = new LinearLayoutManager(detailSlip.this);
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