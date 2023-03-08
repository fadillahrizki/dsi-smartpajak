package com.dsi.smartpajak.network;

import com.dsi.smartpajak.models.PBB;

import java.io.Serializable;
import java.util.List;

public class PBBCallback implements Serializable {
    public String status;
    public String msg;
    public int[] years;
    public PBB pbb;
    public List<PBB> pbbList;
    public List<PBB> items;
    public String jumlah_terhutang;
    public String jumlah_denda;
    public String total_jumlah;
}