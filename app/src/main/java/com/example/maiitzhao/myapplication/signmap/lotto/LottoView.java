package com.example.maiitzhao.myapplication.signmap.lotto;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.maiitzhao.myapplication.signmap.lotto.WheelView.PADDING;

/**
 * 作者：liuyutao
 * 创建时间：17/6/7 上午10:11
 * 类描述： 抽奖View
 * 修改人：
 * 修改内容:
 * 修改时间：
 */
public class LottoView extends RelativeLayout implements View.OnClickListener {
    /**
     * Constant
     */
    private static final int BG_MARGIN_OR_SO = 60; //左右边距
    private static final float BG_WIDTH_BASE = 606; // 背景宽度 基准
    private static final float BG_HEIGHT_BASE = 730;// 背景高度基准


    private static final float STR_BUTTON_WIDTH_BASE = 255; //按钮宽度 基准
    private static final float STR_BUTTON_HEIGHT_BASE = 86;// 按钮高度基准


    private static final float SHADE_HEIGHT_BASE = 429;// 背景高度基准
    /**
     * UI
     */
    private RelativeLayout mRelBg; // 背景
    private ImageView mIvLight;//彩灯
    private Button btnStart; // 抽奖按钮

    private WheelView mWvFirst, mWvSecond; //两个滚轮
    private LinearLayout mWheelContainer; //滚轮容器


    private ProgressBar mProgressBar; //加载抽奖数据的进度条
    /**
     * Data
     */
    private List<Integer> mLottoXuemiList; //固定的抽奖学米奖品
    private List<Integer> mLottoIntegralList;//固定的抽奖积分奖品

    private int mScreenWidth, mScreenHeight;
    private int mBgWidth, mBgHeight;

    private boolean isLotto;


    //item
    private int mItemWidth, mItemHeight;


    private int mFirstIndex, mSecondIndex;// 抽奖的索引
    private int xueMi, point;//抽奖获得的学米 和积分的数量
    /**
     * LayoutParams
     */
    private ViewGroup.LayoutParams mItemParams;
    /**
     * Listener
     */
    private OnLottoListener lottoListener; //抽奖监听

    private Context mContext;

    public LottoView(Context context) {
        this(context, null);
    }

