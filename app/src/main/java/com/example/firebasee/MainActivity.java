package com.example.firebasee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private NavigationView navigationView;
    private Fragment selectorFragment;
    private DrawerLayout mDrawer;
    private ImageView drawer_icon;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nav_drawer_layout);
        //Toolbar toolbar = findViewById(R.id.toolbar);

        drawer_icon = findViewById(R.id.drawer_image);
        FloatingActionButton fab = findViewById(R.id.fab);

        setupFirebaseAuth();

        //default fragment


        //Bottom nav
        NavigationView navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home_nav:
                        selectorFragment = new HomeScreenFragment();
                        break;
                    case R.id.bottom_menu_search:
                        selectorFragment = new SearchFragment();
                        break;
                    case R.id.bottom_menu_heart:
                        selectorFragment = new LikesFragment();
                        break;
                    case R.id.bottom_menu_message:
                        selectorFragment = new MessagesFragment();
                        break;
                    case R.id.bottom_menu_user:
                        selectorFragment = new UserAccountFragment();
                        break;
                }
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).addToBackStack(null).commit();
                }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserAccountFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_home_nav);
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeScreenFragment()).commit();
        }

        //drawer layout
        mDrawer = findViewById(R.id.drawer_layout);
        drawer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"drawer icon pressed....");
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
//        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close);
//
//        mDrawer.addDrawerListener(drawerToggle);
//        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddOnePost.class));
            }
        });
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout_action:
//                Toast.makeText(getApplicationContext(), "Logging out..", Toast.LENGTH_LONG).show();
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, StartActivity.class));
//                finish();
//                return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_user_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserAccountFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeScreenFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GalleryFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_slideshow:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SlideshowFragment()).addToBackStack(null).commit();
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called of Main Activity");
        mAuth.addAuthStateListener(mAuthListener);
        bottomNavigationView.setSelectedItemId(R.id.bottom_menu_home_nav);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called of Main Activity");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}