package com.agsw.FabricView.DrawableObjects

import android.graphics.Canvas
import android.graphics.Matrix

/**
 * Created by emmanuel.proulx on 2017-08-27.
 * This class represents a rotation transform.
 */
class CRotation : CTransform {
    /**
     * @return The number of degrees for this rotation.
     */
    /**
     * Setter for this rotation, in degrees.
     * @param degree The number of degrees for this rotation.
     */
    var rotation = 0

    /**
     * Constructor. You must call setRotation after calling this contructor.
     * @param drawable The object this rotation affects.
     */
    constructor(drawable: CDrawable) {
        this.drawable = drawable
    }

    /**
     * Constructor.
     * @param drawable The object this rotation affects.
     * @param rotation The number of degrees for this rotation.
     */
    constructor(drawable: CDrawable, rotation: Int) {
        this.drawable = drawable
        this.rotation = rotation
    }

    override fun draw(canvas: Canvas) {
        throw UnsupportedOperationException("Don't call draw() directly on this class.")
    }

    //    @Override
    //    public Canvas applyTransform(Canvas canvas) {
    //        Bitmap bitmap = Bitmap.createBitmap(getDrawable().getWidth(), getDrawable().getHeight(), Bitmap.Config.ARGB_8888);
    //        Canvas temp = new Canvas(bitmap);
    //        getDrawable().draw(temp);
    //        temp.rotate(-getRotation());
    //        return temp;
    //    }
    override fun applyTransform(matrix: Matrix) {
        matrix.postRotate(rotation.toFloat())
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj !is CRotation) {
            return false
        }
        if (obj.drawable == null && drawable == null) {
            return true
        }
        return if (drawable != obj.drawable) {
            false
        } else obj.rotation == rotation
    }

    override fun hashCode(): Int {
        return rotation
    }
}