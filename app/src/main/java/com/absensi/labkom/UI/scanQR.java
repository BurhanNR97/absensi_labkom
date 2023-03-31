package com.absensi.labkom.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.absensi.labkom.CaptureCamera;
import com.absensi.labkom.R;
import com.absensi.labkom.model.modelAbsen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.util.Date;

public class scanQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        scanON();
    }

    private void scanON() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Tekan volume atas untuk menyalakan flash"
                            +"\n"
                            +"Tekan volume bawah untuk menonaktifkan flash");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureCamera.class);
        mulai.launch(options);

    }

    ActivityResultLauncher<ScanOptions> mulai = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String id = result.getContents();

        }
    });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(scanQR.this, MainActivity.class);
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
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(scanQR.this);
                    builder.setMessage("Anda sudah absen");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            onBackPressed();
                        }
                    }).show();
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
                                Toast.makeText(scanQR.this, "Berhasil\n"+tgl+"\n"+jam, Toast.LENGTH_SHORT).show();
                                onBackPressed();
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