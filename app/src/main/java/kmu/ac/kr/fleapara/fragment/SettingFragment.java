package kmu.ac.kr.fleapara.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.activity.IcActivity;


public class SettingFragment extends Fragment {

    public SettingFragment() {
        //  Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        RelativeLayout ic_layout = (RelativeLayout)rootView.findViewById(R.id.ic_layout);
        ic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), IcActivity.class));
            }
        });
        return rootView;
    }
}