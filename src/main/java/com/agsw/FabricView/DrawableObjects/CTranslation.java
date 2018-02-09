package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.Vector;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 * Represents a translation towards a certain direction, which in turn is represented by a vector.
 */

public class CTranslation extends CTransform {
    private Vector<Integer> mDirection = new Vector<Integer>(2);

    /**
     * Constructor. You must call setDirection after calling this constructor.
     * @param drawable The object this translation affects.
     */
    public CTranslation(CDrawable drawable) {
        setDrawable(drawable);
    }

    /**
     * Constructor.
     * @param drawable The object this translation affects.
     * @param direction The direction of the translation. Two dimentional vector (x, y).
     */
    public CTranslation(CDrawable drawable, Vector<Integer> direction) {
        setDrawable(drawable);
        mDirection = direction;
    }

    /**
     * @return The direction of the translation. Two dimentional vector (x, y).
     */
    public Vector<Integer> getDirection() {
        return mDirection;
    }

    /**
     * Setter for the translation direction
     * @param direction The new direction of the translation. Two dimentional vector (x, y).
     */
    public void setDirection(Vector<Integer> direction) {
        mDirection = direction;
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
//        temp.translate(mDirection.get(0), mDirection.get(1));
//        return temp;
//    }

    @Override
    public void applyTransform(Matrix m) {
        m.postTranslate(mDirection.get(0), mDirection.get(1));
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if (!(obj instanceof CTranslation)) {
            return false;
        }
        CTranslation other = (CTranslation) obj;
        if(other.getDrawable() == null && this.getDrawable() == null) {
            return true;
        }
        if(!getDrawable().equals(other.getDrawable())) {
            return false;
        }
        return other.mDirection == this.mDirection;
    }

}
