package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by antwan on 10/3/2015.
 * This class represents a piece of text written on the canvas.
 */
public class CText extends CDrawable {
    private static final int MARGIN = 20;
    private String mText;

    /**
     * Constructor.
     * Make sure that the paint has a set text size by calling paint.setTextSize().
     * @param s The string to write.
     * @param x The horizontal position to put the text.
     * @param y The vertical position to put the text.
     * @param p The paint to use for the writing.
     */
    public CText(String s, int x, int y, Paint p) {
        setPaint(p); //Must be before setText()
        setText(s);
        setYcoords(y);
        setXcoords(x);
        calculateTextSizes();
    }

    private void calculateTextSizes() {
        Paint p = getPaint();
        Paint.FontMetrics metric = p.getFontMetrics();
        int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
        int y = (int)(textHeight - metric.descent);

        Rect bounds = new Rect();
        getPaint().getTextBounds(getText(), 0, getText().length(), bounds);
       // setYcoords(getYcoords() + y);
        setHeight(bounds.height() + (MARGIN*2));
        setWidth(bounds.width()+ (MARGIN*2));
    }

    /**
     * Setter for the text to write.
     * @param t The new text to write.
     */
    public void setText(String t) {
        mText = t;
        calculateTextSizes();
    }

    /**
     * @return The text to write.
     */
    public String getText() {
        return mText;
    }

    @Override
    public void setPaint(Paint p) {
        super.setPaint(p);
        if(getText() != null) {
            calculateTextSizes();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Paint p = getPaint();
        Paint.FontMetrics metric = p.getFontMetrics();
        int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
        int y = (int)(textHeight - metric.descent);

        Matrix matrix = new Matrix();
        for (CTransform t:
                getTransforms()) {
            t.applyTransform(matrix);
        }

        Bitmap canvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(canvasBitmap);
        temp.save();
        temp.concat(matrix);
        temp.drawText(getText(), (float) (getXcoords() + MARGIN), (float) (getYcoords() + y + MARGIN), p);
        temp.restore();

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

        if (!(obj instanceof CPath)) {
            return false;
        }
        CText other = (CText) obj;
        if(other.mText == null && this.mText == null) {
            return true;
        }
        return other.mText.equals(this.mText);
    }

}
