package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by antwan on 10/3/2015.
 * This is a series of continuous lines.
 * Note that the superclass' x, y, width, and height are irrelevant here so they are ignored.
 */
public class CPath extends CDrawable {
    private Path mPath;

    /**
     * Default constructor.
     */
    public CPath() {
        mPath = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        for (CTransform t:
             getTransforms()) {
            t.applyTransform(matrix);
        }
        Path copy = new Path(mPath);
        copy.transform(matrix);
        canvas.drawPath(copy, getPaint());
    }

    /**
     * Draws a line from the last line ending to the specified position.
     * @param x The horizontal position of the end of the line.
     * @param y The vertical position of the end of the line.
     */
    public void lineTo(float x, float y) {
        mPath.lineTo(x, y);
        calculatePosition();
    }

    /**
     * Draws a quadratic bezier line from the last line ending to the specified position.
     * @param x1 The x-coordinate of the control point on a quadratic curve
     * @param y1 The y-coordinate of the control point on a quadratic curve
     * @param x2 The x-coordinate of the end point on a quadratic curve
     * @param y2 The y-coordinate of the end point on a quadratic curve
     */
    public void quadTo(float x1, float y1, float x2, float y2) {
        mPath.quadTo(x1, y1, x2, y2);
        calculatePosition();
    }

    /**
     * When drawing a line, use this method first to specify the start position.
     * @param x The start position horizontally.
     * @param y The start position vertically.
     */
    public void moveTo(float x, float y) {
        mPath.moveTo(x, y);
        calculatePosition();
    }

    /**
     * @return The current Path object.
     */
    public Path getPath() {
        return mPath;
    }

    private void calculatePosition() {
        RectF bounds = new RectF();
        mPath.computeBounds(bounds, true);
        setXcoords((int)(bounds.left));
        setYcoords((int)(bounds.top));
        setHeight((int)(bounds.bottom-bounds.top));
        setWidth((int)(bounds.right-bounds.left));

        if(getHeight()==0) {
            setHeight(1);
        }
        if(getWidth()==0) {
            setWidth(1);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CPath)) {
            return false;
        }
        CPath other = (CPath) obj;
        if(other.mPath == null && this.mPath == null) {
            return true;
        }
        return other.mPath == this.mPath;
    }

}

