package com.dsi.smartpajak.models;

import java.io.Serializable;

public class PBB implements Serializable {
    public String nama_wp;
    public String tahun_pajak;
    public String letak_objek_pajak;
    public String nama_alamat_wajib_pajak;
    public String luas_op_bumi;
    public String kelas_op_bumi;
    public String njop_per_m_op_bumi;
    public String total_njop_op_bumi;
    public String luas_op_bangunan;
    public String kelas_op_bangunan;
    public String njop_per_m_op_bangunan;
    public String total_njop_op_bangunan;
    public String njop_dasar;
    public String njop_tkp;
    public String njop_pbb;
    public String pbb_terhutang;
    public String tanggal_jatuh_tempo;
    public String jumlah_pembayaran;
    public String denda;
    public int status_pembayaran;
}