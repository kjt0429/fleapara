package kmu.ac.kr.fleapara.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.activity.ImageActivity;

/**
 * Created by HP on 2018-04-04.
 */

public class EventFragment extends Fragment {

    public EventFragment() {
        //  Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_event,container,false);

        ImageView eventImageView = rootView.findViewById(R.id.eventImage);
        eventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ImageActivity.class));
            }
        });
        return rootView;
    }
}
