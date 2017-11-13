package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 * This class represents a rotation transform.
 */

public class CRotation extends CTransform {
    private int mRotDegree;

    /**
     * Constructor. You must call setRotation after calling this contructor.
     * @param drawable The object this rotation affects.
     */
    public CRotation(CDrawable drawable) {
        setDrawable(drawable);
    }

    /**
     * Constructor.
     * @param drawable The object this rotation affects.
     * @param rotation The number of degrees for this rotation.
     */
    public CRotation(CDrawable drawable, int rotation) {
        setDrawable(drawable);
        mRotDegree = rotation;
    }

    /**
     * @return The number of degrees for this rotation.
     */
    public int getRotation() {
        return mRotDegree;
    }

    /**
     * Setter for this rotation, in degrees.
     * @param degree The number of degrees for this rotation.
     */
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
        m.postRotate(mRotDegree);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
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
