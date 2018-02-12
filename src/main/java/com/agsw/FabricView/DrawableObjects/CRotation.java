package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
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
     * @param x The center of rotation.
     * @param y The center of rotation.
     */
    public CRotation(CDrawable drawable, int x, int y) {
        this(drawable, 0, x, y);
    }

    /**
     * Constructor.
     * @param drawable The object this rotation affects.
     * @param rotation The number of degrees for this rotation.
     * @param x The center of rotation.
     * @param y The center of rotation.
     */
    public CRotation(CDrawable drawable, int rotation, int x, int y) {
        setDrawable(drawable);
        mRotDegree = rotation;
        setXcoords(x);
        setYcoords(y);
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
        if(mRotDegree == 0) {
            //No rotation.
            return;
        }
        m.postRotate(mRotDegree, getXcoords(),
                getYcoords());

    }

    private boolean between(float value, float low, float high) {
        return value >= low && value < high;
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
