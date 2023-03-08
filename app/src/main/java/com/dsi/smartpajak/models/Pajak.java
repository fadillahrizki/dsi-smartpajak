package com.dsi.smartpajak.models;

import java.io.Serializable;

public class Pajak implements Serializable {
    public String masa_pajak;
    public String pajak;
    public String tanggal_cetak;
    public int status;
    public String no_register;
    public String jumlah_ketetapan;
    public String biaya_admin;
    public String denda;
    public String jumlah_setoran;
    public String tanggal_terima;
    public String download_file;
    public String download_url;
    public int type = 1;
}
