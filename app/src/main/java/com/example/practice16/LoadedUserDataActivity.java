package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;

public class LoadedUserDataActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable next = () -> { startActivity(new Intent(this, MainActivity.class)); finish(); };
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_loaded_user_data);
        findViewById(R.id.imageForLoaded).startAnimation(AnimationUtils.loadAnimation(this, R.anim.logo_rotate_animation));
        handler.postDelayed(next, 1500);
    }
    @Override protected void onDestroy() { handler.removeCallbacks(next); super.onDestroy(); }
}
