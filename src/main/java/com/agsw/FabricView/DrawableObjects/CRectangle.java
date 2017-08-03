package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.List;

/**
 * if my son want to be a programmer, i will break his legs.
 * Created by zhangzemin on 16/3/3.
 */
public class CRectangle implements CDrawable {
    private Paint paint;
    private float left, top, right, bottom;
    private int mRotDegree;

    public CRectangle(Paint paint) {
        this.paint = new Paint(paint);
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public int getXcoords() {
        return (int) left;
    }

    @Override
    public int getYcoords() {
        return (int) top;
    }

    @Override
    public void setXcoords(int x) {
        left = x;
    }

    @Override
    public void setYcoords(int y) {
        top = y;
    }

    @Override
    public void setPaint(Paint p) {
        paint = p;
    }

    @Override
    public void draw(Canvas canvas) {
        Log.d("tony", String.format("left : %s ,top : %s , right : %s , bottom : %s ,", left, top, right, bottom));
        canvas.drawRect(left, top, right, bottom, paint);
    }

    @Override
    public int getRotation() {
        return mRotDegree;
    }

    @Override
    public void setRotation(int degree) {
        mRotDegree = degree;
    }


    public void setRectangleCoords(float l, float t, float r, float b) {
        left = l;
        top = t;
        right = r;
        bottom = b;
    }
}
