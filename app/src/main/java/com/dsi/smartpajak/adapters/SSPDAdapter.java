package com.dsi.smartpajak.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dsi.smartpajak.R;
import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.models.Pajak;
import com.dsi.smartpajak.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SSPDAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context ctx;

    private User user;

    private final int VIEW_ITEM = 1;

    private boolean loading;

    private OnLoadMoreListener onLoadMoreListener;

    private OnItemClickListener mOnItemClickListener;

    private List<Pajak> items;

    public SSPDAdapter(Context context, RecyclerView view, List<Pajak> items) {
        this.items = items;

        ctx = context;
        CacheManager cacheManager = new CacheManager(ctx);
        Gson gson = new Gson();
        user = gson.fromJson(cacheManager.getUser(), User.class);

        lastItemViewDetector(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (this.items.get(position).type == 0) {
            return 2;
        } else if (this.items.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return 0;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLyt;
        TextView noKasRegisterTv;
        TextView masaPajakTv;
        TextView jumlahBayarTv;
        TextView tanggalBayarTv;

        private ItemViewHolder(View v) {
            super(v);

            itemLyt = v.findViewById(R.id.item);
            noKasRegisterTv = v.findViewById(R.id.no_register_tv);
            masaPajakTv = v.findViewById(R.id.masa_pajak_tv);
            jumlahBayarTv = v.findViewById(R.id.jumlah_bayar_tv);
            tanggalBayarTv = v.findViewById(R.id.tanggal_bayar_tv);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;

        private TitleViewHolder(View v) {
            super(v);

            titleTv = v.findViewById(R.id.title_tv);
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

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sspd_item, parent, false);
            vh = new ItemViewHolder(v);
        } else if (viewType == 2) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_item, parent, false);
            vh = new TitleViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loader_item, parent, false);
            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Pajak pajak = items.get(position);
            final ItemViewHolder vItem = (ItemViewHolder) holder;

            vItem.noKasRegisterTv.setText(pajak.no_register);
            vItem.masaPajakTv.setText(pajak.masa_pajak);
            vItem.jumlahBayarTv.setText(pajak.jumlah_setoran);
            vItem.tanggalBayarTv.setText(pajak.tanggal_terima);

            vItem.itemLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, pajak, holder.getAdapterPosition());
                    }
                }
            });
        } else if (holder instanceof TitleViewHolder) {
            final TitleViewHolder vItem = (TitleViewHolder) holder;

            vItem.titleTv.setText("" + user.nama_usaha);
        } else {
            ((ProgressViewHolder) holder).progress_bar.setIndeterminate(true);
        }
    }

    public void insertData(List<Pajak> pajakList) {
        setLoaded();

        int positionStart = getItemCount();
        int itemCount = pajakList.size();

        this.items.addAll(pajakList);

        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void resetData() {
        this.items = new ArrayList<>();

        notifyDataSetChanged();
    }

    public void setLoaded() {
        loading = false;

        for (int i = 0; i< getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);

                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);

            notifyItemInserted(getItemCount() - 1);

            loading = true;
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastPos = layoutManager.findLastVisibleItemPosition();

                    if ( ! loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }

                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Pajak obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
}