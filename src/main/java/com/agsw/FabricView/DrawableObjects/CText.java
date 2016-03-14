package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by antwan on 10/3/2015.
 */
public class CText implements CDrawable {
    private String mText;
    private Paint mPaint;
    private int x, y, mRotDegree;

    public CText(String s, int x, int y, Paint p) {
        setText(s);
        setYcoords(y);
        setXcoords(x);
        setPaint(p);
    }

    public void setText(String t) {
        mText = t;
    }

    public String getText() {
        return mText;
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
        canvas.drawText(getText(), (float) getXcoords(), (float) getYcoords(), mPaint);
    }

    @Override
    public int getRotation() {
        return mRotDegree;
    }

    @Override
    public void setRotation(int degree) {
        mRotDegree = degree;
    }
}
