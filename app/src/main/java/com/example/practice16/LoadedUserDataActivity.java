package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class LoadedUserDataActivity extends Activity {
    private MediaPlayer player;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable next = () -> { startActivity(new Intent(this, MainActivity.class)); finish(); };
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_loaded_user_data);
        // Тихий учебный звук хранится текстом base64 и распаковывается в кеш.
        try (InputStream input = getResources().openRawResource(R.raw.noise); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            byte[] block = new byte[1024]; int count;
            while ((count = input.read(block)) != -1) bytes.write(block, 0, count);
            byte[] sound = Base64.decode(bytes.toByteArray(), Base64.DEFAULT);
            File file = new File(getCacheDir(), "loading.wav");
            try (FileOutputStream output = new FileOutputStream(file)) { output.write(sound); }
            player = MediaPlayer.create(this, Uri.fromFile(file));
            if (player != null) { player.setVolume(0.2f, 0.2f); player.setLooping(true); }
        } catch (Exception ignored) { /* загрузка работает и без звука */ }
    }
    @Override protected void onStart() { super.onStart(); if (player != null) player.start(); handler.postDelayed(next, 1500); }
    @Override protected void onStop() { handler.removeCallbacks(next); if (player != null && player.isPlaying()) player.pause(); super.onStop(); }
    @Override protected void onDestroy() { handler.removeCallbacks(next); if (player != null) { player.release(); player = null; } super.onDestroy(); }
}
