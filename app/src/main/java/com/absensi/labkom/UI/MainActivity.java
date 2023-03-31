package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    CardView btnAbsen, btnBayar, btnJadwal, btnScan;
    TextView tampilNama;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_MainMhs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        String prefNIM = getIntent().getStringExtra("id");
        String prefNama = getIntent().getStringExtra("nama");
        tampilNama = findViewById(R.id.namaLogin);
        tampilNama.setText("("+prefNIM+")\n"+prefNama);

        btnScan = findViewById(R.id.cvQR);
        btnAbsen = findViewById(R.id.cvRiwayatAbsen);
        btnJadwal = findViewById(R.id.cvJadwalKuliah);
        btnBayar = findViewById(R.id.cvRiwayatBayar);

        btnAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, absensiku.class);
                intent.putExtra("id", prefNIM);
                intent.putExtra("nama", prefNama);
                intent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(intent);
                finish();
            }
        });

        btnJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JadwalKuliah.class);
                intent.putExtra("id", prefNIM);
                intent.putExtra("nama", prefNama);
                intent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(intent);
                finish();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QRScanner.class);
                intent.putExtra("id", prefNIM);
                intent.putExtra("nama", prefNama);
                intent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(intent);
                finish();
            }
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SlipBayar.class);
                intent.putExtra("id", prefNIM);
                intent.putExtra("nama", prefNama);
                intent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.opUbahSandi) {
            ubahSandi();
        } else
        if (id == R.id.opSignout) {
            keluarMhs();
        }

        return super.onOptionsItemSelected(item);
    }

    private void keluarMhs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_signout, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnYa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    @SuppressLint("MissingInflatedId")
    private void ubahSandi() {
        final String username = getIntent().getStringExtra("id");
        final String id = getIntent().getStringExtra("no");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(id);
        HashMap<String, Object> data = new HashMap<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_sandi, (ConstraintLayout) findViewById(R.id.layoutDialogSandi));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        EditText lama = v.findViewById(R.id.SandiLama);
        EditText baru = v.findViewById(R.id.SandiBru);
        EditText konf = v.findViewById(R.id.SandiKonfirmasi);
        TextInputLayout konfir = v.findViewById(R.id.textInputSandiKonfir);
        TextInputLayout lamaa = v.findViewById(R.id.textInputSandiLama);
        TextInputLayout baruu = v.findViewById(R.id.textInputSandiBru);

        v.findViewById(R.id.btnSimpanSandi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                konf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        konfir.setError(null);
                        konfir.setErrorEnabled(false);
                    }
                });

                lama.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lamaa.setError(null);
                        lamaa.setErrorEnabled(false);
                    }
                });

                baru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        baruu.setError(null);
                        baruu.setErrorEnabled(false);
                    }
                });

                String sLama = lama.getText().toString().trim();
                String sBaru = baru.getText().toString().trim();
                String sKonf = konf.getText().toString().trim();

                if (sLama.isEmpty()) {
                    lamaa.setError("Data harus diisi");
                    lamaa.setErrorEnabled(true);
                } else if (sBaru.isEmpty()) {
                    baruu.setError("Data harus diisi");
                    baruu.setErrorEnabled(true);
                } else if (sKonf.isEmpty()) {
                    konfir.setError("Data harus diisi");
                    konfir.setErrorEnabled(true);
                }

                if (!sBaru.equals(sKonf)) {
                    konfir.setErrorEnabled(true);
                    konfir.setError("Kata sandi tidak cocok");
                } else {
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String pass = snapshot.child("password").getValue().toString().trim();
                            if (!sLama.equals(pass)) {
                                lamaa.setError("Sandi lama tidak cocok");
                                lamaa.setErrorEnabled(true);
                            } else {
                                data.put("password", sKonf);
                                db.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Kata sandi berhasil diganti", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
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
        });

        v.findViewById(R.id.btnBatalSandi).setOnClickListener(new View.OnClickListener() {
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
 }