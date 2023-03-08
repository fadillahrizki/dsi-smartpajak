package com.dsi.smartpajak.network;

import com.dsi.smartpajak.models.JenisHak;
import com.dsi.smartpajak.models.JenisPerolehan;
import com.dsi.smartpajak.models.Pajak;
import com.dsi.smartpajak.models.PayStep;
import com.dsi.smartpajak.models.User;

import java.io.Serializable;
import java.util.List;

public class APICallback implements Serializable {
    public String error;
    public String success;
    public User user;
    public List<Pajak> skpd;
    public List<Pajak> tunggakan;
    public List<Pajak> sspd;
    public int pages;
    public String total_tunggakan;
    public String download_file;
    public String download_url;
    public List<PayStep> pay_steps;
    public String facebook;
    public String instagram;
    public String url;
    public String icon;
    public List<JenisPerolehan> jenis_perolehan;
    public List<JenisHak> jenis_hak;
}
