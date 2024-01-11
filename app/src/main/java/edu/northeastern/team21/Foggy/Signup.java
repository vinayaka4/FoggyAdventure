package edu.northeastern.team21.Foggy;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import edu.northeastern.team21.R;
import edu.northeastern.team21.SystemBarHiddenAbstractActivity;

public class Signup extends SystemBarHiddenAbstractActivity {
    private EditText name, username, email, password, confirmpassword, dob;
    private Button signup;

    private ImageView backgroundImageImageView;
    private DatePickerDialog picker;

    private TextView txtlogin;
    private ProgressBar progressBar;

    private static final String TAG = "__SIGNUP_ACITIVITY__";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(TAG, "now in Portrait mode");
            backgroundImageImageView = findViewById(R.id.imageView_signup_bg);
            backgroundImageImageView.setImageResource(R.drawable.image_loginsignup_bgimage);
        }else {
            Log.d(TAG, "now in Landscape mode");
            backgroundImageImageView = findViewById(R.id.imageView_signup_bg_land);
            backgroundImageImageView.setImageResource(R.drawable.image_loginsignup_bgimage_landscape);
        }


        name = findViewById(R.id.signup_name);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        dob = findViewById(R.id.signup_userdob);
        confirmpassword = findViewById(R.id.signup_confirmpassword);
        signup = findViewById(R.id.signup);
        txtlogin = findViewById(R.id.logintext);
        progressBar = findViewById(R.id.progressBar);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(Signup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtname = name.getText().toString();
                String txtusername = username.getText().toString();
                String txtemail = email.getText().toString();
                String txtdob = dob.getText().toString();
                String txtpassword = password.getText().toString();
                String txtconfirmpassword = confirmpassword.getText().toString();
                if (TextUtils.isEmpty(txtname)) {
                    Toast.makeText(Signup.this, "Name is Empty!", Toast.LENGTH_SHORT).show();
                    name.setError("Name is Empty!");
                    name.requestFocus();
                } else if (TextUtils.isEmpty(txtusername)) {
                    Toast.makeText(Signup.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                    username.setError("Username is Empty!");
                    username.requestFocus();
                } else if (TextUtils.isEmpty(txtemail)) {
                    Toast.makeText(Signup.this, "Email is Empty!", Toast.LENGTH_SHORT).show();
                    email.setError("Email is Empty!");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtemail).matches()) {
                    Toast.makeText(Signup.this, "RE-EMAIL IS EMPTY", Toast.LENGTH_SHORT).show();
                    email.setError("Invalid email type!");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(txtpassword)) {
                    Toast.makeText(Signup.this, "PASSWORD IS EMPTY", Toast.LENGTH_SHORT).show();
                    password.setError("Password is empty!");
                    password.requestFocus();
                } else if (txtpassword.length() < 6) {
                    Toast.makeText(Signup.this, "PASSWORD should be atleast 6 digit", Toast.LENGTH_SHORT).show();
                    password.setError("Password must be atleast 6 characters!");
                    password.requestFocus();
                } else if (TextUtils.isEmpty(txtconfirmpassword)) {
                    Toast.makeText(Signup.this, "CONFIRM PASSWORD IS EMPTY", Toast.LENGTH_SHORT).show();
                    confirmpassword.setError("Please re-enter the password!");
                    confirmpassword.requestFocus();
                } else if (!txtpassword.equals(txtconfirmpassword)) {
                    Toast.makeText(Signup.this, "CONFIRM PASSWORD IS DOES NOT MATCH", Toast.LENGTH_SHORT).show();
                    confirmpassword.setError("Password doesn't match!");
                    confirmpassword.requestFocus();
                    password.clearComposingText();
                    confirmpassword.clearComposingText();

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(txtname, txtusername, txtemail, txtpassword, txtdob);
                }
            }
        });
        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }

    private void registerUser(String txtname, String txtusername, String txtemail, String txtpassword, String txtdob) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserProfile");

        ref.orderByChild("username").equalTo(txtusername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.setError("Username already exists");
                    username.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else {
                    auth.createUserWithEmailAndPassword(txtemail, txtpassword).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseuser = auth.getCurrentUser();
                                FoggyUsers foggyUsers = new FoggyUsers(txtname, txtusername, txtemail, txtdob);
                                DatabaseReference userProfile = ref.child(firebaseuser.getUid());
                                userProfile.setValue(foggyUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Signup.this, "USER REGISTERED", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Signup.this, Login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(Signup.this, "USER NOT REGISTERED", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    email.setError("user already existing try with another email");
                                    email.requestFocus();
                                    progressBar.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database Error: " + error.getMessage());
            }
        });
    }
}
