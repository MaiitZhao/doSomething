package com.example.maiitzhao.myapplication.base;

import android.os.Bundle;
import android.view.View;

import com.example.maiitzhao.myapplication.CommonViewActivity;
import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.paintboard.PaintBoardActivity;
import com.example.maiitzhao.myapplication.pdf.DocumentDisplayActivity;
import com.example.maiitzhao.myapplication.pdf.PdfDisActivity;
import com.example.maiitzhao.myapplication.signmap.SignMapActivity;
import com.example.maiitzhao.myapplication.update.UpdateAppActivity;
import com.example.maiitzhao.myapplication.util.CommonUtil;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.tv_paint_bord, R.id.tv_paint, R.id.tv_update, R.id.tv_pdf, R.id.tv_pdf2, R.id.tv_pdf3, R.id.tv_pdf4,R.id.tv_gridview})
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
            case R.id.tv_gridview:
                CommonUtil.toActivity(CommonViewActivity.class);
                break;
        }
    }
}
