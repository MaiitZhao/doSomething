package com.example.maiitzhao.myapplication;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_trans)
    ImageView ivTrans;
    @BindView(R.id.tv_trans)
    TextView tvTrans;
    @BindView(R.id.iv_float)
    ImageView floatBar;

    @Override
    protected int initContentView() {
        return R.layout.activity_share_element;
    }

    @Override
    protected void initView() {
        int resId = getIntent().getIntExtra("imgId", 0);
        if (resId != 0) {
            ivTrans.setImageResource(resId);
        }

        initToolbar();
        excuteAnimation(0f, 0f, 1.0f, 1.0f);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setTitle("共享元素");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 执行动画
     */
    private void excuteAnimation(float startX, float startY, float endX, float endY) {
        ScaleAnimation anim = new ScaleAnimation(startX, endX, startY, endY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        floatBar.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        excuteAnimation(1.0f, 1.0f, 0f, 0f);
    }
}
