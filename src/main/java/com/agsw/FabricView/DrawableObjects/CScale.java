package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CScale extends CTransform {
    private final CDrawable mDrawable;
    private float mFactor = 0.0f;

    /**
     * You must call setDirection after calling this constructor.
     */
    public CScale(CDrawable drawable) {
        mDrawable = drawable;
    }

    public CScale(CDrawable drawable, float direction) {
        mDrawable = drawable;
        mFactor = direction;
    }


    public float getFactor() {
        return mFactor;
    }

    public void setDirection(float factor) {
        mFactor = factor;
    }

    @Override
    public void draw(Canvas canvas) {
        throw new UnsupportedOperationException("Don't call draw() directly on this class.");
    }

    @Override
    public Canvas applyTransform(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(mDrawable.getWidth(), mDrawable.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);
        mDrawable.draw(temp);
        temp.scale(mFactor, mFactor);
        return temp;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CScale)) {
            return false;
        }
        CScale other = (CScale) obj;
        if(other.mDrawable == null && this.mDrawable == null) {
            return true;
        }
        if(!mDrawable.equals(other.mDrawable)) {
            return false;
        }
        return other.mFactor == this.mFactor;
    }

}
