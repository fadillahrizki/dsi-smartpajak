package com.dsi.smartpajak.fragments;

import android.content.Intent;
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

import com.dsi.smartpajak.MainPADActivity;
import com.dsi.smartpajak.PADLoginActivity;
import com.dsi.smartpajak.R;
import com.dsi.smartpajak.SSPDDetailActivity;
import com.dsi.smartpajak.adapters.PajakAdapter;
import com.dsi.smartpajak.adapters.SSPDAdapter;
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

public class SSPDFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private API api;
    private Gson gson;
    private CacheManager cacheManager;
    private User user;

    private SSPDAdapter sspdAdapter;

    private Call<APICallback> callbackCall = null;

    private int nextPage = 0;
    private int pages = 0;

    private SwipeRefreshLayout swipeRefreshLyt;

    private RelativeLayout loaderLyt;

    private LinearLayout noDataLyt;
    private TextView noDataTv;
    private TextView refreshTv;

    private LinearLayout errorLyt;
    private TextView errorTv;
    private TextView errorRefreshTv;

    public SSPDFragment() {}

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
        View rootView = inflater.inflate(R.layout.fragment_sspd, container, false);

        swipeRefreshLyt = rootView.findViewById(R.id.swipe_refresh_lyt);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        loaderLyt = rootView.findViewById(R.id.loader_lyt);

        noDataLyt = rootView.findViewById(R.id.no_data_lyt);
        noDataTv = rootView.findViewById(R.id.no_data_tv);
        refreshTv = rootView.findViewById(R.id.refresh_tv);

        errorLyt = rootView.findViewById(R.id.error_lyt);
        errorTv = rootView.findViewById(R.id.error_tv);
        errorRefreshTv = rootView.findViewById(R.id.error_refresh_tv);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        sspdAdapter = new SSPDAdapter(getContext(), recyclerView, new ArrayList<Pajak>());

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sspdAdapter);

        loadData(1, false, false);

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

    private void loadData(final int page, final boolean retry, final boolean swipe) {
        if ((page == 1 || retry) && ! swipe) {
            showLoadingView();
        } else if (page > 1) {
            sspdAdapter.setLoading();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callbackCall = api.sspd(user.api_key, page);
                callbackCall.enqueue(new Callback<APICallback>() {
                    @Override
                    public void onResponse(Call<APICallback> call, Response<APICallback> response) {
                        APICallback resp = response.body();

                        if (resp != null) {
                            pages = resp.pages;
                            nextPage = page + 1;

                            if (page == 1) {
                                sspdAdapter.resetData();
                            }

                            showData(resp.sspd, swipe);
                        } else {
                            showErrorView(page);
                        }
                    }

                    @Override
                    public void onFailure(Call<APICallback> call, Throwable t) {
                        if ( ! call.isCanceled()) {
                            showErrorView(page);
                        }
                    }
                });
            }
        }, 1000);
    }

    private void showData(final List<Pajak> results, boolean swipe) {
        swipeProgress(false);

        if (swipe) {
            sspdAdapter.resetData();
        }

        if (results.size() > 0) {
            showDataView(results);
        } else {
            showNoDataView("Tidak ada data");
        }
    }

    private void showDataView(final List<Pajak> results) {
        loaderLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
        noDataLyt.setVisibility(View.GONE);
        swipeRefreshLyt.setVisibility(View.VISIBLE);

        if (nextPage <= 2) {
            Pajak pajakTitle = new Pajak();
            pajakTitle.type = 0;
            List<Pajak> pajakList = new ArrayList<>();
            pajakList.add(pajakTitle);
            sspdAdapter.insertData(pajakList);
        }

        sspdAdapter.insertData(results);

        swipeRefreshLyt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) {
                    callbackCall.cancel();
                }

                nextPage = 0;

                loadData(1, false, true);
            }
        });

        sspdAdapter.setOnLoadMoreListener(new SSPDAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pages >= nextPage && nextPage != 0) {
                    loadData(nextPage, false, false);
                } else {
                    sspdAdapter.setLoaded();
                }
            }
        });

        sspdAdapter.setOnItemClickListener(new SSPDAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final Pajak obj, int position) {
                Intent intent = new Intent(getContext(), SSPDDetailActivity.class);
                intent.putExtra("sspd", obj);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void showLoadingView() {
        loaderLyt.setVisibility(View.VISIBLE);
        swipeRefreshLyt.setVisibility(View.GONE);
        errorLyt.setVisibility(View.GONE);
        noDataLyt.setVisibility(View.GONE);
    }

    private void showErrorView(final int page) {
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
                loadData(page,true, false);
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
                loadData(1, false, false);
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
