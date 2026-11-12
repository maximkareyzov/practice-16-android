package com.example.practice16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class SignActivity extends Activity {
    private EditText login, password, newLogin, newPassword, newName, newAge, newState;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_sign);
        TabHost tabs = findViewById(R.id.tabHost); tabs.setup();
        tabs.addTab(tabs.newTabSpec("in").setIndicator(getString(R.string.sign_in)).setContent(R.id.tabSignIn));
        tabs.addTab(tabs.newTabSpec("up").setIndicator(getString(R.string.sign_up)).setContent(R.id.tabSignUp));
        login = findViewById(R.id.LoginTextView); password = findViewById(R.id.PasswordTextView);
        newLogin = findViewById(R.id.NewLoginTextView); newPassword = findViewById(R.id.NewPasswordTextView);
        newName = findViewById(R.id.NewNameTextView); newAge = findViewById(R.id.NewAgeTextView); newState = findViewById(R.id.NewStateTextView);
    }
    private String text(EditText field) { return field.getText().toString().trim(); }
    private String email(String value) { return value.contains("@") ? value : value + "@practice17.invalid"; }
    private boolean configured() { if (FirebaseApp.getApps(this).isEmpty()) { toast(R.string.firebase_missing); return false; } return true; }
    public void SignIn(View view) {
        String name = text(login), pass = password.getText().toString();
        if (name.isEmpty() || pass.isEmpty()) { toast(R.string.empty_fields); return; }
        if (!configured()) return;
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email(name), pass).addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult().getUser() != null) loadProfile(task.getResult().getUser().getUid(), name);
            else toast(R.string.wrong_login);
        });
    }
    public void SignUp(View view) {
        String name = text(newLogin), pass = newPassword.getText().toString();
        String displayName = text(newName), state = text(newState);
        if (name.isEmpty() || pass.isEmpty() || displayName.isEmpty() || state.isEmpty()) { toast(R.string.empty_fields); return; }
        if (!configured()) return;
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email(name), pass).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful() || task.getResult().getUser() == null) { toast(R.string.wrong_login); return; }
            String uid = task.getResult().getUser().getUid();
            Map<String, Object> profile = new HashMap<>();
            profile.put("name", displayName); profile.put("age", Transform.parseIntOrDefault(text(newAge), 0)); profile.put("state", state);
            FirebaseDatabase.getInstance().getReference(UserStaticInfo.USERS_PROFILE_INFO).child(uid).setValue(profile).addOnCompleteListener(this, saved -> {
                if (saved.isSuccessful()) loadProfile(uid, name);
                else { FirebaseAuth.getInstance().signOut(); toast(R.string.db_error); }
            });
        });
    }
    private void loadProfile(String id, String loginName) {
        FirebaseDatabase.getInstance().getReference(UserStaticInfo.USERS_PROFILE_INFO).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        if (isFinishing() || isDestroyed()) return;
                        String displayName = snapshot.child("name").getValue(String.class);
                        String state = snapshot.child("state").getValue(String.class);
                        Integer age = snapshot.child("age").getValue(Integer.class);
                        if (displayName == null || state == null) {
                            FirebaseAuth.getInstance().signOut(); toast(R.string.db_error); return;
                        }
                        if (UserStaticInfo.signedInUser != null) UserStaticInfo.users.remove(UserStaticInfo.signedInUser);
                        UserStaticInfo.signedInUser = new User(displayName, state, age == null ? 0 : age, 0);
                        goNext(id, loginName);
                    }
                    @Override public void onCancelled(DatabaseError error) {
                        if (!isFinishing() && !isDestroyed()) {
                            FirebaseAuth.getInstance().signOut(); toast(R.string.db_error);
                        }
                    }
                });
    }
    private void goNext(String id, String name) {
        UserStaticInfo.profileId = id;
        startActivity(new Intent(this, MainActivity.class)); finish();
    }
    public void openDemo(View view) { startActivity(new Intent(this, MainActivity.class)); }
    private void toast(int id) { Toast.makeText(this, id, Toast.LENGTH_SHORT).show(); }
}
