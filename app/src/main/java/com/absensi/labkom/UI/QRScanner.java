package com.absensi.labkom.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.absensi.labkom.R;
import com.absensi.labkom.model.modelAbsen;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.util.Date;

public class QRScanner extends AppCompatActivity {
    private String teks = "";
    private TextView teksHasil;
    private CodeScanner codescan;
    private CodeScannerView mScanner;
    private static Boolean isPermissionGranted = false;
    private static int RC_PERMIS = 10;
    private Button btn;
    private LinearLayout ly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        teksHasil = findViewById(R.id.scanner_teks);
        mScanner = findViewById(R.id.scanner_view);
        btn = findViewById(R.id.scanner_btn);
        ly = findViewById(R.id.scanner_linear);

        codescan = new CodeScanner(this, mScanner);
        codescan.setCamera(CodeScanner.CAMERA_BACK);
        codescan.setFormats(CodeScanner.ALL_FORMATS);
        codescan.setAutoFocusMode(AutoFocusMode.SAFE);
        codescan.setScanMode(ScanMode.SINGLE);
        codescan.setAutoFocusEnabled(true);
        codescan.setFlashEnabled(false);
        codescan.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = result.getText();
                        String nim = getIntent().getStringExtra("id");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("slip").child(id);
                        ref.child(nim).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    String ket = snapshot.child("ket").getValue().toString().trim();
                                    if (ket.equals("Lunas")) {
                                        ok(id, nim);
                                    } else if (ket.equals("Belum bayar") || ket.equals("Ditolak")) {
                                        ly.setVisibility(View.VISIBLE);
                                        teks = "";
                                        teks += "Silahkan lakukan pembayaran\n";
                                        teks += "terlebih dahulu";
                                        teksHasil.setText(teks);
                                    } else if (ket.equals("Menunggu konfirmasi")) {
                                        ly.setVisibility(View.VISIBLE);
                                        teks = "";
                                        teks += "Dosen belum memverifikasi\n";
                                        teks += "bukti pembayaran anda\n\n";
                                        teks += "Silahkan hubungi dosen bersangkutan";
                                        teksHasil.setText(teks);
                                    }
                                } else {
                                    ly.setVisibility(View.VISIBLE);
                                    teks = "";
                                    teks += "Anda tidak terdaftar pada kelas ini";
                                    teksHasil.setText(teks);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });

        mScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codescan.startPreview();
                ly.setVisibility(View.GONE);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RC_PERMIS);
            } else {
                isPermissionGranted = true;
            }
        } else {
            isPermissionGranted = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted) {
            codescan.startPreview();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMIS) {
            if (grantResults.length <= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true;
                codescan.startPreview();
                teks = "";
                ly.setVisibility(View.GONE);
            } else {
                isPermissionGranted = false;
            }
        }
    }

    @Override
    protected void onPause() {
        codescan.releaseResources();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(QRScanner.this, MainActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("nama", getIntent().getStringExtra("nama"));
        intent.putExtra("no", getIntent().getStringExtra("no"));
        startActivity(intent);
        finish();
    }

    private void ok(String id, String nim) {
        Date date = new Date();
        String tgl = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        String jam = DateFormat.getTimeInstance().format(date);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("absensi").child(nim);
        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    ly.setVisibility(View.VISIBLE);
                    teksHasil.setText("Anda sudah absen dikelas ini");
                } else {
                    modelAbsen model = new modelAbsen();
                    model.setKode(id);
                    model.setNim(nim);
                    model.setNama(getIntent().getStringExtra("nama"));
                    model.setTgl(tgl);
                    model.setJam(jam);
                    model.setKet("Menunggu konfirmasi");

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("jadwalkuliah").child(id);
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String nip = snapshot.child("nip").getValue().toString();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("absen").child(nip);
                            DatabaseReference nb = reference.child(id);
                            nb.child(nim).setValue(model);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    db.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ly.setVisibility(View.VISIBLE);
                                teks ="";
                                teks += nim +"\n";
                                teks += getIntent().getStringExtra("nama")+"\n";
                                teks += tgl+"\n";
                                teks += jam;
                                teksHasil.setText(teks);
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