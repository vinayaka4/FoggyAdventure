package edu.northeastern.team21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.team21.StickIt.User;
import edu.northeastern.team21.StickIt.UserScreen;

public class StickItActivity extends AppCompatActivity {

    private EditText username;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickit);

        username = findViewById(R.id.username);

        userRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void handleSubmit(View view) {
        String name = username.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Username is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(StickItActivity.this, "Welcome Back, " + name, Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(name);
                    userRef.child(name).setValue(user);
                    Toast.makeText(StickItActivity.this, "New User Congrats, " + name, Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(StickItActivity.this, UserScreen.class);
                intent.putExtra("username", name);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}