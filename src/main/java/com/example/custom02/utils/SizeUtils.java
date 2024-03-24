package com.example.custom02.utils;


import com.example.custom02.App;

public class SizeUtils {
    public static int dip2px(float dpValue) {
        float scale = App.getAppContext().getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
