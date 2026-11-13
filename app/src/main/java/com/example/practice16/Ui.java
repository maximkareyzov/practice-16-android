package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

/** Общие небольшие элементы интерфейса и выход из аккаунта. */
final class Ui {
    private Ui() { }
    static LinearLayout column(Activity activity) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 20, 24, 20);
        activity.setContentView(layout);
        return layout;
    }
    static TextView text(Activity activity, LinearLayout parent, String value) {
        TextView text = new TextView(activity);
        text.setText(value); text.setTextSize(18);
        parent.addView(text, new LinearLayout.LayoutParams(-1, -2));
        return text;
    }
    static Button button(Activity activity, LinearLayout parent, String label, Runnable action) {
        Button b = new Button(activity); b.setText(label);
        parent.addView(b, new LinearLayout.LayoutParams(-1, -2));
        b.setOnClickListener(v -> action.run()); return b;
    }
    static void error(Activity a) { Toast.makeText(a, R.string.db_error, Toast.LENGTH_LONG).show(); }
    static void logout(Activity a) {
        FirebaseAuth.getInstance().signOut();
        if (UserStaticInfo.signedInUser != null) UserStaticInfo.users.remove(UserStaticInfo.signedInUser);
        UserStaticInfo.signedInUser = null; UserStaticInfo.profileId = null;
        Intent intent = new Intent(a, SignActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.startActivity(intent); a.finish();
    }
}
