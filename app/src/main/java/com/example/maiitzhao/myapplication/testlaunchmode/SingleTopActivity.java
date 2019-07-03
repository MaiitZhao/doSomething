package com.example.maiitzhao.myapplication.testlaunchmode;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;

import butterknife.BindView;

/**
 * 类描述：测试启动模式
 * 创建人：zpxiang
 * 创建时间：2019-07-03
 * 修改人：
 * 修改时间：
 */
public class SingleTopActivity extends BaseActivity {

    @BindView(R.id.iv_trans)
    ImageView ivTrans;

    private String tag = this.getClass().getSimpleName();

    @Override
    protected int initContentView() {
        return R.layout.activity_launch_mode;
    }

    @Override
    protected void initView() {
        ivTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleTopActivity.this, SingleTaskActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(tag,tag + "：onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(tag, tag + "：onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(tag, tag + "：onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(tag, tag + "：onDestroy");
    }
}
