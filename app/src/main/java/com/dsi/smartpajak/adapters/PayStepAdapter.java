package com.dsi.smartpajak.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dsi.smartpajak.R;
import com.dsi.smartpajak.models.PayStep;

import java.util.List;

public class PayStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context ctx;

    private List<PayStep> items;

    public PayStepAdapter(Context context, RecyclerView view, List<PayStep> items) {
        this.items = items;

        ctx = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemLyt;
        TextView titleTv;
        LinearLayout contentLyt;
        WebView descriptionWv;
        TextView dateTv;
        View dividerView;

        private ItemViewHolder(View v) {
            super(v);

            itemLyt = v.findViewById(R.id.item_lyt);
            titleTv = v.findViewById(R.id.title_tv);
            contentLyt = v.findViewById(R.id.content_lyt);
            descriptionWv = v.findViewById(R.id.description_wv);
            dateTv = v.findViewById(R.id.date_tv);
            dividerView = v.findViewById(R.id.divider);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_step_item, parent, false);
        vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder vItem = (ItemViewHolder) holder;
        final PayStep payStep = items.get(position);

        vItem.titleTv.setText("" + payStep.judul);

        String html = "<html><head>";
        html += "<style type='text/css'>";
        html += "html,body{margin:0;padding:0;background:transparent;}body{line-height:25px;font-size: 14px;}ul{margin: 0;padding:0 0 0 20px;}";
        html += "</style></head><body>";
        html += payStep.deskripsi;
        html += "</body></html>";

        vItem.descriptionWv.setVerticalScrollBarEnabled(false);
        vItem.descriptionWv.setBackgroundColor(android.R.color.transparent);
        vItem.descriptionWv.loadData(html, "text/html", "UTF-8");

        vItem.dateTv.setText("Diperbarui: " + payStep.tanggal);

        if (position >= (items.size() - 1)) {
            vItem.dividerView.setVisibility(View.GONE);
        } else {
            vItem.dividerView.setVisibility(View.VISIBLE);
        }

        vItem.itemLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payStep.collapsed) {
                    payStep.collapsed = false;
                    expand(vItem.contentLyt);
                } else {
                    payStep.collapsed = true;
                    collapse(vItem.contentLyt);
                }
            }
        });
    }

    private static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}