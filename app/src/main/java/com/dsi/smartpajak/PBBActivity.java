package com.dsi.smartpajak;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsi.smartpajak.adapters.YearAdapter;
import com.dsi.smartpajak.helpers.Tools;
import com.dsi.smartpajak.models.PBB;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.PBBCallback;
import com.dsi.smartpajak.network.ServiceGenerator;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PBBActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private API api;

    private Call<PBBCallback> callbackCall = null;

    private String number;

    private PBB pbb;
    private int[] years;
    private int currentYear;

    private YearAdapter yearAdapter;

    private SwipeRefreshLayout swipeRefreshLyt;

    private TextView pbbOnlineTv;
    private TextView kabAsahanTv;

    private TextView titleTv;

    private TextView detailTv;

    private RecyclerView yearsRv;
    private TextView letakObjekPajakTv;
    private TextView namaAlamatWajibPajakTv;
    private TextView luasOPBumiTv;
    private TextView kelasOPBumiTv;
    private TextView njopPerMOPBumiTv;
    private TextView totalNjopOPBumiTv;
    private TextView luasOPBangunanTv;
    private TextView kelasOPBangunanTv;
    private TextView njopPerMOPBangunanTv;
    private TextView totalNjopOPBangunanTv;
    private TextView njopDasarTv;
    private TextView njopTkpTv;
    private TextView njopPbbTv;
    private TextView pbbTerhutangTv;
    private TextView tanggalJatuhTempoTv;
    private TextView jumlahPembayaranTv;
    private TextView statusPembayaranTv;

    private RelativeLayout loaderLyt;

    private LinearLayout errorLyt;
    private TextView errorTv;
    private TextView errorRefreshTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ServiceGenerator.create(API.class);

        setContentView(R.layout.activity_pbb);

        number = getIntent().getStringExtra("number");

        if (number == null) {
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        swipeRefreshLyt = findViewById(R.id.swipe_refresh_lyt);
        titleTv = findViewById(R.id.title_tv);
        pbbOnlineTv = findViewById(R.id.pbb_online_tv);
        kabAsahanTv = findViewById(R.id.kab_asahan_tv);
        detailTv = findViewById(R.id.detail_tv);
        yearsRv = findViewById(R.id.years_rv);
        letakObjekPajakTv = findViewById(R.id.letak_objek_pajak_tv);
        namaAlamatWajibPajakTv = findViewById(R.id.nama_alamat_wajib_pajak_tv);
        luasOPBumiTv = findViewById(R.id.luas_op_bumi_tv);
        kelasOPBumiTv = findViewById(R.id.kelas_op_bumi_tv);
        njopPerMOPBumiTv = findViewById(R.id.njop_per_m_op_bumi_tv);
        totalNjopOPBumiTv = findViewById(R.id.total_njop_op_bumi_tv);
        luasOPBangunanTv = findViewById(R.id.luas_op_bangunan_tv);
        kelasOPBangunanTv = findViewById(R.id.kelas_op_bangunan_tv);
        njopPerMOPBangunanTv = findViewById(R.id.njop_per_m_op_bangunan_tv);
        totalNjopOPBangunanTv = findViewById(R.id.total_njop_op_bangunan_tv);
        njopDasarTv = findViewById(R.id.njop_dasar_tv);
        njopTkpTv = findViewById(R.id.njop_tkp_tv);
        njopPbbTv = findViewById(R.id.njop_pbb_tv);
        pbbTerhutangTv = findViewById(R.id.pbb_terhutang_tv);
        tanggalJatuhTempoTv = findViewById(R.id.tanggal_jatuh_tempo_tv);
        jumlahPembayaranTv = findViewById(R.id.jumlah_pembayaran_tv);
        statusPembayaranTv = findViewById(R.id.status_pembayaran_tv);

        loaderLyt = findViewById(R.id.loader_lyt);

        errorLyt = findViewById(R.id.error_lyt);
        errorTv = findViewById(R.id.error_tv);
        errorRefreshTv = findViewById(R.id.error_refresh_tv);

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

        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Spannable titlePbb = new SpannableString("PBB");
        titlePbb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titlePbb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pbbOnlineTv.setText(titlePbb);

        Spannable titleOnline = new SpannableString(" ONLINE");
        titleOnline.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titleOnline.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pbbOnlineTv.append(titleOnline);

        Spannable titleKab = new SpannableString("KABUPATEN");
        titleKab.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titleKab.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        kabAsahanTv.setText(titleKab);

        Spannable titleAsahan = new SpannableString(" ASAHAN");
        titleAsahan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titleAsahan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        kabAsahanTv.append(titleAsahan);

        loadData(false, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    private void loadData(final boolean retry, final boolean swipe) {
        if (retry && ! swipe) {
            showLoadingView();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callbackCall = api.pbb(number, currentYear);
                callbackCall.enqueue(new Callback<PBBCallback>() {
                    @Override
                    public void onResponse(Call<PBBCallback> call, Response<PBBCallback> response) {
                        PBBCallback resp = response.body();

                        if (resp.status != null) {
                            if (resp.status.equals("success")) {
                                pbb = resp.pbb;
                                years = resp.years;

                                showDataView();
                            } else if (resp.status.equals("error") && resp.msg != null) {
                                Toast.makeText(getApplicationContext(), "" + resp.msg, Toast.LENGTH_LONG).show();

                                finish();
                            }
                        } else {
                            showErrorView();
                        }
                    }

                    @Override
                    public void onFailure(Call<PBBCallback> call, Throwable t) {
                        if ( ! call.isCanceled()) {
                            showErrorView();
                        }
                    }
                });
            }
        }, 1000);
    }

    private void showDataView() {
        swipeProgress(false);

        loaderLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.VISIBLE);

        yearsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        yearAdapter = new YearAdapter(getApplicationContext(), yearsRv, years, currentYear);

        yearsRv.setItemAnimator(new DefaultItemAnimator());
        yearsRv.setAdapter(yearAdapter);

        titleTv.setText("Tahun Pajak " + currentYear);

        detailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PBBActivity.this, PBBDetailActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("wp", pbb.nama_wp);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        letakObjekPajakTv.setText(pbb.letak_objek_pajak);
        namaAlamatWajibPajakTv.setText(pbb.nama_alamat_wajib_pajak);
        luasOPBumiTv.setText(pbb.luas_op_bumi);
        kelasOPBumiTv.setText(pbb.kelas_op_bumi);
        njopPerMOPBumiTv.setText(pbb.njop_per_m_op_bumi);
        totalNjopOPBumiTv.setText(pbb.total_njop_op_bumi);
        luasOPBangunanTv.setText(pbb.luas_op_bangunan);
        kelasOPBangunanTv.setText(pbb.kelas_op_bangunan);
        njopPerMOPBangunanTv.setText(pbb.njop_per_m_op_bangunan);
        totalNjopOPBangunanTv.setText(pbb.total_njop_op_bangunan);
        njopDasarTv.setText(pbb.njop_dasar);
        njopTkpTv.setText(pbb.njop_tkp);
        njopPbbTv.setText(pbb.njop_pbb);
        pbbTerhutangTv.setText(pbb.pbb_terhutang);
        tanggalJatuhTempoTv.setText(pbb.tanggal_jatuh_tempo);
        jumlahPembayaranTv.setText(pbb.jumlah_pembayaran);

        GradientDrawable statusBg = (GradientDrawable) statusPembayaranTv.getBackground();

        if (pbb.status_pembayaran == 1) {
            statusPembayaranTv.setText("LUNAS");
            statusBg.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
        } else {
            statusPembayaranTv.setText("TERHUTANG");
            statusBg.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        }

        swipeRefreshLyt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) {
                    callbackCall.cancel();
                }

                loadData(false, true);
            }
        });

        yearAdapter.setOnItemClickListener(new YearAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int obj, int position) {
                currentYear = obj;
                showLoadingView();
                loadData(false, false);
            }
        });
    }

    private void showLoadingView() {
        loaderLyt.setVisibility(View.VISIBLE);
        swipeRefreshLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
    }

    private void showErrorView() {
        swipeProgress(false);

        loaderLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.VISIBLE);

        if ( ! Tools.networkChecker(getApplicationContext())) {
            errorTv.setText("Gagal memuat data\nSilahkan periksa koneksi internet anda");
        } else {
            errorTv.setText("Gagal memuat data\nTerjadi kesalahan server");
        }

        errorRefreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(true, false);
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if ( ! show ) {
            swipeRefreshLyt.setRefreshing(show);

            return;
        }

        swipeRefreshLyt.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLyt.setRefreshing(show);
            }
        });
    }
}
