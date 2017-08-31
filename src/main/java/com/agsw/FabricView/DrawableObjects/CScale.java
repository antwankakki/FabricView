package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CScale extends CTransform {
    private float mFactor = 0.0f;

    /**
     * You must call setDirection after calling this constructor.
     */
    public CScale(CDrawable drawable) {
        setDrawable(drawable);
    }

    public CScale(CDrawable drawable, float direction) {
        setDrawable(drawable);
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
        m.setScale(mFactor, mFactor);
    }

    @Override
    public boolean equals(Object obj) {

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
