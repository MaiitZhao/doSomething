package com.example.maiitzhao.myapplication.paintboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.BaseActivity;
import com.example.maiitzhao.myapplication.common.CommonProgressDialog;
import com.example.maiitzhao.myapplication.util.IOUtil;
import com.example.maiitzhao.myapplication.util.ImageUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zpxiang on 2016/4/14.
 */
public class PaintBoardActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private FrameLayout frameLayout;
//    private View common_view;
    private RadioGroup rg_drawgraphics;
    private RadioButton btn_undo;
    private RadioButton btn_redo;
    private RadioButton btn_save;
    private RadioButton btn_picselect;
    private RadioButton btn_drawcycle;
    private RadioButton btn_drawrec;
    private RadioButton btn_drawarrow;
    private RadioButton btn_null;

    private GraffitiView tuyaView;//自定义涂鸦板
    private LinearLayout ll_paintcolor;
    private LinearLayout ll_paintsize;
    private LinearLayout ll_paintstyle;
    private SeekBar sb_size;

    //三个弹出框的相关控件
    private ImageView iv_paintstyle;

    private LinearLayout ll_paintcolor_state;
    private RadioGroup rg_paint_color;

    private int OPEN_PHOTO = 9999;
    private int screenWidth;
    private int screenHeight;
//    private TextView tv_com_title;


    //画线，圆，矩形，以及箭头
    private static final int DRAW_PATH = 0;
    private static final int DRAW_CIRCLE = 1;
    private static final int DRAW_RECTANGLE = 2;
    private static final int DRAW_ARROW = 3;
    private int realHeight;//控件真实高度，去除头部标题后的
    private Bitmap photoBmp;
    private CommonProgressDialog customProgressDialog;//自定义进度条对话框
//    private Button btn_text_save;

    @Override
    protected int initContentView() {
        return R.layout.activity_paintboard;
    }

    protected void initView() {
//        common_view = findViewById(R.id.common_view);
        frameLayout = (FrameLayout) findViewById(R.id.fl_boardcontainer);

        rg_drawgraphics = (RadioGroup) findViewById(R.id.rg_drawgraphics);
        btn_undo = (RadioButton) findViewById(R.id.btn_revoke);
        btn_redo = (RadioButton) findViewById(R.id.btn_clean);
        btn_save = (RadioButton) findViewById(R.id.btn_savesd);
        btn_picselect = (RadioButton) findViewById(R.id.btn_picselect);//图片选择
        btn_drawcycle = (RadioButton) findViewById(R.id.btn_drawcycle);
        btn_drawrec = (RadioButton) findViewById(R.id.btn_drawrec);
        btn_drawarrow = (RadioButton) findViewById(R.id.btn_drawarrow);
        btn_null = (RadioButton) findViewById(R.id.btn_null);

//        btn_text_save = (Button) findViewById(R.id.but_submit);

        ll_paintcolor = (LinearLayout) findViewById(R.id.ll_paint_color);
        ll_paintsize = (LinearLayout) findViewById(R.id.ll_paint_size);
        ll_paintstyle = (LinearLayout) findViewById(R.id.ll_paint_style);
        sb_size = (SeekBar) findViewById(R.id.sb_size);

        ll_paintcolor_state = (LinearLayout) findViewById(R.id.ll_paintcolor_state);
        rg_paint_color = (RadioGroup) findViewById(R.id.rg_paint_color);
        iv_paintstyle = (ImageView) findViewById(R.id.iv_paintstyle);

//        tv_com_title = (TextView) findViewById(R.id.tv_com_title);

        initData();
        initListener();

    }

    private void initData() {
//        tv_com_title.setText("画板");
//        btn_text_save.setText("保存");

        //虽然此时获取的是屏幕宽高，但是我们可以通过控制framlayout来实现控制涂鸦板大小
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        screenWidth = defaultDisplay.getWidth();
        screenHeight = defaultDisplay.getHeight();

        realHeight = (int) (screenHeight - 200);

        tuyaView = new GraffitiView(this, screenWidth, realHeight);

        frameLayout.addView(tuyaView);
        tuyaView.requestFocus();

        tuyaView.selectPaintSize(sb_size.getProgress());

        //设置画笔默认背景
        iv_paintstyle.setBackgroundResource(R.mipmap.paint_style);

        //创建对话框进度条
        customProgressDialog = CommonProgressDialog.createDialog(this);
    }

    private void initListener() {
        ll_paintcolor.setOnClickListener(this);
        ll_paintsize.setOnClickListener(this);
        ll_paintstyle.setOnClickListener(this);
        btn_picselect.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_redo.setOnClickListener(this);
        btn_undo.setOnClickListener(this);
        btn_null.setOnClickListener(this);
//        btn_text_save.setOnClickListener(this);

        sb_size.setOnSeekBarChangeListener(new MySeekChangeListener());

        ll_paintcolor_state.setOnClickListener(this);
        iv_paintstyle.setOnClickListener(this);

        rg_drawgraphics.setOnCheckedChangeListener(this);
    }


    class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tuyaView.selectPaintSize(seekBar.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            tuyaView.selectPaintSize(seekBar.getProgress());
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sb_size.setVisibility(View.GONE);
        }
    }


    private boolean isPaint = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_paint_color:
                changeColorAndSizeState(View.VISIBLE, View.GONE);
                rg_paint_color.setOnCheckedChangeListener(this);
                break;
            case R.id.ll_paint_size:
                changeColorAndSizeState(View.GONE, View.VISIBLE);
                break;
            case R.id.iv_paintstyle:
                ll_paintstyle.setEnabled(true);
                aboutPaintStyleSetting();
                break;
            case R.id.ll_paint_style:
                aboutPaintStyleSetting();
                break;

            //文字保存
