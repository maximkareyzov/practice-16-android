package com.example.practice16;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class UserActivity extends Activity {
    private User activeUser;
    private EditText nameField, stateField, ageField;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_user);
        new UserStaticInfo();
        int position = getIntent().getIntExtra(UserStaticInfo.POSITION, -1);
        if (position < 0 || position >= UserStaticInfo.users.size()) { finish(); return; }
        activeUser = UserStaticInfo.users.get(position);
        nameField = findViewById(R.id.editName);
        stateField = findViewById(R.id.editState);
        ageField = findViewById(R.id.editAge);
        ageField.setText(String.valueOf(activeUser.getAge()));
        nameField.setText(activeUser.getName());
        stateField.setText(activeUser.getState());
        findViewById(R.id.editorBack).setOnClickListener(v -> finish());
        findViewById(R.id.saveButton).setOnClickListener(v -> save());
    }

    private void save() {
        activeUser.setName(nameField.getText().toString());
        activeUser.setState(stateField.getText().toString());
        activeUser.setAge(Transform.parseIntOrDefault(ageField.getText().toString(), activeUser.getAge()));
        finish();
    }
}
