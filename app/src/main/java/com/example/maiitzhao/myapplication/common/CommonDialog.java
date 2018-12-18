package com.example.maiitzhao.myapplication.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.maiitzhao.myapplication.R;


/**
 * 作者：zpxiang
 * 创建时间：2016/10/12 19:00
 * 类描述：
 */
public class CommonDialog extends Dialog implements View.OnClickListener {

    public static final int SIGN_MAP = 1;
    public static final int LOTTO_DIALOG = 2;
    public static final int UPDATE_APP_DIALOG = 3;
    public static final int INSTALL_WARN = 4;

    private int type;

    public CommonDialog(Context context, int theme, int type) {
        super(context, theme);
        this.type = type;
        this.init();
    }

    public CommonDialog(Context context, int type) {
        super(context, R.style.FullScreenDialog);
        this.type = type;
        this.init();
    }

    public CommonDialog(Context context) {
        super(context, R.style.FullScreenDialog);
    }

    public void init() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        int winWidth = wm.getDefaultDisplay().getWidth();
        int winHeight = wm.getDefaultDisplay().getHeight();

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        if (type == SIGN_MAP) {
            getWindow().setAttributes(lp);
            setContentView(R.layout.dialog_sign_map);
        } else if (type == LOTTO_DIALOG) {
            getWindow().setAttributes(lp);
            setContentView(R.layout.dialog_lotto);
        } else if (type == UPDATE_APP_DIALOG) {
            getWindow().setAttributes(lp);
            setContentView(R.layout.dialog_update_app);
        }else if (type == INSTALL_WARN) {
            getWindow().setAttributes(lp);
            setContentView(R.layout.dialog_install_warn);
            findViewById(R.id.iv_close).setOnClickListener(this);
        }/*else if (type == VIP_DIALOG) {
            lp.width = (winWidth / 4) * 3;
            lp.height = winHeight / 3;
            getWindow().setAttributes(lp);
            setContentView(R.layout.dialog_vip);

        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }
}
