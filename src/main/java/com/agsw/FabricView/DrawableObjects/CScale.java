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
     */
    public CScale(CDrawable drawable) {
        setDrawable(drawable);
    }

    /**
     * Constructor.
     * @param drawable The object this scaling affects.
     * @param factor The scaling amount. Set to number between 0 and 1 to shrink, or above 1 to grow.
     */
    public CScale(CDrawable drawable, float factor) {
        setDrawable(drawable);
        mFactor = factor;
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

        Rect lastBounds = getDrawable().getLastBounds();
        final float deltaSX = ((mFactor * lastBounds.width()) - lastBounds.width()) / 2f;
        final float deltaSY = ((mFactor * lastBounds.height()) - lastBounds.height()) / 2f;

        m.postScale(mFactor, mFactor);
//        m.postTranslate(-deltaSX, -deltaSY);

//        m.postTranslate(-getXcoords()/2, -getYcoords()/2);
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
