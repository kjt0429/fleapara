package kmu.ac.kr.fleapara.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.model.MarketModel;
import kmu.ac.kr.fleapara.model.UserModel;

public class MarketActivity extends AppCompatActivity {

    static final String TAG = "MarketActivity";

    Toolbar mToolbar;

    MarketModel marketModel;

    ImageView profileImageView;

    TextView nameTextView;
    TextView lineinfoTextView;
    TextView areaTextView;
    TextView timeTextView;
    TextView costTextView;
    TextView categoryTextView;

    TextView infoTextView;

    TextView sellerNumTextView;

    TextView nicknameTextView;
    TextView emailTextView;

    ImageView hostImageView;

    Button applyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        //상태 표시줄 바 칼라 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.rgb(112, 139, 135));
            }
        }

        //  툴바 생성
        mToolbar = (Toolbar) findViewById(R.id.ToolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //  툴바에 홈 버튼 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //  선택한 MarketModel
        Intent intent = getIntent();
        marketModel = (MarketModel) intent.getSerializableExtra("market");

        profileImageView = (ImageView) findViewById(R.id.profile_imageView);
        Glide.with(getApplicationContext())
                .load(marketModel.profileImageUri)
                .into(profileImageView);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(marketModel.name);

        lineinfoTextView = (TextView) findViewById(R.id.lineinfoTextView);
        lineinfoTextView.setText(marketModel.lineinfo);

        areaTextView = (TextView) findViewById(R.id.area_TV);
        areaTextView.setText(marketModel.area);

        timeTextView = (TextView) findViewById(R.id.time_TV);
        timeTextView.setText("~ " + marketModel.deadlineTime);

        costTextView = (TextView) findViewById(R.id.cost_TV);
        costTextView.setText(marketModel.cost + " 원");

        categoryTextView = (TextView) findViewById(R.id.category_TV);
        categoryTextView.setText(marketModel.category);

        infoTextView = (TextView) findViewById(R.id.info_TV);
        infoTextView.setText(marketModel.info);

        sellerNumTextView = (TextView) findViewById(R.id.sellerNum_TV);
        sellerNumTextView.setText(String.valueOf(marketModel.users.size()) + " 명");


        nicknameTextView = (TextView) findViewById(R.id.nickname_TV);
        emailTextView = (TextView) findViewById(R.id.email_TV);

        hostImageView = (ImageView)findViewById(R.id.host_face_iv);

        applyBtn = (Button) findViewById(R.id.applyBtn);

        if (MainActivity.userModel.uid.equals(marketModel.organizerUid)) {
            applyBtn.setVisibility(View.INVISIBLE);
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUid = MainActivity.userModel.uid;


                if (marketModel.users.containsKey(userUid)) {
                    Toast.makeText(getApplicationContext(), "이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    marketModel.users.put(MainActivity.userModel.uid, false);
                    FirebaseDatabase.getInstance().getReference("market").child(marketModel.uid).child("users").setValue(marketModel.users)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "신청 완료", Toast.LENGTH_SHORT).show();

                                }
                            });
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //  market의 주최자 정보를 가져옴.
        FirebaseDatabase.getInstance().getReference("users").child(marketModel.organizerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);



                nicknameTextView.setText(userModel.nickname);
                emailTextView.setText(userModel.email);

                if(userModel.profileImageUri != null){
                    Glide.with(getApplicationContext())
                            .load(userModel.profileImageUri)
                            .apply(new RequestOptions().circleCrop())
                            .into(hostImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //toolbar의 back키 눌렀을 때 동작
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}