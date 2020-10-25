package com.agsw.FabricView.DrawableObjects

import android.graphics.Canvas
import android.graphics.Matrix

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */
class CScale : CTransform {
    /**
     * @return The scaling factor.
     */
    /**
     * Setter for the scaling factor.
     * @param factor The new scaling factor. Set to number between 0 and 1 to shrink, or above 1 to grow.
     */
    var factor = 0.0f

    /**
     * Constructor.
     * You must call setDirection after calling this constructor.
     * @param drawable The object this scaling affects.
     */
    constructor(drawable: CDrawable) {
        this.drawable = drawable
    }

    /**
     * Constructor.
     * @param drawable The object this scaling affects.
     * @param factor The scaling amount. Set to number between 0 and 1 to shrink, or above 1 to grow.
     */
    constructor(drawable: CDrawable, factor: Float) {
        this.drawable = drawable
        this.factor = factor
    }

    override fun draw(canvas: Canvas) {
        throw UnsupportedOperationException("Don't call draw() directly on this class.")
    }

    //    @Override
    //    public Canvas applyTransform(Canvas canvas) {
    //        Bitmap bitmap = Bitmap.createBitmap(getDrawable().getWidth(), getDrawable().getHeight(), Bitmap.Config.ARGB_8888);
    //        Canvas temp = new Canvas(bitmap);
    //        getDrawable().draw(temp);
    //        temp.scale(mFactor, mFactor);
    //        return temp;
    //    }
    override fun applyTransform(matrix: Matrix) {
        matrix.postScale(factor, factor)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj !is CScale) {
            return false
        }
        if (obj.drawable == null && drawable == null) {
            return true
        }
        return if (drawable != obj.drawable) {
            false
        } else obj.factor == factor
    }

    override fun hashCode(): Int {
        return factor.hashCode()
    }
}