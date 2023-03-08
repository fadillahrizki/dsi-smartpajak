package com.dsi.smartpajak;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dsi.smartpajak.fragments.SKPDFragment;
import com.dsi.smartpajak.fragments.SSPDFragment;
import com.dsi.smartpajak.fragments.TunggakanFragment;
import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.models.Pajak;
import com.dsi.smartpajak.models.User;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

public class SSPDDetailActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sspd_detail);

        Pajak sspd = (Pajak) getIntent().getSerializableExtra("sspd");

        if (sspd == null) {
            Toast.makeText(getApplicationContext(), "Invalid data!", Toast.LENGTH_SHORT).show();

            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView masaPajakTv = findViewById(R.id.masa_pajak_tv);
        TextView noKasRegisterTv = findViewById(R.id.no_register_tv);
        TextView jumlahKetetapanTv = findViewById(R.id.jumlah_ketetapan_tv);
        TextView biayaAdminTv = findViewById(R.id.biaya_admin_tv);
        TextView dendaTv = findViewById(R.id.denda_tv);
        TextView tanggalTerimaTv = findViewById(R.id.tanggal_bayar_tv);
        TextView jumlahBayarTv = findViewById(R.id.jumlah_bayar_tv);

        masaPajakTv.setText("" + sspd.masa_pajak);
        noKasRegisterTv.setText("" + sspd.no_register);
        jumlahKetetapanTv.setText("" + sspd.jumlah_ketetapan);
        biayaAdminTv.setText("" + sspd.biaya_admin);
        dendaTv.setText("" + sspd.denda);
        tanggalTerimaTv.setText("" + sspd.tanggal_terima);
        jumlahBayarTv.setText("" + sspd.jumlah_setoran);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
