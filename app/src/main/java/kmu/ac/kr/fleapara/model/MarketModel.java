package kmu.ac.kr.fleapara.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HP on 2018-04-11.
 */

public class MarketModel implements Serializable{

    private static final long serialVersionUID = 1L;

    public String uid;
    public String profileImageUri;
    public String profileImageUri_Big;


    public String name;             //  마켓명


    public String startTime;        //  개시날
    public String deadlineTime;     //  마감시간


    public String area;             //  개최지 : 도시

    public String lineinfo;         //  한줄소개

    public String info;             //  소개글


    public String cost;             //  비용
    public String category;         //  카테고리


    public String organizerUid;     //  주최자



    //  참가한 유저 목록
    public ArrayList<UserModel> attendUserList
            = new ArrayList<UserModel>();

    //  대기중인 유저 목록
    public ArrayList<UserModel> waitUserList
            = new ArrayList<UserModel>();



    public HashMap<String,Boolean> users = new HashMap<>();


}
