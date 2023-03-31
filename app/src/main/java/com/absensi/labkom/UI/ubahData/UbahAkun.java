package com.absensi.labkom.UI.ubahData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absensi.labkom.R;
import com.absensi.labkom.UI.DataAkunPengguna;
import com.absensi.labkom.model.modelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UbahAkun extends AppCompatActivity implements View.OnClickListener {
    private TextView edID, edPass, edUsername;
    private Button simpan, keluar;
    private TextInputLayout inputPass;
    private DatabaseReference dbUser;
    String level;
    private ArrayList<String> listUser;
    String kode = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_akun);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        kode = getIntent().getStringExtra("kode");
        edID = findViewById(R.id.Ubah_AkunID);
        edPass = findViewById(R.id.Ubah_AkunPassword);
        edUsername = findViewById(R.id.Ubah_AkunUsernam);
        dbUser = FirebaseDatabase.getInstance().getReference("users");
        simpan = findViewById(R.id.btnSimpan_UbahAkun);
        keluar = findViewById(R.id.btnKeluarEditAkun);
        inputPass = findViewById(R.id.textInputAkunPas);

        dataakun();

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        edID.setText(kode);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.child(kode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelUser model = snapshot.getValue(modelUser.class);
                edUsername.setText(model.getUsername());
                edPass.setText(model.getPassword());
                level = model.getLevel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        simpan.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UbahAkun.this, DataAkunPengguna.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        int a = listUser.indexOf(edUsername.getText().toString());
        HashMap<String, Object> input = new HashMap<>();
        input.put("username", edUsername.getText().toString());

        input.put("password", edPass.getText().toString());
        input.put("level", level);
        if (kode == edUsername.getText().toString()) {
            dbUser.child(kode).updateChildren(input).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UbahAkun.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            });
        } else {
            if (a == 0) {
                edUsername.setError("Username sudah ada");
                edUsername.requestFocus();
            } else
            if (a < 0) {
                dbUser.child(kode).updateChildren(input).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UbahAkun.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });
            }
        }

    }

    private void dataakun() {
        listUser = new ArrayList<>();
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelUser model = ds.getValue(modelUser.class);
                    listUser.add(model.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}