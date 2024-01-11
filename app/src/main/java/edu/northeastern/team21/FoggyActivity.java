package edu.northeastern.team21;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.Console;

import edu.northeastern.team21.Foggy.CameraFragment;
import edu.northeastern.team21.Foggy.LandingFragment;
import edu.northeastern.team21.Foggy.ProfileFragment;
import edu.northeastern.team21.Foggy.ProfileItemsEditDialog;

public class FoggyActivity extends SystemBarHiddenAbstractActivity {

    private BottomNavigationView bottomNavigationView;
    private LandingFragment landingFragment = new LandingFragment();
    private CameraFragment cameraFragment = new CameraFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    private static final String TAG = "__FOGGYACTIVITY__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foggy);

        // hide the system bar, extend the activity to the top of screen, force the time and signal color to be dark
        //transparentStatusBarAndNavigation();

        // to get the current fragment on screen
        // if not fragment is on screen (that is when you launch the FoggyActivity), launch landingFragment
        // otherwise, the current fragment on screen will be kept
        Fragment curfragment = getSupportFragmentManager().findFragmentById(R.id.nav_fragment_foggy);
        if(curfragment==null){
            setFragment(landingFragment);
        }

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_landing);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_landing:
                        setFragment(landingFragment);
                        return true;
                    case R.id.navigation_camera:
                        setFragment(cameraFragment);
                        return true;
                    case R.id.navigation_profile:
                        setFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_foggy, fragment).commit();
    }

//    /** below two methods help to hide the status bar in Foggy Activity
//     * */
//    private void transparentStatusBarAndNavigation(){
//        Log.d(TAG, "SDK Version: " + Build.VERSION.SDK_INT);
//        if(Build.VERSION.SDK_INT>=19 && Build.VERSION.SDK_INT<21){
//            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
//        }
//        if(Build.VERSION.SDK_INT>=19){
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                    | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//            );
//        }
//        if(Build.VERSION.SDK_INT>=21){
//            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION , false);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }
//    }
//
//    private void setWindowFlag(int i, boolean b) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        if(b){
//            winParams.flags |= i;
//        }else{
//            winParams.gravity &= ~i;
//        }
//        win.setAttributes(winParams);
//    }



}