//            case R.id.but_submit:
//                aboutSaveToSd();
//                break;


            //上方四个按钮
            case R.id.btn_revoke://撤销
                tuyaView.undo();
                break;
            case R.id.btn_clean://重做
                tuyaView.redo();
                frameLayout.setBackgroundResource(R.color.white);
                //恢复成画笔状态
                tuyaView.setSrcBitmap(null);
                paintStyleSettingDesc(R.mipmap.paint_style, select_paint_style_paint, true);
                tuyaView.drawGraphics(DRAW_PATH);
                break;
            case R.id.btn_picselect://图片选择器
                Intent picIntent = new Intent();
                picIntent.setType("image/*");
                picIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(picIntent, OPEN_PHOTO);
                break;
            case R.id.btn_savesd://保存
                aboutSaveToSd();
                break;

        }
    }

    //保存到sd卡
    private void aboutSaveToSd() {
//        AndPermission.with(this).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action() {
//            @Override
//            public void onAction(List<String> list) {
//
//            }
//        }).onDenied(new Action() {
//            @Override
//            public void onAction(List<String> list) {
//                showToastMessage("没有读写文件权限，请检查手机权限");
//            }
//        }).start();

        saveBitmap();
    }

    private void saveBitmap() {
        if (tuyaView.getSavePath().size() <= 0 || tuyaView.getSavePath() == null) {
            Toast.makeText(this, "涂鸦为空，无法保存", Toast.LENGTH_SHORT).show();
        } else if (tuyaView.getSavePath().size() > 0) {
            if (customProgressDialog != null) {
                customProgressDialog.setCanceledOnTouchOutside(false);//设置屏幕触摸失效
                customProgressDialog.show();
            }

            new Thread() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    //view层的截图
                    frameLayout.setDrawingCacheEnabled(true);
                    Bitmap newBitmap = Bitmap.createBitmap(frameLayout.getDrawingCache());
                    frameLayout.setDrawingCacheEnabled(false);
                    //压缩
                    Bitmap scaleBitmap = ImageUtil.compBitmapByScale(newBitmap, screenWidth, realHeight);
                    String path = tuyaView.saveToSDCard(scaleBitmap);
                    final String[] images = new String[]{path};
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.e("现在图片的路径", path);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("images", images);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            tuyaView.redo();
                            Toast.makeText(PaintBoardActivity.this, "当前涂鸦已保存", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }.start();
        }
    }

    //画笔样式设置
    private void aboutPaintStyleSetting() {
        changeColorAndSizeState(View.GONE, View.GONE);
        if (isPaint) {//当前为画笔,点击后变为橡皮擦
            paintStyleSettingDesc(R.mipmap.reaser_style, select_paint_style_eraser, false);
            btn_null.setChecked(true);//使单选消失
        } else {
            paintStyleSettingDesc(R.mipmap.paint_style, select_paint_style_paint, true);
            tuyaView.drawGraphics(DRAW_PATH);
        }
    }

    //画笔样式设置详情
    private void paintStyleSettingDesc(int paintStyleResouce, int paintStyle, boolean styleTarget) {
        iv_paintstyle.setBackgroundResource(paintStyleResouce);
        tuyaView.selectPaintStyle(paintStyle);
        isPaint = styleTarget;
    }

    //切换画笔颜色和画笔尺寸显隐状态
    private void changeColorAndSizeState(int visible, int gone) {
        ll_paintcolor_state.setVisibility(visible);
        sb_size.setVisibility(gone);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            //处理颜色
            case R.id.rb_purple:
                selectPaintColorAndSetting(0);
                break;
            case R.id.rb_orange:
                selectPaintColorAndSetting(1);
                break;
            case R.id.rb_green:
                selectPaintColorAndSetting(2);
                break;
            case R.id.rb_yellow:
                selectPaintColorAndSetting(3);
                break;
            case R.id.rb_black:
                selectPaintColorAndSetting(4);
                break;


            //以下为画图形状按钮
            case R.id.btn_drawarrow:
                tellPaintStyleAndSetDrawGraphics(DRAW_ARROW, select_paint_style_paint);
                break;
            case R.id.btn_drawrec:
                tellPaintStyleAndSetDrawGraphics(DRAW_RECTANGLE, select_paint_style_paint);
                break;
            case R.id.btn_drawcycle:
                tellPaintStyleAndSetDrawGraphics(DRAW_CIRCLE, select_paint_style_paint);
                break;
        }
    }

    //判断画笔样式并切换画图样式
    private void tellPaintStyleAndSetDrawGraphics(int drawArrow, int select_paint_style_paint) {
        if (isPaint) {
            tuyaView.drawGraphics(drawArrow);
        } else {//当前为橡皮擦
            iv_paintstyle.setBackgroundResource(R.mipmap.paint_style);
            tuyaView.selectPaintStyle(select_paint_style_paint);
            tuyaView.drawGraphics(drawArrow);
            isPaint = true;
        }
    }

    //选择画笔颜色
    private void selectPaintColorAndSetting(int which) {
        tuyaView.selectPaintColor(which);
        ll_paintcolor_state.setVisibility(View.GONE);
    }

    //图片选择后的回传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == OPEN_PHOTO && data != null) {
                Uri mImageCaptureUri = data.getData();
                photoBmp = null;
                if (mImageCaptureUri != null) {
                    try {
                        photoBmp = getBitmapFormUri(this, mImageCaptureUri);//根据uri获取压缩后的bitmap
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                frameLayout.setBackground(new BitmapDrawable(photoBmp));
                tuyaView.redo();
                //压缩bitmap并将它传入到view中
                tuyaView.setSrcBitmap(photoBmp);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private int select_paint_style_paint = 0;
    private int select_paint_style_eraser = 1;


    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        //图片宽高
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x适配高度为标准
        float hh = 480 * realHeight / screenWidth;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }


    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public Bitmap compressImage(Bitmap image) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

        isBm.close();
        baos.close();
        return bitmap;
    }


    @Override
    protected void onStop() {
        super.onStop();
        IOUtil.recycleBitmap(photoBmp);//释放资源
    }

    @Override
    protected void onDestroy() {
        //无法再onstop()中dismiss因为会造成无法弹出进度对话框
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
            customProgressDialog = null;
        }
        super.onDestroy();
    }
}
