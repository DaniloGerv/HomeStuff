package com.univpm.homestuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.univpm.homestuff.fragments.FragmentFamily;
import com.univpm.homestuff.fragments.FragmentHome;
import com.univpm.homestuff.fragments.FragmentProfile;
import com.univpm.homestuff.utilities.ViewPagerAdapter;
import com.univpm.homestuff.utilities.Codes;


public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore context;
    private boolean userAlreadyOnDb;


    private ViewPager viewPage;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userAlreadyOnDb=false;
        user = FirebaseAuth.getInstance().getCurrentUser();

        context = FirebaseFirestore.getInstance();
        viewPage=findViewById(R.id.viewpage);
        tabs=findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPage) ;


        final ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),0);

        viewPagerAdapter.addFragment(new FragmentHome(),"Home");
        viewPagerAdapter.addFragment(new FragmentFamily(),"Famiglia");
        viewPagerAdapter.addFragment(new FragmentProfile(),"Profilo");
        viewPage.setAdapter(viewPagerAdapter);
        tabs.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabs.getTabAt(1).setIcon(R.drawable.ic_people_outline_black_24dp);
        tabs.getTabAt(2).setIcon(R.drawable.ic_person_black_24dp);


        //region User NOT logged
        if(user==null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, Codes.SIGN_REQUEST);

        }
        //endregion

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == Codes.SIGN_REQUEST) {
            if(resultCode == RESULT_OK) {
                user=FirebaseAuth.getInstance().getCurrentUser();
            }
        }
    }



}
