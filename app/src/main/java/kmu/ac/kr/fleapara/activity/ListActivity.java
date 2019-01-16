package kmu.ac.kr.fleapara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.RecyclerItemClickListener;
import kmu.ac.kr.fleapara.model.MarketModel;

public class ListActivity extends AppCompatActivity {

    static final String TAG = "ListActivity";
    ListRecyclerViewAdapter adapter;

    int type;

    ArrayList<MarketModel> marketModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //  툴바 생성
        Toolbar mToolbar = (Toolbar) findViewById(R.id.ToolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //  툴바에 홈 버튼 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //  title
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        type = intent.getIntExtra("type", 0);
        TextView titleTV = (TextView) findViewById(R.id.titleTV);
        titleTV.setText(title);

        //
        // getMarketData();

        //  recyclerview
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new ListRecyclerViewAdapter(marketModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));    //?
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent sendIntent = new Intent(ListActivity.this, MarketActivity.class);
                sendIntent.putExtra("market", marketModels.get(position));
                startActivity(sendIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    public void getMarketData() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("market");

        switch (type) {
            case 1:
                mRef.orderByChild("deadlineTime").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        marketModels.clear();

                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            MarketModel marketModel = item.getValue(MarketModel.class);

                            try {
                                //  시간체크해서 이미 지난거면 삽입하지 않음.
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date deadlineDate = formatter.parse(marketModel.deadlineTime);

                                long time = System.currentTimeMillis();
                                Date nowDate = new Date(time);

                                // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
                                // deadlineDate 가 그날의 0시 0분 0초로 되어 있어서 23시 59분 59초로
                                long diff = deadlineDate.getTime() + (24 * 60 * 60 * 1000 - 1) - nowDate.getTime();

                                if (diff > 0.0) {
                                    marketModels.add(marketModel);
                                }
                            } catch (Exception e) {

                            }

                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "market data set complete");


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case 2:
                mRef.orderByChild("startTime").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        marketModels.clear();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            MarketModel marketModel = item.getValue(MarketModel.class);
                            marketModels.add(marketModel);
                        }
                        Collections.reverse(marketModels);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMarketData();
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


    class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ItemViewHolder> {

        ArrayList<MarketModel> mItem;

        public ListRecyclerViewAdapter(ArrayList<MarketModel> marketModels) {
            mItem = marketModels;
        }

        @NonNull
        @Override
        public ListRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_list_recyclerview, parent, false);

            return new ListRecyclerViewAdapter.ItemViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ListRecyclerViewAdapter.ItemViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext())
                    .load(mItem.get(position).profileImageUri)
                    .into(holder.profileImageView);

            holder.areaTextView.setText(mItem.get(position).area);


            holder.titleTextView.setText(mItem.get(position).name);
            holder.lineinfoTextView.setText(mItem.get(position).lineinfo);
            holder.costTextView.setText(mItem.get(position).cost + " 원");


            if (type == 1) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date deadlineDate = formatter.parse(mItem.get(position).deadlineTime);

                    long time = System.currentTimeMillis();
                    Date nowDate = new Date(time);

                    // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
                    // deadlineDate 가 그날의 0시 0분 0초로 되어 있어서 23시 59분 59초로
                    long diff = deadlineDate.getTime() + (24 * 60 * 60 * 1000 - 1) - nowDate.getTime();
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    long diffHour = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                    int diffdaysi = (int) diffDays;
                    int diffHouri = (int) diffHour;

                    holder.deadlineTextView
                            .setText("마감 " + String.valueOf(diffdaysi) + "일 " + String.valueOf(diffHouri) + "시간 전");


                } catch (ParseException e) {

                }
            } else if (type == 2) {
                holder.areaTextView.setBackgroundResource(R.color.colorPink);
                holder.deadlineTextView.setBackgroundResource(R.color.colorBlue);
                holder.deadlineTextView.setText("NEW");
            }




        }


        @Override
        public int getItemCount() {
            return mItem.size();
        }


        class ItemViewHolder extends RecyclerView.ViewHolder {

            public ImageView profileImageView;
            public TextView areaTextView;
            public TextView deadlineTextView;
            public TextView titleTextView;
            public TextView lineinfoTextView;
            public TextView costTextView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                profileImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
                areaTextView = (TextView) itemView.findViewById(R.id.areaTextView);
                deadlineTextView = (TextView) itemView.findViewById(R.id.deadlineTextView);
                titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
                lineinfoTextView = (TextView) itemView.findViewById(R.id.lineinfoTextView);
                costTextView = (TextView) itemView.findViewById(R.id.costTextView);
            }
        }//ItemViewHolder

    }//ListRecyclerViewAdapter
}
