package com.agsw.FabricView.DrawableObjects

import android.graphics.Canvas
import android.graphics.Matrix
import java.util.*

/**
 * Created by emmanuel.proulx on 2017-08-27.
 * Represents a translation towards a certain direction, which in turn is represented by a vector.
 */
class CTranslation : CTransform {
    /**
     * @return The direction of the translation. Two dimentional vector (x, y).
     */
    /**
     * Setter for the translation direction
     * @param direction The new direction of the translation. Two dimentional vector (x, y).
     */
    var direction = Vector<Int>(2)

    /**
     * Constructor. You must call setDirection after calling this constructor.
     * @param drawable The object this translation affects.
     */
    constructor(drawable: CDrawable) {
        this.drawable = drawable
    }

    /**
     * Constructor. You must call setDirection after calling this constructor.
     * @param drawable The object this translation affects.
     * @param direction The direction of the translation. Two dimentional vector (x, y).
     */
    constructor(drawable: CDrawable, direction: Vector<Int>) {
        this.drawable = drawable
        this.direction = direction
    }

    override fun draw(canvas: Canvas) {
        throw UnsupportedOperationException("Don't call draw() directly on this class.")
    }

    //    @Override
    //    public Canvas applyTransform(Canvas canvas) {
    //        Bitmap bitmap = Bitmap.createBitmap(getDrawable().getWidth(), getDrawable().getHeight(), Bitmap.Config.ARGB_8888);
    //        Canvas temp = new Canvas(bitmap);
    //        getDrawable().draw(temp);
    //        temp.translate(mDirection.get(0), mDirection.get(1));
    //        return temp;
    //    }
    override fun applyTransform(matrix: Matrix) {
        matrix.postTranslate(direction[0].toFloat(), direction[1].toFloat())
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj !is CTranslation) {
            return false
        }
        if (obj.drawable == null && drawable == null) {
            return true
        }
        return if (drawable != obj.drawable) {
            false
        } else obj.direction === direction
    }

    override fun hashCode(): Int {
        return direction.hashCode()
    }
}