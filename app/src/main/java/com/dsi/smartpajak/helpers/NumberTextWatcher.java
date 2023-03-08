package com.dsi.smartpajak.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {

    private EditText et;

    public NumberTextWatcher(EditText et)
    {
        this.et = et;
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        et.removeTextChangedListener(this);
        try {
            String originalString = s.toString();

            Long longval;
            if (originalString.contains(",")) {
                originalString = originalString.replaceAll(",", "");
            }
            longval = Long.parseLong(originalString);

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            String formattedString = formatter.format(longval);

            //setting text after format to EditText
            et.setText(formattedString);
            et.setSelection(et.getText().length());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        et.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

}