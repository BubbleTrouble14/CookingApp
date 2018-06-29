package com.bubbletrouble.cookingapp.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        dialogBoxBubble = new ProgressDialogBoxBubble();
    }


    public void onLoginClicked(View v)
    {
        dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
        init();
        if(!validate())
        {
            dialogBoxBubble.dismiss();
            Toast.makeText(this, "Error with credentials", Toast.LENGTH_SHORT).show();
        }
        else
        {
            login();
        }
    }

    private void login()
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

                       //     updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            dialogBoxBubble.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                       //     updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void init()
    {
        email = txt_email.getText().toString().trim();
        pass = txt_pass.getText().toString().trim();
    }

    private boolean validate()
    {
        boolean valid = true;
        if(email.isEmpty())
        {
            txt_email.setError("Please Enter a valid Email");
            valid = false;
        }
        if(pass.isEmpty())
        {
            txt_pass.setError("Please Enter a valid Password");
            valid = false;
        }
        if(pass.length() < 6)
        {
            txt_pass.setError("The Password must at least have 6 Characters");
        }
        return valid;
    }
}
