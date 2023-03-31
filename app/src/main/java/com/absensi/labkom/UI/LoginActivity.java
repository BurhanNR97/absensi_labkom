package com.absensi.labkom.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelMhs;
import com.absensi.labkom.model.modelUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsr, txtPass;
    DatabaseReference mDatabase;
    DatabaseReference db;
    private modelUser mUser;
    private modelMhs mMhs;
    private ArrayList<modelUser> listUser;
    private ArrayList<modelMhs> listMhs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }

        txtUsr = findViewById(R.id.editTextEmail);
        txtPass = findViewById(R.id.editTextPassword);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        db = FirebaseDatabase.getInstance().getReference("mahasiswa");
        mUser = new modelUser();
        mMhs = new modelMhs();
        listUser = new ArrayList<>();
        listMhs = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelUser usr = ds.getValue(modelUser.class);
                    listUser.add(usr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMhs.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelMhs model = ds.getValue(modelMhs.class);
                    listMhs.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsr.getText().toString();
                String password = txtPass.getText().toString();

                if (username.isEmpty()) {
                    txtUsr.requestFocus();
                    txtUsr.setError("Harap masukkan data");
                } else
                if (password.isEmpty()) {
                    txtPass.requestFocus();
                    txtPass.setError("Harap masukkan data");
                } else {
                    int i = cekUsername(username);
                    String usr = listUser.get(i).getUsername();
                    String nmr = listUser.get(i).getId();
                    String pass = listUser.get(i).getPassword();
                    String level = listUser.get(i).getLevel();
                    if (usr.equals(username)) {
                        if (pass.equals(password)) {
                            switch (level) {
                                case "admin":
                                    startActivity(new Intent(LoginActivity.this, MainAdmin.class));
                                    finish();
                                    break;
                                case "dosen":
                                    Intent intent2 = new Intent(LoginActivity.this, MainDosen.class);
                                    intent2.putExtra("id", username);
                                    startActivity(intent2);
                                    finish();
                                    break;
                                case "mahasiswa":
                                    String nm = ambilNama(username);
                                    Intent intent3 = new Intent(LoginActivity.this, MainActivity.class);
                                    intent3.putExtra("id", username);
                                    intent3.putExtra("nama", nm);
                                    intent3.putExtra("no", nmr);
                                    startActivity(intent3);
                                    finish();
                                    break;
                            }
                        } else {
                            txtPass.requestFocus();
                            txtPass.selectAll();
                            Toast pesan = Toast.makeText(LoginActivity.this, "Username/password salah", Toast.LENGTH_SHORT);
                            pesan.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            pesan.show();
                        }
                    } else {
                        txtPass.requestFocus();
                        txtPass.selectAll();
                        Toast pesan = Toast.makeText(LoginActivity.this, "Username/password salah", Toast.LENGTH_SHORT);
                        pesan.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        pesan.show();
                    }
                }
            }
        });
    }

    private int cekUsername (String username) {
        int indeks = 0;
        for (int n = 0; n < listUser.size(); n++) {
            String ambil = listUser.get(n).getUsername();
            if (ambil.equals(username)) {
                indeks = n;
            }
        }
        return indeks;
    }

    private String ambilNama (String username) {
        String i = "";
        for (int n = 0; n < listMhs.size(); n++) {
            String ambil = listMhs.get(n).getId();
            if (ambil.equals(username)) {
                i = listMhs.get(n).getNama();
            }
        }
        return i;
    }
}