package com.bubbletrouble.cookingapp.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
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

import java.util.Set;

public class DeleteUserActivity extends AppCompatActivity {

    private EditText txt_pass;
    private Button btn_delete_acc;
    private FirebaseUser user;
    private String pass, userID;
    private TextInputLayout layout_pass;
    private ProgressDialogBoxBubble dialogBoxBubble;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        txt_pass = findViewById(R.id.txt_delete_acc_pass);
        btn_delete_acc = findViewById(R.id.btn_delete_acc);

        layout_pass = findViewById(R.id.layout_delete_acc_pass);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dialogBoxBubble = new ProgressDialogBoxBubble();

        txt_pass.addTextChangedListener(new MyTextWatcher(txt_pass));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("message");

        btn_delete_acc.setOnClickListener(new View.OnClickListener() {
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
            if (!validatePass()) {
                return;
            }
                dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),txt_pass.getText().toString().trim());

                userID = user.getUid();

                user.reauthenticateAndRetrieveData(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialogBoxBubble.dismiss();
                            AlertDialog.Builder dialog_delete = new AlertDialog.Builder(DeleteUserActivity.this);
                            dialog_delete.setMessage("Are you sure you want to delete your account?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            dialogBoxBubble.show(getSupportFragmentManager(), "progress_dialog");
                                            mDatabase.child("users").child(userID).removeValue();
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //TODO Delete account from database
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(DeleteUserActivity.this, "Successfully deleted Account", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                                startActivity(new Intent(DeleteUserActivity.this, LoginActivity.class));
                                                                dialogBoxBubble.dismiss();
                                                            }
                                                            else
                                                            {
                                                                dialogBoxBubble.dismiss();
                                                            }
                                                        }
                                                    });
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            dialog_delete.create().show();
                            //TODO Dialog box for yes
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeleteUserActivity.this);
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
                    validatePass();
                    break;
            }
        }
    }
}
