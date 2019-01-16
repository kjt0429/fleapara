package kmu.ac.kr.fleapara.model;

import java.util.ArrayList;

public class UserModel {

    public String uid;                                     //  db uid
    public String profileImageUri;                         //  db profileImageUri

    public ArrayList<String> marketUid
            = new ArrayList<String>();                 //  참가한, 개최한 행사 리스트

    public boolean isSeller;   //  true : 셀러, false : 주최자
    public String name;        //  이름
    public String nickname;     //  별명

    public String tel;         //  전화번호

    public String email;


    public String fcmToken;

}
