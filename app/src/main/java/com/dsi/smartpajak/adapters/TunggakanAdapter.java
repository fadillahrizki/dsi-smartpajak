package com.dsi.smartpajak.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dsi.smartpajak.R;
import com.dsi.smartpajak.models.Pajak;

import java.util.ArrayList;
import java.util.List;

public class TunggakanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context ctx;

    private List<Pajak> items;

    public TunggakanAdapter(Context context, List<Pajak> items) {
        this.items = items;

        ctx = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView masaPajakTv;
        TextView pajakTerhutangTv;
        TextView tanggalCetakSKPDTv;

        private ItemViewHolder(View v) {
            super(v);

            masaPajakTv = v.findViewById(R.id.masa_pajak_tv);
            pajakTerhutangTv = v.findViewById(R.id.pajak_terhutang_tv);
            tanggalCetakSKPDTv = v.findViewById(R.id.tanggal_cetak_skpd_tv);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progress_bar;

        private ProgressViewHolder(View v) {
            super(v);

            progress_bar = v.findViewById(R.id.loader);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tunggakan_item, parent, false);
        vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Pajak pajak = items.get(position);
        final ItemViewHolder vItem = (ItemViewHolder) holder;

        vItem.masaPajakTv.setText(pajak.masa_pajak);
        vItem.pajakTerhutangTv.setText(pajak.pajak);
        vItem.tanggalCetakSKPDTv.setText(pajak.tanggal_cetak);
    }

    public void insertData(List<Pajak> pajakList) {
        int positionStart = getItemCount();
        int itemCount = pajakList.size();

        this.items.addAll(pajakList);

        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void resetData() {
        this.items = new ArrayList<>();

        notifyDataSetChanged();
    }
}