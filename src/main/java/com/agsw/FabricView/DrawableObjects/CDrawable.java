package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by antwan on 10/3/2015.
 * This is the base class for all visible objects and transforms. It is at the top of the whole
 * hierarchy for this library.
 * CDrawables exist on a stack with the newer ones at the top. They can represent either visible
 * objects or transforms of these objects. A transforms is stored the same as a visible object
 * because they can both be undone. In other terms, the stack of CDrawables is the basis of the
 * undo() and redo() methods.
 */
public abstract class CDrawable {
    private int id;
    private static int nextId = 0;

    private int x, y, height, width;
    private Paint mPaint;
    private List<CTransform> mTransforms = new ArrayList<>();
    private Rect lastBounds = null;

    /**
     * If you call this constructor, you MUST call setXCoord(), setYCoord(), setHeight(), setWidth()
     * and setPaint().
     */
    public CDrawable() {
        id = generateNextId();
    }

    /**
     * If you call this constructor, you HAVE to call setHeight() and setWidth().
     *
     * @param x     The X coordinate where the object will be drawn.
     * @param y     The Y coordinate where the object will be drawn.
     * @param paint The style and color to paint this object.
     */
    public CDrawable(int x, int y, Paint paint) {
        id = generateNextId();
        this.x = x;
        this.y = y;
        this.mPaint = paint;
    }

    private static int generateNextId() {
        return nextId++;
    }

    /**
     * @return The incremental ID for this object. Is unique only in the current execution and not
     * globally.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The paint used for drawing this object.
     */
    public Paint getPaint() {
        return mPaint;
    }

    /**
     * Setter for the Paint.
     * @param p The new Paint for drawing this object.
     */
    public void setPaint(Paint p) {
        mPaint = p;
    }

    /**
     * Setter for the height.
     * @param height The new height of this object.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return The current height of this object.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Setter for the width.
     * @param width The new width of this object.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return The current width of this object.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The current "x" position of this object.
     */
    public int getXcoords() {
        return x;
    }

    /**
     * @return The current "y" position of this object.
     */
    public int getYcoords() {
        return y;
    }

    /**
     * Setter for the "x" (horizontal from the left) position.
     * @param x The new "x" position of this object.
     */
    public void setXcoords(int x) {
        this.x = x;
    }

    /**
     * Setter for the "y" (vertical from the top) position.
     * @param y The new "y" position of this object.
     */
    public void setYcoords(int y) {
        this.y = y;
    }

    /**
     * This function is used to draw the current object on the canvas. Subclasses must implement it.
     * @param canvas The canvas to draw on.
     */
    public abstract void draw(Canvas canvas);

    /**
     * Calculates the bounds of this object. Takes into consideration all the transforms attached
     * to it.
     * @return The position of this object on the canvas.
     */
    public Rect computeBounds() {
        RectF bounds = new RectF(x, y, x+width, y+height);
        Matrix m = new Matrix();
        for (CTransform t :
                mTransforms) {
            t.applyTransform(m);
        }
        m.mapRect(bounds);
        lastBounds = new Rect();
        bounds.round(lastBounds);
        return lastBounds;
    }

    public Rect getLastBounds() {
        if(lastBounds == null) {
            int x = getXcoords();
            int y = getYcoords();
            int r = x + getWidth();
            int b = y + getHeight();
            lastBounds = new Rect(x, y, r, b);
        }
        return lastBounds;
    }

    /**
     * @return true if this object has transforms attached to it.
     */
    public boolean hasTransforms() {
        return !mTransforms.isEmpty();
    }

    /**
     * Cancels a transform. Be careful as all other transforms are still on the stack. This
     * can produce weird results if you don't start from the top of the stack.
     * @param transform The transform to cancel.
     */
    public void removeTransform(CTransform transform) {
        mTransforms.remove(transform);
    }

    /**
     * Adds a transform to this object, at the top of the stack.
     * @param transform The transform to add.
     */
    public void addTransform(CTransform transform) {
        mTransforms.add(transform);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof CDrawable)) {
            return false;
        }
        CDrawable other = (CDrawable) obj;
        return other.getId() == this.getId() &&
                other.getXcoords() == this.getXcoords() &&
                other.getXcoords() == this.getXcoords() &&
                other.getYcoords() == this.getYcoords() &&
                other.getHeight() == this.getHeight() &&
                other.getWidth() == this.getWidth() &&
                other.getPaint() == this.getPaint();
    }

    /**
     * @return The stack of all transforms attached to this object.
     */
    public List<CTransform> getTransforms() {
        return mTransforms;
    }
}
