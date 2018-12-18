package com.example.maiitzhao.myapplication.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：适用于单条目以及多条目布局
 * 创建人：zpxiang
 * 创建时间：2018/12/18
 * 修改人：
 * 修改时间：
 */
public abstract class MCommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas = new ArrayList<T>();
    protected int mItemLayoutId;
    private int mPostion;

    public MCommonAdapter(Context context, List<T> datas, int itemLayoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mDatas = datas;
        mItemLayoutId = itemLayoutId;
    }

    public void removeDatas(T t) {
        if(mDatas!=null) {
            mDatas.remove(t);
        }
        notifyDataSetChanged();

    }

    public void removeData(int position) {
        if (this.mDatas == null) {
            return;
        } else if (this.mDatas.size() <= position) {
            return;
        }
        this.mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void setmDatas(List<T> list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    public void removeAlls() {
        if (mDatas != null){
            mDatas.clear();
            notifyDataSetChanged();
        }

    }

    public void addData(T t) {
        if(mDatas !=null) {
            mDatas.add(t);
            notifyDataSetChanged();
        }

    }

    public void addData(List<T> datas) {
        if (datas == null) {
            return;
        }
        if (this.mDatas == null) {
            this.mDatas = datas;
        } else {
            this.mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public Context getmContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (this.mDatas == null) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mPostion = position;
        if (mMultiItemSupport != null) {//多视图,表示此时是复杂条目的listview
            MCommonViewHolder viewHolder = null;  //该viewHolder主要是为了填充布局
            if (convertView != null) {
                viewHolder = (MCommonViewHolder) convertView.getTag();
                if (viewHolder.getLaoutId() != mMultiItemSupport.getLayoutId(position, getItem(position))) {//复杂条目类型不相同时，复用原条目
                    viewHolder = MCommonViewHolder.get(mContext, convertView, parent, mMultiItemSupport.getLayoutId(position, getItem(position)),
                            position);     //(MCommonViewHolder) convertView.getTag();
                }
            } else {
                viewHolder = MCommonViewHolder.get(mContext, convertView, parent, mMultiItemSupport.getLayoutId(position, getItem(position)),
                        position);//复杂条目的布局,new MCommonViewHolder(context,parent,layoutId,position);
            }
            convert(viewHolder, getItem(position));
            return viewHolder.getmConvertView();
        } else {//单个ID，只有单个类型的条目
            final MCommonViewHolder viewHolder = getViewHolder(position, convertView, parent);
            convert(viewHolder, getItem(position));
            return viewHolder.getmConvertView();
        }
    }

    //在这个方法中实现条目的各控件数据的填充，具体实现方式由实现的控件完成
    public abstract void convert(MCommonViewHolder viewHelper, T item);


    public List<T> getmDatas() {
        return mDatas;
    }

    private MCommonViewHolder getViewHolder(int position, View convertView,
                                            ViewGroup parent) {
        return MCommonViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

    public int getPositon() {
        return mPostion;
    }

    public interface MultiItemTypeSupport<T> {
        int getLayoutId(int position, T t);//复杂条目的布局id

        int getViewTypeCount();

        int getItemViewType(int postion, T t);
    }


    protected MultiItemTypeSupport<T> mMultiItemSupport;

    public MCommonAdapter(Context context, List<T> datas, MultiItemTypeSupport<T> multiItemSupport) {

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mMultiItemSupport = multiItemSupport;
        mDatas = datas;
    }


    @Override
    public int getViewTypeCount() {
        if (mMultiItemSupport != null)
            return mMultiItemSupport.getViewTypeCount() + 1;
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemSupport != null)
            return mMultiItemSupport.getItemViewType(position,
                    mDatas.get(position));
        return position >= mDatas.size() ? 0 : 1;
    }

    public int getResourceColor(int color) {
        return getmContext().getResources().getColor(color);
    }


}
