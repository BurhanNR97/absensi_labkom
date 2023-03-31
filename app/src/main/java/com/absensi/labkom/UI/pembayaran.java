package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adpSlip;
import com.absensi.labkom.model.modelSlip;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class pembayaran extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<modelSlip> list;
    ListView layout;
    TextView teks;
    adpSlip adp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_pembayaran);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Slip Pembayaran");

        list = new ArrayList<>();
        layout = findViewById(R.id.rvPembayaran);
        teks = findViewById(R.id.kosongPembayaran);
        adp = new adpSlip(this);

        layout.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView getId = view.findViewById(R.id.listSlipKode);
        TextView getNIM = view.findViewById(R.id.listSlipNIM);
        final String id = getId.getText().toString().trim();
        final String nim = getNIM.getText().toString().trim();
        final String nip = getIntent().getStringExtra("id");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("arslip").child(nip).child(id);
        db.child(nim).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ket = snapshot.child("ket").getValue().toString();

                if (ket.equals("Lunas")) {
                    Toast.makeText(pembayaran.this, "Pembayaran sudah lunas", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(pembayaran.this, R.style.AlertDialogTheme);
                    builder.setCancelable(false);
                    View v = LayoutInflater.from(pembayaran.this).inflate(R.layout.tampil_gambar, (ConstraintLayout) findViewById(R.id.layoutDialogTampilGambar));
                    builder.setView(v);
                    final AlertDialog alertDialog = builder.create();

                    DatabaseReference slip = FirebaseDatabase.getInstance().getReference("slip").child(id);
                    DatabaseReference arslip = FirebaseDatabase.getInstance().getReference("arslip").child(nip);
                    DatabaseReference slipku = FirebaseDatabase.getInstance().getReference("slipku").child(nim);
                    HashMap<String, Object> data = new HashMap<String, Object>();
                    ImageView foto = v.findViewById(R.id.imgTampilGambar);

                    String path = nim + ".jpg";
                    StorageReference ref = FirebaseStorage.getInstance().getReference("slip").child(id);
                    ref.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String pathName = uri.toString();
                            Glide.with(v.getContext()).load(pathName).into(foto);
                        }
                    });

                    v.findViewById(R.id.btnTerima).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            data.put("ket", "Lunas");
                            slip.child(nim).updateChildren(data);
                            arslip.child(id).child(nim).updateChildren(data);
                            slipku.child(id).updateChildren(data);

                            alertDialog.dismiss();
                        }
                    });

                    v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            data.put("ket", "Ditolak");
                            slip.child(nim).updateChildren(data);
                            arslip.child(id).child(nim).updateChildren(data);
                            slipku.child(id).updateChildren(data);

                            alertDialog.dismiss();
                        }
                    });

                    v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String id = getIntent().getStringExtra("id");
        final String kode = getIntent().getStringExtra("kode");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("arslip").child(id);
        db.child(kode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelSlip model = dataSnapshot.getValue(modelSlip.class);
                    list.add(model);
                }
                adp.setList(list);
                layout.setAdapter(adp);

                if (!list.isEmpty()) {
                    layout.setVisibility(View.VISIBLE);
                    teks.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.GONE);
                    teks.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(pembayaran.this, KonfirSlip.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        startActivity(intent);
        finish();
    }
}