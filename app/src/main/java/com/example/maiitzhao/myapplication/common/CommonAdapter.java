package com.example.maiitzhao.myapplication.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：抽取的BaseAdapter  适用于单条目布局
 * 创建人：zpxiang
 * 创建时间：2018/12/18
 * 修改人：
 * 修改时间：
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> datas, int mItemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mDatas = datas;
        this.mItemLayoutId = mItemLayoutId;
    }

    public List<T> getmDatas() {
        return this.mDatas;
    }

    @Override
    public int getCount() {
        if (this.mDatas == null) {
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

    public void removeData(int position) {
        if (this.mDatas == null) {
            return;
        } else if (this.mDatas.size() <= position) {
            return;
        }
        this.mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void removeDatas(T t) {
        if (mDatas != null) {
            mDatas.remove(t);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if (this.mDatas != null) {
            this.mDatas.clear();
        }
        notifyDataSetChanged();
    }

    public void addData(T data) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList<T>();
        }
        this.mDatas.add(data);
        notifyDataSetChanged();
    }

    public void addToFirst(T data) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList<T>();
            this.mDatas.add(data);
        } else {
            this.mDatas.add(0, data);
        }
        notifyDataSetChanged();
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

    public void setmDatas(List<T> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        try {
            convert(position, viewHolder, getItem(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewHolder.getConvertView();
    }

    public abstract void convert(int position, ViewHolder helper, T item);

    public ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {

        return ViewHolder.get(this.mContext, convertView, parent, this.mItemLayoutId, position);
    }

    public Context getmContext() {
        return mContext;
    }

}
