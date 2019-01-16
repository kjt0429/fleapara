package kmu.ac.kr.fleapara.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.activity.MainActivity;
import kmu.ac.kr.fleapara.model.MarketModel;

public class MySellerFragment extends Fragment {

    public ArrayList<MarketModel> attedMarkets = new ArrayList<>();

    public ViewGroup rootView;

    public SellerRecyclerViewAdapter sellerRecyclerViewAdapter;

    public MySellerFragment() {
        //  Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myseller, container, false);

        ImageView profileImageView = (ImageView) rootView.findViewById(R.id.profile_imageView);
        TextView nameTextView = (TextView) rootView.findViewById(R.id.name_textView);
        TextView emailTextView = (TextView) rootView.findViewById(R.id.email_textView);

        if (MainActivity.userModel.profileImageUri != null) {
            Glide.with(getContext())
                    .load(MainActivity.userModel.profileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(profileImageView);
        }

        nameTextView.setText(MainActivity.userModel.name);
        emailTextView.setText(MainActivity.userModel.email);

        createRecyclerView();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getAttedMarkets();
    }

    void getAttedMarkets() {
        attedMarkets.clear();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("market");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mitem : dataSnapshot.getChildren()) {
                    MarketModel marketModel = mitem.getValue(MarketModel.class);

                    if (marketModel.users.containsKey(MainActivity.userModel.uid)) {
                        attedMarkets.add(marketModel);
                        Log.d("MySeller", "attedMarkets 하나 추가");
                    }

                }
                sellerRecyclerViewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void createRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        sellerRecyclerViewAdapter = new SellerRecyclerViewAdapter(attedMarkets);
        recyclerView.setAdapter(sellerRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    class SellerRecyclerViewAdapter extends RecyclerView.Adapter<SellerRecyclerViewAdapter.ItemViewHolder> {
        ArrayList<MarketModel> mItem;

        public SellerRecyclerViewAdapter(ArrayList<MarketModel> _mItem) {
            mItem = _mItem;
        }

        @NonNull
        @Override
        public SellerRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_recyclerview, parent, false);

            return new SellerRecyclerViewAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SellerRecyclerViewAdapter.ItemViewHolder holder, int position) {

            if (mItem.get(position).profileImageUri != null) {
                Glide.with(holder.itemView.getContext())
                        .load(mItem.get(position).profileImageUri)
                        .into(holder.marketImage);
            }

            holder.nameTextView.setText(mItem.get(position).name);
            holder.deadlineTextView.setText("~ "+mItem.get(position).deadlineTime);
            holder.countTextView.setText("신청 인원 : "+ String.valueOf(mItem.get(position).users.size()) + "명");

            if(mItem.get(position).users.get(MainActivity.userModel.uid) == true){
                holder.stateTextView.setBackgroundResource(R.color.colorBlue);
                holder.stateTextView.setText("승  인");
            }else if (mItem.get(position).users.get(MainActivity.userModel.uid) == false) {
                holder.stateTextView.setBackgroundResource(R.color.colorMiddleGray);
                holder.stateTextView.setText("대기중");
            }
        }

        @Override
        public int getItemCount() {
            return mItem.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private ImageView marketImage;
            private TextView stateTextView;
            private TextView nameTextView;
            private TextView deadlineTextView;
            private TextView countTextView;

            public ItemViewHolder(View itemView) {
                super(itemView);

                marketImage = (ImageView) itemView.findViewById(R.id.marketImage);
                stateTextView = (TextView) itemView.findViewById(R.id.stateTextView);
                nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
                deadlineTextView = (TextView) itemView.findViewById(R.id.deadlineTextView);
                countTextView = (TextView) itemView.findViewById(R.id.countTextView);

            }
        }

    }


}

