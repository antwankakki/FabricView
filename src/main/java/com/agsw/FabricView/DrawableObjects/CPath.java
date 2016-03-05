package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by antwan on 10/3/2015.
 */
public class CPath implements CDrawable {
    private int x = 0, y = 0, height, width;
    private Path mPath;
    private Paint mPaint;
    private int mRotDegree;

    public CPath() {
        mPath = new Path();
    }

    public CPath(Paint paint) {
        this();
        mPaint = paint;
    }

    @Override
    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public int getXcoords() {
        return x;
    }

    @Override
    public int getYcoords() {
        return y;
    }

    @Override
    public void setXcoords(int x) {
        this.x = x;
    }

    @Override
    public void setYcoords(int y) {
        this.y = y;
    }

    @Override
    public void setPaint(Paint p) {
        mPaint = p;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public int getRotation() {
        return mRotDegree;
    }

    @Override
    public void setRotation(int degree) {
        mRotDegree = degree;
    }

    public void lineTo(float eventX, float eventY) {
        mPath.lineTo(eventX, eventY);
    }

    public void moveTo(float eventX, float eventY) {
        mPath.moveTo(eventX, eventY);
    }

    public void reset() {
        mPath.reset();
    }
}

