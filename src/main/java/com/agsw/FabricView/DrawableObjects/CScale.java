package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CScale extends CTransform {
    public static final int MINIMUM_SIZE = 10;
    private float mFactor = 0.0f;

    /**
     * Constructor.
     * You must call setDirection after calling this constructor.
     * @param drawable The object this scaling affects.
     * @param x The center of scale.
     * @param y The center of scale.
     */
    public CScale(CDrawable drawable, int x, int y) {
        this(drawable, 1, x, y);
    }

    /**
     * Constructor.
     * @param drawable The object this scaling affects.
     * @param factor The scaling amount. Set to number between 0 and 1 to shrink, or above 1 to grow.
     * @param x The center of scale.
     * @param y The center of scale.
     */
    public CScale(CDrawable drawable, float factor, int x, int y) {
        setDrawable(drawable);
        mFactor = factor;
        setXcoords(x);
        setYcoords(y);
    }

    /**
     * @return The scaling factor.
     */
    public float getFactor() {
        return mFactor;
    }

    /**
     * Setter for the scaling factor.
     * @param factor The new scaling factor. Set to number between 0 and 1 to shrink, or above 1 to grow.
     */
    public void setFactor(float factor, float maxSize) {
        Rect rect = getDrawable().getLastBounds();

        float oldSize = Math.min(rect.width(), rect.height());
        float newSize = oldSize * factor;
        if(newSize < MINIMUM_SIZE) {
            mFactor = MINIMUM_SIZE/oldSize;
        }
        else if (newSize > maxSize) {
            mFactor = maxSize / oldSize;
        }
        else {
            mFactor = factor;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        throw new UnsupportedOperationException("Don't call draw() directly on this class.");
    }

//    @Override
//    public Canvas applyTransform(Canvas canvas) {
//        Bitmap bitmap = Bitmap.createBitmap(getDrawable().getWidth(), getDrawable().getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas temp = new Canvas(bitmap);
//        getDrawable().draw(temp);
//        temp.scale(mFactor, mFactor);
//        return temp;
//    }

    @Override
    public void applyTransform(Matrix m) {
        if(mFactor == 1) {
            //No scaling
            return;
        }

        m.postScale(mFactor, mFactor);
        float x = (getXcoords()*mFactor)/2;
        float y = (getYcoords()*mFactor)/2;
        m.postTranslate(-x, -y);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if (!(obj instanceof CScale)) {
            return false;
        }
        CScale other = (CScale) obj;
        if(other.getDrawable() == null && this.getDrawable() == null) {
            return true;
        }
        if(!getDrawable().equals(other.getDrawable())) {
            return false;
        }
        return other.mFactor == this.mFactor;
    }

}
