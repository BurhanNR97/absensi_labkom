package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adpSlipawal;
import com.absensi.labkom.model.modelSlipbayar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KonfirSlip extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<modelSlipbayar> list = new ArrayList<>();
    adpSlipawal adapter;
    ListView layout;
    TextView teks;
    DatabaseReference db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfir_slip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_konfirslip);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Verifikasi Pembayaran");

        db = FirebaseDatabase.getInstance().getReference("kelas").child(getIntent().getStringExtra("id"));

        adapter = new adpSlipawal(this);
        layout = findViewById(R.id.rvKelas);
        teks = findViewById(R.id.kosongKelas);
        layout.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelSlipbayar model = ds.getValue(modelSlipbayar.class);
                    list.add(model);
                }

                adapter.setList(list);
                layout.setAdapter(adapter);

                if (!list.isEmpty()) {
                    teks.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                } else {
                    layout.setVisibility(View.GONE);
                    teks.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(KonfirSlip.this, MainDosen.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = view.findViewById(R.id.slipkode);
        final String id = getId.getText().toString().trim();

        Intent intent = new Intent(KonfirSlip.this, pembayaran.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("kode", id);
        startActivity(intent);
        finish();
    }
}