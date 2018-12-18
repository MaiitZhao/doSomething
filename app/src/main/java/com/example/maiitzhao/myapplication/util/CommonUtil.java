package com.example.maiitzhao.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.maiitzhao.myapplication.base.AppAplication;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by zpxiang on 2018/11/19.
 * 通用工具包
 */

public class CommonUtil {

    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

    /***         toast相关           ****/
    public static void showToastShort(String msg) {
        Toast.makeText(AppAplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(String msg) {
        Toast.makeText(AppAplication.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    public static void toActivity(Class clazz) {
        AppAplication.getContext().startActivity(new Intent(AppAplication.getContext(), clazz));
    }

    public static void toActivityBundle(Class clazz, Bundle bundle) {
        Intent intent = new Intent(AppAplication.getContext(), clazz);
        intent.putExtra("bundle",bundle);
        AppAplication.getContext().startActivity(intent);
    }

    /***         临时路径相关           ****/
    public static String getAppTmpPath() {
        String path = SD_CARD_PATH + "/emp";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getTempImagePath() {
        String tempPath = getAppTmpPath() + "/" + System.currentTimeMillis() + ".jpg";//产生临时路径
        return tempPath;
    }

    /***         判空相关           ****/
    public static boolean isEmpty(Object[] objs) {
        return null == objs || objs.length <= 0;
    }

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str.trim()) || "null".equals(str));
    }

    public static boolean isEmpty(List<?> list) {
        return (list == null || list.isEmpty() || list.size() == 0);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /***         数据类型转换相关           ****/
    public static Long toLong(Object object) {
        if (object == null) {
            return 0L;
        }
        if (object instanceof Long) {
            return (Long) object;
        }
        try {
            return Long.valueOf(String.valueOf(object));

        } catch (Exception e) {
            return 0L;
        }
    }

    public static Integer toInteger(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        try {
            return Integer.valueOf(toString(object));
        } catch (Exception e) {
            return 0;
        }
    }

    public static float toFloat(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof Float) {
            return (Float) object;
        }
        try {
            return Float.valueOf(toString(object));
        } catch (Exception e) {
            return 0;
        }
    }

    public static String toString(Object object) {

        if (object == null) {
            return "";
        }
        if (object instanceof String) {
            return object.toString();
        }

        if (object instanceof Double || object instanceof Float) {
            return ((Double) object).longValue() + "";
        }
        return String.valueOf(object);
    }


    /****              获得屏幕相关         ****/
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * dp转换成px
     */
    public static int dp2px(float dpValue) {
        float scale = AppAplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
