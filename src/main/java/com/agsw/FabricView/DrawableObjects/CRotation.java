package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CRotation extends CTransform {
    private final CDrawable mDrawable;
    private int mRotDegree;

    /**
     * You must call setRotation after calling this contructor.
     */
    public CRotation(CDrawable drawable) {
        mDrawable = drawable;
    }

    public CRotation(CDrawable drawable, int rotation) {
        mDrawable = drawable;
        mRotDegree = rotation;
    }

    public int getRotation() {
        return mRotDegree;
    }

    public void setRotation(int degree) {
        mRotDegree = degree;
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
        temp.rotate(-getRotation());
        return temp;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CRotation)) {
            return false;
        }
        CRotation other = (CRotation) obj;
        if(other.mDrawable == null && this.mDrawable == null) {
            return true;
        }
        if(!mDrawable.equals(other.mDrawable)) {
            return false;
        }
        return other.mRotDegree == this.mRotDegree;
    }

}
