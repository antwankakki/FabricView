package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by antwan on 10/3/2015.
 */
public class CBitmap extends CDrawable {
    private Bitmap mBitmap;

    public CBitmap(Bitmap src, int x, int y) {
        this(src, x, y, null);
    }

    public CBitmap(Bitmap src, int x, int y, Paint p) {
        mBitmap = src;
        setHeight(mBitmap.getHeight());
        setWidth(mBitmap.getWidth());
        setXcoords(x);
        setYcoords(y);
        setPaint(p);
    }

    public CBitmap(Bitmap src, int x, int y, int height, int width) {
        this(src, x, y, height, width, null);
    }

    public CBitmap(Bitmap src, int x, int y, int height, int width, Paint p) {
        mBitmap = Bitmap.createScaledBitmap(mBitmap, height, width, true);
        setHeight(height);
        setWidth(width);
        setXcoords(x);
        setYcoords(y);
        setPaint(p);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, getXcoords(), getYcoords(), getPaint());
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CBitmap)) {
            return false;
        }
        CBitmap other = (CBitmap) obj;
        if(other.mBitmap == null && this.mBitmap == null) {
            return true;
        }
        return other.mBitmap.sameAs(this.mBitmap);
    }
}
