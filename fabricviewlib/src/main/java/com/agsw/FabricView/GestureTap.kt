package com.agsw.FabricView

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Created by Yuvaraj Kumar Yadav K on 1/4/2018.
 */
class GestureTap : GestureDetector.SimpleOnGestureListener() {
    override fun onDoubleTap(e: MotionEvent): Boolean {
        Log.i("onDoubleTap :", "" + e.action)
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        Log.i("onSingleTap :", "" + e.action)
        return true
    }
}