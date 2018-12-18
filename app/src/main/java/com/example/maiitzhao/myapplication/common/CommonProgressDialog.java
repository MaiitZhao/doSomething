package com.example.maiitzhao.myapplication.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.R;


public class CommonProgressDialog extends Dialog {
    private static CommonProgressDialog commonDialog = null;

    public CommonProgressDialog(Context context){
        super(context);
    }

    public CommonProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CommonProgressDialog createDialog(Context context){
        commonDialog = new CommonProgressDialog(context,R.style.commonDialog);
        commonDialog.setContentView(R.layout.dialog_paintboard);
        return commonDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus){

        if (commonDialog == null){
            return;
        }

        ImageView imageView = (ImageView) commonDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    public void setMessage(String strMessage){
        TextView tvMsg = (TextView) commonDialog.findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }
    }
}
