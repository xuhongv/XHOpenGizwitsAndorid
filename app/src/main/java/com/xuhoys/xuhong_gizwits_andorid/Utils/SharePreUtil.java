package com.xuhoys.xuhong_gizwits_andorid.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 包名：com.xuhoys.xuhong_gizwits_andorid.Utils
 * 文件名：xuhong_gizwits_andorid
 * 创建时间：2018-03-29 12:55
 * 创建者：xuhong
 * CSDN: http://blog.csdn.net/xh870189248
 * 描述：SharedPreferences处理，存储我们的uid和token
 */

public class SharePreUtil {

    private static final String SP_NAME = "config";

    /**
     * 存储String封装
     *
     * @param mContext 上下文
     * @param key      键
     * @param value    数值
     */
    public static void putString(Context mContext, String key, String value) {

        //拿到本地的SharedPreferences的一个对象，设置为只能本地应用才能读取
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        //拿到SharedPreferences的一个操作对象
        SharedPreferences.Editor editor = sp.edit();
        //存储
        editor.putString(key, value);
        //应用一下
        editor.apply();
    }

    /**
     * @param mContext 上下文
     * @param key  键
     * @param defValue 默认defValue
     * @return
     */
    public static String getString(Context mContext, String key, String defValue) {

        //拿到本地的SharedPreferences的一个对象，设置为只能本地应用才能读取
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        //取出来,如果该键下的数值为null，就会默认是defValue
        return sp.getString(key, defValue);
    }


}
