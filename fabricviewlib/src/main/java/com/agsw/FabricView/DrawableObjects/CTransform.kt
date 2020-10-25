package com.agsw.FabricView.DrawableObjects

import android.graphics.Matrix

/**
 * Created by emmanuel.proulx on 2017-08-27.
 * This is the base class for all transforms.
 */
abstract class CTransform : CDrawable() {
    /**
     * @return The drawable object that this transform affects.
     */
    /**
     * Setter for the drawable.
     * @param drawable The new drawable object that this transform affects.
     */
    var drawable: CDrawable? = null

    /**
     * This method will use the provided matrix to transform the drawable.
     * @param matrix The matrix to use for the transform.
     */
    abstract fun applyTransform(matrix: Matrix)
}