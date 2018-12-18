package com.example.maiitzhao.myapplication.signmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.common.HKFontTextView;
import com.example.maiitzhao.myapplication.common.CommonDialog;
import com.example.maiitzhao.myapplication.util.CommonUtil;

/**
 * Created by zpxiang on 2018/3/19.
 * 签到地图弹窗的管理类
 */

public class SignMapDialogViewControl {

    private CommonDialog signMapDialog;
    private HKFontTextView tvTitle;
    private ImageView ivData0;
    private ImageView ivData1;
    private ImageView ivData2;
    private ImageView ivData3;
    private ImageView ivData4;
    private ImageView ivData5;
    private ImageView ivData6;
    private ImageView ivData7;

    private ImageView ivGold4;
    private ImageView ivGold7;
    private ImageView ivSignBtn;

    private RelativeLayout rlSignHead;
    private ImageView ivSignHead;

    private ImageView ivClose;
    private ConstraintLayout constraintLayout;


    private Context context;
    private int signDays = 0;//已签到天数
    private boolean isSign = false;//是否已签到
    private OnSignBtnCliclkListener listener;

    public void setOnSignBtnCliclkListener(OnSignBtnCliclkListener cliclkListener) {
        this.listener = cliclkListener;
    }

    public interface OnSignBtnCliclkListener {
        void startSign();
    }

    public void init(final Context context) {
        this.context = context;
        if (signMapDialog == null) {
            signMapDialog = new CommonDialog(context, CommonDialog.SIGN_MAP);

            ivClose = (ImageView) signMapDialog.findViewById(R.id.iv_close);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signMapDialog.dismiss();
                    if (signBtnState == 1) {
                        ((SignMapActivity) context).showLottoDialog();
                    }
                }
            });
            constraintLayout = (ConstraintLayout) signMapDialog.findViewById(R.id.constraintLayout);

            tvTitle = (HKFontTextView) signMapDialog.findViewById(R.id.tv_sign_date);
            ivData0 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_0);
            ivData1 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_1);
            ivData2 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_2);
            ivData3 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_3);
            ivData4 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_4);
            ivData5 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_5);
            ivData6 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_6);
            ivData7 = (ImageView) signMapDialog.findViewById(R.id.iv_sign_7);
            ivSignBtn = (ImageView) signMapDialog.findViewById(R.id.iv_sign);

            ivGold4 = (ImageView) signMapDialog.findViewById(R.id.iv_gold_4);
            ivGold7 = (ImageView) signMapDialog.findViewById(R.id.iv_gold_7);

            ivSignBtn = (ImageView) signMapDialog.findViewById(R.id.iv_sign);
            ivSignBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isSign) {
                        signMapDialog.dismiss();
                    } else {
                        if (signBtnState == 0) {

                            if (listener != null) {
                                listener.startSign();
                            }
//                            handleSignAnimation();

                        } else if (signBtnState == 1) {
                            signMapDialog.dismiss();
                            ((SignMapActivity) context).showLottoDialog();
                        }

                    }
                }
            });

            ivSignHead = (ImageView) signMapDialog.findViewById(R.id.iv_sign_head);
            rlSignHead = (RelativeLayout) signMapDialog.findViewById(R.id.rl_sign_head);
