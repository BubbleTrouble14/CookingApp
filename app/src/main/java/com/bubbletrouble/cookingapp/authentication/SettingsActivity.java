package com.bubbletrouble.cookingapp.authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bubbletrouble.cookingapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

    }

    public void onSignOutClicked(View v)
    {

    }
}
