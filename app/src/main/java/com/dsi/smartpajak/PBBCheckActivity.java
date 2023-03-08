package com.dsi.smartpajak;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class PBBCheckActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private MaskedEditText numberEt;
    private TextView checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pbb_check);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView pbbCheckTv = findViewById(R.id.pbb_check_tv);
        numberEt = findViewById(R.id.number_et);
        checkBtn = findViewById(R.id.check_btn);

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

        Spannable titleCheck = new SpannableString("CEK");
        titleCheck.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, titleCheck.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pbbCheckTv.setText(titleCheck);

        Spannable titlePBB = new SpannableString(" PBB");
        titlePBB.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlueDark)), 0, titlePBB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pbbCheckTv.append(titlePBB);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberEt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Silahkan isi nomor objek pajak", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(PBBCheckActivity.this, PBBActivity.class);
                    intent.putExtra("number", numberEt.getText().toString().trim());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
