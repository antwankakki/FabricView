package com.agsw.FabricView.DrawableCommend;

import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import com.agsw.FabricView.DrawableObjects.CDrawable;
import com.agsw.FabricView.DrawableObjects.CRectangle;

import java.util.List;


/**
 * if my son want to be a programmer, i will break his legs.
 * Created by zhangzemin on 16/3/3.
 */
public class CRectangleCommend implements Commend {
    private CRectangle rectangle;
    private float startX, startY, lastTouchX, lastTouchY;
    private float left, top, right, bottom;
    private Paint mPaint;
    private Point mStartPoint, mEndPoint;

    public CRectangleCommend(Paint paint, Point startPoint, Point endPoint) {
        mPaint = paint;
        mStartPoint = startPoint;
        mEndPoint = endPoint;
    }


    @Override
    public void onTouchEvent(List<CDrawable> mDrawableList, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.startX = event.getX();
                this.startY = event.getY();
                mStartPoint.set((int) startX, (int) startY);
                rectangle = new CRectangle(mPaint);
                mDrawableList.add(rectangle);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                if (startX > lastTouchX) {
                    left = lastTouchX;
                    right = startX;
                } else {
                    left = startX;
                    right = lastTouchX;
                }

                if (startY > lastTouchY) {
                    bottom = startY;
                    top = lastTouchY;
                } else {
                    top = startY;
                    bottom = lastTouchY;
                }
                mEndPoint.set((int) lastTouchX, (int) lastTouchY);
                rectangle.setRectangleCoords(left, top, right, bottom);
                break;
        }
    }
}
