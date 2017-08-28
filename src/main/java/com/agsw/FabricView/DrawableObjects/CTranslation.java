package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Vector;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CTranslation extends CTransform {
    private final CDrawable mDrawable;
    private Vector<Integer> mDirection = new Vector<Integer>(2);

    /**
     * You must call setDirection after calling this constructor.
     */
    public CTranslation(CDrawable drawable) {
        mDrawable = drawable;
    }

    public CTranslation(CDrawable drawable, Vector<Integer> direction) {
        mDrawable = drawable;
        mDirection = direction;
    }


    public Vector<Integer> getDirection() {
        return mDirection;
    }

    public void setDirection(Vector<Integer> direction) {
        mDirection = direction;
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
        temp.translate(mDirection.get(0), mDirection.get(1));
        return temp;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CTranslation)) {
            return false;
        }
        CTranslation other = (CTranslation) obj;
        if(other.mDrawable == null && this.mDrawable == null) {
            return true;
        }
        if(!mDrawable.equals(other.mDrawable)) {
            return false;
        }
        return other.mDirection == this.mDirection;
    }

}
