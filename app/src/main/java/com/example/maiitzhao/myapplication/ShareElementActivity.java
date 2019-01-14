package com.example.maiitzhao.myapplication;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.base.BaseActivity;

import butterknife.BindView;

/**
 * 类描述：共享元素动画效果界面
 * 创建人：zpxiang
 * 创建时间：2018/12/17
 * 修改人：
 * 修改时间：
 */
public class ShareElementActivity extends BaseActivity {

    @BindView(R.id.iv_trans)
    ImageView ivTrans;
    @BindView(R.id.tv_trans)
    TextView tvTrans;

    @Override
    protected int initContentView() {
        return R.layout.activity_common_view;
    }

    @Override
    protected void initView() {
        int resId = getIntent().getIntExtra("imgId", 0);
        if (resId != 0) {
            ivTrans.setImageResource(resId);
        }
    }
}
