package com.example.maiitzhao.myapplication.update;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.AppAplication;
import com.example.maiitzhao.myapplication.util.CommonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 版本更新管理器
 * zpxiang
 */
public class UpdateAppManager {

    //定义下载目录
    public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory() /*+ "/"
            + AppAplication.getContext().getPackageName()*/ + "/download";

    //定义下载状态常量
    public static final int STATE_NONE = 0;//未下载的状态
    public static final int STATE_DOWNLOADING = 1;//下载中的状态
    public static final int STATE_WAITING = 2;//等待中的状态,任务创建但是run方法没有执行
    public static final int STATE_SUCCESS = 3;//下载完成的状态
    public static final int STATE_ERROR = 4;//下载失败的状态

    private NotificationManager mNotificationManager;

    private UpdateAppBean mUpdateApp;

    private UpdateAppManager() {
        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        File file = new File(DOWNLOAD_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static UpdateAppManager mInstance = new UpdateAppManager();

    public static UpdateAppManager getInstance() {
        return mInstance;
    }

    public UpdateAppBean getDownloadInfo(UpdateAppBean updateAppBean) {
        int id = updateAppBean.getId();

        if (mUpdateApp == null || mUpdateApp.getId() != id) {
            return null;
        }
        return mUpdateApp;
    }

    public void download(UpdateAppBean downloadInfo) {

        if (mUpdateApp == null) {
            mUpdateApp = downloadInfo;
        }
        int state = downloadInfo.getState();
        if (state == STATE_NONE || state == STATE_ERROR) {
            DownloadTask downloadTask = new DownloadTask(downloadInfo);//mUpdateApp

            downloadInfo.setState(STATE_WAITING);
            notifyDownloadStateChange(downloadInfo);

            ThreadPoolManager.getInstance().execute(downloadTask);
        }
    }

    private static final int NOTIFY_ID = 0;
    private NotificationCompat.Builder mBuilder;

    /**
     * 开启下载任务
     */
    class DownloadTask implements Runnable {
        private UpdateAppBean downloadInfo;

        public DownloadTask(UpdateAppBean downloadInfo) {
            super();
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            downloadInfo.setState(STATE_DOWNLOADING);
            notifyDownloadStateChange(downloadInfo);

            final File file = new File(downloadInfo.getPath());
            OkHttpUtils.get()
                    .url(downloadInfo.getDownloadUrl())
                    .build()
                    .execute(new FileCallBack(DOWNLOAD_DIR, "课海.apk") {
                        @Override
                        public void inProgress(float progress, long total, int id) {
                            Log.e("response", progress + ":" + total);

                            int rate = Math.round(progress * 100);
                            if (mBuilder != null) {
                                mBuilder.setContentTitle("正在下载：课海")
                                        .setContentText(rate + "%")
                                        .setSmallIcon(R.mipmap.logo)
                                        .setProgress(100, rate, false)
                                        .setWhen(System.currentTimeMillis());
                                Notification notification = mBuilder.build();
                                notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                                mNotificationManager.notify(NOTIFY_ID, notification);
                            }

                            //下载进度更新
                            downloadInfo.setCurrentLength((long) (progress * total));
                            downloadInfo.setSize(total);
                            notifyDownloadProgressChange(downloadInfo);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e, int id) {
                            Log.e("response", e.getMessage() + ":" + response.code() + ":" + response.message() + ":" + id);
                            processErrorState(downloadInfo);
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            try {
                                if (AppUpdateUtils.isAppOnForeground(getContext()) || mBuilder == null) {//App前台运行
                                    mNotificationManager.cancel(NOTIFY_ID);
                                    installApp(downloadInfo);
                                } else {//App后台运行,更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                                    Intent installAppIntent = AppUpdateUtils.getInstallAppIntent(getContext(), file);
                                    PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(contentIntent)
                                            .setContentTitle("课海")
                                            .setContentText("下载完成，请点击安装")
                                            .setSmallIcon(R.mipmap.logo)
                                            .setProgress(0, 0, false)
                                            .setDefaults((Notification.DEFAULT_ALL));
                                    Notification notification = mBuilder.build();
                                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                                    mNotificationManager.notify(NOTIFY_ID, notification);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //说明下载完成了
                            downloadInfo.setState(STATE_SUCCESS);
                            notifyDownloadStateChange(downloadInfo);
                        }

                        @Override
                        public void onBefore(Request request, int id) {
                            super.onBefore(request, id);
                            //初始化通知栏
                            setUpNotification();
                        }
                    });
        }
    }

    /**
     * 处理下载失败的状态
     */
    private void processErrorState(UpdateAppBean downloadInfo) {
        Log.e("response", downloadInfo.toString());

        downloadInfo.setState(STATE_ERROR);
        //通知监听器状态更改
        notifyDownloadStateChange(downloadInfo);
        File file = new File(downloadInfo.getPath());
        file.delete();//删除失败文件
        downloadInfo.setCurrentLength(0);//清空已经下载的长度
        try {
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 通知所有的监听器状态更改了
     *
     * @param downloadInfo
     */
    private void notifyDownloadStateChange(final UpdateAppBean downloadInfo) {
        AppAplication.mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mObserver.onDownloadStateChange(downloadInfo);
            }
        });
    }

    /**
     * 通知所有的监听器下载进度更新了
     *
     * @param downloadInfo
     */
    private void notifyDownloadProgressChange(final UpdateAppBean downloadInfo) {
        AppAplication.mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mObserver.onDownloadProgressChange(downloadInfo);
            }
        });
    }

    private DownloadObserver mObserver;

    /**
     * 注册下载监听器
     *
     * @param observer
     */
    public void registerDownloadObserver(DownloadObserver observer) {
        this.mObserver = observer;
    }

    /**
     * 移除下载监听器
     *
     * @param observer
     */
    public void unregisterDownloadObserver(DownloadObserver observer) {
        mObserver = null;
    }

    /**
     * 下载过程的监听器
     *
     * @author Administrator
     */
    public interface DownloadObserver {
        /**
         * 下载进度改变的回调方法
         */
        void onDownloadProgressChange(UpdateAppBean downloadInfo);

        /**
         * 下载状态改变的回调
         */
        void onDownloadStateChange(UpdateAppBean downloadInfo);
    }

    /**
     * 安装的方法
     */
    public void installApp(UpdateAppBean appInfo) {
        try {
            Intent intent = getInstallAppIntent(getContext(), new File(appInfo.getPath()));
            if (getContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                getContext().startActivity(intent);
            }
        } catch (Exception e) {
            CommonUtil.showToastShort("文件解析错误，请重新下载");
            processErrorState(appInfo);
        }
    }

    public Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
        }
        return null;
    }

    public Context getContext() {
        return AppAplication.getContext();
    }

    /**
     * 创建通知
     */
    private static final String CHANNEL_ID = "app_update_id";
    private static final CharSequence CHANNEL_NAME = "app_update_channel";

    private void setUpNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
//            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            channel.enableVibration(false);
//            channel.enableLights(false);
//
//            mNotificationManager.createNotificationChannel(channel);
//        }


        mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setContentTitle("开始下载")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.mipmap.lib_update_app_update_icon)
                .setLargeIcon(AppUpdateUtils.drawableToBitmap(getContext().getResources().getDrawable(R.mipmap.logo)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

}

