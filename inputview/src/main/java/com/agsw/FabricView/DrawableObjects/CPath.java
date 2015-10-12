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

    public CPath()
    {
        mPath = new Path();
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
        this.x=x;
    }

    @Override
    public void setYcoords(int y) {
        this.y=y;
    }

    @Override
    public void setPaint(Paint p) {
        mPaint = p;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    public void lineTo(float eventX, float eventY) {
        mPath.lineTo(eventX, eventY);
    }

    public void moveTo(float eventX, float eventY) {
        mPath.moveTo(eventX,eventY);
    }
}

