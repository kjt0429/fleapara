package kmu.ac.kr.fleapara.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.model.UserModel;

public class SignupActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    Toolbar toolbar;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;

    TextView nameTextView, textView2;
    EditText nameEditText, telEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        //  toolbar 에 back btn 생성
        toolbar = (Toolbar) findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radioBtn1);
        radioButton1 = (RadioButton) findViewById(R.id.radioBtn1);
        radioButton2 = (RadioButton) findViewById(R.id.radioBtn2);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.getBackground().setColorFilter(Color.rgb(112, 139, 135), PorterDuff.Mode.SRC_IN);

        nameTextView = (TextView) findViewById(R.id.nameTextView);

        telEditText = (EditText) findViewById(R.id.telEditText);
        telEditText.getBackground().setColorFilter(Color.rgb(112, 139, 135), PorterDuff.Mode.SRC_IN);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtn1:
                        nameTextView.setText("이 름");
                        nameEditText.setHint("이름을 입력해주세요.");
                        nameEditText.setText("");
                        break;
                    case R.id.radioBtn2:
                        nameTextView.setText("업체 이름");
                        nameEditText.setHint("업체 이름을 입력해주세요.");
                        nameEditText.setText("");

                        break;
                }
            }
        });


        Button signupBtn = (Button) findViewById(R.id.signupButton);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  name, tel EditText 공백 체크
                if (nameEditText.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (telEditText.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserModel userModel = new UserModel();
                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                userModel.name = nameEditText.getText().toString();
                userModel.tel = telEditText.getText().toString();
                userModel.isSeller = radioButton1.isChecked();

                userModel.nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                userModel.email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                userModel.profileImageUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

                mRef.child("users").child(userModel.uid).setValue(userModel).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            }
                        }
                );

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
