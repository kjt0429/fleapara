package kmu.ac.kr.fleapara.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.RecyclerItemClickListener;
import kmu.ac.kr.fleapara.activity.AddMarketActivity;
import kmu.ac.kr.fleapara.activity.OrganizeActivity;
import kmu.ac.kr.fleapara.model.MarketModel;

public class MyOrganizerFragment extends Fragment {

    ViewGroup rootView;

    TextView emptyTextView;


    RecyclerView recyclerView;
    UserMarketRecyclerAdapter userMarketRecyclerAdapter;

    ValueEventListener valueEventListener;

    private ArrayList<MarketModel> marketModelArrayList = new ArrayList<MarketModel>();

    public MyOrganizerFragment() {
        //  Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myorganizer, container, false);

        emptyTextView = (TextView) rootView.findViewById(R.id.emptyTextView);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.userMarket_RecyclerView);

        /*Button tmpBtn = (Button) rootView.findViewById(R.id.addMarketBtn);
        tmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddMarketActivity.class));
            }
        });*/


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddMarketActivity.class));
            }
        });
        //getMarketData();

        createRecyclerView();


        return rootView;
    }

    private void createRecyclerView() {

        recyclerView.setHasFixedSize(true);
        userMarketRecyclerAdapter = new UserMarketRecyclerAdapter(marketModelArrayList);
        recyclerView.setAdapter(userMarketRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), OrganizeActivity.class);
                intent.putExtra("market",marketModelArrayList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

    @Override
    public void onResume() {
        super.onResume();
        getMarketData();
    }

    private void getMarketData() {


        FirebaseDatabase.getInstance().getReference().child("market").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        marketModelArrayList.clear();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            MarketModel marketModel = item.getValue(MarketModel.class);
                            if (marketModel.organizerUid.toString().equals(uid.toString())) {
                                marketModelArrayList.add(marketModel);
                            }

                        }

                        Log.d("MyOrganizerFragment", "value listener check!");

                        if (marketModelArrayList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        } else if (marketModelArrayList.size() != 0) {
                            emptyTextView.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        userMarketRecyclerAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }


    class UserMarketRecyclerAdapter extends RecyclerView.Adapter<UserMarketRecyclerAdapter.ItemViewHolder> {
        ArrayList<MarketModel> mItem;


        public UserMarketRecyclerAdapter(ArrayList<MarketModel> items) {
            mItem = items;
        }

        //  새로운 뷰 홀더 생성
        @Override
        public UserMarketRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usermarket_recyclerview, parent, false);

            return new UserMarketRecyclerAdapter.ItemViewHolder(view);
        }

        //  View 의 내용을 해당 포지션의 데이터로 바꿈
        @Override
        public void onBindViewHolder(UserMarketRecyclerAdapter.ItemViewHolder holder, final int position) {
            Glide.with(holder.itemView.getContext())
                    .load(mItem.get(position).profileImageUri)
                    .into(holder.imageView);

            String attendNum = String.valueOf(mItem.get(position).users.size());
            holder.attendNumTextView.setText("현재 참가인원: " + attendNum + " 명");
            holder.titleTextView.setText(mItem.get(position).name);
            holder.lineinfoTextView.setText(mItem.get(position).lineinfo);
            holder.costTextView.setText(mItem.get(position).cost + " 원");



        }


        @Override
        public int getItemCount() {
            return mItem.size();
        }


        class ItemViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            private TextView attendNumTextView;
            private TextView titleTextView;
            private TextView lineinfoTextView;
            private TextView costTextView;

            public ItemViewHolder(View itemView) {
                super(itemView);

                imageView = (ImageView) itemView.findViewById(R.id.profileImageView);

                attendNumTextView = (TextView) itemView.findViewById(R.id.attendNum);
                titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
                lineinfoTextView = (TextView) itemView.findViewById(R.id.lineinfoTextView);
                costTextView = (TextView) itemView.findViewById(R.id.costTextView);

            }
        }

    }

}





