package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.absensi.labkom.UI.tambahData.TambahAkun;
import com.absensi.labkom.UI.ubahData.UbahAkun;
import com.absensi.labkom.adapter.adpUsers;
import com.absensi.labkom.model.modelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataAkunPengguna extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private TextView teks;
    private ListView layout;
    private DatabaseReference dbUser;
    private adpUsers adapters;
    private ArrayList<modelUser> listUser;
    private ArrayList<modelUser> listUsers;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_akun_pengguna);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_dataakun);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Data Akun Pengguna");

        teks = findViewById(R.id.kosongUsers);
        layout = findViewById(R.id.rvUsers);
        dbUser = FirebaseDatabase.getInstance().getReference("users");
        adapters = new adpUsers(this);
        layout.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dataakun("id");
    }

    private void dataakun(String s) {
        listUser = new ArrayList<>();
        listUsers = new ArrayList<>();
        dbUser.orderByChild(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                listUsers.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelUser model = ds.getValue(modelUser.class);
                    listUser.add(model);
                    listUsers.add(model);
                }

                if (listUser.isEmpty()) {
                    layout.setVisibility(View.GONE);
                    teks.setVisibility(View.VISIBLE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    teks.setVisibility(View.GONE);
                }

                adapters.setUsersList(listUser);
                layout.setAdapter(adapters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sortir(int pos) {

        switch (pos) {
            case 1 :

            case 2 :
                Collections.sort(listUser, new Comparator<modelUser>() {
                    @Override
                    public int compare(modelUser t0, modelUser t1) {
                        return t0.getUsername().compareToIgnoreCase(t1.getUsername());
                    }
                });
            case 3 :
                Collections.sort(listUser, new Comparator<modelUser>() {
                    @Override
                    public int compare(modelUser t0, modelUser t1) {
                        return Integer.compare(Integer.parseInt(t0.getPassword()), Integer.parseInt(t1.getPassword()));
                    }
                });
            case 4 :
                Collections.sort(listUser, new Comparator<modelUser>() {
                    @Override
                    public int compare(modelUser t0, modelUser t1) {
                        return t0.getLevel().compareToIgnoreCase(t1.getLevel());
                    }
                });
        }
        adapters.setUsersList(listUser);
        layout.setAdapter(adapters);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DataAkunPengguna.this, MainAdmin.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_data_akun, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_dataakun:
                Intent intent = new Intent(DataAkunPengguna.this, TambahAkun.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
                finish();
                break;
            case R.id.sort_id:
                dataakun("id");
                break;
            case R.id.sort_user:
                dataakun("username");
                break;
            case R.id.sort_pass:
                dataakun("password");
                break;
            case R.id.sort_level:
                dataakun("level");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getID = view.findViewById(R.id.akun_id);
        final String id = getID.getText().toString().trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(DataAkunPengguna.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(DataAkunPengguna.this).inflate(R.layout.dialog_item, (ConstraintLayout) findViewById(R.id.containerItem));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnUbahIsi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(DataAkunPengguna.this, UbahAkun.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("kode", id);
                startActivity(intent);
                finish();
            }
        });

        v.findViewById(R.id.btnHapusIsi).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DataAkunPengguna.this, R.style.AlertDialogTheme);
                alertBuilder.setCancelable(false);
                View vv = LayoutInflater.from(DataAkunPengguna.this).inflate(R.layout.dialog_hapus, (ConstraintLayout) findViewById(R.id.layoutDialogContainerHapus));
                alertBuilder.setView(vv);
                final AlertDialog dialog = alertBuilder.create();

                dbUser.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("username").getValue().toString().equals("admin")) {
                            vv.findViewById(R.id.btnYaHapus).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dbUser.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(DataAkunPengguna.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                                alertDialog.cancel();
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(DataAkunPengguna.this, "Tidak dapat menghapus data", Toast.LENGTH_SHORT).show();
                            alertDialog.cancel();
                            dialog.cancel();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (dialog.getWindow()!=null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
                alertDialog.cancel();
            }
        });

        v.findViewById(R.id.btnBatalIsi).setOnClickListener(new View.OnClickListener() {
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