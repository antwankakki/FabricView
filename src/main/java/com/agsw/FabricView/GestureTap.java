package com.agsw.FabricView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


/**
 * Created by Yuvaraj Kumar Yadav K on 1/4/2018.
 */

public class GestureTap extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i("onDoubleTap :", "" + e.getAction());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i("onSingleTap :", "" + e.getAction());
        return true;
    }

}
