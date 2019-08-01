package com.song.songup.mvcapplication.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description：描述信息
 * @Author：Song UP
 * @Date：2019/6/14 14:34
 * 修改备注：
 */
public abstract class BaseFragment extends Fragment {
    Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initView(),container,false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null){
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public abstract int initView();
    public abstract void initData(Bundle savedInstanceState);
}
