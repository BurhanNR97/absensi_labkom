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
import com.absensi.labkom.UI.tambahData.TambahJadwal;
import com.absensi.labkom.UI.ubahData.UbahJadwal;
import com.absensi.labkom.adapter.adpJadwal;
import com.absensi.labkom.model.modelJadwal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DataJadwal extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private adpJadwal adapter;
    private ArrayList<modelJadwal> myList;
    DatabaseReference db;
    ListView layoutJadwal;
    TextView kosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_jadwal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_datajadwal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Data Jadwal Kuliah");

        db = FirebaseDatabase.getInstance().getReference("jadwalkuliah");
        layoutJadwal = findViewById(R.id.rvJadwal);
        layoutJadwal.setOnItemClickListener(this);
        myList = new ArrayList<>();
        adapter = new adpJadwal(DataJadwal.this);
        kosong = (TextView) findViewById(R.id.kosongDataJadwal);
    }

    public void setListView() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelJadwal model = dataSnapshot.getValue(modelJadwal.class);
                    myList.add(model);
                }

                adapter.setJadwalList(myList);
                layoutJadwal.setAdapter(adapter);

                if (myList.size() == 0) {
                    layoutJadwal.setVisibility(View.GONE);
                    kosong.setVisibility(View.VISIBLE);
                } else {
                    layoutJadwal.setVisibility(View.VISIBLE);
                    kosong.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        Intent intent = new Intent(DataJadwal.this, MainAdmin.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_data_jadwal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_datajadwal) {
            Intent intent = new Intent(DataJadwal.this, TambahJadwal.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void hapusData (String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataJadwal.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(DataJadwal.this).inflate(R.layout.dialog_hapus, (ConstraintLayout) findViewById(R.id.layoutDialogContainerHapus));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnYaHapus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusFoto(id);
                db.child(id).removeValue();
                Toast.makeText(DataJadwal.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
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

    private void hapusFoto(String id) {
        StorageReference ref = FirebaseStorage.getInstance().getReference("QRcode").child(id);
        ref.delete();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = (TextView)view.findViewById(R.id.listKodeJadwal);
        final String nid = getId.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(DataJadwal.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(DataJadwal.this).inflate(R.layout.dialog_item, (ConstraintLayout) findViewById(R.id.containerItem));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnUbahIsi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int n = 0; n < myList.size(); n++) {
                    if (myList.get(n).getKode().equals(nid)) {
                        Intent intent = new Intent(DataJadwal.this, UbahJadwal.class);
                        intent.putExtra("kode", myList.get(n).getKode());
                        intent.putExtra("nama", myList.get(n).getNama());
                        intent.putExtra("nip", myList.get(n).getNip());
                        intent.putExtra("semester", myList.get(n).getSemester());
                        intent.putExtra("sks", myList.get(n).getSks());
                        intent.putExtra("waktu", myList.get(n).getWaktu());
                        intent.putExtra("ruang", myList.get(n).getRuang());
                        intent.putExtra("tgl", myList.get(n).getTanggal());
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