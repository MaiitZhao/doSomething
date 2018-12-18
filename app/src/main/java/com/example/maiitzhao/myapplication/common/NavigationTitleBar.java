package com.example.maiitzhao.myapplication.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.R;

import butterknife.OnClick;

import static com.example.maiitzhao.myapplication.common.TitleLeftBtnStatus.*;

/**
 * 作者：zpxiang
 * 创建时间：2018/5/11 上午10:30
 * 类描述：通用头部, 满足任何颜色的title
 * 修改人：
 * 修改内容:
 * 修改时间：
 */
public class NavigationTitleBar extends RelativeLayout implements View.OnClickListener {

    private ImageView ivBack;
    private TextView tvLeftTitle;
    private LinearLayout llLeftContainer;
    private TextView tvTitle;
    private ImageView ivRightImg;
    private TextView tvRightTitle;
    private RelativeLayout rlRightContainer;
    private RelativeLayout mRlTitleBlock;

    private Context context;
    private String title;
    private int titleColor;
    private String leftTitle;
    private String rightTitle;
    private int rightImg;
    private int backgroundColor;
    private boolean imgRightState;
    private TitleLeftBtnStatus status;

    private OnLeftClickListener onLeftClickListener;
    private OnRightClickListener onRightClickListener;

    public interface OnLeftClickListener {
        void onLeftClick();
    }

    public interface OnRightClickListener {
        void onRightClick();
    }

    public NavigationTitleBar(Context context) {
        this(context, null);
    }

    public NavigationTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
        initView(context);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.activity_common_title);
        // 返回按钮是否显示
        int leftStatus = typedArray.getInt(R.styleable.activity_common_title_left_states, 0);
        status = values()[leftStatus];
        // 右边是否显示
        imgRightState = typedArray.getBoolean(R.styleable.activity_common_title_right_states, false);
        // 左边文字
        leftTitle = typedArray.getString(R.styleable.activity_common_title_left_title);
        // title背景颜色
        backgroundColor = typedArray.getColor(R.styleable.activity_common_title_background_color, context.getResources().getColor(R.color.black));
        // title 文字
        title = typedArray.getString(R.styleable.activity_common_title_title);
        // title 文字颜色
        titleColor = typedArray.getColor(R.styleable.activity_common_title_title_color, context.getResources().getColor(R.color.white));
        // 右边文字
        rightTitle = typedArray.getString(R.styleable.activity_common_title_right_title);
        // 右边图标
        rightImg = typedArray.getResourceId(R.styleable.activity_common_title_right_img, 0);
        typedArray.recycle();
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_common_title, null);

        mRlTitleBlock = (RelativeLayout) view.findViewById(R.id.rl_title_block);
        llLeftContainer = (LinearLayout) view.findViewById(R.id.ll_left_feature);
        tvLeftTitle = (TextView) view.findViewById(R.id.tv_left_title);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);

        tvTitle = (TextView) view.findViewById(R.id.tv_com_title);

        rlRightContainer = (RelativeLayout) view.findViewById(R.id.rl_right_feature);
        tvRightTitle = (TextView) view.findViewById(R.id.tv_right_title);
        ivRightImg = (ImageView) view.findViewById(R.id.iv_right_img);

        handleLeftBtnStatus();

        // 设置title
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        mRlTitleBlock.setBackgroundColor(backgroundColor);

        if (imgRightState) {
            rlRightContainer.setVisibility(View.GONE);
        } else {
            rlRightContainer.setVisibility(View.VISIBLE);
            // 设置右边按钮图片
            ivRightImg.setImageResource(rightImg);
            // 设置右边title
            if (!TextUtils.isEmpty(rightTitle)) {
                tvRightTitle.setText(rightTitle);
            }
        }

        llLeftContainer.setOnClickListener(this);
        rlRightContainer.setOnClickListener(this);

        addView(view);
    }

    private void handleLeftBtnStatus() {
        switch (status) {
            case Back:
                ivBack.setVisibility(VISIBLE);
                tvLeftTitle.setVisibility(GONE);
                ivBack.setOnClickListener(this);
                break;
            case Text:
                tvLeftTitle.setVisibility(VISIBLE);
                ivBack.setVisibility(GONE);
                tvLeftTitle.setText(leftTitle);
                tvLeftTitle.setOnClickListener(this);
                break;
            case None:
                tvLeftTitle.setVisibility(GONE);
                ivBack.setVisibility(GONE);
                break;
        }
    }

    private LayoutParams getWrapParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置标题
     */
    public void setTitle(String text) {
        tvTitle.setText(text);
    }

    /**
     * 设置标题是否显示
     */
    public void setTitleVisble(int visble) {
        tvTitle.setVisibility(visble);
    }

    public void setTitleAlpha(float alpha) {
        tvTitle.setAlpha(alpha);
    }

    /**
     * 左边显示隐藏
     */
    public void setLeftVisibility(int visibility) {
        llLeftContainer.setVisibility(visibility);
    }

    /**
     * 右边显示隐藏
     */
    public void setRightVisibility(int visibility) {
        rlRightContainer.setVisibility(visibility);
    }

    /**
     * 设置右边按钮状态
     */
    public void setLeftEnabled(boolean enable) {
        llLeftContainer.setEnabled(enable);
    }

    /**
     * 设置右边按钮状态
     */
    public void setRightEnabled(boolean enable) {
        rlRightContainer.setEnabled(enable);
    }

    /**
     * 设置右边文字按钮字体颜色
     */
    public void setRightColor(int color) {
        tvRightTitle.setTextColor(color);
    }

    /**
     * 设置左侧特殊返回点击
     */
    public void setLeftOnClickListener(OnLeftClickListener onClick) {
        this.onLeftClickListener = onClick;
    }

    /**
     * 设置右侧特殊返回点击
     */
    public void setRightOnClickListener(OnRightClickListener onClick) {
        this.onRightClickListener = onClick;
    }

    @OnClick({R.id.ll_left_feature, R.id.rl_right_feature})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_left_feature:
                if (onLeftClickListener != null && status == TitleLeftBtnStatus.Text) {
                    onLeftClickListener.onLeftClick();
                    return;
                }

                if (status == TitleLeftBtnStatus.None) {
                    return;
                }
                ((Activity) context).finish();
                break;

            case R.id.rl_right_feature:
                if (onRightClickListener != null) {
                    onRightClickListener.onRightClick();
                } else {
                    ((Activity) context).finish();
                }
                break;
        }
    }
}
