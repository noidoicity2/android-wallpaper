package com.zeniwave.materialwallpaper.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.zeniwave.materialwallpaper.R;
import com.zeniwave.materialwallpaper.activity.ActivityWallpaperDetail;
import com.zeniwave.materialwallpaper.activity.MainActivity;
import com.zeniwave.materialwallpaper.adapter.AdapterWallpaper;
import com.zeniwave.materialwallpaper.database.dao.AppDatabase;
import com.zeniwave.materialwallpaper.database.dao.DAO;
import com.zeniwave.materialwallpaper.database.prefs.AdsPref;
import com.zeniwave.materialwallpaper.database.prefs.SharedPref;
import com.zeniwave.materialwallpaper.model.Wallpaper;
import com.zeniwave.materialwallpaper.util.AdsManager;
import com.zeniwave.materialwallpaper.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    private AdapterWallpaper adapterWallpaper;
    SharedPref sharedPref;
    View lytNoFavorite;
    AdsManager adsManager;
    AdsPref adsPref;
    Activity activity;
    DAO db;
    int wallpaperColumnCount;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        db = AppDatabase.getDb(activity).get();
        lytNoFavorite = rootView.findViewById(R.id.lyt_not_found);
        sharedPref = new SharedPref(activity);
        wallpaperColumnCount = sharedPref.getWallpaperSpanCount();
        adsManager = new AdsManager(activity);
        adsPref = new AdsPref(activity);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(wallpaperColumnCount, StaggeredGridLayoutManager.VERTICAL));

        adapterWallpaper = new AdapterWallpaper(activity, recyclerView, new ArrayList<>());
        recyclerView.setAdapter(adapterWallpaper);

        return rootView;
    }

    private void displayData(final List<Wallpaper> wallpapers) {
        adapterWallpaper.setItems(wallpapers);
        if (wallpapers.size() == 0) {
            lytNoFavorite.setVisibility(View.VISIBLE);
        } else {
            lytNoFavorite.setVisibility(View.GONE);
        }

        adapterWallpaper.setOnItemClickListener((v, obj, position) -> {
            Constant.wallpapers.clear();
            Constant.wallpapers.addAll(wallpapers);
            Constant.position = position;
            Intent intent = new Intent(activity, ActivityWallpaperDetail.class);
            startActivity(intent);

            ((MainActivity) activity).showInterstitialAd();
            ((MainActivity) activity).destroyBannerAd();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        displayData(db.getAllFavorite());
    }

}
