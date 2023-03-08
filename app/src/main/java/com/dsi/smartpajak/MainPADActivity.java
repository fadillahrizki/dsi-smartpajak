package com.dsi.smartpajak;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dsi.smartpajak.fragments.ProfileFragment;
import com.dsi.smartpajak.fragments.SKPDFragment;
import com.dsi.smartpajak.fragments.SSPDFragment;
import com.dsi.smartpajak.fragments.TunggakanFragment;
import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.helpers.Tools;
import com.dsi.smartpajak.models.User;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.APICallback;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPADActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private static final String TAG_SKPD = "SKPDFragment";
    private static final String TAG_TUNGGAKAN = "TunggakanFragment";
    private static final String TAG_SSPD = "SSPDFragment";
    private static final String TAG_PROFILE = "ProfileFragment";
    private static String CURRENT_TAG = TAG_SKPD;

    API api;
    Gson gson;
    CacheManager cacheManager;
    User user;

    private boolean shouldLoadHomeFragOnBackPress = true;

    public static int navItemIndex = 0;

    BottomNavigationView bottomNv;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ServiceGenerator.create(API.class);
        cacheManager = new CacheManager(this);
        gson = new Gson();
        user = gson.fromJson(cacheManager.getUser(),User.class);

        if ( ! cacheManager.isLoggedIn()) {
            finish();
        }

        mHandler = new Handler();

        setContentView(R.layout.activity_main_pad);

        Toolbar toolbar = findViewById(R.id.toolbar);
        bottomNv = findViewById(R.id.bottom_nv);

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

        bottomNv.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_skpd:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_SKPD;
                        break;
                    case R.id.nav_tunggakan:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_TUNGGAKAN;
                        break;
                    case R.id.nav_sspd:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SSPD;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                }

                loadFragment();

                return true;
            }
        });

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_SKPD;
            loadFragment();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        sendFCMTokenToServer();
    }

    @Override
    public void onBackPressed() {
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_SSPD;
                loadFragment();
                bottomNv.setSelectedItemId(R.id.nav_skpd);
                return;
            }
        }

        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pad_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFragment() {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_lyt, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    private Fragment getFragment() {
        switch (navItemIndex) {
            case 1:
                return new TunggakanFragment();
            case 2:
                return new SSPDFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new SKPDFragment();
        }
    }

    private void sendFCMTokenToServer() {
        String FCMToken = FirebaseInstanceId.getInstance().getToken();
        String FCMTokenRemoved = cacheManager.getFCMTokenRemoved();
        String deviceName = Tools.getDeviceName();
        String deviceSerial = Tools.getDeviceSerial();
        String deviceOs = Tools.getDeviceOS();

        if (FCMTokenRemoved != null) {
            Call<APICallback> apiCb = api.registerFCMToken(user.api_key,FCMToken,deviceName,deviceSerial,deviceOs);
            apiCb.enqueue(new Callback<APICallback>() {
                @Override
                public void onResponse(Call<APICallback> call, Response<APICallback> response) {
                    APICallback resp = response.body();

                    if ( resp != null && resp.success != null) {
                        FirebaseMessaging.getInstance().subscribeToTopic("global");

                        cacheManager.setFCMTokenRemoved(null);
                    }
                }

                @Override
                public void onFailure(Call<APICallback> call, Throwable t) {

                }
            });
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("global");
        }
    }

    private void logout() {
        cacheManager.setLogin(false);
        cacheManager.setUser(null);

        Toast.makeText(getApplicationContext(), "Anda telah keluar dari akun", Toast.LENGTH_LONG).show();

        finish();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
