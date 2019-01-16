package kmu.ac.kr.fleapara;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by HP on 2018-04-06.
 */


public class BannerFragment extends Fragment {

    private int position;
    ImageView imageView;

    public BannerFragment(){

    }

    public static BannerFragment getInstance(int position) {
        BannerFragment bannerFragment = new BannerFragment();
        bannerFragment.position = position;
        return bannerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_banner,container, false);
        imageView = rootView.findViewById(R.id.imageView);

        //  여기서 배너 이미지 업로드, 나중 AWS server 바꾸면서 수정
        switch (position){
            case 0: imageView.setImageResource(R.drawable.img_banner0);
                break;
            case 1: imageView.setImageResource(R.drawable.img_banner1);
                break;
            case 2: imageView.setImageResource(R.drawable.img_banner2);
                break;
            case 3: imageView.setImageResource(R.drawable.img_banner3);
                break;
            case 4: imageView.setImageResource(R.drawable.img_banner4);
                break;
        }

        return rootView;
    }
}