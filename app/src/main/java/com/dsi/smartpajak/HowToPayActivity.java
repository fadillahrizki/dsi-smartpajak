package com.dsi.smartpajak;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsi.smartpajak.adapters.PBBDetailAdapter;
import com.dsi.smartpajak.adapters.PayStepAdapter;
import com.dsi.smartpajak.helpers.Tools;
import com.dsi.smartpajak.models.PBB;
import com.dsi.smartpajak.models.PayStep;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.APICallback;
import com.dsi.smartpajak.network.PBBCallback;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.dsi.smartpajak.services.DownloadService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HowToPayActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private API api;

    private Call<APICallback> callbackCall = null;

    private PayStepAdapter payStepAdapter;

    private SwipeRefreshLayout swipeRefreshLyt;

    private TextView howToPayTv;

    private RecyclerView recyclerView;

    private RelativeLayout loaderLyt;

    private LinearLayout errorLyt;
    private TextView errorTv;
    private TextView errorRefreshTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ServiceGenerator.create(API.class);

        setContentView(R.layout.activity_how_to_pay);

        Toolbar toolbar = findViewById(R.id.toolbar);

        swipeRefreshLyt = findViewById(R.id.swipe_refresh_lyt);

        howToPayTv = findViewById(R.id.how_to_pay_tv);

        recyclerView = findViewById(R.id.recycler_view);

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

        Spannable titleCheck = new SpannableString("CARA");
        titleCheck.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titleCheck.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        howToPayTv.setText(titleCheck);

        Spannable titlePBB = new SpannableString(" BAYAR");
        titlePBB.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titlePBB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        howToPayTv.append(titlePBB);

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
                callbackCall = api.howToPay();
                callbackCall.enqueue(new Callback<APICallback>() {
                    @Override
                    public void onResponse(Call<APICallback> call, Response<APICallback> response) {
                        APICallback resp = response.body();

                        if (resp.error == null) {
                            showDataView(resp.pay_steps);
                        } else {
                            showErrorView();
                        }
                    }

                    @Override
                    public void onFailure(Call<APICallback> call, Throwable t) {
                        if ( ! call.isCanceled()) {
                            showErrorView();
                        }
                    }
                });
            }
        }, 1000);
    }

    private void showDataView(List<PayStep> paySteps) {
        swipeProgress(false);

        loaderLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        payStepAdapter = new PayStepAdapter(getApplicationContext(), recyclerView, paySteps);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(payStepAdapter);

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
