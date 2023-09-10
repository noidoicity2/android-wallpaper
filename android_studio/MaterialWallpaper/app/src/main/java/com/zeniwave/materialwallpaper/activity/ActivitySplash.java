package com.zeniwave.materialwallpaper.activity;

import static com.zeniwave.materialwallpaper.Config.ALLOW_VPN_ACCESS;
import static com.zeniwave.materialwallpaper.util.Constant.LOCALHOST_ADDRESS;
import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zeniwave.materialwallpaper.BuildConfig;
import com.zeniwave.materialwallpaper.Config;
import com.zeniwave.materialwallpaper.R;
import com.zeniwave.materialwallpaper.callback.CallbackSettings;
import com.zeniwave.materialwallpaper.database.prefs.AdsPref;
import com.zeniwave.materialwallpaper.database.prefs.SharedPref;
import com.zeniwave.materialwallpaper.model.AdStatus;
import com.zeniwave.materialwallpaper.model.Ads;
import com.zeniwave.materialwallpaper.model.App;
import com.zeniwave.materialwallpaper.model.Menu;
import com.zeniwave.materialwallpaper.model.Placement;
import com.zeniwave.materialwallpaper.model.Settings;
import com.zeniwave.materialwallpaper.rest.RestAdapter;
import com.zeniwave.materialwallpaper.util.AdsManager;
import com.zeniwave.materialwallpaper.util.Constant;
import com.zeniwave.materialwallpaper.util.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = "ActivitySplash";
    ProgressBar progressBar;
    ImageView imgSplash;
    Call<CallbackSettings> callbackCall = null;
    SharedPref sharedPref;
    AdsManager adsManager;
    AdsPref adsPref;
    AdStatus adStatus;
    Placement adPlacement;
    Settings settings;
    Ads ads;
    App app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getThemeFullScreen(this);
        setContentView(R.layout.activity_splash);

        sharedPref = new SharedPref(this);
        adsManager = new AdsManager(this);
        adsManager.initializeAd();

        adsPref = new AdsPref(this);

        imgSplash = findViewById(R.id.img_splash);
        if (sharedPref.getIsDarkTheme()) {
            imgSplash.setImageResource(R.drawable.bg_splash_dark);
        } else {
            imgSplash.setImageResource(R.drawable.bg_splash_default);
        }

        progressBar = findViewById(R.id.progressBar);

        adsPref = new AdsPref(this);

        if (ALLOW_VPN_ACCESS) {
            requestAds();
        } else {
            if (Tools.isVpnConnectionAvailable()) {
                new AlertDialog.Builder(ActivitySplash.this)
                        .setTitle(getString(R.string.vpn_detected))
                        .setMessage(getString(R.string.close_vpn))
                        .setPositiveButton(getString(R.string.dialog_option_ok), (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            } else {
                requestAds();
            }
        }

    }

    private void requestAds() {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && adsPref.getIsAppOpenAdOnStart()) {
            if (!Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                Tools.postDelayed(() -> {
                    switch (adsPref.getMainAds()) {
                        case ADMOB:
                            if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                            } else {
                                requestConfig();
                            }
                            break;
                        case GOOGLE_AD_MANAGER:
                            if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                            } else {
                                requestConfig();
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                            } else {
                                requestConfig();
                            }
                            break;
                        case WORTISE:
                            if (!adsPref.getWortiseAppOpenId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                            } else {
                                requestConfig();
                            }
                            break;
                        default:
                            requestConfig();
                            break;
                    }
                }, Config.DELAY_SPLASH);
            } else {
                requestConfig();
            }
        } else {
            requestConfig();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void requestConfig() {
        if (Config.SERVER_KEY.contains("XXXXX")) {
            new AlertDialog.Builder(this)
                    .setTitle("App not configured")
                    .setMessage("Please put your Server Key from settings menu in your admin panel to Config.java file, you can see the documentation for more detailed instructions.")
                    .setPositiveButton(getString(R.string.dialog_option_ok), (dialogInterface, i) -> showAppOpenAdIfAvailable(false))
                    .setCancelable(false)
                    .show();
        } else {
            String data = Tools.decode(Config.SERVER_KEY);
            String[] results = data.split("_applicationId_");
            String baseUrl = results[0].replace("http://localhost", LOCALHOST_ADDRESS);
            String applicationId = results[1];
            sharedPref.setBaseUrl(baseUrl);
            if (applicationId.equals(BuildConfig.APPLICATION_ID)) {
                requestAPI(baseUrl);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Whoops! invalid access key or applicationId, please check your configuration")
                        .setPositiveButton("Ok", (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            }
            Log.d(TAG, "Start request config");
        }
    }

    private void requestAPI(String baseUrl) {
        this.callbackCall = RestAdapter.createAPI(baseUrl).getSettings(BuildConfig.APPLICATION_ID);
        this.callbackCall.enqueue(new Callback<CallbackSettings>() {
            public void onResponse(@NonNull Call<CallbackSettings> call, @NonNull Response<CallbackSettings> response) {
                CallbackSettings resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    ads = resp.ads;
                    adStatus = resp.ads_status;
                    adPlacement = resp.ads_placement;
                    settings = resp.settings;
                    app = resp.app;
                    sharedPref.saveConfig(settings.privacy_policy, settings.more_apps_url);
                    adsManager.saveAds(ads);
                    adsManager.saveAdsPlacement(adPlacement);

                    if (Config.ENABLE_ONLINE_TAB_MENU_IN_ADMIN_PANEL) {
                        sharedPref.saveMenuList(resp.menus);
                    } else {
                        List<Menu> menus = new ArrayList<>();
                        addMenu(menus);
                        sharedPref.saveMenuList(menus);
                    }

                    adsPref.setNativeAdStyle(ads.native_ad_style_post_list, ads.native_ad_style_exit_dialog);
                    if (app.status != null && app.status.equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityRedirect.class);
                        intent.putExtra("redirect_url", app.redirect_url);
                        startActivity(intent);
                        finish();
                    } else {
                        showAppOpenAdIfAvailable(adsPref.getIsOpenAd());
                    }
                } else {
                    showAppOpenAdIfAvailable(false);
                }
            }

            public void onFailure(@NonNull Call<CallbackSettings> call, @NonNull Throwable th) {
                Log.e("onFailure", "" + th.getMessage());
                showAppOpenAdIfAvailable(false);
            }
        });
    }

    private void addMenu(List<Menu> menus) {
        menus.add(new Menu(getString(R.string.tab_menu_recent), Constant.Order.RECENT, Constant.Filter.WALLPAPER, "0"));
        menus.add(new Menu(getString(R.string.tab_menu_featured), Constant.Order.FEATURED, Constant.Filter.BOTH, "0"));
        menus.add(new Menu(getString(R.string.tab_menu_popular), Constant.Order.POPULAR, Constant.Filter.WALLPAPER, "0"));
        menus.add(new Menu(getString(R.string.tab_menu_random), Constant.Order.RANDOM, Constant.Filter.WALLPAPER, "0"));
        menus.add(new Menu(getString(R.string.tab_menu_live), Constant.Order.RECENT, Constant.Filter.LIVE_WALLPAPER, "0"));
    }

    private void showAppOpenAdIfAvailable(boolean showAd) {
        Tools.postDelayed(() -> {
            if (showAd) {
                if (Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                    adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnStart(), this::startMainActivityNew);
                } else {
                    startMainActivityNew();
                }
            } else {
                startMainActivityNew();
            }
        }, 100);
    }

    private void startMainActivityNew() {
        Tools.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, Config.DELAY_SPLASH);
    }

}
