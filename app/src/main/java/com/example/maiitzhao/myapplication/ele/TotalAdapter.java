package com.example.maiitzhao.myapplication.ele;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maiitzhao.myapplication.R;
import com.example.maiitzhao.myapplication.util.CommonUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

/**
 * 类描述：界面recycleview的适配器
 * 创建人：zpxiang
 * 创建时间：2019/1/9
 * 修改人：
 * 修改时间：
 */

public class TotalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<TotalAdapter.HeaderViewHolder> {

    private Context context;
    private boolean isHome = false;

    public TotalAdapter(Context context, boolean isHome) {
        this.context = context;
        this.isHome = isHome;
    }

    @Override
    public long getHeaderId(int position) {
        int resId;
        if (position == 0 && isHome) {
            resId = -1;
        } else {
            resId = 1;
        }
        return resId;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_header, null);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case R.layout.item_recommend:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.showToastShort("点击整体");
                    }
                });
                holder = new RecommendViewHolder(view);
                break;
            case R.layout.item_header://经测试事件无效
                view.findViewById(R.id.type1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.showToastShort("综合排序");
                    }
                });
                view.findViewById(R.id.type2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.showToastShort("好评优先");
                    }
                });
                view.findViewById(R.id.type3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.showToastShort("距离最近");
                    }
                });
                view.findViewById(R.id.type4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.showToastShort("筛选");
                    }
                });
                break;
            case R.layout.item_test:

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.showToastShort(String.valueOf(view.getTag()));
                    }
                });
                holder = new ViewHolder(view);
                break;

        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecommendViewHolder) {
            //绑定推荐

        } else if (holder instanceof HeaderViewHolder) {
            //绑定头数据
        } else {
            holder.itemView.setTag("模拟商品描述" + position);
            ((ViewHolder) holder).mContent.setText("模拟商品描述" + position);
            if (position % 3 == 0) {
                ((ViewHolder) holder).mIvIcon.setImageResource(R.mipmap.ic_test1);
            } else if (position % 3 == 1) {
                ((ViewHolder) holder).mIvIcon.setImageResource(R.mipmap.ic_test2);
            } else if (position % 3 == 2) {
                ((ViewHolder) holder).mIvIcon.setImageResource(R.mipmap.ic_test3);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewResId;

        if (position == 0 && isHome) {
            viewResId = R.layout.item_recommend;
        } else {
            viewResId = R.layout.item_test;
        }

        return viewResId;
    }

    @Override
    public int getItemCount() {
        return 30;
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView type1;
        TextView type2;
        TextView type3;
        TextView type4;

        HeaderViewHolder(View itemView) {
            super(itemView);
            type1 = (TextView) itemView.findViewById(R.id.type1);
            type2 = (TextView) itemView.findViewById(R.id.type2);
            type3 = (TextView) itemView.findViewById(R.id.type3);
            type4 = (TextView) itemView.findViewById(R.id.type4);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mContent;
        ImageView mIvIcon;

        ViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mIvIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    class RecommendViewHolder extends RecyclerView.ViewHolder {
        TextView mStvSelect;
        ImageView mIvCorrect;

        RecommendViewHolder(View itemView) {
            super(itemView);
//            mStvSelect = itemView.findViewById(R.id.stv_question_select);
//            mIvCorrect = itemView.findViewById(R.id.iv_correct);
        }
    }
}
