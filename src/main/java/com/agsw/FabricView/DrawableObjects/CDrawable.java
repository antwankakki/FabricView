package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antwan on 10/3/2015.
 */
public abstract class CDrawable {
    private int id;
    private static int nextId = 0;

    private int x, y, height, width;
    private Paint mPaint;
    private List<CTransform> mTransforms = new ArrayList<>();

    /**
     * If you call this constructor, you MUST call setXCoord(), setYCoord(), setHeight(), setWidth()
     * and setPaint().
     */
    public CDrawable() {
        id = generateNextId();
    }

    /**
     * If you call this constructor, you HAVE to call setHeight() and setWidth().
     *
     * @param x     The X coordinate where the object will be drawn.
     * @param y     The Y coordinate where the object will be drawn.
     * @param paint The color to paint this object.
     */
    public CDrawable(int x, int y, Paint paint) {
        id = generateNextId();
        this.x = x;
        this.y = y;
        this.mPaint = paint;
    }

    private static int generateNextId() {
        return nextId++;
    }

    public int getId() {
        return id;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint p) {
        mPaint = p;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getXcoords() {
        return x;
    }

    public int getYcoords() {
        return y;
    }

    public void setXcoords(int x) {
        this.x = x;
    }

    public void setYcoords(int y) {
        this.y = y;
    }

    public abstract void draw(Canvas canvas);

    public void applyTransforms(Canvas base) {
        Bitmap bitmap = Bitmap.createBitmap(base.getWidth(), base.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);
        for (CTransform t :
                mTransforms) {
            temp = t.applyTransform(temp);
        }
        base.drawBitmap(bitmap, getXcoords(), getYcoords(), getPaint());
    }

    public boolean hasTransforms() {
        return !mTransforms.isEmpty();
    }

    public void removeTransform(CTransform transform) {
        mTransforms.remove(transform);
    }

    public void addTransform(CTransform transform) {
        mTransforms.add(transform);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof CDrawable)) {
            return false;
        }
        CDrawable other = (CDrawable) obj;
        return other.getId() == this.getId() &&
                other.getXcoords() == this.getXcoords() &&
                other.getXcoords() == this.getXcoords() &&
                other.getYcoords() == this.getYcoords() &&
                other.getHeight() == this.getHeight() &&
                other.getWidth() == this.getWidth() &&
                other.getPaint() == this.getPaint();
    }
}
