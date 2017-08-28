package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by antwan on 10/3/2015.
 */
public class CText extends CDrawable {
    private String mText;

    /**
     * Make sure that the paint has a set text size by calling paint.setTextSize().
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

    public void setText(String t) {
        mText = t;
        calculateTextSizes();
    }

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
