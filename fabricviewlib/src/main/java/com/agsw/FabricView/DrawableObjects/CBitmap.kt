package com.agsw.FabricView.DrawableObjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

/**
 * Created by antwan on 10/3/2015.
 * This drawable object represents a bitmap image.
 */
class CBitmap : CDrawable {
    /**
     * @return The bitmap encapsulated in this class.
     */
    var bitmap: Bitmap? = null
    /**
     * Constructor. Creates a bitmap object at the specified position. The width and the height are
     * obtained from the bitmap.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param p The paint to use.
     */
    /**
     * Constructor. Creates a bitmap object at the specified position.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     */
    @JvmOverloads
    constructor(src: Bitmap, x: Int, y: Int, p: Paint? = null) {
        bitmap = src
        height = bitmap?.height?:0
        width = bitmap?.width?:0
        xcoords = x
        ycoords = y
        paint = p
    }
    /**
     * Constructor. Creates a bitmap object at the specified position.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param height the height of this object.
     * @param width the width of this object.
     * @param p The paint to use.
     */
    /**
     * Constructor. Creates a bitmap object at the specified position.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param h the height of this object.
     * @param w the width of this object.
     */
    @JvmOverloads
    constructor(src: Bitmap, x: Int, y: Int, h: Int, w: Int, p: Paint? = null) {
        bitmap = bitmap?.let { Bitmap.createScaledBitmap(it, h, w, true) }
        height = h
        width = w
        xcoords = x
        ycoords = y
        paint = p
    }

    override fun draw(canvas: Canvas) {
        val matrix = Matrix()
        for (t in transforms) {
            t.applyTransform(matrix)
        }
        val canvasBitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val temp = Canvas(canvasBitmap)
        temp.save()
        temp.concat(matrix)
        bitmap?.let { temp.drawBitmap(it, xcoords.toFloat(), ycoords.toFloat(), paint) }
        temp.restore()
        //Bitmap transformedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(canvasBitmap, 0f, 0f, paint)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (!super.equals(other)) {
            return false
        }
        if (other !is CBitmap) {
            return false
        }
        return if (other.bitmap == null && bitmap == null) {
            true
        } else {
            other.bitmap?.sameAs(bitmap) == true
        }
    }

    override fun hashCode(): Int {
        return bitmap.hashCode()
    }
}