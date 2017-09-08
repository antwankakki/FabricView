package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by antwan on 10/3/2015.
 * This class represents a piece of text written on the canvas.
 */
public class CText extends CDrawable {
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
        setText(s);
        setYcoords(y);
        setXcoords(x);
        setPaint(p);
        calculateTextSizes();
    }

    private void calculateTextSizes() {
        Rect bounds = new Rect();
        getPaint().getTextBounds(getText(), 0, getText().length(), bounds);
        setHeight(bounds.height());
        setWidth(bounds.width());
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
        calculateTextSizes();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(getText(), (float) getXcoords(), (float) getYcoords(), getPaint());
    }

    @Override
    public boolean equals(Object obj) {
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
