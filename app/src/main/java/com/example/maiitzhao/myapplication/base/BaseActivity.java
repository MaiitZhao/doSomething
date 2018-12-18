package com.example.maiitzhao.myapplication.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zpxiang on 2018/11/19.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();
    }

    private void init() {
        super.setContentView(initContentView());
        bind = ButterKnife.bind(this);
    }

    protected abstract int initContentView();

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
