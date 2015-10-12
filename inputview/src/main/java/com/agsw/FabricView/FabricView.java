package com.agsw.FabricView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.agsw.FabricView.DrawableObjects.CDrawable;
import com.agsw.FabricView.DrawableObjects.CPath;
import com.agsw.FabricView.DrawableObjects.CText;

import java.util.ArrayList;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by antwan on 10/3/2015.
 */
public class FabricView extends View {

    // painting objects and properties
    private ArrayList<CDrawable> mDrawableList = new ArrayList<CDrawable>();

    // each path will have its properties
    int id = 0;

    private int mColor = Color.BLACK;
    private int mBackgroundColor = Color.WHITE;
    private Paint.Style mStyle = Paint.Style.STROKE;
    private float mSize = 5f;
    private float mZoomLevel = 1.0f;
    private float mHorizontalOffset = 1, mVerticalOffset = 1;
    private boolean redrawBackground = true;
    private int mBackgroundMode = BACKGROUND_STYLE_NOTEBOOK_PAPER;
    public int NOTEBOOK_LEFT_LINE_COLOR = Color.RED;
    public int mAutoscrollDistance = 100;
    private boolean mTextExpectTouch;

    // Vars to decrease dirty area and increase performance
    private float lastTouchX, lastTouchY;
    private final RectF dirtyRect = new RectF();
    //flags
    public static final int BACKGROUND_STYLE_BLANK = 0;
    public static final int BACKGROUND_STYLE_NOTEBOOK_PAPER = 1;
    public static final int BACKGROUND_STYLE_GRAPH_PAPER = 2;

    //private const
    public static final int NOTEBOOK_LEFT_LINE_PADDING = 120;
    CPath currentPath;
    Paint currentPaint;

    /**
     * Default Constructor, sets sane values.
     * @param context the activity that containts the view
     * @param attrs view attributes
     */
    public FabricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setBackgroundColor(mBackgroundColor);
        mTextExpectTouch = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // check if background needs to be redrawn
        drawBackground(canvas, mBackgroundMode);

        // go through each item in the list and draw it
        for (int i = 0; i < mDrawableList.size(); i++) {
            mDrawableList.get(i).draw(canvas);
        }
    }

    /**
     * Handles user touch event
     * @param event the user's motion event
     * @return true, the event is consumed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get location of touch
        float eventX = event.getX();
        float eventY = event.getY();

        if (mTextExpectTouch)
        {
            drawTextFromKeyboard((int)eventX, (int)eventY);
            mTextExpectTouch = true;
            // comsume the event
            return true;
        }
        // based on the users action, start drawing
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // create new path and paint
                currentPath = new CPath();
                currentPaint = new Paint();
                currentPaint.setAntiAlias(true);
                currentPaint.setColor(mColor);
                currentPaint.setStyle(mStyle);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeWidth(mSize);
                currentPath.moveTo(eventX, eventY);
                currentPath.setPaint(currentPaint);
                // capture touched locations
                lastTouchX = eventX;
                lastTouchY = eventY;

                mDrawableList.add(currentPath);
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                currentPath.lineTo(eventX, eventY);
                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points.
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    if (historicalX < dirtyRect.left) {
                        dirtyRect.left = historicalX;
                    } else if (historicalX > dirtyRect.right) {
                        dirtyRect.right = historicalX;
                    }
                    if (historicalY < dirtyRect.top) {
                        dirtyRect.top = historicalY;
                    } else if (historicalY > dirtyRect.bottom) {
                        dirtyRect.bottom = historicalY;
                    }
                    currentPath.lineTo(historicalX, historicalY);
                }

                // After replaying history, connect the line to the touch point.
                currentPath.lineTo(eventX, eventY);
                cleanDirtyRegion(eventX, eventY);
                break;
            default:
                return false;
        }

        // let android know to repaint

        // Include some padding to ensure nothing is clipped
        invalidate(
                (int) (dirtyRect.left - 20),
                (int) (dirtyRect.top - 20),
                (int) (dirtyRect.right + 20),
                (int) (dirtyRect.bottom + 20));

        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }


    /*******************************************
     * Drawing Events
     ******************************************/
    public void drawBackground(Canvas canvas, int backgroundMode)
    {
        Paint linePaint = new Paint();
        linePaint.setColor(Color.argb(50, 0, 0, 0));
        linePaint.setStyle(mStyle);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeWidth(mSize - 2f);
        switch (backgroundMode)
        {
            case BACKGROUND_STYLE_GRAPH_PAPER : drawGraphPaperBackground(canvas, linePaint);
                break;
            case BACKGROUND_STYLE_NOTEBOOK_PAPER : drawNotebookPaperBackground(canvas, linePaint);
            default:
                break;
        }
        redrawBackground = false;
    }

    private void drawGraphPaperBackground (Canvas canvas, Paint paint)
    {
        int i = 0;
        boolean doneH = false, doneV = false;

        // while we still need to draw either H or V
        while (!(doneH && doneV))
        {

            // check if there is more H lines to draw
            if (i < canvas.getHeight())
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
            else
                doneH = true;

            // check if there is more V lines to draw
            if (i < canvas.getWidth())
                canvas.drawLine(i, 0, i, canvas.getHeight(), paint);
            else
                doneV = true;

            // declare as done
            i+=75;
        }
    }

    private void drawNotebookPaperBackground (Canvas canvas, Paint paint)
    {
        int i = 0;
        boolean doneV = false;
        // draw horizental lines
        while (!(doneV))
        {
            if (i < canvas.getHeight())
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
            else
                doneV = true;
            i+=75;
        }
        // change line color
        paint.setColor(NOTEBOOK_LEFT_LINE_COLOR);
        // draw side line
        canvas.drawLine(NOTEBOOK_LEFT_LINE_PADDING, 0,
                        NOTEBOOK_LEFT_LINE_PADDING, canvas.getHeight(),paint);


    }

    public void drawText(String text, int x, int y, Paint p)
    {
        mDrawableList.add(new CText(text, x, y, p));
        invalidate();
    }

    public void drawTextFromKeyboard(int x, int y)
    {

    }

    public void drawTextFromKeyboard(){
        Toast.makeText(getContext(), "Touch where you want the text to be", Toast.LENGTH_LONG).show();
        mTextExpectTouch = true;
    }

    private void cleanDirtyRegion(float eventX, float eventY) {
        // figure out the sides of the dirty region
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    public void cleanPage()
    {
        while (!(mDrawableList.isEmpty()))
        {
            mDrawableList.remove(0);
        }
        invalidate();
    }

    /**
     * Draws an image on the canvas
     * @param x location of the image
     * @param y location of the image
     * @param width the width of the image
     * @param height the height of the image
     * @param pic the image itself
     */
    public void drawImage(int x, int y, int width, int height, Bitmap pic)
    {
        // get the scaled version
        pic = createScaledBitmap(pic, width, height, true);

        // add it to the image draw

    }
    /*******************************************
     * Getters and Setters
     ******************************************/
    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getBackgroundMode() {
        return mBackgroundMode;
    }

    public void setBackgroundMode(int mBackgroundMode) {
        this.mBackgroundMode = mBackgroundMode;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public Paint.Style getStyle() {
        return mStyle;
    }

    public void setStyle(Paint.Style mStyle) {
        this.mStyle = mStyle;
    }

    public float getSize() {
        return mSize;
    }

    public void setSize(float mSize) {
        this.mSize = mSize;
    }




}
