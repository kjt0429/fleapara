package kmu.ac.kr.fleapara.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.activity.SearchResultActivity;

public class SearchFragment extends Fragment implements View.OnClickListener {

    public SearchFragment() {
        //  Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        Button goodsBtn = (Button) rootView.findViewById(R.id.goodsBtn);
        Button kitchenBtn = (Button) rootView.findViewById(R.id.kitchenBtn);
        Button clothesBtn = (Button) rootView.findViewById(R.id.clothesBtn);

        Button flowerBtn = (Button) rootView.findViewById(R.id.flowerBtn);
        Button phonecaseBtn = (Button) rootView.findViewById(R.id.phonecaseBtn);
        Button fashionBtn = (Button) rootView.findViewById(R.id.fashionBtn);

        Button toyBtn = (Button) rootView.findViewById(R.id.toyBtn);
        Button candleBtn = (Button) rootView.findViewById(R.id.candleBtn);
        Button foodBtn = (Button) rootView.findViewById(R.id.foodBtn);

        Button beautyBtn = (Button) rootView.findViewById(R.id.beautyBtn);
        Button animalBtn = (Button) rootView.findViewById(R.id.animalBtn);
        Button interiorBtn = (Button) rootView.findViewById(R.id.interiorBtn);

        goodsBtn.setOnClickListener(this);
        kitchenBtn.setOnClickListener(this);
        clothesBtn.setOnClickListener(this);

        flowerBtn.setOnClickListener(this);
        phonecaseBtn.setOnClickListener(this);
        fashionBtn.setOnClickListener(this);

        toyBtn.setOnClickListener(this);
        candleBtn.setOnClickListener(this);
        foodBtn.setOnClickListener(this);

        beautyBtn.setOnClickListener(this);
        animalBtn.setOnClickListener(this);
        interiorBtn.setOnClickListener(this);




        return rootView;
    }

    @Override
    public void onClick(View v) {
        String category = null;
        switch (v.getId()) {
            case R.id.goodsBtn:
                category = "문구";
                break;

            case R.id.kitchenBtn:
                category = "주방";
                break;

            case R.id.clothesBtn:
                category = "의류";
                break;

            case R.id.flowerBtn:
                category = "꽃";
                break;

            case R.id.phonecaseBtn:
                category = "휴대폰케이스";
                break;

            case R.id.fashionBtn:
                category = "패션잡화";
                break;

            case R.id.toyBtn:
                category = "장난감";
                break;

            case R.id.candleBtn:
                category = "캔들";
                break;

            case R.id.foodBtn:
                category = "식품";
                break;

            case R.id.beautyBtn:
                category = "뷰티";
                break;

            case R.id.animalBtn:
                category = "반려동물";
                break;

            case R.id.interiorBtn:
                category = "인테리어";
                break;
        }

        Intent intent = new Intent(getContext(), SearchResultActivity.class);
        intent.putExtra("category",category);
        startActivity(intent);

    }
}
