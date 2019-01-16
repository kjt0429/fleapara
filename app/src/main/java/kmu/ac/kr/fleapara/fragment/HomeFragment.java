package kmu.ac.kr.fleapara.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pm10.library.CircleIndicator;

import java.util.ArrayList;
import java.util.Collections;

import kmu.ac.kr.fleapara.BannerFragmentAdapter;
import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.RecyclerAdapter;
import kmu.ac.kr.fleapara.activity.ListActivity;
import kmu.ac.kr.fleapara.model.MarketModel;

public class HomeFragment extends Fragment {
    public ViewGroup rootView;

    private String[] names = {};
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<MarketModel> mItems = new ArrayList<>();

    private String[] names2 = {"newMarket_1", "newMarket_2", "newMarket_3", "newMarket_4", "newMarket_5", "newMarket_6"};
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter adapter2;
    private ArrayList<MarketModel> mItems2 = new ArrayList<>();


    ScrollView scrollView;

    public HomeFragment() {
        //  Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });

        //  배너 뷰페이저 생성
        createBannerViewPager();

        //  리사이클러뷰 (마감 임박)
        createRecyclerView();

        //  뉴 마켓
        createRecyclerView2();

        ImageView deadlineIV = (ImageView) rootView.findViewById(R.id.deadlineIV);
        deadlineIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("title", "마감 임박");
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });

        ImageView newIV = (ImageView)rootView.findViewById(R.id.newIV);
        newIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("title", "신규 플리마켓");
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getMarketData();
    }


    private void getMarketData() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mFirebaseDatabase.getReference("market");

        /*  deadlineTime 값에 따라 오름차순 정렬된 data에서
         *  앞에서 5개의 값을 가져옴. */
        mRef.orderByChild("deadlineTime").limitToFirst(5)
                .addListenerForSingleValueEvent
                        (new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 mItems.clear();
                                 for (DataSnapshot item : dataSnapshot.getChildren()) {
                                     MarketModel marketModel = item.getValue(MarketModel.class);
                                     mItems.add(marketModel);
                                 }
                                 adapter.notifyDataSetChanged();
                                 Log.d("HomeFragment", "deadlineTime Value listener check!");
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         }
                        );
        /*  startTime 값에 따라 오름차순 정렬된 data에서
         *  뒤에서 5개의 값을 가져옴. */
        mRef.orderByChild("startTime").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mItems2.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    MarketModel marketModel = item.getValue(MarketModel.class);
                    mItems2.add(marketModel);
                }

                Collections.reverse(mItems2);
                adapter2.notifyDataSetChanged();
                Log.d("HomeFragment", "startTime Value listener check!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void createBannerViewPager() {
        //  프래그먼트에서 findViewById 하면 안됨 : 아래 방법 이용
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        // viewPager.setAdapter(new BannerFragmentAdapter(getSupportFragmentManager()));
        viewPager.setAdapter(new BannerFragmentAdapter(getChildFragmentManager()));

        CircleIndicator circleIndicator = (CircleIndicator) rootView.findViewById(R.id.circle_indicator);
        circleIndicator.setupWithViewPager(viewPager);
    }

    private void createRecyclerView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        //  각 item 크기에 상관없이 recyclerView 사이즈 고정
        recyclerView.setHasFixedSize(true);

        //  어뎁터 만들고 바인딩.
        adapter = new RecyclerAdapter(getContext(), mItems, R.layout.item_recycler_view);
        recyclerView.setAdapter(adapter);

        //  layoutManger를 통해서 RecyclerView -> HORIZONTAL
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);


        //setData();

    }


    private void createRecyclerView2() {
        recyclerView2 = (RecyclerView) rootView.findViewById(R.id.recyclerView2);

        //  각 item 크기에 상관없이 recyclerView 사이즈 고정
        recyclerView2.setHasFixedSize(true);

        //  어뎁터 만들고 바인딩.
        adapter2 = new RecyclerAdapter(getContext(), mItems2, R.layout.item_recycler_view2);
        recyclerView2.setAdapter(adapter2);

        //  layoutManger를 통해서 RecyclerView -> HORIZONTAL
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(layoutManager);

    }

}
