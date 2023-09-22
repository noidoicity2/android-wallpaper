package com.zeniwave.wallpaper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zeniwave.wallpaper.Config;
import com.zeniwave.wallpaper.R;
import com.zeniwave.wallpaper.database.prefs.SharedPref;
import com.zeniwave.wallpaper.model.Category;
import com.zeniwave.wallpaper.util.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    Context context;
    private OnItemClickListener mOnItemClickListener;
    private List<Category> items;

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterCategory(Context ctx, List<Category> items) {
        this.items = items;
        context = ctx;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView total_wallpaper;
        public ImageView category_image;
        public CardView card_view;
        public RelativeLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            total_wallpaper = v.findViewById(R.id.total_wallpaper);
            category_image = v.findViewById(R.id.category_image);
            card_view = v.findViewById(R.id.card_view);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Category c = items.get(position);

        holder.category_name.setText(c.category_name);

        if (Config.SHOW_WALLPAPER_COUNT_ON_CATEGORY) {
            holder.total_wallpaper.setVisibility(View.VISIBLE);
            holder.total_wallpaper.setText(Tools.withSuffix(Long.parseLong(c.total_wallpaper)) + " " + context.getResources().getString(R.string.wallpaper_count_text));
        } else {
            holder.total_wallpaper.setVisibility(View.GONE);
        }

        SharedPref sharedPref = new SharedPref(context);
        if (sharedPref.getIsDarkTheme()) {
            holder.card_view.setCardBackgroundColor(context.getResources().getColor(R.color.color_dark_toolbar));
        } else {
            holder.card_view.setCardBackgroundColor(context.getResources().getColor(R.color.color_shimmer));
        }

        Glide.with(context)
                .load(sharedPref.getBaseUrl() + "/upload/category/" + c.category_image.replace(" ", "%20"))
                .placeholder(R.drawable.ic_transparent)
                .thumbnail(Tools.requestBuilder(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.category_image);

        holder.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, c, position);
            }
        });
    }

    public void insertData(List<Category> items) {
        this.items.addAll(items);
    }

    public void setListData(List<Category> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}