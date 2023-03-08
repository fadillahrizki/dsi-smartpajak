package com.dsi.smartpajak.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dsi.smartpajak.R;
import com.dsi.smartpajak.models.PBB;

import java.util.List;

public class PBBDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context ctx;

    private List<PBB> items;

    public PBBDetailAdapter(Context context, RecyclerView view, List<PBB> items) {
        this.items = items;

        ctx = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tahunPajakTv;
        TextView pbbTerhutangTv;
        TextView tanggalJatuhTempoTv;
        TextView dendaTv;
        TextView jumlahTv;

        private ItemViewHolder(View v) {
            super(v);

            tahunPajakTv = v.findViewById(R.id.tahun_pajak_tv);
            pbbTerhutangTv = v.findViewById(R.id.pbb_terhutang_tv);
            tanggalJatuhTempoTv = v.findViewById(R.id.tanggal_jatuh_tempo_tv);
            dendaTv = v.findViewById(R.id.denda_tv);
            jumlahTv = v.findViewById(R.id.jumlah_tv);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pbb_detail_item, parent, false);
        vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder vItem = (ItemViewHolder) holder;
        final PBB item = items.get(position);

        vItem.tahunPajakTv.setText(item.tahun_pajak);
        vItem.pbbTerhutangTv.setText(item.pbb_terhutang);
        vItem.tanggalJatuhTempoTv.setText(item.tanggal_jatuh_tempo);
        vItem.dendaTv.setText(item.denda);
        vItem.jumlahTv.setText(item.jumlah_pembayaran);
    }
}