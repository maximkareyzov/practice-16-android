package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

/** Профиль собеседника и управление собственным чёрным списком. */
public class ProfileActivity extends Activity {
    private String uid, other; private boolean blocked, blockKnown;
    private DatabaseReference profileRef, blockRef; private ValueEventListener profileListener, blockListener;
    private TextView info; private Button blockButton, chatButton;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { Ui.logout(this); return; }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); other = getIntent().getStringExtra("uid");
        if (other == null || other.isEmpty()) { finish(); return; }
        LinearLayout layout = Ui.column(this); Ui.text(this, layout, uid.equals(other) ? "Мой профиль" : "Профиль собеседника");
        info = Ui.text(this, layout, "Загрузка…");
        Ui.button(this, layout, "Назад", this::finish);
        if (uid.equals(other)) Ui.button(this, layout, "Выйти из аккаунта", () -> Ui.logout(this));
        else {
            chatButton = Ui.button(this, layout, "Открыть чат", () -> { Intent i = new Intent(this, ChatActivity.class); i.putExtra("uid", other); startActivity(i); });
            blockButton = Ui.button(this, layout, "Загрузка…", this::toggleBlock); blockButton.setEnabled(false);
        }
        profileRef = FirebaseDatabase.getInstance().getReference("UsersProfileInfo").child(other);
        blockRef = FirebaseDatabase.getInstance().getReference("Blocked").child(uid).child(other);
    }
    private void toggleBlock() {
        if (!blockKnown) return;
        blockButton.setEnabled(false);
        blockRef.setValue(blocked ? null : true).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) { Ui.error(this); blockButton.setEnabled(blockKnown); }
        });
    }
    @Override protected void onStart() {
        super.onStart(); if (profileRef == null) return;
        profileListener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot data) {
                String name = data.child("name").getValue(String.class), status = data.child("state").getValue(String.class);
                Object age = data.child("age").getValue();
                info.setText(name == null ? "Профиль не найден" : "Имя: " + name + "\nВозраст: " + (age == null ? "—" : age) + "\nСтатус: " + (status == null ? "—" : status));
            }
            @Override public void onCancelled(DatabaseError error) { info.setText("Не удалось загрузить профиль"); Ui.error(ProfileActivity.this); }
        }; profileRef.addValueEventListener(profileListener);
        if (blockButton != null) {
            blockKnown = false;
            blockListener = new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot data) { blocked = Boolean.TRUE.equals(data.getValue(Boolean.class)); blockKnown = true; blockButton.setText(blocked ? "Разблокировать" : "Заблокировать"); blockButton.setEnabled(true); }
                @Override public void onCancelled(DatabaseError error) { blockKnown = false; blockButton.setEnabled(false); Ui.error(ProfileActivity.this); }
            }; blockRef.addValueEventListener(blockListener);
        }
    }
    @Override protected void onStop() {
        if (profileRef != null && profileListener != null) profileRef.removeEventListener(profileListener);
        if (blockRef != null && blockListener != null) blockRef.removeEventListener(blockListener);
        profileListener = null; blockListener = null; super.onStop();
    }
}
