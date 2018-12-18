package com.example.maiitzhao.myapplication.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.util.CommonUtil;
import com.example.maiitzhao.myapplication.util.ImageUtil;


/**
 * @author zpxiang
 */
public class RoundImageView extends AppCompatImageView {

    private final static String TAG = "RoundImageView";

    private int mBorderThickness = 0;
    private Context mContext;
    private int defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    private float radiusDegree = -1;

    //默认显示的文字
    private String defText = "";

    public RoundImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.RoundImageView_border_thickness, 0);
        mBorderOutsideColor = a.getColor(R.styleable.RoundImageView_border_outside_color, defaultColor);
        mBorderInsideColor = a.getColor(R.styleable.RoundImageView_border_inside_color, defaultColor);
        radiusDegree = a.getDimension(R.styleable.RoundImageView_radius_degree, -1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (getDrawable() == null && CommonUtil.isEmpty(defText) == false) {
                this.drawText(canvas);
                canvas.restore();

                return;
            }
            Drawable drawable = getDrawable();
            if (drawable == null) {
                setImageResource(R.mipmap.def_user);
                return;
            }

            if (getWidth() == 0 || getHeight() == 0) {
                return;
            }
            this.measure(0, 0);
            if (drawable.getClass() == NinePatchDrawable.class)
                return;
            //Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = ImageUtil.drawableToBitmap(drawable);//((TransitionDrawable) drawable).getBitmap();
            //System.out.println("图片的尺寸:"+drawable.getIntrinsicWidth() + " - " +drawable.getIntrinsicHeight());
            if (b == null) {
                return;
            }
            Bitmap bitmap = b.copy(Config.ARGB_8888, true);
            if (defaultWidth == 0) {
                defaultWidth = getWidth();

            }
            if (defaultHeight == 0) {
                defaultHeight = getHeight();
            }
            // 保证重新读取图片后不会因为图片大小而改变控件宽、高的大小（针对宽、高为wrap_content布局的imageview，但会导致margin无效）
            // if (defaultWidth != 0 && defaultHeight != 0) {
            // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            // defaultWidth, defaultHeight);
            // setLayoutParams(params);
            // }


            if (radiusDegree == -1) {

                int radius = 0;
                if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
                    radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
                    // 画内圆
                    drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
                    // 画外圆
                    drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
                } else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor) {// 定义画一个边框
                    radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
                    drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
                } else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor) {// 定义画一个边框
                    radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
                    drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderOutsideColor);
                } else {// 没有边框
                    radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
                }
                Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
                canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
            } else {
                canvas.drawBitmap(ImageUtil.getRoundedCornerBitmap(bitmap, this.radiusDegree), 0, 0, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius 半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
		/* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
		/* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

    /**
     * 画文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        int x = getWidth();
        int y = getHeight();
        //canvas.drawColor(Color.GRAY);

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);//充满
        p.setColor(Color.GRAY);
        p.setAntiAlias(true);// 设置画笔的锯齿效果
        RectF oval3 = new RectF(0, 0, getWidth(), getWidth());// 设置个新的长方形
        canvas.drawRoundRect(oval3, radiusDegree, radiusDegree, p);//第二个参数是x半径，第三个参数是y半径


        Paint pText = new Paint();
        pText.setColor(getResources().getColor(R.color.color_fffef9));
        //p.setTypeface(font);
        pText.setAntiAlias(true);//去除锯齿
        pText.setFilterBitmap(true);//对位图进行滤波处理
        pText.setTextSize(scalaFonts((int) (getWidth() * 0.7f)));
        float tX = (x - getFontlength(pText, defText)) / 2;
        float tY = (y - getFontHeight(pText)) / 2 + getFontLeading(pText);
        canvas.drawText(defText, tX, tY, pText);
    }

    public void setDefText(String defText) {
        if (!CommonUtil.isEmpty(defText) && defText.length() > 1) {
            defText = defText.substring(0, 1);
        }
        this.defText = defText;
        postInvalidate();
    }

    public void setBorderOutsideColor(String color) {
        if (!CommonUtil.isEmpty(color) && color.length() > 1) {
            mBorderOutsideColor = Color.parseColor(color);
            postInvalidate();
        }

    }

    /**
     * 根据屏幕系数比例获取文字大小
     *
     * @return
     */
    private static float scalaFonts(int size) {
        //暂未实现
        return size;
    }

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }
}
