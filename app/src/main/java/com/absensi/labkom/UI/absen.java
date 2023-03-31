package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adpAbsen;
import com.absensi.labkom.model.modelAbsen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class absen extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<modelAbsen> list;
    ListView layout;
    TextView teks;
    adpAbsen adp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_konfirAbseniku);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Daftar Mahasiswa");

        list = new ArrayList<>();
        layout = findViewById(R.id.rvAbsenku);
        teks = findViewById(R.id.kosongAbsenku);
        adp = new adpAbsen(this);

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
        final String id = getIntent().getStringExtra("id");
        final String kode = getIntent().getStringExtra("kode");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("absen").child(id);
        db.child(kode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelAbsen model = dataSnapshot.getValue(modelAbsen.class);
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
        Intent intent = new Intent(absen.this, KonfirMhs.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getNIM = view.findViewById(R.id.absen_nim);
        String nim = getNIM.getText().toString().trim();
        final String id = getIntent().getStringExtra("id");
        final String kode = getIntent().getStringExtra("kode");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("absen").child(id);
        DatabaseReference dbn = db.child(kode);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("absensi").child(nim);
        HashMap<String, Object> data = new HashMap<String, Object>();

        AlertDialog.Builder builder = new AlertDialog.Builder(absen.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(absen.this).inflate(R.layout.dialog_absen, (ConstraintLayout) findViewById(R.id.containerAbsen));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnAbsen_hadir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.put("ket", "Hadir");
                dbn.child(nim).updateChildren(data);
                dbRef.child(kode).updateChildren(data);
                alertDialog.dismiss();
            }
        });

        v.findViewById(R.id.btnAbsen_tidak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.put("ket", "Tidak hadir");
                dbn.child(nim).updateChildren(data);
                dbRef.child(kode).updateChildren(data);
                alertDialog.dismiss();
            }
        });

        v.findViewById(R.id.btnAbsen_batal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow()!=null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}