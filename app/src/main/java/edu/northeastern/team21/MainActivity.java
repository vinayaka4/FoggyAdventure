package edu.northeastern.team21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.team21.Foggy.Login;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void aboutUs(View view) {
        Intent aboutMe = new Intent(this, AboutUsActivity.class);
        startActivity(aboutMe);

    }

    public void handleService(View view) {
        Intent service = new Intent(this, WebserviceActivity.class);
        startActivity(service);
    }


    public void handleStickIt(View view) {
        Intent stickit = new Intent(this, StickItActivity.class);
        startActivity(stickit);
    }

    public void handleFoggy(View view) {
        Intent stickit = new Intent(this, Login.class);
        startActivity(stickit);

    }
}
