package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by antwan on 10/3/2015.
 * This drawable object represents a bitmap image.
 */
public class CBitmap extends CDrawable {
    private Bitmap mBitmap;

    /**
     * Constructor. Creates a bitmap object at the specified position.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     */
    public CBitmap(Bitmap src, int x, int y) {
        this(src, x, y, null);
    }

    /**
     * Constructor. Creates a bitmap object at the specified position. The width and the height are
     * obtained from the bitmap.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param p The paint to use.
     */
    public CBitmap(Bitmap src, int x, int y, Paint p) {
        mBitmap = src;
        setHeight(mBitmap.getHeight());
        setWidth(mBitmap.getWidth());
        setXcoords(x);
        setYcoords(y);
        setPaint(p);
    }

    /**
     * Constructor. Creates a bitmap object at the specified position.
     * @param src The source bitmap.
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param height the height of this object.
     * @param width the width of this object.
     */
    public CBitmap(Bitmap src, int x, int y, int height, int width) {
        this(src, x, y, height, width, null);
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
    public CBitmap(Bitmap src, int x, int y, int height, int width, Paint p) {
        mBitmap = Bitmap.createScaledBitmap(mBitmap, height, width, true);
        setHeight(height);
        setWidth(width);
        setXcoords(x);
        setYcoords(y);
        setPaint(p);
    }

    /**
     * @return The bitmap encapsulated in this class.
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        for (CTransform t:
                getTransforms()) {
            t.applyTransform(matrix);
        }
        Bitmap canvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(canvasBitmap);
        temp.save();
        temp.concat(matrix);
        temp.drawBitmap(mBitmap, getXcoords(), getYcoords(), getPaint());
        temp.restore();
//        Bitmap transformedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        canvas.drawBitmap(canvasBitmap, 0, 0, getPaint());
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CBitmap)) {
            return false;
        }
        CBitmap other = (CBitmap) obj;
        if(other.mBitmap == null && this.mBitmap == null) {
            return true;
        }
        return other.mBitmap.sameAs(this.mBitmap);
    }
}
