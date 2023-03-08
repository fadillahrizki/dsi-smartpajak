package com.dsi.smartpajak;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;
import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dsi.smartpajak.helpers.CacheManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainOldActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    CacheManager cacheManager;

    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 17326;

    RelativeLayout pbbCheckBtn;
    RelativeLayout padLoginBtn;
    RelativeLayout padDashboardBtn;
    RelativeLayout padHowToPayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cacheManager = new CacheManager(this);

        requestPermissions();

        setContentView(R.layout.activity_main);

        pbbCheckBtn = findViewById(R.id.pbb_check_btn);
        padLoginBtn = findViewById(R.id.pad_login_btn);
        padDashboardBtn = findViewById(R.id.pad_dashboard_btn);
        padHowToPayBtn = findViewById(R.id.pad_how_to_pay_btn);

        pbbCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainOldActivity.this, PBBCheckActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        padLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainOldActivity.this, PADLoginActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        padDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainOldActivity.this, MainPADActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        padHowToPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainOldActivity.this, HowToPayActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        Toast.makeText(MainOldActivity.this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
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
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, FLEXIBLE, MainOldActivity.this,MY_REQUEST_CODE);
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
}
