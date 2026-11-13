package com.example.practice16;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

/** Две ветки сообщений: получатель / отправитель / идентификатор. */
public class ChatActivity extends Activity {
    private String uid, other;
    private DatabaseReference mineRef, theirsRef, incomingRef, outgoingRef;
    private ValueEventListener mineListener, theirsListener, incomingListener, outgoingListener;
    private boolean mineKnown, theirsKnown, blockedByMe, blockedByOther;
    private TextView status; private EditText input; private Button send; private ArrayAdapter<String> adapter;
    private final ArrayList<String> lines = new ArrayList<>();
    private final ArrayList<Message> incoming = new ArrayList<>(), outgoing = new ArrayList<>();
    private static final class Message {
        final long time; final String key, text; final boolean mine;
        Message(long time, String key, String text, boolean mine) { this.time = time; this.key = key; this.text = text; this.mine = mine; }
    }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { Ui.logout(this); return; }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); other = getIntent().getStringExtra("uid");
        if (other == null || other.isEmpty() || uid.equals(other)) { finish(); return; }
        LinearLayout layout = Ui.column(this); Ui.text(this, layout, "Переписка");
        Ui.button(this, layout, "Назад к профилю", this::finish);
        status = Ui.text(this, layout, "Проверка блокировки…");
        ListView list = new ListView(this); layout.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines); list.setAdapter(adapter);
        input = new EditText(this); input.setHint("Сообщение"); input.setMaxLines(4); input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(2000)}); layout.addView(input);
        send = Ui.button(this, layout, "Отправить", this::sendMessage); send.setEnabled(false);
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        mineRef = root.child("Blocked").child(uid).child(other); theirsRef = root.child("Blocked").child(other).child(uid);
        incomingRef = root.child("Messages").child(uid).child(other); outgoingRef = root.child("Messages").child(other).child(uid);
    }
    private void showStatus() {
        boolean ready = mineKnown && theirsKnown && !blockedByMe && !blockedByOther;
        send.setEnabled(ready);
        status.setText(!mineKnown || !theirsKnown ? "Проверка блокировки…" : blockedByMe ? "Вы заблокировали пользователя" : blockedByOther ? "Собеседник ограничил возможность писать" : "Можно отправлять сообщения");
    }
    private void sendMessage() {
        String message = input.getText().toString().trim();
        if (!mineKnown || !theirsKnown || blockedByMe || blockedByOther || message.isEmpty()) return;
        send.setEnabled(false);
        Map<String, Object> data = new HashMap<>(); data.put("text", message); data.put("timestamp", ServerValue.TIMESTAMP);
        outgoingRef.push().setValue(data).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) { if (input.getText().toString().trim().equals(message)) input.setText(""); }
            else Toast.makeText(this, "Сообщение не отправлено: проверьте блокировку, интернет и правила базы", Toast.LENGTH_LONG).show();
            showStatus();
        });
    }
    private ValueEventListener blockListener(boolean mine) {
        return new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot data) {
                if (mine) { mineKnown = true; blockedByMe = Boolean.TRUE.equals(data.getValue(Boolean.class)); }
                else { theirsKnown = true; blockedByOther = Boolean.TRUE.equals(data.getValue(Boolean.class)); }
                showStatus();
            }
            @Override public void onCancelled(DatabaseError error) { if (mine) mineKnown = false; else theirsKnown = false; showStatus(); Ui.error(ChatActivity.this); }
        };
    }
    private ValueEventListener messageListener(boolean mine) {
        return new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot data) {
                ArrayList<Message> target = mine ? outgoing : incoming; target.clear();
                for (DataSnapshot child : data.getChildren()) {
                    String text = child.child("text").getValue(String.class); Long timestamp = child.child("timestamp").getValue(Long.class);
                    if (text != null && child.getKey() != null) target.add(new Message(timestamp == null ? 0 : timestamp, child.getKey(), text, mine));
                }
                ArrayList<Message> all = new ArrayList<>(incoming); all.addAll(outgoing);
                Collections.sort(all, (a, b) -> { int c = Long.compare(a.time, b.time); return c != 0 ? c : a.key.compareTo(b.key); });
                lines.clear(); for (Message m : all) lines.add((m.mine ? "Вы: " : "Собеседник: ") + m.text);
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError error) { Ui.error(ChatActivity.this); }
        };
    }
    @Override protected void onStart() {
        super.onStart(); if (mineRef == null) return;
        mineKnown = false; theirsKnown = false; showStatus();
        mineListener = blockListener(true); theirsListener = blockListener(false);
        incomingListener = messageListener(false); outgoingListener = messageListener(true);
        mineRef.addValueEventListener(mineListener); theirsRef.addValueEventListener(theirsListener);
        incomingRef.addValueEventListener(incomingListener); outgoingRef.addValueEventListener(outgoingListener);
    }
    @Override protected void onStop() {
        if (mineRef != null && mineListener != null) mineRef.removeEventListener(mineListener);
        if (theirsRef != null && theirsListener != null) theirsRef.removeEventListener(theirsListener);
        if (incomingRef != null && incomingListener != null) incomingRef.removeEventListener(incomingListener);
        if (outgoingRef != null && outgoingListener != null) outgoingRef.removeEventListener(outgoingListener);
        mineKnown = false; theirsKnown = false; super.onStop();
    }
}
