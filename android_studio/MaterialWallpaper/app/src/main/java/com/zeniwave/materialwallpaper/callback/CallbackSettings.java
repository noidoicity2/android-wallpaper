package com.zeniwave.materialwallpaper.callback;

import com.zeniwave.materialwallpaper.model.AdStatus;
import com.zeniwave.materialwallpaper.model.Ads;
import com.zeniwave.materialwallpaper.model.App;
import com.zeniwave.materialwallpaper.model.Menu;
import com.zeniwave.materialwallpaper.model.Placement;
import com.zeniwave.materialwallpaper.model.Settings;

import java.util.ArrayList;
import java.util.List;

public class CallbackSettings {

    public String status;
    public App app = null;
    public List<Menu> menus = new ArrayList<>();
    public Settings settings = null;
    public Ads ads = null;
    public AdStatus ads_status = null;
    public Placement ads_placement = null;

}
