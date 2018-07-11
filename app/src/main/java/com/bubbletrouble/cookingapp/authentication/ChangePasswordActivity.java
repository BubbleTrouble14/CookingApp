package com.bubbletrouble.cookingapp.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bubbletrouble.cookingapp.ProgressDialogBoxBubble;
import com.bubbletrouble.cookingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText txt_pass_curr, txt_pass_new, txt_pass_newconf;
    private String pass;
    private static String TAG = "ChangePasswordActivity";
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialogBoxBubble dialogBoxBubble;
    private Button btn_pass;
    private TextInputLayout layout_pass_curr, layout_pass_new, layout_pass_newconf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txt_pass_curr = findViewById(R.id.txt_change_password_current);
        txt_pass_new  = findViewById(R.id.txt_change_password_new);
        txt_pass_newconf = findViewById(R.id.txt_change_password_new_conf);
        btn_pass = findViewById(R.id.btn_update_pass);

        layout_pass_curr = findViewById(R.id.layout_change_pass_current);
        layout_pass_new = findViewById(R.id.layout_change_pass_new);
        layout_pass_newconf = findViewById(R.id.layout_change_pass_newconf);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialogBoxBubble = new ProgressDialogBoxBubble();

        txt_pass_curr.setVisibility(View.VISIBLE);
        txt_pass_new.setVisibility(View.GONE);
        txt_pass_newconf.setVisibility(View.GONE);

        txt_pass_curr.addTextChangedListener(new MyTextWatcher(txt_pass_curr));
        txt_pass_new.addTextChangedListener(new MyTextWatcher(txt_pass_new));
        txt_pass_newconf.addTextChangedListener(new MyTextWatcher(txt_pass_newconf));

        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(view);
            }
        });

    }

    public void update(View v)
    {
        if(user != null)
        {
            if(txt_pass_curr.getVisibility() == View.VISIBLE)
            {
                if (!validateCurrPass()) {
                    return;
                }
                dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),txt_pass_curr.getText().toString().trim());
                user.reauthenticateAndRetrieveData(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            txt_pass_curr.setVisibility(View.GONE);
                            txt_pass_new.setVisibility(View.VISIBLE);
                            txt_pass_newconf.setVisibility(View.VISIBLE);

                            layout_pass_curr.setVisibility(View.GONE);
                            layout_pass_new.setVisibility(View.VISIBLE);
                            layout_pass_newconf.setVisibility(View.VISIBLE);

                            dialogBoxBubble.dismiss();
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {}
                                    });
                            builder.create().show();
                            dialogBoxBubble.dismiss();
                        }
                    }
                });
            }
            else {
                if (!validateNewPass()) {
                    return;
                }
                if (!validateNewPassconf()) {
                    return;
                }
                dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
                user.updatePassword(txt_pass_new.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Successfully updated password", Toast.LENGTH_SHORT).show();
                                   // startActivity(new Intent(ChangePasswordActivity.this, SettingsActivity.class));
                                    finish();
                                    dialogBoxBubble.dismiss();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                    builder.setMessage(task.getException().getMessage())
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });
                                    builder.create().show();
                                    dialogBoxBubble.dismiss();
                                }
                            }
                        });
            }
        }
    }

    private boolean validateCurrPass() {
        pass = txt_pass_curr.getText().toString().trim();

        if (pass.isEmpty()) {
            layout_pass_curr.setError(getString(R.string.err_msg_password));
            requestFocus(txt_pass_curr);
            return false;
        }
        else {
            layout_pass_curr.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNewPass() {
        pass = txt_pass_new.getText().toString().trim();

        if (pass.isEmpty()) {
            txt_pass_new.setError(getString(R.string.err_msg_password));
            requestFocus(txt_pass_new);
            return false;
        }
        if(txt_pass_new.getText().toString().trim().length() < 6)
        {
            layout_pass_new.setError(getString(R.string.err_msg_password_length));
            requestFocus(txt_pass_new);
            return false;
        }
        else {
            layout_pass_new.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNewPassconf() {
        if (txt_pass_newconf.getText().toString().trim().isEmpty()) {
            txt_pass_newconf.setError(getString(R.string.err_msg_password));
            requestFocus(txt_pass_newconf);
            return false;
        }
        if(!txt_pass_newconf.getText().toString().trim().equals(txt_pass_new.getText().toString().trim()))
        {
            layout_pass_newconf.setError(getString(R.string.err_msg_passwconf));
            requestFocus(txt_pass_newconf);
            return false;
        }
        else {
            layout_pass_newconf.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txt_signup_username:
                    validateCurrPass();
                    break;
                case R.id.txt_signup_email:
                    validateNewPass();
                    break;
                case R.id.txt_signup_pass:
                    validateNewPassconf();
                    break;
            }
        }
    }
}
