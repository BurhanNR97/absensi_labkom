package com.absensi.labkom.UI;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainDosen extends AppCompatActivity {

    CardView btnLogout, menuJadwal, menuSlip, menuMhs;
    TextView tampilNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dosen);

        String pref = getIntent().getStringExtra("id");
        tampilNama = findViewById(R.id.viewNamaDosen);
        namaDosen(pref);

        btnLogout = findViewById(R.id.menuLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainDosen.this, R.style.AlertDialogTheme);
                builder.setCancelable(false);
                View v = LayoutInflater.from(MainDosen.this).inflate(R.layout.dialog_signout, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
                builder.setView(v);
                final AlertDialog alertDialog = builder.create();

                v.findViewById(R.id.btnYa).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainDosen.this, LoginActivity.class));
                        finish();
                    }
                });

                v.findViewById(R.id.btnTidak).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });

                if (alertDialog.getWindow()!=null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });

        menuJadwal = findViewById(R.id.menuJadwal);
        menuJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDosen.this, JadwalDosen.class);
                intent.putExtra("id", pref);
                startActivity(intent);
                finish();
            }
        });

        menuSlip = findViewById(R.id.menuSlip);
        menuSlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDosen.this, KonfirSlip.class);
                intent.putExtra("id", pref);
                startActivity(intent);
                finish();
            }
        });

        menuMhs = findViewById(R.id.menuMhs);
        menuMhs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDosen.this, KonfirMhs.class);
                intent.putExtra("id", pref);
                startActivity(intent);
                finish();
            }
        });
    }

    private void namaDosen(String id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("dosen");
        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaDos = snapshot.child("nama").getValue().toString();
                tampilNama.setText(namaDos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}