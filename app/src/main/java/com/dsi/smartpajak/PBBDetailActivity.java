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

import com.dsi.smartpajak.adapters.PBBDetailAdapter;
import com.dsi.smartpajak.adapters.YearAdapter;
import com.dsi.smartpajak.helpers.Tools;
import com.dsi.smartpajak.models.PBB;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.PBBCallback;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.dsi.smartpajak.services.DownloadService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PBBDetailActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private API api;

    private Call<PBBCallback> callbackCall = null;

    private String number;
    private String wp;

    private PBB pbb;
    private List<PBB> items;
    private String jumlahTerhutang;
    private String jumlahDenda;
    private String totalJumlah;

    private PBBDetailAdapter detailAdapter;

    private SwipeRefreshLayout swipeRefreshLyt;

    private TextView pbbOnlineTv;
    private TextView kabAsahanTv;

    private TextView letakObjekPajakTv;
    private TextView namaAlamatWajibPajakTv;
    private RecyclerView itemsRv;
    private TextView totalPBBTerhutangTv;
    private TextView totalDendaTv;
    private TextView grandTotalTv;

    private TextView downloadPdfTv;

    private RelativeLayout loaderLyt;

    private LinearLayout errorLyt;
    private TextView errorTv;
    private TextView errorRefreshTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ServiceGenerator.create(API.class);

        setContentView(R.layout.activity_pbb_detail);

        number = getIntent().getStringExtra("number");
        wp = getIntent().getStringExtra("wp");

        if (number == null) {
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        swipeRefreshLyt = findViewById(R.id.swipe_refresh_lyt);
        pbbOnlineTv = findViewById(R.id.pbb_online_tv);
        kabAsahanTv = findViewById(R.id.kab_asahan_tv);
        letakObjekPajakTv = findViewById(R.id.letak_objek_pajak_tv);
        namaAlamatWajibPajakTv = findViewById(R.id.nama_alamat_wajib_pajak_tv);
        itemsRv = findViewById(R.id.items_rv);
        totalPBBTerhutangTv = findViewById(R.id.total_pbb_terhutang_tv);
        totalDendaTv = findViewById(R.id.total_denda_tv);
        grandTotalTv = findViewById(R.id.grand_total_tv);
        downloadPdfTv = findViewById(R.id.download_pdf_tv);

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
                callbackCall = api.pbbDetail(number);
                callbackCall.enqueue(new Callback<PBBCallback>() {
                    @Override
                    public void onResponse(Call<PBBCallback> call, Response<PBBCallback> response) {
                        PBBCallback resp = response.body();

                        if (resp.status != null) {
                            if (resp.status.equals("success")) {
                                pbb = resp.pbb;
                                items = resp.items;
                                jumlahTerhutang = resp.jumlah_terhutang;
                                jumlahDenda = resp.jumlah_denda;
                                totalJumlah = resp.total_jumlah;

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

        letakObjekPajakTv.setText(pbb.letak_objek_pajak);
        namaAlamatWajibPajakTv.setText(pbb.nama_alamat_wajib_pajak);

        itemsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        detailAdapter = new PBBDetailAdapter(getApplicationContext(), itemsRv, items);

        itemsRv.setItemAnimator(new DefaultItemAnimator());
        itemsRv.setAdapter(detailAdapter);

        totalPBBTerhutangTv.setText(jumlahTerhutang);
        totalDendaTv.setText(jumlahDenda);
        grandTotalTv.setText(totalJumlah);

        downloadPdfTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                String url = "http://pbb.asahankab.go.id:81/app/cetak-rincian?nop=" + number;
                Toast.makeText(getApplicationContext(), "Mendownload file ...", Toast.LENGTH_SHORT).show();
                startService(DownloadService.getDownloadService(getApplicationContext(), wp + " _ " + number + " _ " + formattedDate + ".pdf", url));
            }
        });

        swipeRefreshLyt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) {
                    callbackCall.cancel();
                }

                loadData(false, true);
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
