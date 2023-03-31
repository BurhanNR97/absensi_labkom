package com.absensi.labkom.UI.tambahData;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TambahAkun extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<String> list;
    private DatabaseReference dbDosen, dbMhs;
    private TextView edID, edPass, edUsername;
    private Button simpan, keluar;
    private TextInputLayout inputPass;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TambahAkun.this, DataAkunPengguna.class));
        finish();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_akun);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        dbMhs = FirebaseDatabase.getInstance().getReference("mahasiswa");
        dbDosen = FirebaseDatabase.getInstance().getReference("dosen");
        edID = findViewById(R.id.Tambah_AkunID);
        edPass = findViewById(R.id.Tambah_AkunPassword);
        edUsername = findViewById(R.id.Tambah_AkunUsernam);
        list = new ArrayList<>();
        simpan = findViewById(R.id.btnSimpan_TambahAkun);
        keluar = findViewById(R.id.btnKeluarAddAkun);
        inputPass = findViewById(R.id.textInputAkunPass);

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        myData();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        Query ref = db.orderByKey().limitToLast(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String teks = ds.getKey();
                    int a = Integer.parseInt(teks.substring(3));
                    int b = a + 1;
                    char[] c = String.valueOf(b).toCharArray();
                    if (c.length == 1) {
                        edID.setText("ID-0000"+b);
                    } else
                    if (c.length == 2) {
                        edID.setText("ID-000"+b);
                    } else
                    if (c.length == 3) {
                        edID.setText("ID-00"+b);
                    } else
                    if (c.length == 4) {
                        edID.setText("ID-0"+b);
                    } else
                    if (c.length == 5) {
                        edID.setText("ID-"+b);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        simpan.setOnClickListener(this);

        edUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPass.setErrorEnabled(false);
            }
        });

        edPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPass.setErrorEnabled(false);
            }
        });
    }

    private void myData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    list.add(ds.child("username").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        String id = edID.getText().toString().trim();
        String username = edUsername.getText().toString().trim();
        String password = edPass.getText().toString().trim();

        if (username.isEmpty()) {
            edUsername.requestFocus();
            edUsername.setError("Data harus diisi");
        } else
        if (password.isEmpty()) {
            inputPass.setError("Data harus diisi");
            simpan.requestFocus();
        } else {
            int a = list.indexOf(username);
            if (a >= 0) {
                edUsername.requestFocus();
                edUsername.setError("Username sudah ada");
            } else {
                modelUser model = new modelUser();
                model.setId(id);
                model.setUsername(username);
                model.setPassword(password);
                model.setLevel("admin");

                DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference("users");
                dbUser.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TambahAkun.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });
            }
        }
    }
}