package edu.northeastern.team21.Foggy;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import edu.northeastern.team21.FoggyActivity;
import edu.northeastern.team21.R;
import edu.northeastern.team21.SystemBarHiddenAbstractActivity;

public class Login extends SystemBarHiddenAbstractActivity {

    EditText email, password;
    TextView txtsignup;
    Button loginbtn;
    ImageView backgroundImageImageView;

    FirebaseAuth authProfile;

    ProgressBar progressbar;

    private static final String TAG = "__LOGIN_ACTIVITY__";

    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(TAG, "now in Portrait mode");
            backgroundImageImageView = findViewById(R.id.imageView_login_bg);
            backgroundImageImageView.setImageResource(R.drawable.image_loginsignup_bgimage);
        }else {
            Log.d(TAG, "now in Landscape mode");
            backgroundImageImageView = findViewById(R.id.imageView_login_bg_land);
            backgroundImageImageView.setImageResource(R.drawable.image_loginsignup_bgimage_landscape);
        }

        email = findViewById(R.id.login_useremail);
        password = findViewById(R.id.login_password);
        loginbtn = findViewById(R.id.login_button);
        txtsignup = findViewById(R.id.signuptext);
        progressbar = findViewById(R.id.login_progressBar);
        authProfile= FirebaseAuth.getInstance();





        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        startActivity(new Intent(this, FoggyActivity.class));
//                        Toast.makeText(this, "Precise location access granted.", Toast.LENGTH_SHORT).show();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                        Toast.makeText(this, "Only approximate location access granted.", Toast.LENGTH_SHORT).show();
                    } else {
                        // No location access granted.
                        Toast.makeText(this, "No location access granted.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtemail = email.getText().toString();
                String txtpswd = password.getText().toString();

                if (TextUtils.isEmpty(txtemail)) {
                    Toast.makeText(Login.this, "Password is empty!", Toast.LENGTH_SHORT).show();
                    email.setError("Password is empty!");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtemail).matches()) {
                    Toast.makeText(Login.this, "Invalid email type!", Toast.LENGTH_SHORT).show();
                    email.setError("Invalid email type!");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(txtpswd)) {
                    Toast.makeText(Login.this, "Password is empty!", Toast.LENGTH_SHORT).show();
                    password.setError("Password is empty!");
                    password.requestFocus();

                } else {
                    progressbar.setVisibility(View.VISIBLE);
                    userlogin(txtemail, txtpswd);
                }
            }
        });
        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

    }

    private void userlogin(String txtemail, String txtPswd) {

        authProfile.signInWithEmailAndPassword(txtemail, txtPswd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    locationPermissionRequest.launch(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });
                    Toast.makeText(Login.this, "LOGGING IN", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        email.setError("User not found!");
                        email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        email.setError("Invalid Credentials!");
                        email.requestFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(Login.this, "Try Again", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(this, "You are already logged in!", Toast.LENGTH_SHORT).show();
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });

            finish();
        } else {
            Toast.makeText(this, "You can login!", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * This method will be called when layout changes between portrait mode and landscape mode
     * two diff layouts are used for these two modes, but components are same
     * */
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Log.d(TAG, "now in Landscape mode");
//            //setContentView(R.layout.activity_my_landscape);
//        } else {
//            Log.d(TAG, "now in Portrait mode");
//            //setContentView(R.layout.activity_my_portrait);
//        }
//
//    }
}


