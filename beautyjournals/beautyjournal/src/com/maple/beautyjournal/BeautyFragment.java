package com.maple.beautyjournal;

import com.maple.beautyjournal.base.BaseFragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;

/**
 * Created by mosl on 14-4-14.
 */
public class BeautyFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View view=inflater.inflate(R.layout.meizhuang_layout,null);

        return view;
    }

}
