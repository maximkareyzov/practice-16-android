package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends Activity {
    private final List<User> users = UserStaticInfo.users;
    private UserListAdapter adapter;
    private View userPanel;
    private int selectedPosition = -1;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        new UserStaticInfo();
        setContentView(R.layout.activity_main);
        adapter = new UserListAdapter();
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        userPanel = findViewById(R.id.userPanel);
        findViewById(R.id.backButton).setOnClickListener(v -> {
            selectedPosition = -1;
            userPanel.setVisibility(View.GONE);
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            showUser(users.get(position));
        });
        if (state != null) {
            selectedPosition = state.getInt("selectedPosition", -1);
            if (selectedPosition >= 0 && selectedPosition < users.size()) showUser(users.get(selectedPosition));
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedPosition", selectedPosition);
    }

    @Override protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
        if (selectedPosition >= 0 && selectedPosition < users.size()) showUser(users.get(selectedPosition));
    }

    private void openEditor(int position) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserStaticInfo.POSITION, position);
        startActivity(intent);
    }

    private void showUser(User user) {
        ((TextView) findViewById(R.id.panelName)).setText(user.getName());
        ((TextView) findViewById(R.id.panelState)).setText(user.getState());
        ((TextView) findViewById(R.id.panelAge)).setText(String.valueOf(user.getAge()));
        userPanel.setVisibility(View.VISIBLE);
    }

    private class UserListAdapter extends BaseAdapter {
        @Override public int getCount() { return users.size(); }
        @Override public User getItem(int position) { return users.get(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View row, ViewGroup parent) {
            if (row == null) row = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_user, parent, false);
            User user = getItem(position);
            ((TextView) row.findViewById(R.id.itemName)).setText(user.getName());
            ((TextView) row.findViewById(R.id.itemState)).setText(user.getState());
            int indicator;
            switch (user.getStateSignal()) {
                case 1: indicator = R.drawable.back_online; break;
                case 2: indicator = R.drawable.back_departed; break;
                default: indicator = R.drawable.back_offline;
            }
            row.findViewById(R.id.stateRound).setBackgroundResource(indicator);
            return row;
        }
    }
}
