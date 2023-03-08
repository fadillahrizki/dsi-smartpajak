package com.dsi.smartpajak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KalkulatorBphtbActivity extends AppCompatActivity {

    EditText edtLuasTanah;
    EditText edtNjopTanah;
    TextView tvLuasNjopTanah;
    Double luasNjopTanah = 0.0;

    EditText edtLuasBng;
    EditText edtNjopBng;
    TextView tvLuasNjopBng;
    Double luasNjopBng = 0.0;

    TextView tvTotalNjopPbb;
    Double totalNjopPbb = 0.0;

    View perhitunganNjop;
    View perhitunganBphtb;

    private ArrayAdapter jenisHakAdapter;
    private ArrayAdapter jenisPerolehanAdapter;

    List<JenisHak> jenisHakList;
    private String[] jenisHaks;
    private String selectedJenisHak = "";

    List<JenisPerolehan> jenisPerolehanList;
    private String[] jenisPerolehans;
    private String selectedJenisPerolehan = "";

    Spinner jenisHakSpinner;
    Spinner jenisPerolehanSpinner;

    TextView tvNpoptkp;
    TextView tvNpopkp;
    TextView tvBea;

    EditText edtHargaTransaksi;
    EditText edtNpop;

    Double npopkp = 0.0;
    Double beaPercent = 0.05;
    Double pengenaanBphtb = 0.0;
    Double pengurang = 0.0;

    EditText edtPengurangan;
    TextView tvTotalPengurangan;

    TextView tvPengenaanBphtb;

    Toolbar toolbar;

    DecimalFormat rpFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalkulator_bphtb);

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

        perhitunganNjop = findViewById(R.id.perhitungan_njop);
        perhitunganBphtb = findViewById(R.id.perhitungan_bphtb);

        jenisHakSpinner = perhitunganBphtb.findViewById(R.id.jenis_hak_spinner);

        jenisPerolehanSpinner = perhitunganBphtb.findViewById(R.id.jenis_perolehan_spinner);
        tvNpoptkp = perhitunganBphtb.findViewById(R.id.tv_npoptkp);
        tvNpopkp = perhitunganBphtb.findViewById(R.id.tv_npopkp);
        tvBea = perhitunganBphtb.findViewById(R.id.tv_bea);

        edtHargaTransaksi = perhitunganBphtb.findViewById(R.id.edt_harga_transaksi);
        edtNpop = perhitunganBphtb.findViewById(R.id.edt_npop);

        edtPengurangan = perhitunganBphtb.findViewById(R.id.edt_pengurangan);
        tvTotalPengurangan = perhitunganBphtb.findViewById(R.id.tv_total_pengurangan);

        tvPengenaanBphtb = perhitunganBphtb.findViewById(R.id.tv_pengenaan_bphtb);

        edtLuasTanah = perhitunganNjop.findViewById(R.id.edt_luas_tanah);
        edtNjopTanah = perhitunganNjop.findViewById(R.id.edt_njop_tanah);
        tvLuasNjopTanah = perhitunganNjop.findViewById(R.id.tv_luas_njop_tanah);

        edtLuasBng = perhitunganNjop.findViewById(R.id.edt_luas_bng);
        edtNjopBng = perhitunganNjop.findViewById(R.id.edt_njop_bng);
        tvLuasNjopBng = perhitunganNjop.findViewById(R.id.tv_luas_njop_bng);

        tvTotalNjopPbb = perhitunganNjop.findViewById(R.id.tv_total_njop_pbb);

        TextView title = perhitunganNjop.findViewById(R.id.tv_title);

        Spannable titlePerhitungan = new SpannableString("Kalkulator");

        titlePerhitungan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titlePerhitungan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(titlePerhitungan);

        Spannable titleBPHTB = new SpannableString(" BPHTB");
        titleBPHTB.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titleBPHTB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.append(titleBPHTB);
    }

    private void getData(){
        ServiceGenerator2.create(API.class).getBeaKetetapan().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                beaPercent = Double.parseDouble(body.get("bea").getAsString())/100;
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });

        ServiceGenerator2.create(API.class).getJenisHak().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                JsonArray results = body.getAsJsonArray("results");
                if (results != null) {
                    List<JenisHak> data = new ArrayList<JenisHak>();
                    for (int i=0;i<results.size();i++){
                        JenisHak jenisHak = new JenisHak();
                        jenisHak.name = results.get(i).getAsJsonObject().get("name").getAsString();
                        data.add(jenisHak);
                    }

                    jenisHakList = data;

                    List<String> list = new ArrayList<>();
                    list.add("Pilih jenis hak");
                    for (JenisHak p : data) {
                        String name = p.name;
                        list.add(name);
                    }

                    jenisHaks = list.toArray(new String[jenisHakList.size()]);
                    jenisHakAdapter = new ArrayAdapter(KalkulatorBphtbActivity.this,android.R.layout.simple_spinner_item,jenisHaks);
                    jenisHakAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    jenisHakSpinner.setAdapter(jenisHakAdapter);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });

        ServiceGenerator2.create(API.class).getJenisPerolehan().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                JsonArray results = body.getAsJsonArray("results");
                if (results != null) {
                    List<JenisPerolehan> data = new ArrayList<JenisPerolehan>();
                    for (int i=0;i<results.size();i++){
                        JsonObject object = results.get(i).getAsJsonObject();
                        JenisPerolehan jenisPerolehan = new JenisPerolehan();
                        jenisPerolehan.id = object.get("id").getAsString();
                        jenisPerolehan.name = object.get("name").getAsString();
                        jenisPerolehan.npoptkp = object.get("npoptkp").getAsString();
                        data.add(jenisPerolehan);
                    }

                    jenisPerolehanList = data;

                    List<String> list = new ArrayList<>();
                    list.add("Pilih jenis perolehan");
                    for (JenisPerolehan p : data) {
                        String name = p.name;
                        list.add(name);
                    }

                    jenisPerolehans = list.toArray(new String[jenisPerolehanList.size()]);
                    jenisPerolehanAdapter = new ArrayAdapter(KalkulatorBphtbActivity.this,android.R.layout.simple_spinner_item,jenisPerolehans);
                    jenisPerolehanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    jenisPerolehanSpinner.setAdapter(jenisPerolehanAdapter);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });
    }

    private void listener(){
        jenisHakSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    selectedJenisHak = "";
                }else{
                    JenisHak jenisHak = jenisHakList.get(position-1);
                    selectedJenisHak = jenisHak.name;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        jenisPerolehanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    selectedJenisPerolehan = "";
                }else{
                    JenisPerolehan jenisPerolehan = jenisPerolehanList.get(position-1);
                    selectedJenisPerolehan = jenisPerolehan.npoptkp;

                    Double npoptkp = Double.parseDouble(selectedJenisPerolehan);
                    String npopString = edtNpop.getText().toString();

                    if (npopString.contains(",")) {
                        npopString = npopString.replaceAll(",", "");
                    }

                    Double nilaiNpop = npopString.length() > 0 ? Double.parseDouble(npopString) : 0;

                    npopkp = nilaiNpop - npoptkp;
                    npopkp = npopkp > 0 ? npopkp : 0;

                    tvNpoptkp.setText(rpFormat.format(npoptkp));
                    tvNpopkp.setText(rpFormat.format(npopkp));

                    Double bea = (npopkp * beaPercent) > 0 ? (npopkp * beaPercent) : 0;
                    tvBea.setText(rpFormat.format(bea));
                    pengenaanBphtb = bea;
                    pengurang = pengurang * pengenaanBphtb / 100;
                    tvTotalPengurangan.setText(rpFormat.format(pengurang));
                    tvPengenaanBphtb.setText(rpFormat.format(pengenaanBphtb-pengurang));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                totalNjopPbb = luasNjopTanah + luasNjopBng;
                tvTotalNjopPbb.setText(rpFormat.format(totalNjopPbb));
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
                totalNjopPbb = luasNjopTanah + luasNjopBng;
                tvTotalNjopPbb.setText(rpFormat.format(totalNjopPbb));
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

                totalNjopPbb = luasNjopTanah + luasNjopBng;
                tvTotalNjopPbb.setText(rpFormat.format(totalNjopPbb));
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
                totalNjopPbb = luasNjopTanah + luasNjopBng;
                tvTotalNjopPbb.setText(rpFormat.format(totalNjopPbb));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPengurangan.addTextChangedListener(new NumberTextWatcher(edtPengurangan));
        edtPengurangan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String oriString = s.toString();

                if (oriString.contains(",")) {
                    oriString = oriString.replaceAll(",", "");
                }

                Double value = oriString.length() > 0 ? Double.parseDouble(oriString) : 0;

                pengurang = value * pengenaanBphtb / 100;
                tvTotalPengurangan.setText(rpFormat.format(pengurang));
                tvPengenaanBphtb.setText(rpFormat.format(pengenaanBphtb-pengurang));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtHargaTransaksi.addTextChangedListener(new NumberTextWatcher(edtHargaTransaksi));

        edtNpop.addTextChangedListener(new NumberTextWatcher(edtNpop));
        edtNpop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String oriString = s.toString();

                if (oriString.contains(",")) {
                    oriString = oriString.replaceAll(",", "");
                }

                Double nilaiNpop = oriString.length() > 0 ? Double.parseDouble(oriString) : 0;
                Double npoptkp = Double.parseDouble(selectedJenisPerolehan);

                npopkp = nilaiNpop - npoptkp;
                npopkp = npopkp > 0 ? npopkp : 0;
                tvNpopkp.setText(rpFormat.format(npopkp));
                Double bea = (npopkp * beaPercent) > 0 ? (npopkp * beaPercent) : 0;
                tvBea.setText(rpFormat.format(bea));
                pengenaanBphtb = bea;
                pengurang = pengurang * pengenaanBphtb / 100;

                tvTotalPengurangan.setText(rpFormat.format(pengurang));
                tvPengenaanBphtb.setText(rpFormat.format(pengenaanBphtb-pengurang));
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