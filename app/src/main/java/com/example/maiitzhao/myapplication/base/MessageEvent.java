package com.example.maiitzhao.myapplication.base;

/**
 * 类描述：eventbus事件
 * 创建人：zpxiang
 * 创建时间：2019/1/10
 * 修改人：
 * 修改时间：
 */

public class MessageEvent {

    public static final int CHANGE_STYPE = 10000;

    private int type;
    private Object data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
