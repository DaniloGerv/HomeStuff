package com.univpm.homestuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.univpm.homestuff.fragments.FragmentHome;
import com.univpm.homestuff.fragments.FragmentProfile;
import com.univpm.homestuff.utilities.Codes;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore context;
    private boolean userAlreadyOnDb;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        userAlreadyOnDb=false;
        FirebaseAuth.getInstance().signOut();
        user = FirebaseAuth.getInstance().getCurrentUser();
        context = FirebaseFirestore.getInstance();




      //  auth.signOut();
        //region User NOT logged
        if(user==null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, Codes.SIGN_REQUEST);

        }
        //endregion
        else
         //region User IS logged
        {
          navigate();
        }
    }

    private void navigate()
    {
        getSupportActionBar().setTitle(R.string.benvenutoTitle);
        bottomNav=findViewById(R.id.bottom_nav_home);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch (item.getItemId())
                {
                    case R.id.menu_home:
                        selectedFragment=new FragmentHome();
                        break;
                    case R.id.menu_profilo:
                        Log.d("PROVA","prova");

                        selectedFragment=new FragmentProfile();
                        break;
                }

                if (selectedFragment!=null)  //Change of fragment to view
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                }
                return true;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == Codes.SIGN_REQUEST) {
            if(resultCode == RESULT_OK) {
                user=FirebaseAuth.getInstance().getCurrentUser();
                navigate();
            }
        }
    }



}
