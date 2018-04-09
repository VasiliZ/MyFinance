package com.github.vasiliz.myfinance;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Objects;

public class LoginActivity extends BaseActivity implements View.OnClickListener, IContractView {

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private FirebaseAuth mAuth;
    private final String TAG = LoginActivity.class.getSimpleName();
    private final String mCreateAccount = "create Account ";
    private final String mDidntCreateAccount = "Failure on create account ";
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    private void createAccount(final String pEmail, final String pPassword) {
        Log.d(TAG, mCreateAccount + pEmail);

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(pEmail, pPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, mCreateAccount);
                        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        sendEmailVerify();
                        updateUI(firebaseUser);
                    } else {
                        exceptionsFirebase(task);
                        Log.d(TAG, mDidntCreateAccount, task.getException());
                        updateUI(null);
                    }
                    hideProgressDialog();
                });
    }


    private void signIn(final String pEmail, final String pPassword) {
        Log.d(TAG, "signIn:" + pEmail);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(pEmail, pPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Account created");
                        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        updateUI(firebaseUser);
                    } else {
                        exceptionsFirebase(task);
                        Log.d(TAG, "Failure on create account", task.getException());
                        updateUI(null);
                    }
                    hideProgressDialog();
                });
    }

    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
    }


    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    private void sendEmailVerify() {
        findViewById(R.id.verify_email_button).setEnabled(false);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Log.d("verify", String.valueOf(firebaseUser.isEmailVerified()));
                            Toast.makeText(this, R.string.verify_email + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.false_verify_email + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUI(final UserInfo pFirebaseUser) {
        hideProgressDialog();
        if (pFirebaseUser != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    pFirebaseUser.getEmail(), pFirebaseUser.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, pFirebaseUser.getUid()));
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!pFirebaseUser.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        final String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setText(R.string.required);
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        final String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setText(R.string.required);
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(final View view) {
        final int i = view.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerify();
        }
    }


    public void exceptionsFirebase(final Task pTask) {
        try {
            throw Objects.requireNonNull(pTask.getException());
        } catch (final FirebaseAuthUserCollisionException pCollisionException) {
            Toast.makeText(this, R.string.auth_user_collision, Toast.LENGTH_SHORT).show();
        } catch (final FirebaseAuthWeakPasswordException e) {
            Toast.makeText(this, R.string.auth_weak_password, Toast.LENGTH_SHORT).show();
        } catch (final FirebaseAuthInvalidCredentialsException pCredentialException) {
            Toast.makeText(this, R.string.auth_invalid_credentials, Toast.LENGTH_SHORT).show();
        } catch (final Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (mFirebaseUser.isEmailVerified()){
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, mFirebaseUser.getUid()));
        }
        
        super.onResume();


    }

    @Override
    protected void onRestart() {



        super.onRestart();


    }
}
