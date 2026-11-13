package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/** Список реальных зарегистрированных пользователей. */
public class MessengerActivity extends Activity {
    private final ArrayList<String> ids = new ArrayList<>(), names = new ArrayList<>();
    private DatabaseReference profiles; private ValueEventListener listener;
    private ArrayAdapter<String> adapter; private TextView hint;
    private String uid;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { Ui.logout(this); return; }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayout layout = Ui.column(this);
        Ui.text(this, layout, "Контакты");
        Ui.button(this, layout, "Мой профиль", () -> openProfile(uid));
        Ui.button(this, layout, "Выйти из аккаунта", () -> Ui.logout(this));
        CheckBox sound = new CheckBox(this); sound.setText("Звук при загрузке");
        sound.setChecked(getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true));
        sound.setOnCheckedChangeListener((b, checked) -> getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("sound_enabled", checked).apply());
        layout.addView(sound);
        hint = Ui.text(this, layout, "Загрузка контактов…");
        ListView list = new ListView(this);
        layout.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names); list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> openProfile(ids.get(position)));
        profiles = FirebaseDatabase.getInstance().getReference(UserStaticInfo.USERS_PROFILE_INFO);
    }
    private void openProfile(String id) {
        Intent intent = new Intent(this, ProfileActivity.class); intent.putExtra("uid", id); startActivity(intent);
    }
    @Override protected void onStart() {
        super.onStart(); if (profiles == null) return;
        listener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot data) {
                ids.clear(); names.clear();
                for (DataSnapshot p : data.getChildren()) {
                    if (p.getKey() == null || p.getKey().equals(uid)) continue;
                    String name = p.child("name").getValue(String.class);
                    if (name != null) { ids.add(p.getKey()); names.add(name); }
                }
                adapter.notifyDataSetChanged();
                hint.setText(names.isEmpty() ? "Других пользователей пока нет" : "Выберите собеседника");
            }
            @Override public void onCancelled(DatabaseError error) { hint.setText("Не удалось загрузить контакты"); Ui.error(MessengerActivity.this); }
        };
        profiles.addValueEventListener(listener);
    }
    @Override protected void onStop() { if (profiles != null && listener != null) profiles.removeEventListener(listener); listener = null; super.onStop(); }
}
