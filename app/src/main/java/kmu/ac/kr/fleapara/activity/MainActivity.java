package kmu.ac.kr.fleapara.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.fragment.EventFragment;
import kmu.ac.kr.fleapara.fragment.HomeFragment;
import kmu.ac.kr.fleapara.fragment.MyOrganizerFragment;
import kmu.ac.kr.fleapara.fragment.MySellerFragment;
import kmu.ac.kr.fleapara.fragment.SearchFragment;
import kmu.ac.kr.fleapara.fragment.SettingFragment;
import kmu.ac.kr.fleapara.model.UserModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    public HomeFragment homeFragment;
    public SearchFragment searchFragment;
    public MySellerFragment mySellerFragment;
    public MyOrganizerFragment myOrganizerFragment;
    public EventFragment eventFragment;
    public SettingFragment settingFragment;


    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference mRef;

    FirebaseUser mFirebaseUser;
    public static UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        //  나중에 각 유저 값 따라서 My fragment 는 하나만 만들기
        searchFragment = new SearchFragment();
        mySellerFragment = new MySellerFragment();
        myOrganizerFragment = new MyOrganizerFragment();

        eventFragment = new EventFragment();
        settingFragment = new SettingFragment();

        getUserData();

        //  Init fragment
        getSupportFragmentManager().beginTransaction().add(R.id.content, homeFragment).commit();

        /*  Change Status bar Color */
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.rgb(112, 139, 135));
            }
        }


        /*  Create Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //  타이틀 안나오게
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        /*  Set Toggle, drawer, toolbar (나중에 수정 요망) *//*
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false); // 토글 드로월 끄고 NavigationIcon 넣어줌
        toolbar.setNavigationIcon(R.drawable.img_toolbar_menu); // 토글 대신 Custom Toggle

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //    Navigation Icon (toggle) 을 누르면 왼쪽에서 drawer
                drawer.openDrawer(Gravity.LEFT);
            }
        });


        //  왼쪽에서 나오는 창
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/


        //  Bottom Navigationview : 나중에 보완, 탭 이동이 조금 느림.
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //  각 경우에 따라 짜기
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, homeFragment).commit();
                        return true;

                    case R.id.navigation_search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, searchFragment).commit();
                        return true;

                    //  나중에 여기서 오류날 가능성도 있음.
                    case R.id.navigation_my:
                        if (userModel.isSeller == true) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content, mySellerFragment).commit();
                            return true;

                        } else if (userModel.isSeller != true) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content, myOrganizerFragment).commit();
                            return true;
                        }

                    case R.id.navigation_event:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, eventFragment).commit();
                        return true;
                    case R.id.navigation_setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, settingFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getUserData() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef.child("users").child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModel = dataSnapshot.getValue(UserModel.class);

                //Toast.makeText(getApplicationContext(), userModel.name + "firebaseUserModel 가져옴", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
