package kmu.ac.kr.fleapara.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.model.UserModel;

public class PushActivity extends AppCompatActivity {
    UserModel userModel;
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAEkaZPR0:APA91bHOYlcmBtKBtVC233ve4-YlTWBwn5ajz70akxwgoCKwMZTifPSpPHSwtpOPulGbRPD066YeVvOWcNiXDFX80LAstbAuSlhTMzWnwDWe1HxCA-AVXnnX2G2641JZZGXvT6UoRSLV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        Button tokenBtn = (Button) findViewById(R.id.tokenBtn);

        final EditText editText = (EditText) findViewById(R.id.editText);


        //  token을 얻고, push message를 보냄.
        tokenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");

                mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userModel = dataSnapshot.getValue(UserModel.class);
                        userModel.fcmToken = FirebaseInstanceId.getInstance().getToken();

                        mRef.child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (editText.getText().toString().equals("")) {
                                    Toast.makeText(getApplicationContext(), "message를 입력하세요.", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    sendPushMessage(editText.getText().toString());
                                    Toast.makeText(getApplicationContext(), "send:"+editText.getText().toString(), Toast.LENGTH_SHORT).show();
                                    editText.setText("");
                                }

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Button backBtn = (Button)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    void sendPushMessage(final String _message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", _message);
                    notification.put("title", getString(R.string.app_name));
                    root.put("notification", notification);
                    root.put("to", userModel.fcmToken);
                    // FMC 메시지 생성 end

                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
