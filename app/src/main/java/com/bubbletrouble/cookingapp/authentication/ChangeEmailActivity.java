package com.bubbletrouble.cookingapp.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bubbletrouble.cookingapp.ProgressDialogBoxBubble;
import com.bubbletrouble.cookingapp.R;
import com.bubbletrouble.cookingapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText txt_pass, txt_email;
    private String email, pass;
    private static String TAG = "ChangeEmailActivity";
    private Button btn_email;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialogBoxBubble dialogBoxBubble;
    private TextInputLayout layout_pass, layout_email;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        txt_pass = findViewById(R.id.txt_change_email_password);
        txt_email  = findViewById(R.id.txt_change_email);
        btn_email = findViewById(R.id.btn_update_email);

        layout_pass = findViewById(R.id.layout_change_email_pass);
        layout_email = findViewById(R.id.layout_change_email);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialogBoxBubble = new ProgressDialogBoxBubble();

        txt_pass.setVisibility(View.VISIBLE);
        txt_email.setVisibility(View.GONE);

        txt_pass.addTextChangedListener(new MyTextWatcher(txt_pass));
        txt_email.addTextChangedListener(new MyTextWatcher(txt_email));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_email.setOnClickListener(new View.OnClickListener() {
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
            if(txt_pass.getVisibility() == View.VISIBLE)
            {
                if (!validatePass()) {
                    return;
                }
                dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),txt_pass.getText().toString().trim());
                user.reauthenticateAndRetrieveData(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            txt_pass.setVisibility(View.GONE);
                            txt_email.setVisibility(View.VISIBLE);

                            layout_pass.setVisibility(View.GONE);
                            layout_email.setVisibility(View.VISIBLE);

                            dialogBoxBubble.dismiss();
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmailActivity.this);
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
                if (!validateEmail()) {
                    return;
                }
                dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangeEmailActivity.this, "Successfully updated email", Toast.LENGTH_SHORT).show();
                                    try {
                                        writeNewUser(user.getUid(), user.getDisplayName(), email);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                   // startActivity(new Intent(ChangeEmailActivity.this, SettingsActivity.class));
                                    finish();
                                    dialogBoxBubble.dismiss();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmailActivity.this);
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

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private boolean validatePass() {
        pass = txt_pass.getText().toString().trim();

        if (pass.isEmpty()) {
            layout_pass.setError(getString(R.string.err_msg_password));
            requestFocus(txt_pass);
            return false;
        }
        else {
            layout_pass.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        email = txt_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            layout_email.setError(getString(R.string.err_msg_email));
            requestFocus(txt_email);
            return false;
        } else {
            layout_email.setErrorEnabled(false);
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                case R.id.txt_change_email_password:
                    validatePass();
                    break;
                case R.id.txt_change_email:
                    validateEmail();
                    break;
            }
        }
    }
}
