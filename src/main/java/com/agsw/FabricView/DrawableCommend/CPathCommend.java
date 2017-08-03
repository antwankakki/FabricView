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
        mStartPoint = startPoint;
        mEndPoint = endPoint;
        this.isStraightLine = isStraightLine;
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

                mPath = new CPath(new Paint(mPaint));
                mStartPoint.set((int) eventX, (int) eventY);
                mPath.moveTo(eventX, eventY);
                mDrawableList.add(mPath);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mEndPoint.set((int) eventX, (int) eventY);
                if (!isStraightLine) {
                    mPath.lineTo(eventX, eventY);
                } else {
                    mPath.reset();
                    mPath.moveTo(mStartPoint.x, mStartPoint.y);
                    mPath.lineTo(eventX, eventY);
                }

                break;
        }
    }
}
