package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.absensi.labkom.adapter.adpJadwalku;
import com.absensi.labkom.model.modelJadwal;
import com.absensi.labkom.model.modelKuliah;
import com.absensi.labkom.model.modelSlip;
import com.absensi.labkom.model.modelSlipbayar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JadwalKuliah extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private adpJadwalku adapter;
    Uri imageUri;
    final int kodeGallery = 100, kodeKamera = 99;
    private ArrayList<modelJadwal> myList;
    private ArrayList<modelKuliah> list;
    private ArrayList<String> listNIM;
    DatabaseReference db, ref;
    ListView layoutJadwal;
    ProgressDialog progressDialog;
    TextView kosong;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_kuliah);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_datajadwalku);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Jadwal Kuliah");

        db = FirebaseDatabase.getInstance().getReference("jadwalkuliah");
        ref = FirebaseDatabase.getInstance().getReference("kuliah");
        layoutJadwal = findViewById(R.id.rvJadwalk);
        myList = new ArrayList<>();
        list = new ArrayList<>();
        layoutJadwal.setOnItemClickListener(this);
        adapter = new adpJadwalku(JadwalKuliah.this);
        kosong = (TextView) findViewById(R.id.kosongDataJadwalku);
    }

    private void setListView() {
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
                    for (int o=0; o<myList.size(); o++) {
                        list.clear();
                        ref.child(myList.get(o).getKode()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    modelKuliah model = dataSnapshot.getValue(modelKuliah.class);
                                    list.add(model);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
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
        listNIM = new ArrayList<>();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(JadwalKuliah.this, MainActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("nama", getIntent().getStringExtra("nama"));
        intent.putExtra("no", getIntent().getStringExtra("no"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = (TextView)view.findViewById(R.id.listKuliahKode);
        final String kode = getId.getText().toString();
        final String nimku = getIntent().getStringExtra("id");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("kuliah").child(kode);
        dr.child(nimku).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    Toast.makeText(JadwalKuliah.this, "Jadwal sudah diambil", Toast.LENGTH_SHORT).show();
                } else {
                    dialogku(kode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void dialogku(final String kode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JadwalKuliah.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(JadwalKuliah.this).inflate(R.layout.dialog_kuliah, (ConstraintLayout) findViewById(R.id.layoutDialogContainerku));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnYaku).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("kuliah").child(kode);
                modelKuliah model = new modelKuliah();
                for (int n=0; n<myList.size(); n++) {
                    if (myList.get(n).getKode().equals(kode)) {
                        String nipp = myList.get(n).getNip();
                        String matkul = myList.get(n).getNama();
                        String tgl = myList.get(n).getTanggal();
                        model.setKode(myList.get(n).getKode());
                        model.setNim(getIntent().getStringExtra("id"));
                        model.setDosen(nipp);
                        model.setMatkul(matkul);
                        model.setRuang(myList.get(n).getRuang());
                        model.setJam(myList.get(n).getWaktu());
                        model.setTanggal(tgl);
                        model.setKet("Menunggu konfirmasi");
                        reference.child(getIntent().getStringExtra("id")).setValue(model)
                                .addOnCompleteListener(JadwalKuliah.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            simpanSlip(kode, getIntent().getStringExtra("id"),
                                                    getIntent().getStringExtra("nama"), nipp, matkul, tgl);
                                            //tambahSlip(getIntent().getStringExtra("id"), kode);
                                            Toast.makeText(JadwalKuliah.this, "Jadwal berhasil ditambah", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
                                    }
                                });
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("kuliahku")
                                .child(getIntent().getStringExtra("id"));
                        ref.child(kode).setValue(model);
                    }
                }
            }
        });

        v.findViewById(R.id.btnBatalku).setOnClickListener(new View.OnClickListener() {
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

    @SuppressLint("MissingInflatedId")
    private void tambahSlip(String nim, String kode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JadwalKuliah.this, R.style.AlertDialogTheme);
        builder.setCancelable(false);
        View v = LayoutInflater.from(JadwalKuliah.this).inflate(R.layout.dialog_slip, (ConstraintLayout) findViewById(R.id.layoutDialogContainerSlip));
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        v.findViewById(R.id.btnYaSlip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent mIntent = new Intent(JadwalKuliah.this, UploadSlip.class);
                mIntent.putExtra("id", nim);
                mIntent.putExtra("nama", getIntent().getStringExtra("nama"));
                mIntent.putExtra("kode", kode);
                mIntent.putExtra("no", getIntent().getStringExtra("no"));
                startActivity(mIntent);
                finish();
            }
        });

        v.findViewById(R.id.btnTidakSlip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void simpanSlip(String kode, String nim, String nama, String nip, String matkul, String tgl) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("slip").child(kode);
        modelSlip model = new modelSlip();
        model.setKode(kode);
        model.setNim(nim);
        model.setNama(nama);
        model.setKet("Belum bayar");
        model.setTanggal("-");
        db.child(nim).setValue(model);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("slipku").child(nim);
        ref.child(kode).setValue(model);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("arslip").child(nip);
        dr.child(kode).child(nim).setValue(model);

        DatabaseReference ds = FirebaseDatabase.getInstance().getReference("kelas").child(nip);
        modelSlipbayar mod = new modelSlipbayar();
        mod.setKode(kode);
        mod.setNama(matkul);
        mod.setTanggal(tgl);
        ds.child(kode).setValue(mod);
    }
}