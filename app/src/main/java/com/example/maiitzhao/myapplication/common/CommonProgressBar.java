package com.example.maiitzhao.myapplication.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.example.maiitzhao.myapplication.R;


/**
 * Created by zpxiang on 2017/1/05
 * 勋章进度条
 */
public class CommonProgressBar extends View {
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private int DEFAULT_HEIGHT_DP = 20;

    private int borderWidth;

    private float maxProgress = 100f;

    private Paint textPaint;

    private Paint bgPaint;

    private Paint pgPaint;

    private String progressText;

    private Rect textRect;

    private RectF bgRectf;

    /**
     * 左右来回移动的滑块
     */
    private Bitmap flikerBitmap;

    /**
     * 滑块移动最左边位置，作用是控制移动
     */
    private float flickerLeft;

    /**
     * 进度条 bitmap ，包含滑块
     */
    private Bitmap pgBitmap;

    private Canvas pgCanvas;

    /**
     * 当前进度
     */
    private float progress;


    /**
     * 进度文本、边框、进度条颜色
     */
    private int progressColor;

    private int textSize;

    private int radius;

    BitmapShader bitmapShader;
    private int bgColor;
    private int mType;
    private int textColor;
    private Path progressRectPath, cornerRectPath;

    public CommonProgressBar(Context context) {
        this(context, null, 0);
    }

    public CommonProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FlikerProgressBar);
        try {
            textSize = (int) ta.getDimension(R.styleable.FlikerProgressBar_pbtextSize, 12);
            textColor = ta.getColor(R.styleable.FlikerProgressBar_pbtextColor, Color.parseColor("#ffffff"));
            progressColor = ta.getColor(R.styleable.FlikerProgressBar_loadingColor, Color.parseColor("#0099ff"));
            bgColor = ta.getColor(R.styleable.FlikerProgressBar_backgroundColor, getResources().getColor(R.color.white));
            radius = (int) ta.getDimension(R.styleable.FlikerProgressBar_radius, 0);
            borderWidth = (int) ta.getDimension(R.styleable.FlikerProgressBar_borderWidth, 1);
            mType = ta.getInt(R.styleable.FlikerProgressBar_textDisStyle, 0);
        } finally {
            ta.recycle();
        }
    }

    private void init() {
        progressRectPath = new Path();
        cornerRectPath = new Path();

        setBgPaint(Paint.Style.FILL, borderWidth, bgColor);

        pgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pgPaint.setStyle(Paint.Style.FILL);
        pgPaint.setColor(progressColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        textRect = new Rect();
        bgRectf = new RectF(borderWidth, borderWidth, getMeasuredWidth() - borderWidth, getMeasuredHeight() - borderWidth);

        flikerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.flicker);
        flickerLeft = -flikerBitmap.getWidth();

        initPgBimap();
    }

    private void initPgBimap() {
        int width = getMeasuredWidth() - borderWidth;
        int height = getMeasuredHeight() - borderWidth;
        if (width > 0 && height > 0) {
            pgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pgCanvas = new Canvas(pgBitmap);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST://当给出的是wrapContent时，会设置宽度为DEFAULT_HEIGHT_DP
                height = dp2px(DEFAULT_HEIGHT_DP);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);

        if (pgBitmap == null) {
            init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //背景
        drawBackGround(canvas);

        //进度
        drawProgress(canvas);

        //进度text
        drawProgressText(canvas);

        //变色处理
        drawColorProgressText(canvas);
    }

    /**
     * 边框
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        //left、top、right、bottom不要贴着控件边，否则border只有一半绘制在控件内,导致圆角处线条显粗
        canvas.drawRoundRect(bgRectf, radius, radius, bgPaint);

//        bgPaint.setPathEffect(new CornerPathEffect(dp2px(radius)));
//
//        //完整矩形左上角，右下角的点
//        Point lu = new Point(0, 0);
//        Point rd = new Point(getMeasuredWidth(), getMeasuredHeight());
//
//        //完整进度条长度
//        cornerRectPath.reset();
//        cornerRectPath.moveTo(lu.x, lu.y);
//        cornerRectPath.lineTo(rd.x, lu.y);
//        cornerRectPath.lineTo(rd.x, rd.y);
//        cornerRectPath.lineTo(lu.x, rd.y);
//        cornerRectPath.close();
//        canvas.drawPath(cornerRectPath, bgPaint);
    }

    /**
     * 进度
     */
    @SuppressLint("WrongConstant")
    private void drawProgress(Canvas canvas) {
        float right = (progress / maxProgress) * getMeasuredWidth();
        pgCanvas.save(Canvas.CLIP_SAVE_FLAG);
        pgCanvas.clipRect(0, 0, right, getMeasuredHeight());
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();

        pgPaint.setXfermode(xfermode);
        pgCanvas.drawBitmap(flikerBitmap, flickerLeft, 0, pgPaint);
        pgPaint.setXfermode(null);

        //控制显示区域
        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        pgPaint.setShader(bitmapShader);
        canvas.drawRoundRect(bgRectf, radius, radius, pgPaint);

//        //完整矩形左上角，右下角的点
//        pgPaint.setPathEffect(new CornerPathEffect(dp2px(radius)));
//        Point lu = new Point(0, 0);
//        Point rd = new Point((int) right, getMeasuredHeight());
//
//        float length = right;
//        //进度右下角点
//
//        //已下载长度
//        pgPaint.setColor(progressColor);
//        progressRectPath.reset();
//        progressRectPath.moveTo(lu.x, lu.y);
//        progressRectPath.lineTo(lu.x + progress * 0.01f * length, lu.y);
//        progressRectPath.lineTo(lu.x + progress * 0.01f * length, rd.y);
//        progressRectPath.lineTo(lu.x, rd.y);
//        progressRectPath.close();
//        canvas.drawPath(progressRectPath, pgPaint);
    }


    /**
     * 画进度提示文本
     *
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        progressText = getProgressText();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);
        int tWidth = textRect.width();
        int tHeight = textRect.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        canvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
    }

    /**
     * 变色处理
     *
     * @param canvas
     */
    @SuppressLint("WrongConstant")
    private void drawColorProgressText(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        int tWidth = textRect.width();
        int tHeight = textRect.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        float progressWidth = (progress / maxProgress) * getMeasuredWidth();
        if (progressWidth > xCoordinate) {
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            float right = Math.min(progressWidth, xCoordinate + tWidth * 1.1f);
            canvas.clipRect(xCoordinate, 0, right, getMeasuredHeight());
            canvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
            canvas.restore();
        }
    }

    //设置当前进度以及最大进度
    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    public void setMaxProgress(long maxProgress) {
        this.maxProgress = maxProgress;
    }

    public float getProgress() {
        return progress;
    }


    private String getProgressText() {
        String progressText;
        if (mType == 1) {
            progressText = (Math.round(progress / maxProgress * 10000) / 100 + "%");
        } else {
            progressText = (int) progress + "/" + (int) maxProgress;
        }
        return progressText;
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }


    /**
     * 设置相关画笔
     */
    public void setBgPaint(Paint.Style style, float borderWidth, int bgColor) {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bgPaint.setStyle(style);
        bgPaint.setStrokeWidth(borderWidth);
        bgPaint.setColor(bgColor);
    }
}
