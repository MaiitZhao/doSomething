package com.example.maiitzhao.myapplication.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.CommonViewActivity;
import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.ele.ELMActivity;
import com.example.maiitzhao.myapplication.paintboard.PaintBoardActivity;
import com.example.maiitzhao.myapplication.pdf.DocumentDisplayActivity;
import com.example.maiitzhao.myapplication.pdf.PdfDisActivity;
import com.example.maiitzhao.myapplication.signmap.SignMapActivity;
import com.example.maiitzhao.myapplication.update.UpdateAppActivity;
import com.example.maiitzhao.myapplication.util.CommonUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_gridview)
    ImageView tvGrid;

    @BindView(R.id.iv_trans)
    ImageView ivTrans;
    @BindView(R.id.tv_trans)
    TextView tvTrans;

    @Override
    protected int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.tv_paint_bord, R.id.tv_paint, R.id.tv_update, R.id.tv_pdf, R.id.tv_pdf2, R.id.tv_pdf3,
            R.id.tv_pdf4, R.id.tv_gridview, R.id.ll_trans, R.id.tv_ele})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_paint_bord:
                CommonUtil.showToastShort("涂鸦板");
                CommonUtil.toActivity(PaintBoardActivity.class);
                break;
            case R.id.tv_paint:
                CommonUtil.showToastShort("签到");
                CommonUtil.toActivity(SignMapActivity.class);
                break;
            case R.id.tv_update:
                CommonUtil.showToastShort("下载");
                CommonUtil.toActivity(UpdateAppActivity.class);
                break;
            case R.id.tv_pdf:
                CommonUtil.toActivity(PdfDisActivity.class);
                break;
            case R.id.tv_pdf2:
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                CommonUtil.toActivityBundle(DocumentDisplayActivity.class, bundle);
                break;
            case R.id.tv_pdf3:
                Bundle bundleDoc = new Bundle();
                bundleDoc.putInt("type", 2);
                CommonUtil.toActivityBundle(DocumentDisplayActivity.class, bundleDoc);
                break;
            case R.id.tv_pdf4:
                Bundle bundlePPT = new Bundle();
                bundlePPT.putInt("type", 3);
                CommonUtil.toActivityBundle(DocumentDisplayActivity.class, bundlePPT);
                break;
            case R.id.tv_ele:
                CommonUtil.toActivity(ELMActivity.class);
                break;
            case R.id.tv_gridview:
                Intent intent = new Intent(this, CommonViewActivity.class);
                Bundle bundleXioguo = ActivityOptionsCompat.makeSceneTransitionAnimation(this, tvGrid, getString(R.string.trans_tag_image)).toBundle();
                startActivity(intent, bundleXioguo);
                break;
            case R.id.ll_trans:
                Intent intent2 = new Intent(this, CommonViewActivity.class);
                Pair<View, String> pair1 = new Pair<View, String>(ivTrans, getString(R.string.trans_tag_image));
                Pair<View, String> pair2 = new Pair<View, String>(tvTrans, getString(R.string.trans_tag_text));
                startActivity(intent2, ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2).toBundle());
                break;
        }
    }
}

