package com.agsw.FabricView.DrawableObjects

import android.graphics.*
import java.util.*

/**
 * Created by antwan on 10/3/2015.
 * This is the base class for all visible objects and transforms. It is at the top of the whole
 * hierarchy for this library.
 * CDrawables exist on a stack with the newer ones at the top. They can represent either visible
 * objects or transforms of these objects. A transforms is stored the same as a visible object
 * because they can both be undone. In other terms, the stack of CDrawables is the basis of the
 * undo() and redo() methods.
 */
abstract class CDrawable {
    /**
     * @return The incremental ID for this object. Is unique only in the current execution and not
     * globally.
     */
    var id: Int
        private set

    /**
     * @return The stack of all transforms attached to this object.
     */
    val transforms: List<CTransform>
        get() = mTransforms

    companion object {
        private var nextId = 0
        private fun generateNextId(): Int {
            return nextId++
        }
    }

    /**
     * @return The current "x" position of this object.
     */
    /**
     * Setter for the "x" (horizontal from the left) position.
     * @param x The new "x" position of this object.
     */
    var xcoords = 0
    /**
     * @return The current "y" position of this object.
     */
    /**
     * Setter for the "y" (vertical from the top) position.
     * @param y The new "y" position of this object.
     */
    var ycoords = 0
    /**
     * @return The current height of this object.
     */
    /**
     * Setter for the height.
     * @param height The new height of this object.
     */
    var height = 0
    /**
     * @return The current width of this object.
     */
    /**
     * Setter for the width.
     * @param width The new width of this object.
     */
    var width = 0
    /**
     * @return The paint used for drawing this object.
     */
    /**
     * Setter for the Paint.
     * @param p The new Paint for drawing this object.
     */
    open var paint: Paint? = null
    private val mTransforms: MutableList<CTransform> = ArrayList()

    /**
     * If you call this constructor, you MUST call setXCoord(), setYCoord(), setHeight(), setWidth()
     * and setPaint().
     */
    constructor() {
        id = generateNextId()
    }

    /**
     * If you call this constructor, you HAVE to call setHeight() and setWidth().
     *
     * @param x     The X coordinate where the object will be drawn.
     * @param y     The Y coordinate where the object will be drawn.
     * @param paint The style and color to paint this object.
     */
    constructor(x: Int, y: Int, paint: Paint?) {
        id = generateNextId()
        xcoords = x
        ycoords = y
        this.paint = paint
    }

    /**
     * This function is used to draw the current object on the canvas. Subclasses must implement it.
     * @param canvas The canvas to draw on.
     */
    abstract fun draw(canvas: Canvas)

    /**
     * Calculates the bounds of this object. Takes into consideration all the transforms attached
     * to it.
     * @return The position of this object on the canvas.
     */
    fun computeBounds(): Rect {
        val bounds = RectF(
            xcoords.toFloat(),
            ycoords.toFloat(),
            (xcoords + width).toFloat(),
            (ycoords + height).toFloat()
        )
        val m = Matrix()
        for (t in mTransforms) {
            t.applyTransform(m)
        }
        m.mapRect(bounds)
        val result = Rect()
        bounds.round(result)
        return result
    }

    /**
     * @return true if this object has transforms attached to it.
     */
    fun hasTransforms(): Boolean {
        return mTransforms.isNotEmpty()
    }

    /**
     * Cancels a transform. Be careful as all other transforms are still on the stack. This
     * can produce weird results if you don't start from the top of the stack.
     * @param transform The transform to cancel.
     */
    fun removeTransform(transform: CTransform) {
        mTransforms.remove(transform)
    }

    /**
     * Adds a transform to this object, at the top of the stack.
     * @param transform The transform to add.
     */
    fun addTransform(transform: CTransform) {
        mTransforms.add(transform)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (obj !is CDrawable) {
            return false
        }
        return obj.id == id && obj.xcoords == xcoords && obj.xcoords == xcoords && obj.ycoords == ycoords && obj.height == height && obj.width == width && obj.paint === paint
    }
}