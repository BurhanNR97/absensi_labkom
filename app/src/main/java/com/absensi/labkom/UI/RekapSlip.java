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
import com.absensi.labkom.adapter.adpJadwalDosen;
import com.absensi.labkom.model.modelJadwalDosen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RekapSlip extends AppCompatActivity implements AdapterView.OnItemClickListener {
    adpJadwalDosen adapter;
    ArrayList<modelJadwalDosen> list;
    ListView layout;
    TextView teks;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_slip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_rekapslip);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Rekap Pembayaran");

        adapter = new adpJadwalDosen(this);
        list = new ArrayList<>();
        layout = findViewById(R.id.rvRekapSlip);
        teks = findViewById(R.id.kosongRekapSlip);

        layout.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RekapSlip.this, MainAdmin.class));
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("absen");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getRef().getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("kelas");
                    ref.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                modelJadwalDosen model = ds.getValue(modelJadwalDosen.class);
                                list.add(model);
                            }

                            if (list.isEmpty()) {
                                layout.setVisibility(View.GONE);
                                teks.setVisibility(View.VISIBLE);
                            } else {
                                layout.setVisibility(View.VISIBLE);
                                teks.setVisibility(View.GONE);
                            }

                            adapter.setJadwalList(list);
                            layout.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = view.findViewById(R.id.jd_kode);
        final String id = getId.getText().toString().trim();
        Intent intent = new Intent(RekapSlip.this, detailSlip.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }
}