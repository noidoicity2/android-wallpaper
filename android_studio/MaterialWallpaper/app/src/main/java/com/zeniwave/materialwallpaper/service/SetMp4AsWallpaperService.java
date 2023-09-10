package com.zeniwave.materialwallpaper.service;

import android.media.MediaPlayer;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.zeniwave.materialwallpaper.database.prefs.SharedPref;

public class SetMp4AsWallpaperService extends WallpaperService {

    protected static int playHeadTime = 0;

    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    class VideoEngine extends Engine {

        private final String TAG = "LiveWallpaper";
        private final MediaPlayer mediaPlayer;

        public VideoEngine() {
            super();
            Log.i(TAG, "(VideoEngine)");
            SharedPref sharedPref = new SharedPref(getApplicationContext());
            String url = sharedPref.getMp4Path() + "/" + sharedPref.getMp4Name();
            MediaPlayer create = MediaPlayer.create(SetMp4AsWallpaperService.this, Uri.parse(url));
            this.mediaPlayer = create;
            create.setLooping(true);
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
            Log.i(this.TAG, "onSurfaceCreated");
            this.mediaPlayer.setSurface(holder.getSurface());
            this.mediaPlayer.start();
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            Log.i(this.TAG, "( INativeWallpaperEngine ): onSurfaceDestroyed");
            SetMp4AsWallpaperService.playHeadTime = this.mediaPlayer.getCurrentPosition();
            this.mediaPlayer.reset();
            this.mediaPlayer.release();
        }
    }
}