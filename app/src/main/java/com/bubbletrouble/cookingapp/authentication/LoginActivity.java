package com.bubbletrouble.cookingapp.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bubbletrouble.cookingapp.MainActivity;
import com.bubbletrouble.cookingapp.ProgressDialogBoxBubble;
import com.bubbletrouble.cookingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txt_email, txt_pass;
    private TextView txtV_sigup;
    private String email, pass;
    private TextInputLayout layout_email, layout_pass;
    private ProgressDialogBoxBubble dialogBoxBubble;
    private static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        txt_email = findViewById(R.id.txt_login_email);
        txt_pass = findViewById(R.id.txt_login_pass);
        txtV_sigup = findViewById(R.id.txtV_login_creatacc);
        txtV_sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        layout_email = findViewById(R.id.layout_login_email);
        layout_pass = findViewById(R.id.layout_login_pass);
        dialogBoxBubble = new ProgressDialogBoxBubble();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
    }

    public void onLoginClicked(View v)
    {
        submitForm();
    }

    private void loginFirebaseAcc()
    {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialogBoxBubble.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // FIRE ZE MISSILES!
                                        }
                                    });
                            builder.create().show();
                            dialogBoxBubble.dismiss();
                            //TODO
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            navHeaderName.setText(user.getDisplayName());
//            navHeaderEmail.setText(user.getEmail());
//        } else {
//            navHeaderName.setText(null);
//            navHeaderEmail.setText(null);
//        }
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePass()) {
            return;
        }
        dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
        loginFirebaseAcc();
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

        if (email.isEmpty()) {
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
}
