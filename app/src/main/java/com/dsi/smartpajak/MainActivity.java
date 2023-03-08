package com.dsi.smartpajak;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.models.JenisHak;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.APICallback;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.dsi.smartpajak.network.ServiceGenerator2;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;
import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    CacheManager cacheManager;

    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 17326;

    API api;

    Call<APICallback> apiCb = null;

    String facebook = null;
    String instagram = null;

    LinearLayout pbbCheckBtn;
    LinearLayout padHowToPayBtn;
    LinearLayout padLoginBtn;
    LinearLayout padDashboardBtn;
    LinearLayout askBappendaBtn;

    LinearLayout websiteBphtb;
    LinearLayout bphtb;
    LinearLayout kalkulatorBphtb;
    LinearLayout kalkulatorPbb;

    ImageView icon_websiteBphtb;
    ImageView icon_bphtb;
    ImageView icon_kalkulatorBphtb;
    ImageView icon_kalkulatorPbb;

    String padUrl = "";
    String websiteBphtbUrl = "";
    String bphtbUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cacheManager = new CacheManager(this);
        api = ServiceGenerator.create(API.class);

        requestPermissions();

        setContentView(R.layout.activity_main);

        pbbCheckBtn = findViewById(R.id.pbb_check_btn);
        padHowToPayBtn = findViewById(R.id.pad_how_to_pay_btn);
        padLoginBtn = findViewById(R.id.pad_login_btn);
        padDashboardBtn = findViewById(R.id.pad_dashboard_btn);
        askBappendaBtn = findViewById(R.id.ask_bappenda_btn);

        websiteBphtb = findViewById(R.id.website_bphtb);
        bphtb = findViewById(R.id.bphtb);
        kalkulatorBphtb = findViewById(R.id.kalkulator_bphtb);
        kalkulatorPbb = findViewById(R.id.kalkulator_pbb);

        icon_websiteBphtb = findViewById(R.id.icon_website_bphtb);
        icon_bphtb = findViewById(R.id.icon_bphtb);
        icon_kalkulatorBphtb = findViewById(R.id.icon_kalkulator_bphtb);
        icon_kalkulatorPbb = findViewById(R.id.icon_kalkulator_pbb);

        ServiceGenerator2.create(API.class).getUrl().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                JsonObject url = body.getAsJsonObject("url");
                JsonObject icon = body.getAsJsonObject("icon");

                padUrl = url.get("pad").getAsString();
                websiteBphtbUrl = url.get("website").getAsString();
                bphtbUrl = url.get("bphtb").getAsString();

                Glide
                        .with(MainActivity.this)
                        .load(icon.get("website").getAsString())
                        .placeholder(R.drawable.website)
                        .into(icon_websiteBphtb);
                Glide
                        .with(MainActivity.this)
                        .load(icon.get("bphtb").getAsString())
                        .placeholder(R.drawable.bphtb)
                        .into(icon_bphtb);
                Glide
                        .with(MainActivity.this)
                        .load(icon.get("kalkulator_bphtb").getAsString())
                        .placeholder(R.drawable.kalkulator_bphtb)
                        .into(icon_kalkulatorBphtb);
                Glide
                        .with(MainActivity.this)
                        .load(icon.get("kalkulator_pbb").getAsString())
                        .placeholder(R.drawable.kalkulator_pbb)
                        .into(icon_kalkulatorPbb);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });

        websiteBphtb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteBphtbUrl));
                startActivity(browserIntent);
            }
        });

        bphtb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bphtbUrl));
                startActivity(browserIntent);
            }
        });

        kalkulatorBphtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, KalkulatorBphtbActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        kalkulatorPbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, KalkulatorPbbActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        pbbCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, PBBCheckActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        padLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(padUrl));
                startActivity(browserIntent);
//                Intent mainIntent = new Intent(MainActivity.this, PADLoginActivity.class);
//                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        padDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, MainPADActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        padHowToPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, HowToPayActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        askBappendaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socmedDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cacheManager.isLoggedIn()) {
            padLoginBtn.setVisibility(View.GONE);
            padDashboardBtn.setVisibility(View.VISIBLE);
        } else {
            padLoginBtn.setVisibility(View.VISIBLE);
            padDashboardBtn.setVisibility(View.GONE);
        }

        checkUpdate();

        apiCb = api.sosmed();
        apiCb.enqueue(new Callback<APICallback>() {
            @Override
            public void onResponse(Call<APICallback> call, Response<APICallback> response) {
                APICallback resp = response.body();

                if (resp.facebook != null && resp.instagram != null) {
                    facebook = resp.facebook;
                    instagram = resp.instagram;
                } else if (resp.error != null) {
                    Toast.makeText(getApplicationContext(), resp.error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APICallback> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan server", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener((InstallStateUpdatedListener) this);
        }
    }

    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(MainActivity.this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void checkUpdate(){
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(listener);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)){
                    requestUpdate(appUpdateInfo);
                }
            }
        });
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo){
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, FLEXIBLE,MainActivity.this,MY_REQUEST_CODE);
            resume();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    Toast.makeText(this,"Aplikasi telah diperbarui", Toast.LENGTH_LONG).show();
                    break;
                case RESULT_IN_APP_UPDATE_FAILED:
                    Toast.makeText(this,"Aplikasi gagal diperbarui", Toast.LENGTH_LONG).show();
            }
        }
    }

    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                notifyUser();
            }
        }
    };

    private void notifyUser() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.message), "Pembaruan baru saja di download", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Mulai Ulang", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    private void resume(){
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                    notifyUser();
                }
            }
        });
    }

    private void socmedDialog() {
        String[] items = {"Facebook", "Instagram"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook));
                    startActivity(browserIntent);
                } else if (which == 1) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram));
                    startActivity(browserIntent);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
