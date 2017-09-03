package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by antwan on 10/3/2015.
 */
public class CPath extends CDrawable {
    private Path mPath;

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

    public void lineTo(float eventX, float eventY) {
        mPath.lineTo(eventX, eventY);
        calculatePosition();
    }
    public void quadTo(float x1, float y1, float x2, float y2) {
        mPath.quadTo(x1, y1, x2, y2);
        calculatePosition();
    }

    public void moveTo(float eventX, float eventY) {
        mPath.moveTo(eventX, eventY);
        calculatePosition();
    }

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

