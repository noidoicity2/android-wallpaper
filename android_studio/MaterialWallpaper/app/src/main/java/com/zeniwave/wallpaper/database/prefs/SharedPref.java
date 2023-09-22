package com.zeniwave.wallpaper.database.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.zeniwave.wallpaper.Config;
import com.zeniwave.wallpaper.model.Menu;
import com.zeniwave.wallpaper.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPref {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setBaseUrl(String baseUrl) {
        editor.putString("base_url", baseUrl);
        editor.apply();
    }

    public String getBaseUrl() {
        return sharedPreferences.getString("base_url", "http://10.0.2.2/material_wallpaper");
    }

    public void saveConfig( String privacyPolicy, String moreAppsUrl) {
        editor.putString("privacy_policy", privacyPolicy);
        editor.putString("more_apps_url", moreAppsUrl);
        editor.apply();
    }

    public String getPrivacyPolicy() {
        return sharedPreferences.getString("privacy_policy", "");
    }

    public String getMoreAppsUrl() {
        return sharedPreferences.getString("more_apps_url", "");
    }

    public void saveGif(String gifPath, String gifName) {
        editor.putString("gif_path", gifPath);
        editor.putString("gif_name", gifName);
        editor.apply();
    }

    public void saveMp4(String mp4Path, String mp4Name) {
        editor.putString("mp4_path", mp4Path);
        editor.putString("mp4_name", mp4Name);
        editor.apply();
    }

    public String getGifPath() {
        return sharedPreferences.getString("gif_path", "0");
    }

    public String getGifName() {
        return sharedPreferences.getString("gif_name", "0");
    }

    public String getMp4Path() {
        return sharedPreferences.getString("mp4_path", "0");
    }

    public String getMp4Name() {
        return sharedPreferences.getString("mp4_name", "0");
    }

    //wallpaper view type
    public Integer getDisplayWallpaperPosition(int position) {
        return sharedPreferences.getInt("display_wallpaper_position", position);
    }

    public void updateDisplayWallpaperPosition(int position) {
        editor.putInt("display_wallpaper_position", position);
        editor.apply();
    }

    public Integer getWallpaperSpanCount() {
        return sharedPreferences.getInt("wallpaper_span_count", Config.DEFAULT_WALLPAPER_VIEW_TYPE);
    }

    public void setWallpaperSpanCount(int spanCount) {
        editor.putInt("wallpaper_span_count", spanCount);
        editor.apply();
    }

    //category view type
    public Integer getDisplayCategoryPosition(int position) {
        return sharedPreferences.getInt("display_category_position", position);
    }

    public void updateDisplayCategoryPosition(int position) {
        editor.putInt("display_category_position", position);
        editor.apply();
    }

    public Integer getCategorySpanCount() {
        return sharedPreferences.getInt("category_span_count", Config.DEFAULT_CATEGORY_VIEW_TYPE);
    }

    public void updateCategorySpanCount(int columns) {
        editor.putInt("category_span_count", columns);
        editor.apply();
    }

    public void setDefaultSortWallpaper() {
        editor.putInt("sort_act", Constant.SORT_RECENT);
        editor.apply();
    }

    public Integer getCurrentSortWallpaper() {
        return sharedPreferences.getInt("sort_act", 0);
    }

    public void updateSortWallpaper(int position) {
        editor.putInt("sort_act", position);
        editor.apply();
    }

    public Boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", Config.SET_DARK_MODE_AS_DEFAULT_THEME);
    }

    public void setIsDarkTheme(Boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public Integer getInAppReviewToken() {
        return sharedPreferences.getInt("in_app_review_token", 0);
    }

    public void updateInAppReviewToken(int value) {
        editor.putInt("in_app_review_token", value);
        editor.apply();
    }

    public void saveMenuList(List<Menu> apps) {
        Gson gson = new Gson();
        String json = gson.toJson(apps);
        editor.putString("menu", json);
        editor.apply();
    }

    public List<Menu> getMenuList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("menu", null);
        Type type = new TypeToken<ArrayList<Menu>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}
