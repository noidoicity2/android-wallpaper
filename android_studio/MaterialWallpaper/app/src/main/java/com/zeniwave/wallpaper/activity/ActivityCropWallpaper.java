package com.zeniwave.wallpaper.activity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.zeniwave.wallpaper.Config;
import com.zeniwave.wallpaper.R;
import com.zeniwave.wallpaper.database.prefs.SharedPref;
import com.zeniwave.wallpaper.util.Constant;
import com.zeniwave.wallpaper.util.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canhub.cropper.CropImageView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class ActivityCropWallpaper extends AppCompatActivity {

    String imageUrl;
    Bitmap bitmap = null;
    CropImageView cropImageView;
    CoordinatorLayout parentView;
    SharedPref sharedPref;
    FloatingActionButton fabSetAction;
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_set_wallpaper);
        Tools.setNavigation(this);
        Tools.setupToolbar(this, findViewById(R.id.appbar_layout), findViewById(R.id.toolbar), "", true);

        imageUrl = getIntent().getStringExtra("image_url");

        sharedPref = new SharedPref(this);
        fabSetAction = findViewById(R.id.fab_set_action);
        cropImageView = findViewById(R.id.cropImageView);
        parentView = findViewById(R.id.coordinatorLayout);

        loadWallpaper();

    }

    public void loadWallpaper() {
        Glide.with(this)
                .load(imageUrl.replace(" ", "%20"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        bitmap = ((BitmapDrawable) resource).getBitmap();
                        cropImageView.setImageBitmap(bitmap);
                        fabSetAction.setOnClickListener(view -> showBottomSheetSetAction(cropImageView.getCroppedImage()));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    private void showBottomSheetSetAction(Bitmap bitmap) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_set_action, null);

        FrameLayout lytBottomSheet = view.findViewById(R.id.bottom_sheet);
        LinearLayout lytSetWallpaper = view.findViewById(R.id.lyt_set_wallpaper);
        LinearLayout lytSetLiveWallpaper = view.findViewById(R.id.lyt_set_live_wallpaper);

        lytSetWallpaper.setVisibility(View.VISIBLE);
        lytSetLiveWallpaper.setVisibility(View.GONE);

        LinearLayout btnSetHomeScreen = view.findViewById(R.id.btn_set_home_screen);
        LinearLayout btnSetLockScreen = view.findViewById(R.id.btn_set_lock_screen);
        LinearLayout btnSetBothScreen = view.findViewById(R.id.btn_set_both);
        LinearLayout btnSetWith = view.findViewById(R.id.btn_set_with);
        LinearLayout btnSetCrop = view.findViewById(R.id.btn_set_crop);
        LinearLayout btnSetLive = view.findViewById(R.id.btn_set_live);
        LinearLayout btnSetSave = view.findViewById(R.id.btn_set_save);

        ImageView imgSetHomeScreen = view.findViewById(R.id.img_set_home_screen);
        ImageView imgSetLockScreen = view.findViewById(R.id.img_set_lock_screen);
        ImageView imgSetBothScreen = view.findViewById(R.id.img_set_both);
        ImageView imgSetWith = view.findViewById(R.id.img_set_with);
        ImageView imgSetCrop = view.findViewById(R.id.img_set_crop);
        ImageView imgSetLive = view.findViewById(R.id.img_set_live);
        ImageView imgSetSave = view.findViewById(R.id.img_set_save);

        TextView txtSetWallpaper = view.findViewById(R.id.txt_set_wallpaper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtSetWallpaper.setText(getString(R.string.txt_set_both_screen));
        } else {
            txtSetWallpaper.setText(getString(R.string.txt_set_wallpaper));
        }

        if (sharedPref.getIsDarkTheme()) {
            imgSetHomeScreen.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
            imgSetLockScreen.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
            imgSetBothScreen.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
            imgSetWith.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
            imgSetCrop.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
            imgSetLive.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
            imgSetSave.setColorFilter(getResources().getColor(R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
        } else {
            imgSetHomeScreen.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
            imgSetLockScreen.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
            imgSetBothScreen.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
            imgSetWith.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
            imgSetCrop.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
            imgSetLive.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
            imgSetSave.setColorFilter(getResources().getColor(R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btnSetHomeScreen.setOnClickListener(v -> setWallpaper(view, bitmap, Constant.HOME_SCREEN, true));
            btnSetLockScreen.setOnClickListener(v -> setWallpaper(view, bitmap, Constant.LOCK_SCREEN, true));
        } else {
            btnSetHomeScreen.setVisibility(View.GONE);
            btnSetLockScreen.setVisibility(View.GONE);
        }

        btnSetBothScreen.setOnClickListener(v -> setWallpaper(view, bitmap, Constant.BOTH, true));

        btnSetWith.setVisibility(View.GONE);
        btnSetCrop.setVisibility(View.GONE);
        btnSetLive.setVisibility(View.GONE);
        btnSetSave.setVisibility(View.GONE);

        if (this.sharedPref.getIsDarkTheme()) {
            lytBottomSheet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_sheet_dark));
        } else {
            lytBottomSheet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_sheet_light));
        }

        if (Config.ENABLE_RTL_MODE) {
            if (sharedPref.getIsDarkTheme()) {
                mBottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialogDarkRtl);
            } else {
                mBottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialogLightRtl);
            }
        } else {
            if (sharedPref.getIsDarkTheme()) {
                mBottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialogDark);
            } else {
                mBottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialogLight);
            }
        }
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    private void setWallpaper(View view, Bitmap bitmap, String setAction, boolean showProgress) {

        if (showProgress) {
            LinearLayout lytSetAction = view.findViewById(R.id.lyt_set_action);
            ProgressBar progressBarSetAction = view.findViewById(R.id.progress_bar_set_action);
            lytSetAction.setVisibility(View.GONE);
            progressBarSetAction.setVisibility(View.VISIBLE);
            mBottomSheetDialog.setCancelable(false);
        }

        switch (setAction) {
            case Constant.HOME_SCREEN:
                Tools.postDelayed(() -> {
                    try {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            showSnackBar(getString(R.string.msg_wallpaper_success));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackBar(getString(R.string.msg_wallpaper_failed));
                    }
                }, 100);
                new Handler(Looper.getMainLooper()).postDelayed(() -> mBottomSheetDialog.dismiss(), Constant.DELAY_SET);
                break;

            case Constant.LOCK_SCREEN:
                Tools.postDelayed(() -> {
                    try {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                            showSnackBar(getString(R.string.msg_wallpaper_success));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackBar(getString(R.string.msg_wallpaper_failed));
                    }
                }, 100);
                new Handler(Looper.getMainLooper()).postDelayed(() -> mBottomSheetDialog.dismiss(), Constant.DELAY_SET);
                break;

            case Constant.BOTH:
                Tools.postDelayed(() -> {
                    try {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                        wallpaperManager.setBitmap(bitmap);
                        showSnackBar(getString(R.string.msg_wallpaper_success));
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackBar(getString(R.string.msg_wallpaper_failed));
                    }
                }, 100);
                new Handler(Looper.getMainLooper()).postDelayed(() -> mBottomSheetDialog.dismiss(), Constant.DELAY_SET);
                break;

            default:
                //nothing
                break;
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

}
