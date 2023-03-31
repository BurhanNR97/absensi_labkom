package com.absensi.labkom.UI;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelDosen;
import com.absensi.labkom.model.modelMhs;
import com.absensi.labkom.model.modelUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainAdmin extends AppCompatActivity {

    Button btnLogout;
    LinearLayout layoutDosen, layoutMhs, layoutAkun;
    TextView jmlDosen, jmlMhs, jmlAkun;
    String pref = "";
    ArrayList<modelDosen> listDosen = new ArrayList<>();
    ArrayList<modelUser> listUser = new ArrayList<>();
    ArrayList<modelMhs> listMhs = new ArrayList<>();
    ConstraintLayout layoutJadwal, layoutAbsen, layoutBayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        FirebaseApp.initializeApp(this);
        layoutDosen = findViewById(R.id.layoutDosen);
        layoutMhs = findViewById(R.id.layoutMhs);
        layoutAkun = findViewById(R.id.layoutAkun);
        layoutAbsen = findViewById(R.id.layoutAbsensi);
        layoutBayar = findViewById(R.id.layoutSlip);

        jmlMhs = findViewById(R.id.jmlMhs);
        jmlDosen = findViewById(R.id.jmlDosen);
        jmlAkun = findViewById(R.id.jmlAkun);

        layoutAkun();
        layoutMhs();
        layoutDosen();

        dataDosen();
        dataMhs();
        dataAkun();

        layoutAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainAdmin.this, RekapAbsen.class));
                finish();
            }
        });

        layoutBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainAdmin.this, RekapSlip.class));
                finish();
            }
        });

        layoutJadwal = findViewById(R.id.layoutJadwal);
        layoutJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdmin.this, DataJadwal.class);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference dbAkun = FirebaseDatabase.getInstance().getReference("users");
        dbAkun.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelUser model = dataSnapshot.getValue(modelUser.class);
                    listUser.add(model);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainAdmin.this, R.style.AlertDialogTheme);
                builder.setCancelable(false);
                View v = LayoutInflater.from(MainAdmin.this).inflate(R.layout.dialog_signout, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
                builder.setView(v);
                final AlertDialog alertDialog = builder.create();

                v.findViewById(R.id.btnYa).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainAdmin.this, LoginActivity.class));
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
    }

    private void dataDosen() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("dosen");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jmlDosen.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dataMhs() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("mahasiswa");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jmlMhs.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dataAkun() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jmlAkun.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void layoutDosen() {
        layoutDosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdmin.this, DataDosen.class);
                intent.putExtra("id", pref);
                startActivity(intent);
                finish();
            }
        });
    }

    private void layoutMhs() {
        layoutMhs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdmin.this, DataMahasiswa.class);
                intent.putExtra("id", pref);
                startActivity(intent);
                finish();
            }
        });
    }

    private void layoutAkun() {
        layoutAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdmin.this, DataAkunPengguna.class);
                intent.putExtra("id", pref);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataDosen();
        dataMhs();
        dataAkun();
        jmlAkun.setText(""+listUser.size());
        jmlDosen.setText(""+listDosen.size());
        jmlMhs.setText(""+listMhs.size());
    }
}