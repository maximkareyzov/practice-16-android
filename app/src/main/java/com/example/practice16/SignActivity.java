package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SignActivity extends Activity {
    private EditText login, password;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_sign);
        login = findViewById(R.id.LoginTextView);
        password = findViewById(R.id.PasswordTextView);
    }
    private String email(String value) { return value.contains("@") ? value : value + "@practice17.invalid"; }
    public void SignIn(View view) {
        String name = login.getText().toString().trim();
        String pass = password.getText().toString();
        if (name.isEmpty() || pass.isEmpty()) { toast(R.string.empty_fields); return; }
        if (FirebaseApp.getApps(this).isEmpty()) { toast(R.string.firebase_missing); return; }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email(name), pass).addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult().getUser() != null) {
                UserStaticInfo.profileId = task.getResult().getUser().getUid();
                startActivity(new Intent(this, MainActivity.class)); finish();
            } else toast(R.string.wrong_login);
        });
    }
    public void openDemo(View view) { startActivity(new Intent(this, MainActivity.class)); }
    private void toast(int id) { Toast.makeText(this, id, Toast.LENGTH_SHORT).show(); }
}
