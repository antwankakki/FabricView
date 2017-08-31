package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public abstract class CTransform extends CDrawable {

    private CDrawable mDrawable;

    public CDrawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(CDrawable drawable) {
        this.mDrawable = drawable;
    }

    public abstract void applyTransform(Matrix matrix);
}
