package com.agsw.FabricView.DrawableObjects

import android.graphics.*
import kotlin.math.ceil

/**
 * Created by antwan on 10/3/2015.
 * This class represents a piece of text written on the canvas.
 */
class CText(
    /**
     * Setter for the text to write.
     * @param t The new text to write.
     */
    private var text: String, x: Int, y: Int, override var paint: Paint?
) : CDrawable() {

    companion object {
        private const val MARGIN = 20
    }

    /**
     * Constructor.
     * Make sure that the paint has a set text size by calling paint.setTextSize().
     * @param s The string to write.
     * @param x The horizontal position to put the text.
     * @param y The vertical position to put the text.
     * @param p The paint to use for the writing.
     */
    init {
        //Must be before setText()
        ycoords = y
        xcoords = x
        calculateTextSizes()
    }

    private var mText: String? = null
    private fun calculateTextSizes() {
        val p = paint
        val metric = p?.fontMetrics
        val descent = (metric?.descent?:0.0).toDouble()
        val ascent = (metric?.ascent?:0.0).toDouble()
        val x = descent - ascent
        val textHeight = ceil(x).toInt()
        val y = (textHeight - descent).toInt()
        val bounds = Rect()
        paint?.getTextBounds(text, 0, text.length, bounds)
        // setYcoords(getYcoords() + y);
        height = bounds.height() + MARGIN * 2
        width = bounds.width() + MARGIN * 2
    }

    /**
     * @return The text to write.
     */
    override fun draw(canvas: Canvas) {
        val p = paint
        val metric = p?.fontMetrics
        val descent = (metric?.descent?:0.0).toDouble()
        val ascent = (metric?.ascent?:0.0).toDouble()
        val x = descent - ascent
        val textHeight = ceil(x)
            .toInt()
        val y = (textHeight - descent).toInt()
        val matrix = Matrix()
        for (t in transforms) {
            t.applyTransform(matrix)
        }
        val canvasBitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val temp = Canvas(canvasBitmap)
        temp.save()
        temp.concat(matrix)
        if (p != null) {
            temp.drawText(text, (xcoords + MARGIN).toFloat(), (ycoords + y + MARGIN).toFloat(), p)
        }
        temp.restore()
        canvas.drawBitmap(canvasBitmap, 0f, 0f, paint)
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
        val other = obj as CText
        return if (other.mText == null && mText == null) {
            true
        } else other.mText == mText
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + (paint?.hashCode() ?: 0)
        result = 31 * result + (mText?.hashCode() ?: 0)
        return result
    }
}