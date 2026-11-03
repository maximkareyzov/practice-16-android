package com.example.practice16;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final List<User> users = new ArrayList<>();
    private UserListAdapter adapter;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        users.add(new User("Иван", "Я устал", 19));
        users.add(new User("Анна", "Учусь программировать", 20));
        users.add(new User("Мария", "На занятиях", 21));
        users.add(new User("Алексей", "Работаю над проектом", 19));
        adapter = new UserListAdapter();
        ((ListView) findViewById(R.id.listView)).setAdapter(adapter);
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
            return row;
        }
    }
}
