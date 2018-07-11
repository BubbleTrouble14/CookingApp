package com.bubbletrouble.cookingapp.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bubbletrouble.cookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupFirebaseListener();
    }

    public void onChangePasswordClicked(View v)
    {
        startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
    }

    public void onChangeEmailClicked(View v)
    {
        startActivity(new Intent(SettingsActivity.this, ChangeEmailActivity.class));
    }

    public void onDeleteAccountClicked(View v)
    {
        startActivity(new Intent(SettingsActivity.this, DeleteUserActivity.class));
    }

    public void onSignOutClicked(View v)
    {
        FirebaseAuth.getInstance().signOut();
    }

    private void setupFirebaseListener()
    {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                   // Toast.makeText(SettingsActivity.this, "sign_in" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);

        }
    }
}