//            ImageLoader.getInstance().displayImage(ResBox.getInstance().resourceUserHead(String.valueOf(Token.getMainUserId())), ivSignHead, LoaderImage.UserIntegralConfig);
        }
    }

    /**
     * 定位头像位置，并弹窗
     *
     * @param locationIndex 已签到天数
     * @param isSign        签到状态
     */
    public void show(int locationIndex, boolean isSign) {
        if (!isSign) {
            ivSignBtn.setImageResource(R.mipmap.btn_sign_map);
            signBtnState = 0;
        } else {
            ivSignBtn.setImageResource(R.mipmap.btn_already_sign_map);
            signBtnState = 2;
        }

        this.isSign = isSign;
        this.signDays = locationIndex;
        if (signMapDialog.isShowing()) {
            return;
        }

        rlSignHead.setVisibility(View.VISIBLE);
        setHeadImgLocation(locationIndex);
        signMapDialog.show();
    }


    private void setHeadImgLocation(final int locationIndex) {
        final ImageView imageView = getNeededView(locationIndex);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlSignHead.setX(imageView.getX() - (rlSignHead.getWidth() - imageView.getWidth()) / 2);
                rlSignHead.setY(imageView.getY() - (rlSignHead.getHeight() + imageView.getHeight()) / 2);
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                handleViewDis(locationIndex);
            }
        });
    }

    private void handleViewDis(int locationIndex) {
        ivGold4.setVisibility(View.GONE);
        ivGold7.setVisibility(View.GONE);
        switch (locationIndex) {
            case 0:
                strengthAnimation(4);
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_not_sign);
                ivData2.setImageResource(R.mipmap.img_not_sign);
                ivData3.setImageResource(R.mipmap.img_not_sign);
                ivData5.setImageResource(R.mipmap.img_not_sign);
                ivData6.setImageResource(R.mipmap.img_not_sign);
                break;
            case 1:
                strengthAnimation(4);
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_not_sign);
                ivData3.setImageResource(R.mipmap.img_not_sign);
                ivData5.setImageResource(R.mipmap.img_not_sign);
                ivData6.setImageResource(R.mipmap.img_not_sign);
                break;
            case 2:
                strengthAnimation(4);
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_already_sign);
                ivData3.setImageResource(R.mipmap.img_not_sign);
                ivData5.setImageResource(R.mipmap.img_not_sign);
                ivData6.setImageResource(R.mipmap.img_not_sign);
                break;
            case 3:
                strengthAnimation(4);
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_already_sign);
                ivData3.setImageResource(R.mipmap.img_already_sign);
                ivData5.setImageResource(R.mipmap.img_not_sign);
                ivData6.setImageResource(R.mipmap.img_not_sign);
                break;
            case 4:
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_already_sign);
                ivData3.setImageResource(R.mipmap.img_already_sign);
                ivData4.setVisibility(View.INVISIBLE);
                ivGold4.setImageResource(R.mipmap.img_sign_map_gold);
                ivGold4.setVisibility(View.VISIBLE);
                ivData5.setImageResource(R.mipmap.img_not_sign);
                ivData6.setImageResource(R.mipmap.img_not_sign);
                break;
            case 5:
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_already_sign);
                ivData3.setImageResource(R.mipmap.img_already_sign);
                ivData4.setVisibility(View.INVISIBLE);
                ivGold4.setVisibility(View.VISIBLE);
                ivData5.setImageResource(R.mipmap.img_already_sign);
                ivData6.setImageResource(R.mipmap.img_not_sign);
                break;
            case 6:
                strengthAnimation(7);
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_already_sign);
                ivData3.setImageResource(R.mipmap.img_already_sign);
                ivData4.setVisibility(View.INVISIBLE);
                ivGold4.setVisibility(View.VISIBLE);
                ivData5.setImageResource(R.mipmap.img_already_sign);
                ivData6.setImageResource(R.mipmap.img_already_sign);
                break;
            case 7:
                ivData1.setImageResource(R.mipmap.img_already_sign);
                ivData2.setImageResource(R.mipmap.img_already_sign);
                ivData3.setImageResource(R.mipmap.img_already_sign);
                ivData4.setVisibility(View.INVISIBLE);
                ivGold4.setVisibility(View.VISIBLE);
                ivData5.setImageResource(R.mipmap.img_already_sign);
                ivData6.setImageResource(R.mipmap.img_already_sign);
                ivData7.setVisibility(View.INVISIBLE);
                ivGold7.setVisibility(View.VISIBLE);
                ivGold7.setImageResource(R.mipmap.img_sign_map_gold);
                break;
        }
    }

    /**
     * 开启伸展动画
     */
    private void strengthAnimation(int index) {
        ImageView imageView = getNeededView(index);
        ScaleAnimation anim = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setStartOffset(100);
        anim.setRepeatMode(Animation.REVERSE); // 放大并缩小，时间为750*2
        anim.setRepeatCount(Animation.INFINITE); // 无限循环
        imageView.startAnimation(anim);
    }


    private ImageView getNeededView(int locationIndex) {
        ImageView imageView = null;

        switch (locationIndex) {
            case 0:
                imageView = ivData0;
                break;
            case 1:
                imageView = ivData1;
                break;
            case 2:
                imageView = ivData2;
                break;
            case 3:
                imageView = ivData3;
                break;
            case 4:
                imageView = ivData4;
                break;
            case 5:
                imageView = ivData5;
                break;
            case 6:
                imageView = ivData6;
                break;
            case 7:
                imageView = ivData7;
                break;
        }

        return imageView;
    }

    private int signBtnState = 0;//签到按钮的状态  0代表未被点过 1代表是第一次签到且签到完成  2代表已经签到过

    /**
     * 处理签到动画
     * 1.判断下一个是否为4或7 需要特殊处理
     * 2.普通处理准状态
     */
    public void handleSignAnimation() {
        startSignAnimation(getNeededView(signDays), getNeededView(signDays + 1));
    }

    /**
     * 签到跳跃动画  二阶贝塞尔
     *
     * @param startImageView
     * @param endImageView
     */
    private void startSignAnimation(ImageView startImageView, final ImageView endImageView) {
        int[] startArray = getLocationArray(startImageView);
        int[] endArray = getLocationArray(endImageView);

        Point startPosition = new Point(startArray[0] - (rlSignHead.getWidth() - startImageView.getWidth()) / 2, startArray[1] - (rlSignHead.getHeight() + startImageView.getHeight()) / 2);//起始位置
        Point endPosition;
        if (signDays == 3 || signDays == 6) {
            endPosition = new Point(endArray[0] - (rlSignHead.getWidth() - startImageView.getWidth()) / 2 + startImageView.getWidth() / 2 + 15,
                    endArray[1] - (rlSignHead.getHeight() + startImageView.getHeight()) / 2);
        } else if (signDays == 4) {
            endPosition = new Point(endArray[0] - (rlSignHead.getWidth() - startImageView.getWidth()) / 2 - startImageView.getWidth() / 2 + 15,
                    endArray[1] - (rlSignHead.getHeight() + startImageView.getHeight()) / 2 + startImageView.getHeight() / 2 - 15);
        } else {
            endPosition = new Point(endArray[0] - (rlSignHead.getWidth() - startImageView.getWidth()) / 2,
                    endArray[1] - (rlSignHead.getHeight() + startImageView.getHeight()) / 2);
        }

        int pointX, pointY;
        if (signDays == 1) {
            pointY = (startPosition.y + endPosition.y) / 2;
            pointX = Math.max(startPosition.x, endPosition.x) + 200;
        } else {
            pointX = Math.min(startPosition.x, endPosition.x) + Math.abs(startPosition.x - endPosition.x) / 2;
            pointY = Math.min(startPosition.y, endPosition.y) - 100;
        }
        Point controllPoint = new Point(pointX, pointY);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint), startPosition, endPosition);
        valueAnimator.setDuration(900);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //我们通过point记录运动的轨迹，然后，物品随着轨迹运动
                Point point = (Point) valueAnimator.getAnimatedValue();
                rlSignHead.setX(point.x);
                rlSignHead.setY(point.y);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (signDays == 3) {
                    ivData4.clearAnimation();
                    ivData4.setVisibility(View.INVISIBLE);
                    frameAnimation(getNeededView(signDays + 1), ivGold4);
                } else if (signDays == 6) {
                    ivData7.clearAnimation();
                    ivData7.setVisibility(View.INVISIBLE);
                    frameAnimation(getNeededView(signDays + 1), ivGold7);
                } else {
                    handleAniamtionEndState();
                }
            }
        });
    }

    /**
     * 动画结束之后的处理
     */
    private void handleAniamtionEndState() {
        signBtnState = 1;
        ivSignBtn.setImageResource(R.mipmap.btn_already_sign_map);
        CommonUtil.showToastShort("签到成功");

        if (signDays == 3) {
            ivData4.setVisibility(View.INVISIBLE);
            ivGold4.setVisibility(View.VISIBLE);
            ivGold4.setImageResource(R.mipmap.img_sign_map_gold);
        } else if (signDays == 6) {
            ivData7.setVisibility(View.INVISIBLE);
            ivGold7.setVisibility(View.VISIBLE);
            ivGold7.setImageResource(R.mipmap.img_sign_map_gold);
        }
    }


    /**
     * 金币飞出、以及金币减少的帧动画
     *
     * @param lower     下层的imageview
     * @param upperView 上层的imageview  播放金币减少帧动画
     */
    private void frameAnimation(ImageView lower, ImageView upperView) {

        int[] locationArray = new int[2];
        lower.getLocationInWindow(locationArray);

        playFlyOffAnimation(locationArray);
        goldReduceFrameAnimation(upperView);
    }

    /**
     * 金币飞出动画
     */
    private void playFlyOffAnimation(int[] imgLocation) {
        int[] location = getLocationArray(ivClose);

        final int duration = 350;
        for (int i = 0; i < 16; i++) {

            final ImageView img = new ImageView(context);
            img.setImageResource(R.mipmap.img_gold_flyoff);
            img.setScaleType(ImageView.ScaleType.MATRIX);
            img.setX(imgLocation[0] + 20);
            img.setY(imgLocation[1] - 20);
            img.setAlpha(0);
            constraintLayout.addView(img);

            Point startPosition = new Point(imgLocation[0], imgLocation[1]);//起始位置
            Point endPosition = new Point(location[0] + ivClose.getWidth(), location[1] - ivClose.getHeight());

            int pointX = imgLocation[0];
            int pointY = location[1];
            final Point controllPoint = new Point(pointX, pointY);

            //透明度动画
            ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0, 1, 0);
            alphaAnimator.setDuration(duration);
            alphaAnimator.setStartDelay(i * 120);
            alphaAnimator.start();
            alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    long currentPlayTime = valueAnimator.getCurrentPlayTime();
                    if (currentPlayTime <= 0.45 * duration) {
                        float alpha1 = (float) (currentPlayTime / (0.45 * duration));
                        img.setAlpha((int) (alpha1 * 255));
                    } else if (currentPlayTime < 0.75 * duration && currentPlayTime > 0.45 * duration) {
                        img.setAlpha(255);
                    } else {
                        float alpha3 = 1 - (float) (currentPlayTime) / duration;
                        img.setAlpha((int) (alpha3 * 255));
                    }
                }
            });

            ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator(controllPoint), startPosition, endPosition);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.setStartDelay(i * 120);
            valueAnimator.start();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    //我们通过point记录运动的轨迹，然后，物品随着轨迹运动
                    Point point = (Point) valueAnimator.getAnimatedValue();
                    img.setX(point.x);
                    img.setY(point.y);
                }
            });

            final int finalI = i;
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    constraintLayout.removeView(img);
                    if (finalI == 15) {//最后一个
                        handleAniamtionEndState();
                    }
                }
            });
        }
    }


    /**
     * 金币减少帧动画
     */
    private void goldReduceFrameAnimation(ImageView upperView) {
        upperView.setVisibility(View.VISIBLE);
        upperView.setImageResource(R.drawable.animotion_gold_reduce);
        AnimationDrawable animationPlay = (AnimationDrawable) upperView.getDrawable();
        animationPlay.start();
    }

    //获取相对父控件坐标
    private int[] getLocationArray(ImageView imageView) {
        int[] location = new int[2];
        location[0] = (int) imageView.getX();
        location[1] = (int) imageView.getY();
        return location;
    }


    /**
     * 贝塞尔曲线（二阶抛物线）
     * controllPoint 是中间的转折点   startValue 是起始的位置   endValue 是结束的位置
     */
    public class BizierEvaluator implements TypeEvaluator<Point> {

        private Point controllPoint;

        public BizierEvaluator(Point controllPoint) {
            this.controllPoint = controllPoint;
        }

        @Override
        public Point evaluate(float t, Point startValue, Point endValue) {
            int x = (int) ((1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * controllPoint.x + t * t * endValue.x);
            int y = (int) ((1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * controllPoint.y + t * t * endValue.y);
            return new Point(x, y);
        }
    }
}
