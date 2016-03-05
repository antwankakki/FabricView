package com.agsw.FabricView.DrawableCommend;

import android.view.MotionEvent;

import com.agsw.FabricView.DrawableObjects.CDrawable;

import java.util.List;


/**
 * if my son want to be a programmer, i will break his legs.
 * Created by zhangzemin on 16/3/3.
 */
public interface Commend {
    void onTouchEvent(List<CDrawable> mDrawableList, MotionEvent event);
}
