package com.example.maiitzhao.myapplication.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import butterknife.ButterKnife;

/**
 * Created by zpxiang on 2018/11/19.
 * 全局application   部分功能的全局初始化操作
 */

public class AppAplication extends Application {

    private static Context context;
    private static AppAplication application;

    public static Handler mainHandler;


    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        context = getApplicationContext();
        application = this;
        mainHandler = new Handler();

        //初始化腾讯x5Webview内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("tencent", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public static AppAplication getInstance() {
        return application;
    }

    public  static Context getContext() {
        return context;
    }

    public Typeface getHkTypeface() {
        return Typeface.createFromAsset(getAssets(), "fonts/hksn.ttf");
    }
}
