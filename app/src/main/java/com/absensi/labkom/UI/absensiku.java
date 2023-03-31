package com.absensi.labkom.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.absensi.labkom.R;
import com.absensi.labkom.adapter.adpAbsensiku;
import com.absensi.labkom.model.modelAbsen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class absensiku extends AppCompatActivity {
    ArrayList<modelAbsen> list;
    ListView layout;
    TextView teks;
    adpAbsensiku adp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensiku);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.hijau));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_konfirAbsensiku);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Riwayat Absensi");

        list = new ArrayList<>();
        layout = findViewById(R.id.rvAbsensiku);
        teks = findViewById(R.id.kosongAbsensiku);
        adp = new adpAbsensiku(this);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("absensi");
        db.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelAbsen model = dataSnapshot.getValue(modelAbsen.class);
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
        Intent intent = new Intent(absensiku.this, MainActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("nama", getIntent().getStringExtra("nama"));
        intent.putExtra("no", getIntent().getStringExtra("no"));
        startActivity(intent);
        finish();
    }
}