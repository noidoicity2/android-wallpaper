package com.zeniwave.wallpaper.callback;

import com.zeniwave.wallpaper.model.AdStatus;
import com.zeniwave.wallpaper.model.Ads;
import com.zeniwave.wallpaper.model.App;
import com.zeniwave.wallpaper.model.Menu;
import com.zeniwave.wallpaper.model.Placement;
import com.zeniwave.wallpaper.model.Settings;

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