    public LottoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        mContext = context;
        mScreenWidth = CommonUtil.getScreenWidth(mContext);
        mScreenHeight = CommonUtil.getScreenHeight(mContext);
        isLotto = false;
        initPrize();
        initLottoBg();
        initLottoButton();
        initWheelContainer();
        initItemParams();
        initWheelViewFirst();
        initWheelViewSecond();
        addShade();
        initLight();
        initProgressBar();
    }

    /**
     * 初始化进度条
     */
    public void initProgressBar() {
        mProgressBar = new ProgressBar(mContext);
        LayoutParams mParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mParams.addRule(CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(mParams);
        addView(mProgressBar);
        mProgressBar.setVisibility(GONE);


    }

    /**
     * 初始化背景
     */
    private void initLottoBg() {
        mRelBg = new RelativeLayout(mContext);

        mBgWidth = mScreenWidth - BG_MARGIN_OR_SO * 2;
        mBgHeight = (int) (mBgWidth * (BG_HEIGHT_BASE / BG_WIDTH_BASE));

        LayoutParams mParams = new LayoutParams(mBgWidth, mBgHeight);
        mParams.addRule(CENTER_IN_PARENT);
        mRelBg.setLayoutParams(mParams);
        mRelBg.setBackgroundResource(R.mipmap.ic_lotto_bg);
        addView(mRelBg);
    }

    /**
     * 初始化抽奖彩灯
     */
    private void initLight() {
        mIvLight = new ImageView(mContext);
        //设置位置
        // 606  551
        int mWidth = (int) (mBgWidth * (551 / BG_WIDTH_BASE));
        int mHeight = (int) (mBgHeight * (475 / BG_HEIGHT_BASE));
        LayoutParams mParams = new LayoutParams(mWidth, mHeight);
        mParams.addRule(CENTER_HORIZONTAL);
        mParams.topMargin = (int) (mBgHeight * (97 / BG_HEIGHT_BASE));
        mIvLight.setLayoutParams(mParams);
        mRelBg.addView(mIvLight);

        //加载帧动画
        AnimationDrawable anim = new AnimationDrawable();
        for (int i = 1; i <= 2; i++) {
            int id = getResources().getIdentifier("ic_lotto_light_0" + i, "drawable", mContext.getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            anim.addFrame(drawable, 300);
        }
        anim.setOneShot(false);
        mIvLight.setBackgroundDrawable(anim);
        anim.start();
    }

    /**
     * 初始化抽奖按钮
     */
    private void initLottoButton() {
        btnStart = new Button(mContext);

        int mWidth = (int) (mBgWidth * 0.427);
        int mHeight = (int) (mWidth * (STR_BUTTON_HEIGHT_BASE / STR_BUTTON_WIDTH_BASE));
        LayoutParams mParams = new LayoutParams(mWidth, mHeight);
        mParams.addRule(CENTER_HORIZONTAL);
        mParams.addRule(ALIGN_PARENT_BOTTOM);
        mParams.bottomMargin = mBgHeight / 20;
        btnStart.setLayoutParams(mParams);
        mRelBg.addView(btnStart);
        btnStart.setOnClickListener(this);
        btnStart.setBackgroundResource(R.mipmap.ic_lotto_ctn_del);


    }

    /**
     * 初始化奖品集合
     */
    private void initPrize() {
        mLottoXuemiList = new ArrayList<Integer>();
        mLottoIntegralList = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            mLottoXuemiList.add(i);
            mLottoIntegralList.add(i * 10);
        }


    }

    /**
     * 初始化Itm布局
     */
    private void initItemParams() {

        mItemParams = new ViewGroup.LayoutParams(mItemWidth, mItemHeight);
    }

    /**
     * 初始化滚动容器
     */
    private void initWheelContainer() {
        mWheelContainer = new LinearLayout(mContext);
        //设置位置


        int mWheelWidth = (int) (mBgWidth / 6.0f * 5);
        int mWhellHeight = (int) (mBgHeight * (SHADE_HEIGHT_BASE / BG_HEIGHT_BASE));
        mItemWidth = mWheelWidth / 2 - PADDING * 2;
        mItemHeight = mWhellHeight / 3;
        LayoutParams mParams = new LayoutParams(mWheelWidth
                , mWhellHeight);
        mParams.topMargin = (int) (mBgHeight * (118.0f / BG_HEIGHT_BASE)) + 4;
        mParams.addRule(CENTER_HORIZONTAL);
        mWheelContainer.setLayoutParams(mParams);
        mWheelContainer.setBackgroundColor(Color.parseColor("#D00B1E"));
        addView(mWheelContainer);
    }

    /**
     * 初始化第一个滚轮
     */
    private void initWheelViewFirst() {
        mWvFirst = new WheelView(mContext);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        mWvFirst.setLayoutParams(mParams);
        mWvFirst.setEnabled(false);
        mWvFirst.setCyclic(true);
        mWvFirst.setViewAdapter(new SlotMachineAdapter(mContext, 1, mLottoXuemiList));
        mWheelContainer.addView(mWvFirst);
        mWvFirst.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

            }
        });

        mWvFirst.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                isLotto = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

            }
        });


    }

    /**
     * 初始化第二个滚轮
     */
    private void initWheelViewSecond() {
        mWvSecond = new WheelView(mContext);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        mWvSecond.setLayoutParams(mParams);
        mWvSecond.setEnabled(false);
        mWvSecond.setCyclic(true);
        mWvSecond.setViewAdapter(new SlotMachineAdapter(mContext, 2, mLottoIntegralList));
        mWheelContainer.addView(mWvSecond);

        mWvFirst.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

            }
        });

        mWvSecond.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                mWvFirst.upDataPrizeBg();
                mWvSecond.upDataPrizeBg();
                isLotto = false;
                //抽奖结束
                if (lottoListener != null) {
                    lottoListener.OnLottoFinish(xueMi, point);
                }

            }
        });
    }

    /**
     * 添加遮罩
     */
    private void addShade() {
        ImageView mIvShade = new ImageView(mContext);
        int mWidth = (int) (mBgWidth / 6.0f * 5);
        int mHeight = (int) (mBgHeight * (SHADE_HEIGHT_BASE / BG_HEIGHT_BASE));

        LayoutParams mParams = new LayoutParams(mWidth
                , mHeight);
        mParams.addRule(CENTER_HORIZONTAL);
        mParams.topMargin = (int) (mBgHeight * (118.0f / BG_HEIGHT_BASE)) + 4;
        mIvShade.setLayoutParams(mParams);
        mIvShade.setBackgroundResource(R.mipmap.ic_lotto_shade);
        addView(mIvShade);
    }

    @Override
    public void onClick(View view) {

        btnStart.setEnabled(false);
//        loadPrizeInfo();
    }

    /**
     * 设置抽奖监听
     *
     * @param listener
     */
    public void addOnLottoListener(OnLottoListener listener) {
        this.lottoListener = listener;

    }

    /**
     * 设置加载索引
     *
     * @param mFirstIndex  第一个滚筒的索引
     * @param mSecondIndex 第二个滚筒的索引
     */
    public void addIndex(int mFirstIndex, int mSecondIndex) {
        this.mFirstIndex = mFirstIndex;
        this.mSecondIndex = mSecondIndex;
    }

    /**
     * 老虎机适配器
     */
    private class SlotMachineAdapter extends AbstractWheelAdapter {
        // 布局膨胀器
        private Context context;
        private int type;

        private List<Integer> mData;

        /**
         * 构造函数
         */
        public SlotMachineAdapter(Context context, int type, List<Integer> mData) {
            this.context = context;
            this.type = type;
            this.mData = mData;
        }

        @Override
        public int getItemsCount() {

            return mData.size();
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {


            View mItemView;
            if (cachedView != null) {
                mItemView = cachedView;
            } else {
                if (type == 1) {
                    mItemView = LayoutInflater.from(context).inflate(R.layout.item_lotto_xuemi, null);
                } else {
                    mItemView = LayoutInflater.from(context).inflate(R.layout.item_lotto_integral, null);
                }

            }
            TextView tvCount = (TextView) mItemView.findViewById(R.id.tv_count);
            tvCount.setText("+" + mData.get(index));
            mItemView.setLayoutParams(mItemParams);

            return mItemView;
        }
    }

    public interface OnLottoListener {

        void OnLottoFinish(int xuemi, int point);
    }

    /**
     * 抽奖
     */
    private void lotto() {
        //保证旋转的次数
        if (mFirstIndex > 9) {
            mFirstIndex %= 10;
        }
        if (mSecondIndex > 10) {
            mSecondIndex %= 10;
        }
        mWvFirst.scroll(30 + mFirstIndex, 2000);
        mWvSecond.scroll(30 + mSecondIndex, 3000);
    }

    /**
     * 加载抽奖信息
     */
//    public void loadPrizeInfo() {
//        RequestParams params = ResBox.commonRequestParams();
//        ApiHttpClient.post(mContext, ResBox.getInstance().signLottery(), params, new HttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                isLotto = true;
//                mProgressBar.setVisibility(VISIBLE);
//            }
//
//            @Override
//            public void onRequestSuccess(HttpResponse response) {
//                mProgressBar.setVisibility(GONE);
//                int mFirstIndex = response.getInt("xueMiIndex");
//                int mSecondIndex = response.getInt("pointIndex");
//                xueMi = response.getInt("xueMi");
//                point = response.getInt("point");
//                addIndex(mFirstIndex, mSecondIndex);
//                lotto();
//
//            }
//
//            @Override
//            public void onRequestFail(HttpResponse response) {
//                super.onRequestFail(response);
//                Toast.makeText(mContext, response.getResMsg(), Toast.LENGTH_LONG).show();
//                mProgressBar.setVisibility(GONE);
//                btnStart.setEnabled(true);
//                isLotto = false;
//
//            }
//        });
//
//
//    }

    /**
     * 是否还在进行抽奖
     * @return
     */
    public boolean isLotto() {
        return isLotto;
    }

}
