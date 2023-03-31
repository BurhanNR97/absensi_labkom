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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adpSlip;
import com.absensi.labkom.model.modelSlip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SlipBayar extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<modelSlip> list;
    ListView layout;
    TextView teks;
    adpSlip adp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip_bayar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_bayarr);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Slip Pembayaran");

        list = new ArrayList<>();
        layout = findViewById(R.id.rvBayarr);
        teks = findViewById(R.id.kosongBayarr);
        adp = new adpSlip(this);

        layout.setOnItemClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String nim = getIntent().getStringExtra("id");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("slipku").child(nim);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelSlip model = dataSnapshot.getValue(modelSlip.class);
                    list.add(model);
                }
                adp.setList(list);
                layout.setAdapter(adp);

                if (!list.isEmpty()) {
                    layout.setVisibility(View.VISIBLE);
                    teks.setVisibility(View.GONE);
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
    public void onBackPressed() {
        Intent intent = new Intent(SlipBayar.this, MainActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("nama", getIntent().getStringExtra("nama"));
        intent.putExtra("no", getIntent().getStringExtra("no"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = (TextView)view.findViewById(R.id.listSlipKode);
        final String kode = getId.getText().toString();
        TextView getNim = (TextView)view.findViewById(R.id.listSlipNIM);
        final String Nim = getNim.getText().toString();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("slip").child(kode);
        db.child(Nim).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String ket = snapshot.child("ket").getValue().toString();
                if (ket.equals("Lunas")) {
                    Toast.makeText(SlipBayar.this, "Pembayaran sudah lunas", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("jadwalkuliah").child(kode);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final String nip = snapshot.child("nip").getValue().toString();

                            Intent intent = new Intent(SlipBayar.this, UploadSlip.class);
                            intent.putExtra("id", getIntent().getStringExtra("id"));
                            intent.putExtra("nama", getIntent().getStringExtra("nama"));
                            intent.putExtra("no", getIntent().getStringExtra("no"));
                            intent.putExtra("kode", kode);
                            intent.putExtra("nip", nip);
                            startActivity(intent);
                            finish();
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
}