package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 * This is the base class for all transforms.
 */

public abstract class CTransform extends CDrawable {

    private CDrawable mDrawable;

    /**
     * @return The drawable object that this transform affects.
     */
    public CDrawable getDrawable() {
        return mDrawable;
    }

    /**
     * Setter for the drawable.
     * @param drawable The new drawable object that this transform affects.
     */
    public void setDrawable(CDrawable drawable) {
        this.mDrawable = drawable;
    }

    /**
     * This method will use the provided matrix to transform the drawable.
     * @param matrix The matrix to use for the transform.
     */
    public abstract void applyTransform(Matrix matrix);
}
