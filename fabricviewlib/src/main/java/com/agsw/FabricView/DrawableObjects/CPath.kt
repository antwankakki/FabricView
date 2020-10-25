package com.agsw.FabricView.DrawableObjects

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF

/**
 * Created by antwan on 10/3/2015.
 * This is a series of continuous lines.
 * Note that the superclass' x, y, width, and height are irrelevant here so they are ignored.
 */
class CPath : CDrawable() {
    /**
     * @return The current Path object.
     */
    var path: Path = Path()
    override fun draw(canvas: Canvas) {
        val matrix = Matrix()
        for (t in transforms) {
            t.applyTransform(matrix)
        }
        val copy = Path(path)
        copy.transform(matrix)
        paint?.let { canvas.drawPath(copy, it) }
    }

    /**
     * Draws a line from the last line ending to the specified position.
     * @param x The horizontal position of the end of the line.
     * @param y The vertical position of the end of the line.
     */
    fun lineTo(x: Float, y: Float) {
        path.lineTo(x, y)
        calculatePosition()
    }

    /**
     * Draws a quadratic bezier line from the last line ending to the specified position.
     * @param x1 The x-coordinate of the control point on a quadratic curve
     * @param y1 The y-coordinate of the control point on a quadratic curve
     * @param x2 The x-coordinate of the end point on a quadratic curve
     * @param y2 The y-coordinate of the end point on a quadratic curve
     */
    fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        path.quadTo(x1, y1, x2, y2)
        calculatePosition()
    }

    /**
     * When drawing a line, use this method first to specify the start position.
     * @param x The start position horizontally.
     * @param y The start position vertically.
     */
    fun moveTo(x: Float, y: Float) {
        path.moveTo(x, y)
        calculatePosition()
    }

    private fun calculatePosition() {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        xcoords = bounds.left.toInt()
        ycoords = bounds.top.toInt()
        height = (bounds.bottom - bounds.top).toInt()
        width = (bounds.right - bounds.left).toInt()
        if (height == 0) {
            height = 1
        }
        if (width == 0) {
            width = 1
        }
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (!super.equals(obj)) {
            return false
        }
        if (obj !is CPath) {
            return false
        }
        return obj.path === path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

}