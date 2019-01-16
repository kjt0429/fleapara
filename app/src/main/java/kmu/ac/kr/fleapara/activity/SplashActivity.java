package kmu.ac.kr.fleapara.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import kmu.ac.kr.fleapara.BuildConfig;
import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.model.UserModel;

public class SplashActivity extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*  Change Status bar Color */
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.rgb(112, 139, 135));
            }
        }

        //  status bar 제거   : 나중 색상 변경
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //  getInstance()
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //  BuildConfig.DEBUG는 요청횟수를 무한대로 할 수 있도록 초기화
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        //  Default xml 선택
        firebaseRemoteConfig.setDefaults(R.xml.default_config);

        //  서버에서 값을 덮어씌움 : 1초마다 요청
        firebaseRemoteConfig.fetch(1)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();
                        } else {
                        }
                        displayMessage();
                    }
                });
    }

    //  서버에서 값을 받고 그에 따른 메시지 출력
    private void displayMessage() {
        boolean configCaps = firebaseRemoteConfig.getBoolean("splash_message_caps");
        String configMessage = firebaseRemoteConfig.getString("splash_message");

        //  if caps==1 (서버 점검중) 이라면 AlertDialog Message 출력
        //  else (서버 점검중이 아니라면 로그인 화면 출력)
        if (configCaps) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(configMessage).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        } else {


            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            /*
             *    firebaseUser 정보가 있고, firebaseDatabase(정식등록)이 됬으면 MainActivity
             *   firebaseUser 정보가 없거나, firebaseDatabase 정보가 없으면 LoginActivity
             */
            if (firebaseUser != null) {     //  값가져온다.
                FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                                //  DB 정보가 없으면 있으면 MainActivity, SignupActivity.
                                if (userModel != null) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                } else if (userModel == null) {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            } else {                    //  firebaseUser 정보가 없을때,
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }


        }
    }
}
