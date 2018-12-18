package com.example.maiitzhao.myapplication.pdf;


import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;
import com.example.maiitzhao.myapplication.common.NavigationTitleBar;
import com.example.maiitzhao.myapplication.update.AppUpdateUtils;
import com.example.maiitzhao.myapplication.util.CommonUtil;
import com.tencent.smtt.sdk.TbsReaderView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.maiitzhao.myapplication.update.UpdateAppManager.DOWNLOAD_DIR;

public class DocumentDisplayActivity extends BaseActivity{

    @BindView(R.id.navigation_bar)
    NavigationTitleBar navigationTitleBar;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    private String uri;
    private TbsReaderView mTbsReaderView;
    private int type;

    @Override
    protected int initContentView() {
        return R.layout.activity_document_display;
    }

    @Override
    protected void initView() {
        type = getIntent().getBundleExtra("bundle").getInt("type");
        if (type == 1) {
            uri = "http://4sapioss-1253856364.costj.myqcloud.com/2018-11-30-11-42-th-5c00b18f6ee68.pdf";
        } else if (type == 2) {
            uri = "http://4sapioss-1253856364.costj.myqcloud.com/2018-12-04-19-21-th-5c06633cb3792.docx";
        }else if (type == 3){
            uri = DOWNLOAD_DIR + "/person.pptx";
        }

        navigationTitleBar.setTitle(getFileType(uri).toUpperCase());

        mTbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {
                Log.e("tencent", "onCallBackAction: " + integer);
            }
        });

        relativeLayout.addView(mTbsReaderView, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        handlePermission();
    }


    private void download() {
        String destFileName = "person." + (type == 1 ? "pdf":"doc");
        OkHttpUtils.get()
                .url(uri)
                .build()
                .execute(new FileCallBack(DOWNLOAD_DIR, destFileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        Log.e("tencent", progress + ":" + total);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        Log.e("tencent", e.getMessage() + ":" + response.code() + ":" + response.message() + ":" + id);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        try {
                            if (AppUpdateUtils.isAppOnForeground(DocumentDisplayActivity.this)) {//App前台运行
//                                fileView.loadUrl(DOWNLOAD_DIR + "person.pdf");
                                disPlay(response);
                            } else {//App后台运行,更新参数,注意flags要使用FLAG_UPDATE_CURRENT

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }
                });
    }

    private void handlePermission(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this,perms)){
           handleEvent();
        }else {
            EasyPermissions.requestPermissions(this,"",100,perms);
        }
    }


    private void handleEvent(){
        switch (type){
            case 1:
            case 2:
                download();
                break;
            case 3:
                disPlay(new File(uri));
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void disPlay(File file) {
//        fileView.displayFile(file);

        //第一个参数：文件类型
        boolean bool = mTbsReaderView.preOpen(getFileType(file.toString()), false);
        Log.e("tencent", "" + getFileType(file.toString()));
        if (bool) {
            Bundle bundle = new Bundle();
            bundle.putString("filePath", file.toString());//源文件
            //存放临时文件的目录。运行后，会在path2的目录下生成.tbs...的文件
            bundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
            mTbsReaderView.openFile(bundle);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, new EasyPermissions.PermissionCallbacks() {
            @Override
            public void onPermissionsGranted(int requestCode, List<String> perms) {
                Log.e("处理",requestCode + "");
                if (requestCode == 100){
                    handleEvent();
                }
            }

            @Override
            public void onPermissionsDenied(int requestCode, List<String> perms) {
                CommonUtil.showToastShort("您未授权相应权限！");
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }
        });
    }

    //拼接文件类型后缀
    private String getFileType(String paramString) {
        String str = "";
        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf(".");
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        return str;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTbsReaderView.onStop();
    }
}
