package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CRotation extends CTransform {
    private int mRotDegree;

    /**
     * You must call setRotation after calling this contructor.
     */
    public CRotation(CDrawable drawable) {
        setDrawable(drawable);
    }

    public CRotation(CDrawable drawable, int rotation) {
        setDrawable(drawable);
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

//    @Override
//    public Canvas applyTransform(Canvas canvas) {
//        Bitmap bitmap = Bitmap.createBitmap(getDrawable().getWidth(), getDrawable().getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas temp = new Canvas(bitmap);
//        getDrawable().draw(temp);
//        temp.rotate(-getRotation());
//        return temp;
//    }

    @Override
    public void applyTransform(Matrix m) {
        m.setRotate(mRotDegree);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CRotation)) {
            return false;
        }
        CRotation other = (CRotation) obj;
        if(other.getDrawable() == null && this.getDrawable() == null) {
            return true;
        }
        if(!getDrawable().equals(other.getDrawable())) {
            return false;
        }
        return other.mRotDegree == this.mRotDegree;
    }

}
