package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by antwan on 10/3/2015.
 */
public interface CDrawable {
    Paint getPaint();

    int getXcoords();

    int getYcoords();

    void setXcoords(int x);

    void setYcoords(int y);

    void setPaint(Paint p);

    void draw(Canvas canvas);

    int getRotation();

    void setRotation(int degree);
}
