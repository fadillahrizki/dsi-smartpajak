package com.dsi.smartpajak;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.helpers.Tools;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.APICallback;
import com.dsi.smartpajak.network.ServiceGenerator;
import com.google.gson.Gson;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PADLoginActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    CacheManager cacheManager;
    API api;

    Call<APICallback> loginCb = null;

    boolean isShowPassword = false;

    MaskedEditText usernameEt;
    EditText passwordEt;
    ImageView passwordVisibilityIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cacheManager = new CacheManager(this);
        api = ServiceGenerator.create(API.class);

        setContentView(R.layout.activity_pad_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView loginTv = findViewById(R.id.login_tv);
        usernameEt = findViewById(R.id.username_et);
        passwordEt = findViewById(R.id.password_et);
        passwordVisibilityIv = findViewById(R.id.password_visibility_iv);
        TextView loginBtn = findViewById(R.id.login_btn);

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

        Spannable titleLogin = new SpannableString("LOGIN");
        titleLogin.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titleLogin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTv.setText(titleLogin);

        Spannable titlePAD = new SpannableString(" PAD");
        titlePAD.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titlePAD.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTv.append(titlePAD);

        passwordVisibilityIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ! isShowPassword ) {
                    isShowPassword = true;
                    passwordVisibilityIv.setImageResource(R.drawable.ic_visibility_soft_grey_18dp);
                    passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    isShowPassword = false;
                    passwordVisibilityIv.setImageResource(R.drawable.ic_visibility_off_soft_grey_18dp);
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void loginRequest() {
        if (usernameEt.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username wajib diisi", Toast.LENGTH_LONG).show();
        } else if (passwordEt.getText().toString().length() < 1) {
            Toast.makeText(getApplicationContext(), "Password wajib diisi", Toast.LENGTH_LONG).show();
        } else if ( ! Tools.networkChecker(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
        } else {
            String username = usernameEt.getText().toString().trim();
            String password = passwordEt.getText().toString();

            loginCb = api.login(username, password);
            loginCb.enqueue(new Callback<APICallback>() {
                @Override
                public void onResponse(Call<APICallback> call, Response<APICallback> response) {
                    APICallback resp = response.body();

                    if (resp.user != null) {
                        Gson gson = new Gson();

                        cacheManager.setLogin(true);
                        cacheManager.setUser(gson.toJson(resp.user));

                        Toast.makeText(getApplicationContext(), "Anda berhasil login ke akun", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PADLoginActivity.this, MainPADActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
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
    }
}
