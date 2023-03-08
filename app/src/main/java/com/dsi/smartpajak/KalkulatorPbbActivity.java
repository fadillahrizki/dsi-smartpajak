package com.dsi.smartpajak;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dsi.smartpajak.helpers.NumberTextWatcher;
import com.dsi.smartpajak.models.JenisHak;
import com.dsi.smartpajak.models.JenisPerolehan;
import com.dsi.smartpajak.network.API;
import com.dsi.smartpajak.network.ServiceGenerator2;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KalkulatorPbbActivity extends AppCompatActivity {

    EditText edtLuasTanah;
    EditText edtNjopTanah;
    TextView tvLuasNjopTanah;
    Double luasNjopTanah = 0.0;

    EditText edtLuasBng;
    EditText edtNjopBng;
    TextView tvLuasNjopBng;
    Double luasNjopBng = 0.0;

    TextView tvNjopDasar;
    TextView tvNjoptkp;
    Double njopDasar = 0.0;
    Double njopPerhitungan = 0.0;
    Double njoptkp = 0.0;
    Double jumlahPembayaran = 0.1;
    Double jumlahPembayaranPercent = 0.001;

    TextView tvNjopPerhitungan;
    TextView tvJumlahPembayaran;
    TextView tvJumlahPembayaranTxt;

    View perhitunganPbb;
    Toolbar toolbar;

    DecimalFormat rpFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalkulator_pbb);

        init();
        getData();
        listener();
    }

    private void init(){

        toolbar = findViewById(R.id.toolbar);

        rpFormat.applyPattern("Rp#,###,###,###");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        perhitunganPbb = findViewById(R.id.perhitungan_pbb);

        edtLuasTanah = perhitunganPbb.findViewById(R.id.edt_luas_tanah);
        edtNjopTanah = perhitunganPbb.findViewById(R.id.edt_njop_tanah);
        tvLuasNjopTanah = perhitunganPbb.findViewById(R.id.tv_luas_njop_tanah);

        edtLuasBng = perhitunganPbb.findViewById(R.id.edt_luas_bng);
        edtNjopBng = perhitunganPbb.findViewById(R.id.edt_njop_bng);
        tvLuasNjopBng = perhitunganPbb.findViewById(R.id.tv_luas_njop_bng);

        tvNjopDasar = perhitunganPbb.findViewById(R.id.tv_njop_dasar);
        tvNjoptkp = perhitunganPbb.findViewById(R.id.tv_njoptkp);

        tvNjopPerhitungan = perhitunganPbb.findViewById(R.id.tv_njop_perhitungan);
        tvJumlahPembayaran = perhitunganPbb.findViewById(R.id.tv_jumlah_pembayaran);
        tvJumlahPembayaranTxt = perhitunganPbb.findViewById(R.id.tv_jumlah_pembayaran_txt);

        TextView title = perhitunganPbb.findViewById(R.id.tv_title);
        TextView titleBphtb = perhitunganPbb.findViewById(R.id.tv_title);

        Spannable titlePerhitungan = new SpannableString("Kalkulator");

        titlePerhitungan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titlePerhitungan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(titlePerhitungan);
        titleBphtb.setText(titlePerhitungan);

        Spannable titleBPHTB = new SpannableString(" PBB");
        titleBPHTB.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titleBPHTB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleBphtb.append(titleBPHTB);

    }

    private void getData(){
        ServiceGenerator2.create(API.class).getBeaKetetapan().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                njoptkp = Double.parseDouble(body.get("njoptkp").getAsString());
                tvNjoptkp.setText(rpFormat.format(njoptkp));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });

    }

    private void listener(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edtLuasTanah.addTextChangedListener(new NumberTextWatcher(edtLuasTanah));
        edtLuasTanah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String luasString = s.toString();

                if (luasString.contains(",")) {
                    luasString = luasString.replaceAll(",", "");
                }

                Double luas = luasString.length() > 0 ? Double.parseDouble(luasString) : 0;

                String njopString = edtNjopTanah.getText().toString();

                if (njopString.contains(",")) {
                    njopString = njopString.replaceAll(",", "");
                }

                Double njop = njopString.length() > 0 ? Double.parseDouble(njopString) : 0;

                luasNjopTanah = luas * njop;

                tvLuasNjopTanah.setText(rpFormat.format(luasNjopTanah));

                njopDasar = luasNjopTanah + luasNjopBng;
                tvNjopDasar.setText(rpFormat.format(njopDasar));

                njopPerhitungan = njopDasar-njoptkp;
                tvNjopPerhitungan.setText(rpFormat.format(njopPerhitungan));

                if(njopPerhitungan > 200000000 && njopPerhitungan <= 800000000){
                    jumlahPembayaran = 0.15;
                    jumlahPembayaranPercent = 0.0015;
                }else if(njopPerhitungan > 800000000){
                    jumlahPembayaran = 0.20;
                    jumlahPembayaranPercent = 0.002;
                }

                tvJumlahPembayaranTxt.setText("Jumlah Pembayaran PBB "+jumlahPembayaran+"%");
                tvJumlahPembayaran.setText(rpFormat.format(njopPerhitungan*jumlahPembayaranPercent));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtNjopTanah.addTextChangedListener(new NumberTextWatcher(edtNjopTanah));
        edtNjopTanah.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String luasString = edtLuasTanah.getText().toString();

                if (luasString.contains(",")) {
                    luasString = luasString.replaceAll(",", "");
                }

                Double luas = luasString.length() > 0 ? Double.parseDouble(luasString) : 0;

                String njopString = s.toString();

                if (njopString.contains(",")) {
                    njopString = njopString.replaceAll(",", "");
                }

                Double njop = njopString.length() > 0 ? Double.parseDouble(njopString) : 0;

                luasNjopTanah = luas * njop;

                tvLuasNjopTanah.setText(rpFormat.format(luasNjopTanah));
                njopDasar = luasNjopTanah + luasNjopBng;
                tvNjopDasar.setText(rpFormat.format(njopDasar));

                njopPerhitungan = njopDasar-njoptkp;
                tvNjopPerhitungan.setText(rpFormat.format(njopPerhitungan));

                if(njopPerhitungan > 200000000 && njopPerhitungan <= 800000000){
                    jumlahPembayaran = 0.15;
                    jumlahPembayaranPercent = 0.0015;
                }else if(njopPerhitungan > 800000000){
                    jumlahPembayaran = 0.20;
                    jumlahPembayaranPercent = 0.002;
                }

                tvJumlahPembayaranTxt.setText("Jumlah Pembayaran PBB "+jumlahPembayaran+"%");
                tvJumlahPembayaran.setText(rpFormat.format(njopPerhitungan*jumlahPembayaranPercent));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtLuasBng.addTextChangedListener(new NumberTextWatcher(edtLuasBng));
        edtLuasBng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String luasString = s.toString();

                if (luasString.contains(",")) {
                    luasString = luasString.replaceAll(",", "");
                }

                Double luas = luasString.length() > 0 ? Double.parseDouble(luasString) : 0;

                String njopString = edtNjopBng.getText().toString();

                if (njopString.contains(",")) {
                    njopString = njopString.replaceAll(",", "");
                }

                Double njop = njopString.length() > 0 ? Double.parseDouble(njopString) : 0;

                luasNjopBng = luas * njop;

                tvLuasNjopBng.setText(rpFormat.format(luasNjopBng));

                njopDasar = luasNjopTanah + luasNjopBng;
                tvNjopDasar.setText(rpFormat.format(njopDasar));

                njopPerhitungan = njopDasar-njoptkp;
                tvNjopPerhitungan.setText(rpFormat.format(njopPerhitungan));

                if(njopPerhitungan > 200000000 && njopPerhitungan <= 800000000){
                    jumlahPembayaran = 0.15;
                    jumlahPembayaranPercent = 0.0015;
                }else if(njopPerhitungan > 800000000){
                    jumlahPembayaran = 0.20;
                    jumlahPembayaranPercent = 0.002;
                }

                tvJumlahPembayaranTxt.setText("Jumlah Pembayaran PBB "+jumlahPembayaran+"%");
                tvJumlahPembayaran.setText(rpFormat.format(njopPerhitungan*jumlahPembayaranPercent));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtNjopBng.addTextChangedListener(new NumberTextWatcher(edtNjopBng));
        edtNjopBng.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String luasString = edtLuasBng.getText().toString();

                if (luasString.contains(",")) {
                    luasString = luasString.replaceAll(",", "");
                }

                Double luas = luasString.length() > 0 ? Double.parseDouble(luasString) : 0;

                String njopString = s.toString();

                if (njopString.contains(",")) {
                    njopString = njopString.replaceAll(",", "");
                }

                Double njop = njopString.length() > 0 ? Double.parseDouble(njopString) : 0;

                luasNjopBng = luas * njop;

                tvLuasNjopBng.setText(rpFormat.format(luasNjopBng));

                njopDasar = luasNjopTanah + luasNjopBng;
                tvNjopDasar.setText(rpFormat.format(njopDasar));

                njopPerhitungan = njopDasar-njoptkp;
                tvNjopPerhitungan.setText(rpFormat.format(njopPerhitungan));

                if(njopPerhitungan > 200000000 && njopPerhitungan <= 800000000){
                    jumlahPembayaran = 0.15;
                    jumlahPembayaranPercent = 0.0015;
                }else if(njopPerhitungan > 800000000){
                    jumlahPembayaran = 0.20;
                    jumlahPembayaranPercent = 0.002;
                }
                tvJumlahPembayaranTxt.setText("Jumlah Pembayaran PBB "+jumlahPembayaran+"%");
                tvJumlahPembayaran.setText(rpFormat.format(njopPerhitungan*jumlahPembayaranPercent));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}