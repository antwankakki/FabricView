package com.agsw.FabricView.DrawableCommend;

import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import com.agsw.FabricView.DrawableObjects.CDrawable;
import com.agsw.FabricView.DrawableObjects.CPath;

import java.util.List;


/**
 * if my son want to be a programmer, i will break his legs.
 * Created by zhangzemin on 16/3/4.
 */
public class CPathCommend implements Commend {
    private CPath mPath;
    private Paint mPaint;
    private boolean isStraightLine = false;
    private Point mStartPoint, mEndPoint;


    public CPathCommend(Paint paint, boolean isStraightLine, Point startPoint, Point endPoint) {
        mPaint = paint;
        this.isStraightLine = isStraightLine;
        mStartPoint = startPoint;
        mEndPoint = endPoint;
    }

    @Override
    public void onTouchEvent(List<CDrawable> mDrawableList, MotionEvent event) {
        // get location of touch
        float eventX = event.getX();
        float eventY = event.getY();

        // based on the users action, start drawing
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // create new path and paint

                mPath = new CPath(mPaint);
                mStartPoint.set((int)eventX , (int)eventY);
                mPath.moveTo(eventX, eventY);
                mDrawableList.add(mPath);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (!isStraightLine)
                    mPath.lineTo(eventX, eventY);
                mEndPoint.set((int)eventX , (int)eventY);
                break;
        }
    }
}
