package com.example.maiitzhao.myapplication.signmap;


import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;
import com.example.maiitzhao.myapplication.common.CommonDialog;
import com.example.maiitzhao.myapplication.signmap.lotto.LottoView;
import com.example.maiitzhao.myapplication.util.CommonUtil;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.maiitzhao.myapplication.common.CommonDialog.LOTTO_DIALOG;

public class SignMapActivity extends BaseActivity implements LottoView.OnLottoListener {

    private CommonDialog mLottoDialog;// 抽奖弹出框
    private SignMapDialogViewControl signMapDialogViewControl;


    @BindView(R.id.btn_sign)
    Button btnSign;
    @BindView(R.id.et_sign_days)
    EditText etSign;

    @Override
    protected int initContentView() {
        return R.layout.activity_sign_map;
    }

    @Override
    protected void initView() {
        initLottoDialog();

        signMapDialogViewControl = new SignMapDialogViewControl();
        signMapDialogViewControl.init(this);
        signMapDialogViewControl.setOnSignBtnCliclkListener(new SignMapDialogViewControl.OnSignBtnCliclkListener() {
            @Override
            public void startSign() {
                signMapDialogViewControl.handleSignAnimation();
            }
        });
    }

    @OnClick({R.id.btn_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
                String trim = etSign.getText().toString().trim();
                if (CommonUtil.isEmpty(trim)) {
                    return;
                }

                Integer days = CommonUtil.toInteger(trim);
                if (days > 7) {
                    return;
                }
                signMapDialogViewControl.show(days, false);
                break;
        }
    }


    /**
     * 初始化抽奖弹出框
     */
    private void initLottoDialog() {
        mLottoDialog = new CommonDialog(this, R.style.FullScreenDialog, LOTTO_DIALOG);
        Button btnClose = (Button) mLottoDialog.findViewById(R.id.btn_close_lotto);
        final LottoView mLottoView = (LottoView) mLottoDialog.findViewById(R.id.lv_lotto);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLottoView.isLotto()) {
                    mLottoDialog.dismiss();
                }
            }
        });

        mLottoView.addOnLottoListener(this);
        mLottoDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {
                    if (mLottoView != null && mLottoView.isLotto()) {
                        return true;
                    }
                }
                return false;
            }
        });
    }


    //弹出抽奖弹出框
    public void showLottoDialog() {
        mLottoDialog.show();
    }

    @Override
    public void OnLottoFinish(int xuemi, int point) {
        if (!mLottoDialog.isShowing()) {
            return;
        }
//        tvXueMi.setText(xuemi + "");
//        tvIntegral.setText(point + "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLottoDialog.dismiss();
//                mLottoPrizeShowDialog.show();
            }
        }, 1000);
    }
}
