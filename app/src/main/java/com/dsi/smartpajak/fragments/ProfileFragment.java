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

public class ProfileFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private API api;
    private Gson gson;
    private CacheManager cacheManager;
    private User user;

    public ProfileFragment() {}

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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView npwpdTv = rootView.findViewById(R.id.npwpd_tv);
        TextView namaUsahaTv = rootView.findViewById(R.id.nama_usaha_tv);
        TextView alamatTv = rootView.findViewById(R.id.alamat_tv);
        TextView kecamatanTv = rootView.findViewById(R.id.kecamatan_tv);
        TextView desaTv = rootView.findViewById(R.id.desa_tv);
        TextView kontakTv = rootView.findViewById(R.id.kontak_tv);

        npwpdTv.setText("" + user.npwpd);
        namaUsahaTv.setText("" + user.nama_usaha);
        alamatTv.setText("" + user.alamat);
        kecamatanTv.setText("" + user.kecamatan);
        desaTv.setText("" + user.kelurahan);
        kontakTv.setText("" + user.hp);

        return rootView;
    }
}
