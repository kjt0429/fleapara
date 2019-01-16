package kmu.ac.kr.fleapara;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kmu.ac.kr.fleapara.activity.MarketActivity;
import kmu.ac.kr.fleapara.model.MarketModel;

/**
 * Created by HP on 2018-04-09.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    ArrayList<MarketModel> mItem;
    Context mContext;
    private int mLayoutId;

    public RecyclerAdapter(Context context, ArrayList<MarketModel> items, int layoutId) {
        mContext = context;
        mItem = items;
        mLayoutId = layoutId;
    }

    //  새로운 뷰 홀더 생성
    @Override
    public RecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);

        return new ItemViewHolder(view);
    }

    //  View 의 내용을 해당 포지션의 데이터로 바꿈
    @Override
    public void onBindViewHolder(RecyclerAdapter.ItemViewHolder holder, final int position) {


        holder.nameTextView.setText(mItem.get(position).name);
        holder.areaTextView.setText(mItem.get(position).area);
        holder.infoTextView.setText(mItem.get(position).lineinfo);
        holder.costTextView.setText(mItem.get(position).cost + " 원");

        Glide.with(holder.itemView.getContext())
                .load(mItem.get(position).profileImageUri)
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MarketActivity.class);
                intent.putExtra("market", mItem.get(position));

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }


    //  커스텀 뷰 홀더
//  item layout에 존재하는 위젯들을 바인딩
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView nameTextView;
        private TextView areaTextView;
        private TextView infoTextView;
        private TextView costTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.ImageView_market);
            nameTextView = (TextView) itemView.findViewById(R.id.TextView_name);
            areaTextView = (TextView) itemView.findViewById(R.id.TextView_area);
            infoTextView = (TextView) itemView.findViewById(R.id.TextView_info);
            costTextView = (TextView) itemView.findViewById(R.id.TextView_cost);
        }
    }

}
