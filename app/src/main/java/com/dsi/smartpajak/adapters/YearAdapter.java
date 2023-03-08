package com.dsi.smartpajak.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dsi.smartpajak.R;
import com.dsi.smartpajak.models.Pajak;

import java.util.ArrayList;
import java.util.List;

public class YearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context ctx;

    private OnItemClickListener mOnItemClickListener;

    private int[] items;

    private int current;

    public YearAdapter(Context context, RecyclerView view, int[] items, int current) {
        this.items = items;
        this.current = current;

        ctx = context;
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView yearTv;

        private ItemViewHolder(View v) {
            super(v);

            yearTv = v.findViewById(R.id.year_tv);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.year_item, parent, false);
        vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder vItem = (ItemViewHolder) holder;

        vItem.yearTv.setText("" + items[position]);

        if (current == items[position]) {
            vItem.yearTv.setBackgroundResource(R.drawable.btn_year_yellow_bg);
        } else {
            vItem.yearTv.setBackgroundResource(R.drawable.btn_year_blue_bg);
        }

        vItem.yearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, items[position], holder.getAdapterPosition());
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int year, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
}