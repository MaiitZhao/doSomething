package com.example.maiitzhao.myapplication.pdf;


import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;
import com.example.maiitzhao.myapplication.update.AppUpdateUtils;
import com.example.maiitzhao.myapplication.util.CommonUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
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

public class PdfDisActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.pdfView)
    PDFView pdfView;
    private String uri;

    @Override
    protected int initContentView() {
        return R.layout.activity_pdf;
    }

    @Override
    protected void initView() {
        uri = "http://4sapioss-1253856364.costj.myqcloud.com/2018-11-30-11-42-th-5c00b18f6ee68.pdf";
//        uri = "http://4sapioss-1253856364.costj.myqcloud.com/2018-12-04-19-21-th-5c06633cb3792.docx";

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            download();
        } else {
            EasyPermissions.requestPermissions(this, "", 100, perms);
        }
    }

    private void download() {
        OkHttpUtils.get()
                .url(uri)
                .build()
                .execute(new FileCallBack(DOWNLOAD_DIR, System.currentTimeMillis() + ".doc") {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        Log.e("response", progress + ":" + total);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        Log.e("response", e.getMessage() + ":" + response.code() + ":" + response.message() + ":" + id);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        try {
                            if (AppUpdateUtils.isAppOnForeground(PdfDisActivity.this)) {//App前台运行
                                displayFromUri(response);
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

    private void displayFromUri(File file) {

        pdfView.fromFile(file)
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 100) {
            download();

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        CommonUtil.showToastShort("您未授权相应权限！");
    }

    private final String TAG = "PDF";

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
//        pageNumber = page;
//        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }
}
