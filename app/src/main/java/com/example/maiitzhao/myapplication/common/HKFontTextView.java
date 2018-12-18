package com.example.maiitzhao.myapplication.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.base.AppAplication;

import java.lang.reflect.Field;

/**
 * Created by zpxiang on 2017/7/31.
 * 自定义字体TextView
 * 目前默认使用方正开通字体
 */

@SuppressLint("AppCompatCustomView")
public class HKFontTextView extends TextView {

    private void setDefaultFont() {
        setTypeface(AppAplication.getInstance().getHkTypeface());
    }


    TextPaint m_TextPaint;
    int mInnerColor;
    int mOuterColor;
    float strokeWidth;
    boolean isFakeBold = false;//描边默认不加粗

    public HKFontTextView(Context context, int outerColor, int innnerColor, float strokeWidth, boolean isEnableFont) {
        super(context);
        if(isEnableFont) {
            setDefaultFont();
        }
        m_TextPaint = this.getPaint();
        this.mInnerColor = innnerColor;
        this.mOuterColor = outerColor;
        this.strokeWidth = strokeWidth;

        // TODO Auto-generated constructor stub
    }

    public HKFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultFont();
        m_TextPaint = this.getPaint();
        //获取自定义的XML属性名称
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.fontStyle);
        //获取对应的属性值
        this.mInnerColor = a.getColor(R.styleable.fontStyle_text_color,0xffffff);
        this.mOuterColor = a.getColor(R.styleable.fontStyle_stroke_color,0xffffff);
        this.strokeWidth = a.getDimension(R.styleable.fontStyle_stroke_width,5);
        this.m_bDrawSideLine = a.getBoolean(R.styleable.fontStyle_is_stroke,true);
        this.isFakeBold = a.getBoolean(R.styleable.fontStyle_is_bold,false);
        a.recycle();
    }

    public HKFontTextView(Context context, AttributeSet attrs, int defStyle, int outerColor, int innnerColor) {
        super(context, attrs, defStyle);
        setDefaultFont();
        m_TextPaint = this.getPaint();
        this.mInnerColor = innnerColor;
        this.mOuterColor = outerColor;
        // TODO Auto-generated constructor stub
    }

    private boolean m_bDrawSideLine = true; // 默认采用描边

    /**
     *
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (m_bDrawSideLine) {
            // 描外层
            // super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归
            setTextColorUseReflection(mOuterColor);
            m_TextPaint.setStyle(Paint.Style.FILL_AND_STROKE); // 描边种类
            if (isFakeBold) {
                m_TextPaint.setFakeBoldText(false);
//                m_TextPaint.setAntiAlias(true);
                m_TextPaint.setStrokeWidth(1);
            } else {
                m_TextPaint.setFakeBoldText(true);
                m_TextPaint.setStrokeWidth(strokeWidth); // 描边宽度
            }

            super.onDraw(canvas);

            // 描内层，恢复原先的画笔

            // super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归
            setTextColorUseReflection(mInnerColor);
            m_TextPaint.setStrokeWidth(0);
            m_TextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            m_TextPaint.setFakeBoldText(false);

        }
        super.onDraw(canvas);
    }

    /**
     * 使用反射的方法进行字体颜色的设置
     * @param color
     */
    private void setTextColorUseReflection(int color) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        m_TextPaint.setColor(color);
    }

    //设置字体内外颜色
    public void setInnerAndOutTextColor(int innerColor,int outerColor){
        this.mInnerColor = innerColor;
        this.mOuterColor = outerColor;
        postInvalidate();
        invalidate();
    }

    /**
     * 设置边框颜色
     * @param color
     */
    public void setStrokeColor(int color) {
        this.mOuterColor = color;
        postInvalidate();
        invalidate();

    }
}
