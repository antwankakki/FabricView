package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.Vector;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public class CTranslation extends CTransform {
    private Vector<Integer> mDirection = new Vector<Integer>(2);

    /**
     * You must call setDirection after calling this constructor.
     */
    public CTranslation(CDrawable drawable) {
        setDrawable(drawable);
    }

    public CTranslation(CDrawable drawable, Vector<Integer> direction) {
        setDrawable(drawable);
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
        m.setTranslate(mDirection.get(0), mDirection.get(1));
    }

    @Override
    public boolean equals(Object obj) {

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
