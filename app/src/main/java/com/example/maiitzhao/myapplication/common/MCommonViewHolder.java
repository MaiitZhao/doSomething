package com.example.maiitzhao.myapplication.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zpxiang on 201811/01.
 */
/**
 * 类描述：多条目布局的viewholder
 * 创建人：zpxiang
 * 创建时间：2018/12/18
 * 修改人：
 * 修改时间：
 */
public class MCommonViewHolder {

    private final SparseArray<View> mViews;

    private int mPostion;

    private View mConvertView;

    private int mLayoutId;

    private Context context;

    private MCommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        mPostion = position;
        this.mLayoutId = layoutId;
        mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);//条目布局
        mConvertView.setTag(this);

        this.context = context;
    }

    public int getLaoutId() {
        return mLayoutId;
    }

    public View getmConvertView() {
        return mConvertView;
    }

    //获取viewholder
    public static MCommonViewHolder get(Context context, View convertView, ViewGroup parent,
                                        int layoutId, int position) {
        if (convertView == null) {
            return new MCommonViewHolder(context, parent, layoutId, position);
        }
        return (MCommonViewHolder) convertView.getTag();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public MCommonViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        String url = (String) view.getTag();
        if (url == null || !url.equals(text)) {
            view.setText(text);
        }
        return this;
    }

    public int getPosition() {
        return mPostion;
    }

    public MCommonViewHolder setImageResource(int ViewId, int drawableId) {
        ImageView view = getView(ViewId);
        view.setImageResource(drawableId);
        return this;
    }

    public MCommonViewHolder setBackgroundResource(int viewId, int drawableId) {
        getView(viewId).setBackgroundResource(drawableId);
        return this;
    }


    public MCommonViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public MCommonViewHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    public MCommonViewHolder setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }
}
