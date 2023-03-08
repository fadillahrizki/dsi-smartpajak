package com.dsi.smartpajak.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsi.smartpajak.R;
import com.dsi.smartpajak.adapters.PajakAdapter;
import com.dsi.smartpajak.adapters.TunggakanAdapter;
import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.helpers.Tools;
import com.dsi.smartpajak.models.Pajak;
import com.dsi.smartpajak.models.User;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.APICallback;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.dsi.smartpajak.services.DownloadService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TunggakanFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private API api;
    private Gson gson;
    private CacheManager cacheManager;
    private User user;

    private TunggakanAdapter tunggakanAdapter;

    private Call<APICallback> callbackCall = null;

    private String totalTunggakan;
    private String downloadFile;
    private String downloadURL;

    private SwipeRefreshLayout swipeRefreshLyt;

    private TextView titleTv;

    private TextView totalTunggakanTv;

    private RelativeLayout downloadBtn;

    private RelativeLayout loaderLyt;

    private LinearLayout noDataLyt;
    private TextView noDataTv;
    private TextView refreshTv;

    private LinearLayout errorLyt;
    private TextView errorTv;
    private TextView errorRefreshTv;

    public TunggakanFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ServiceGenerator.create(API.class);
        cacheManager = new CacheManager(getContext());
        gson = new Gson();
        user = gson.fromJson(cacheManager.getUser(),User.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tunggakan, container, false);

        swipeRefreshLyt = rootView.findViewById(R.id.swipe_refresh_lyt);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        titleTv = rootView.findViewById(R.id.title_tv);

        totalTunggakanTv = rootView.findViewById(R.id.total_tunggakan_tv);

        downloadBtn = rootView.findViewById(R.id.download_btn);

        loaderLyt = rootView.findViewById(R.id.loader_lyt);

        noDataLyt = rootView.findViewById(R.id.no_data_lyt);
        noDataTv = rootView.findViewById(R.id.no_data_tv);
        refreshTv = rootView.findViewById(R.id.refresh_tv);

        errorLyt = rootView.findViewById(R.id.error_lyt);
        errorTv = rootView.findViewById(R.id.error_tv);
        errorRefreshTv = rootView.findViewById(R.id.error_refresh_tv);

        titleTv.setText(user.nama_usaha);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        tunggakanAdapter = new TunggakanAdapter(getContext(), new ArrayList<Pajak>());

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tunggakanAdapter);

        loadData(false, false);

        return rootView;
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
        if ( ! swipe) {
            showLoadingView();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callbackCall = api.tunggakan(user.api_key);
                callbackCall.enqueue(new Callback<APICallback>() {
                    @Override
                    public void onResponse(Call<APICallback> call, Response<APICallback> response) {
                        APICallback resp = response.body();

                        if (resp != null) {
                            totalTunggakan = resp.total_tunggakan;
                            downloadFile = resp.download_file;
                            downloadURL = resp.download_url;

                            tunggakanAdapter.resetData();

                            showData(resp.tunggakan, swipe);
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

    private void showData(final List<Pajak> results, boolean swipe) {
        swipeProgress(false);

        if (swipe) {
            tunggakanAdapter.resetData();
        }

        if (results.size() > 0) {
            showDataView(results);

            totalTunggakanTv.setText(totalTunggakan);

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Mendownload file ...", Toast.LENGTH_SHORT).show();
                    getActivity().startService(DownloadService.getDownloadService(getContext(), downloadFile, downloadURL));
                }
            });
        } else {
            showNoDataView("Tidak ada data");
        }
    }

    private void showDataView(final List<Pajak> results) {
        loaderLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
        noDataLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.VISIBLE);

        tunggakanAdapter.insertData(results);

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
        noDataLyt.setVisibility(View.GONE);
    }

    private void showErrorView() {
        swipeProgress(false);

        loaderLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.VISIBLE);

        if ( ! Tools.networkChecker(getContext())) {
            errorTv.setText("Gagal memuat data\nSilahkan periksa koneksi internet anda");
        } else {
            errorTv.setText("Gagal memuat data\nTerjadi kesalahan server");
        }

        noDataLyt.setVisibility(View.GONE);

        errorRefreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(true, false);
            }
        });
    }

    private void showNoDataView(String text) {
        loaderLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
        noDataLyt.setVisibility(View.VISIBLE);
        noDataTv.setText(text);

        refreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData( false, false);
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
