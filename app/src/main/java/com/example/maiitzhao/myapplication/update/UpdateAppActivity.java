package com.example.maiitzhao.myapplication.update;


import android.Manifest;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;
import com.example.maiitzhao.myapplication.common.CommonProgressBar;
import com.example.maiitzhao.myapplication.common.CommonDialog;
import com.example.maiitzhao.myapplication.util.AppUtil;
import com.example.maiitzhao.myapplication.util.CommonUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class UpdateAppActivity extends BaseActivity implements UpdateAppManager.DownloadObserver,View.OnClickListener,EasyPermissions.PermissionCallbacks {

    private CommonDialog mUpdateDialog;

    @BindView(R.id.btn_update)
    Button btnUpdate;

    private String mDownloadJson;
    private int mMeasuredWidth;
    private LinearLayout llProgress;
    private ImageView mIvProgress;
    private CommonProgressBar mProgressBar;
    private ImageButton mIbInstall;
    private Button mBtnClose;
    private UpdateAppBean mUpdateAppBean;
    private CommonDialog warnDialog;

    @Override
    protected int initContentView() {
        return R.layout.activity_update_app;
    }

    @Override
    protected void initView() {
        initDialog();
        initData();
    }

    private void initDialog() {
        mUpdateDialog = new CommonDialog(this, R.style.FullScreenDialog, CommonDialog.UPDATE_APP_DIALOG);
        llProgress = (LinearLayout) mUpdateDialog.findViewById(R.id.ll_install_progress);
        mIvProgress = (ImageView) mUpdateDialog.findViewById(R.id.iv_progress);
        mProgressBar = (CommonProgressBar) mUpdateDialog.findViewById(R.id.pb_install);

        mIbInstall = (ImageButton) mUpdateDialog.findViewById(R.id.ib_install);
        mIbInstall.setOnClickListener(this);
        mBtnClose = (Button) mUpdateDialog.findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);

        RelativeLayout rlInstall = (RelativeLayout) mUpdateDialog.findViewById(R.id.rl_install);
        int width = CommonUtil.getScreenWidth(this) - CommonUtil.dp2px(40);
        int height = 9 * width / 10;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlInstall.getLayoutParams();
        layoutParams.height = height;
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    }


    private void initData() {

        mDownloadJson = "http://c02.banhai.com/uploads/download-apk.json";
        mMeasuredWidth = CommonUtil.getScreenWidth(this) - CommonUtil.dp2px(120);
        mProgressBar.setProgress(0);
        mProgressBar.setMaxProgress(100);

        initUpdateInfo();
        UpdateAppBean downloadInfo = UpdateAppManager.getInstance().getDownloadInfo(mUpdateAppBean);
        if (downloadInfo != null) {
            onDownloadStateChange(downloadInfo);
        }

        UpdateAppManager.getInstance().registerDownloadObserver(this);
    }

    private void initUpdateInfo() {
        mUpdateAppBean = new UpdateAppBean();
        mUpdateAppBean.setId(1);
        //设置apk 的保存路径
        mUpdateAppBean.setPath(UpdateAppManager.DOWNLOAD_DIR + "/"
                + "课海.apk");

        mUpdateAppBean.setId(0);
        mUpdateAppBean.setSize(0);
        //设置 apk 的下载地址
        mUpdateAppBean.setState(UpdateAppManager.STATE_NONE);// 初始化为未下载状态
        mUpdateAppBean.setCurrentLength(0);
    }


    @OnClick({R.id.btn_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                if (mUpdateDialog != null && !mUpdateDialog.isShowing()) {
                    mUpdateDialog.show();
                }
                break;

            case R.id.btn_close:
                mUpdateDialog.dismiss();
                break;

            case R.id.ib_install:
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(this,perms)){
                    update();
                }else {
                    EasyPermissions.requestPermissions(this,"",100,perms);
                }

                break;
        }
    }

    private void update(){
        final UpdateAppBean downloadInfo = UpdateAppManager.getInstance().getDownloadInfo(mUpdateAppBean);
        if (downloadInfo == null) {//首次下载

            OkHttpUtils.get()
                    .url(mDownloadJson)
                    .build()
                    .execute(new JsonInfoCallback() {
                        @Override
                        public JsonInfo parseNetworkResponse(Response response, int id) throws Exception {
                            String string = response.body().string();
                            JsonInfo jsonInfo = new Gson().fromJson(string, JsonInfo.class);
                            return jsonInfo;
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e, int id) {
                            CommonUtil.showToastShort( "请检查网络后重试！");
                        }

                        @Override
                        public void onResponse(JsonInfo response, int id) {
                            if (downloadInfo == null) {
                                mUpdateAppBean.setDownloadUrl(response.kehaiStudent);
                            } else {
                                downloadInfo.setDownloadUrl(response.kehaiStudent);
                            }
                            handleState(downloadInfo);
                        }
                    });
        } else {//不是首次下载
            if (downloadInfo.getState() == UpdateAppManager.STATE_ERROR) {
                handleState(downloadInfo);
            } else if (downloadInfo.getState() == UpdateAppManager.STATE_SUCCESS) {
                UpdateAppManager.getInstance().installApp(downloadInfo);
                mUpdateDialog.dismiss();
            }
        }
    }

    class JsonInfo {
        public String banhaiTeacher;
        public String banhaiStudent;
        public String kehaiStudent;

        @Override
        public String toString() {
            return "JsonInfo{" +
                    "banhaiTeacher='" + banhaiTeacher + '\'' +
                    ", banhaiStudent='" + banhaiStudent + '\'' +
                    ", kehaiStudent='" + kehaiStudent + '\'' +
                    '}';
        }
    }

    public abstract class JsonInfoCallback extends Callback<JsonInfo> {
        //非UI线程，支持任何耗时操作
        public JsonInfo parseNetworkResponse(Response response) throws IOException {
            String string = response.body().string();
            JsonInfo jsonInfo = new Gson().fromJson(string, JsonInfo.class);
            return jsonInfo;
        }
    }


    /**
     * 判断安装前的状态
     */
    private void handleState(UpdateAppBean downloadInfo) {
        if (!AppUtil.isWifiConnected(this)) {
            showWarnDialog();
            return;
        }

        installKH(downloadInfo);
    }


    private void showWarnDialog() {
        if (warnDialog == null) {
            warnDialog = new CommonDialog(this, R.style.FullScreenDialog, CommonDialog.INSTALL_WARN);
            warnDialog.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    warnDialog.dismiss();
                    installKH(mUpdateAppBean);
                }
            });
        }

        if (!warnDialog.isShowing()) {
            warnDialog.show();
        }
    }

    /**
     * 安装
     */
    private void installKH(UpdateAppBean downloadInfo) {
        if (downloadInfo == null) {
            UpdateAppManager.getInstance().download(mUpdateAppBean);
        } else {
            UpdateAppManager.getInstance().download(downloadInfo);
        }
    }

    @Override
    public void onDownloadStateChange(UpdateAppBean downloadInfo) {
        if (mUpdateAppBean == null || mUpdateAppBean.getId() != downloadInfo.getId()) {
            return;
        }

        switch (downloadInfo.getState()) {
            case UpdateAppManager.STATE_SUCCESS:
                mIbInstall.setVisibility(VISIBLE);
                llProgress.setVisibility(INVISIBLE);
                mUpdateDialog.dismiss();
                UpdateAppManager.getInstance().unregisterDownloadObserver(this);
                break;
            case UpdateAppManager.STATE_ERROR:
                CommonUtil.showToastShort("下载出错了，请重新下载！");
                mIbInstall.setVisibility(VISIBLE);
                llProgress.setVisibility(INVISIBLE);
                break;
            case UpdateAppManager.STATE_DOWNLOADING:
                mIbInstall.setVisibility(GONE);
                llProgress.setVisibility(VISIBLE);
                float percent = downloadInfo.getCurrentLength() * 100f / downloadInfo.getSize();
                mProgressBar.setProgress(percent);
                break;
        }
    }

    /**
     * 下载进度更新的回调
     */
    @Override
    public void onDownloadProgressChange(UpdateAppBean downloadInfo) {
        if (mUpdateAppBean == null || mUpdateAppBean.getId() != downloadInfo.getId()) {//如果当前正在下载的app和当前界面 不是同一个，则不应该更新UI
            return;
        }

        if (llProgress.getVisibility() == INVISIBLE) {
            llProgress.setVisibility(VISIBLE);
            mIbInstall.setVisibility(GONE);
        }
        float percent = downloadInfo.getCurrentLength() * 100f / downloadInfo.getSize();
        mProgressBar.setProgress(percent);
        mIvProgress.setX(percent / 100f * mMeasuredWidth + CommonUtil.dp2px(9));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 100){
            update();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        CommonUtil.showToastShort("您未授权相应权限！");
    }
}
