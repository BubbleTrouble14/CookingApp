package com.bubbletrouble.cookingapp.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bubbletrouble.cookingapp.MainActivity;
import com.bubbletrouble.cookingapp.ProgressDialogBoxBubble;
import com.bubbletrouble.cookingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txt_username, txt_email, txt_pass, txt_passconf;
    private String username, email, pass, passconf;
    private Button btn_signup;
    private ProgressDialogBoxBubble dialogBoxBubble;
    private static String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_sign_up);

        txt_username = findViewById(R.id.txt_signup_username);
        txt_email = findViewById(R.id.txt_signup_email);
        txt_pass = findViewById(R.id.txt_signup_pass);
        txt_passconf = findViewById(R.id.txt_signup_passconf);
        btn_signup = findViewById(R.id.btn_signup);
        dialogBoxBubble = new ProgressDialogBoxBubble();
    }

    public void onSignUpClicked(View v)
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
            onValidateSuccess();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
     //   FirebaseUser currentUser = mAuth.getCurrentUser();
      //TODO  updateUI(currentUser);
    }

    private void init()
    {
        username = txt_username.getText().toString().trim();
        email = txt_email.getText().toString().trim();
        pass = txt_pass.getText().toString().trim();
        passconf = txt_passconf.getText().toString().trim();
    }

    private boolean validate()
    {
        boolean valid = true;
        if(username.isEmpty())
        {
            txt_username.setError("Please Enter a valid Username");
            valid = false;
        }
        if(email.isEmpty() || !isEmailValid(email))
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
        if(passconf.isEmpty() || !passconf.equals(pass))
        {
            txt_passconf.setError("Make sure you typed in the same Password");
            valid = false;
        }
        return valid;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void onValidateSuccess()
    {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialogBoxBubble.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                           //TODO updateUI(user);
                        } else {
                            dialogBoxBubble.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //TODO updateUI(null);
                        }
                        // ...
                    }
                });
    }
}
