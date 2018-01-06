package com.ren.myqrtest.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by win10 on 2017/10/26.
 */

public class Util {

    public static int getStatusBarHeight(Context context) {
        int sbar = 38;// 默认为38，貌似大部分是这样的
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
}
