package com.tugasoft.fintuga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.tugasoft.fintuga.MainActivity;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.MySharedPreferences;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FirebaseAuthenticationActivity extends AppCompatActivity {
    List<AuthUI.IdpConfig> providers;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_firebase_autentication);
        providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
        showSignInOptions();
    }

    private void showSignInOptions() {
        startActivityForResult(((AuthUI.SignInIntentBuilder) ((AuthUI.SignInIntentBuilder) ((AuthUI.SignInIntentBuilder) AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)).setTheme(R.style.AppTheme)).setLogo(R.mipmap.ic_launcher)).build(), 123);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123) {
            IdpResponse fromResultIntent = IdpResponse.fromResultIntent(intent);
            if (i2 == -1) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (MySharedPreferences.getBool(MySharedPreferences.KEY_IS_WELCOME_SCREEN_SHOWN)) {
                        startActivity(new Intent(this, DashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, MoreActivity.class));
                    }
                    finish();
                }
            } else if (fromResultIntent != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                FirebaseUiException error = fromResultIntent.getError();
                Objects.requireNonNull(error);
                sb.append(error.getMessage());
                Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
                Log.e("TAG", "onActivityResult: " + sb);
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}