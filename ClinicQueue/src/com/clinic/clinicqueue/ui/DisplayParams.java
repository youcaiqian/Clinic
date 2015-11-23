package com.clinic.clinicqueue.ui;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayParams {  
    public int screenWidth;  
    public int screenHeight;  
    public int densityDpi;  
    public float scale;  
    public float fontScale;  
    public int screenOrientation;  
    public final static int SCREEN_ORIENTATION_VERTICAL = 1;  
    public final static int SCREEN_ORIENTATION_HORIZONTAL = 2;  
  
    private static DisplayParams singleInstance;  
  
    private DisplayParams(Context context) {  
        DisplayMetrics dm = context.getResources().getDisplayMetrics();  
        screenWidth = dm.widthPixels;  
        screenHeight = dm.heightPixels;  
        densityDpi = dm.densityDpi;  
        scale = dm.density;  
        fontScale = dm.scaledDensity;  
        screenOrientation = screenHeight > screenWidth ? SCREEN_ORIENTATION_VERTICAL  
                : SCREEN_ORIENTATION_HORIZONTAL;  
    }  
  
    public static DisplayParams getInstance(Context context) {  
        if (singleInstance == null) {  
            singleInstance = new DisplayParams(context);  
        }  
        return singleInstance;  
    }  
  
    public static DisplayParams getNewInstance(Context context) {  
        if (singleInstance != null) {  
            singleInstance = null;  
        }  
        return getInstance(context);  
    }  
}  
