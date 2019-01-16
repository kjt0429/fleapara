package kmu.ac.kr.fleapara.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


//  Firebase Cloud Server에 단말을 등록하는 Service
//  이 서비스에 대한 intent-filter 를 manifest에 추가해야됨
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "myIID";

    public FirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh() 호출 됨.");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token : " + refreshedToken);

    }


}
