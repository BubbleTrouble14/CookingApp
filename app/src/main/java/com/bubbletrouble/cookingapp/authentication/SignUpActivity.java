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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bubbletrouble.cookingapp.MainActivity;
import com.bubbletrouble.cookingapp.ProgressDialogBoxBubble;
import com.bubbletrouble.cookingapp.R;
import com.bubbletrouble.cookingapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText txt_username, txt_email, txt_pass, txt_passconf;
    private String email, pass;
    private TextInputLayout layout_username, layout_email, layout_pass, layout_passconf;
    private ProgressDialogBoxBubble dialogBoxBubble;
    private static String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        txt_username = findViewById(R.id.txt_signup_username);
        txt_email = findViewById(R.id.txt_signup_email);
        txt_pass = findViewById(R.id.txt_signup_pass);
        txt_passconf = findViewById(R.id.txt_signup_passconf);
        dialogBoxBubble = new ProgressDialogBoxBubble();

        layout_username = findViewById(R.id.layout_signup_txtUsername);
        layout_email = findViewById(R.id.layout_signup_txtEmail);
        layout_pass = findViewById(R.id.layout_signup_txtPass);
        layout_passconf = findViewById(R.id.layout_signup_passconf);

        txt_username.addTextChangedListener(new MyTextWatcher(txt_username));
        txt_email.addTextChangedListener(new MyTextWatcher(txt_email));
        txt_pass.addTextChangedListener(new MyTextWatcher(txt_pass));
        txt_passconf.addTextChangedListener(new MyTextWatcher(txt_passconf));
    }

//    public void onSignUpClicked(View v)
//    {
//        dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
//        init();
//        if(!validate())
//        {
//            dialogBoxBubble.dismiss();
//            Toast.makeText(this, "Error with credentials", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            onValidateSuccess();
//        }
//    }

    public void onSignUpClicked(View v)
    {
        submitForm();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
     //   FirebaseUser currentUser = mAuth.getCurrentUser();
      //TODO  updateUI(currentUser);
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void createFirebaseAcc()
    {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(txt_username.getText().toString().trim()).build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        dialogBoxBubble.dismiss();
                                        try {
                                            writeNewUser(user.getUid(), txt_username.getText().toString().trim(), user.getEmail());
                                        } catch (Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
                                        }
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    }
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {}
                                                });
                                        builder.create().show();
                                        dialogBoxBubble.dismiss();
                                    }
                                }
                            });

                           //TODO updateUI(user);
                        } else {
                            dialogBoxBubble.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {}
                                    });
                            builder.create().show();
                            //TODO updateUI(null);
                        }
                    }
                });
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePass()) {
            return;
        }
        if (!validatePassconf()) {
            return;
        }
        dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
        createFirebaseAcc();
    }

    private boolean validateName() {
        if (txt_username.getText().toString().trim().isEmpty()) {
            layout_username.setError(getString(R.string.err_msg_name));
            requestFocus(txt_username);
            return false;
        } else {
            layout_username.setErrorEnabled(false);
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

    private boolean validatePass() {
        pass = txt_pass.getText().toString().trim();

        if (pass.isEmpty()) {
            layout_pass.setError(getString(R.string.err_msg_password));
            requestFocus(txt_pass);
            return false;
        }
        if(txt_pass.getText().toString().trim().length() < 6)
        {
            layout_pass.setError(getString(R.string.err_msg_password_length));
            requestFocus(txt_pass);
            return false;
        }
        else {
            layout_pass.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassconf() {
        if (txt_passconf.getText().toString().trim().isEmpty()) {
            layout_passconf.setError(getString(R.string.err_msg_password));
            requestFocus(txt_passconf);
            return false;
        }
        if(!txt_passconf.getText().toString().trim().equals(txt_pass.getText().toString().trim()))
        {
            layout_passconf.setError(getString(R.string.err_msg_passwconf));
            requestFocus(txt_passconf);
            return false;
        }
        else {
            layout_passconf.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                    validateName();
                    break;
                case R.id.txt_signup_email:
                    validateEmail();
                    break;
                case R.id.txt_signup_pass:
                    validatePass();
                    break;
                case R.id.txt_signup_passconf:
                    validatePassconf();
                    break;
            }
        }
    }
}
