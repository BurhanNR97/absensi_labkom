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
import com.absensi.labkom.UI.tambahData.TambahMahasiswa;
import com.absensi.labkom.UI.ubahData.UbahMahasiswa;
import com.absensi.labkom.adapter.adpMhs;
import com.absensi.labkom.model.modelMhs;
import com.absensi.labkom.model.modelUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DataMahasiswa extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private adpMhs adapter;
    private ArrayList<modelMhs> list;
    private ArrayList<modelUser> listUser;
    private DatabaseReference db;
    ListView layoutMhs;
    TextView kosong;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_mahasiswa);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_datamhs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Data Mahasiswa");

        db = FirebaseDatabase.getInstance().getReference("mahasiswa");
        layoutMhs = findViewById(R.id.rvMhs);
        layoutMhs.setOnItemClickListener(this);
        list = new ArrayList<>();
        listUser = new ArrayList<>();
        adapter = new adpMhs(DataMahasiswa.this);
        kosong = findViewById(R.id.kosongDataMhs);
    }

    public void setListView() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelMhs model = dataSnapshot.getValue(modelMhs.class);
                    list.add(model);
                }

                adapter.setMhsList(list);
                layoutMhs.setAdapter(adapter);

                if (list.size() == 0) {
                    layoutMhs.setVisibility(View.GONE);
                    kosong.setVisibility(View.VISIBLE);
                } else {
                    layoutMhs.setVisibility(View.VISIBLE);
                    kosong.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tampilUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DataMahasiswa.this, MainAdmin.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_data_mhs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_datamhs) {
            Intent i = new Intent(DataMahasiswa.this, TambahMahasiswa.class);
            i.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void hapusData (String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataMahasiswa.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(DataMahasiswa.this).inflate(R.layout.dialog_hapus, (ConstraintLayout) findViewById(R.id.layoutDialogContainerHapus));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnYaHapus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < listUser.size(); i++) {
                    if (listUser.get(i).getUsername().equals(id)) {
                        hapusUser(listUser.get(i).getId());
                    }
                }
                hapusFoto(id);
                db.child(id).removeValue();
                Toast.makeText(DataMahasiswa.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        v.findViewById(R.id.btnTidakHapus).setOnClickListener(new View.OnClickListener() {
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

    private void tampilUser() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.addValueEventListener(new ValueEventListener() {
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
    }

    private void hapusUser(String nip) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("mahasiswa").child(nip);
        db.removeValue();
    }

    private void hapusFoto(String nip) {
        StorageReference ref = FirebaseStorage.getInstance().getReference("mahasiswa").child(nip);
        ref.delete();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = (TextView)view.findViewById(R.id.listIDmhs);
        final String nid = getId.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(DataMahasiswa.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(DataMahasiswa.this).inflate(R.layout.dialog_item, (ConstraintLayout) findViewById(R.id.containerItem));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnUbahIsi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int n = 0; n < list.size(); n++) {
                    if (list.get(n).getId().equals(nid)) {
                        Intent intent = new Intent(DataMahasiswa.this, UbahMahasiswa.class);
                        intent.putExtra("nim", list.get(n).getId());
                        intent.putExtra("nama", list.get(n).getNama());
                        intent.putExtra("jk", list.get(n).getJK());
                        intent.putExtra("alamat", list.get(n).getAlamat());
                        intent.putExtra("tgl", list.get(n).getTgl_lahir());
                        intent.putExtra("hp", list.get(n).getHP());
                        intent.putExtra("id", getIntent().getStringExtra("id"));
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        v.findViewById(R.id.btnHapusIsi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusData(nid);
                alertDialog.dismiss();
            }
        });

        v.findViewById(R.id.btnBatalIsi).setOnClickListener(new View.OnClickListener() {
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