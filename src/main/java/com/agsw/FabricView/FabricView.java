package com.agsw.FabricView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.agsw.FabricView.DrawableObjects.CBitmap;
import com.agsw.FabricView.DrawableObjects.CDrawable;
import com.agsw.FabricView.DrawableObjects.CPath;
import com.agsw.FabricView.DrawableObjects.CText;

import java.util.ArrayList;

/**
 * Created by antwan on 10/3/2015.
 * A library for creating graphics on an object model on top of canvas.
 */
public class FabricView extends View {

    /**********************************************************************************************/
    /*************************************     Vars    *******************************************/
    /*********************************************************************************************/
    // painting objects and properties
    private ArrayList<CDrawable> mDrawableList = new ArrayList<>();
    private ArrayList<CDrawable> mUndoList = new ArrayList<>();
    private int mColor = Color.BLACK;

    // Canvas interaction modes
    private int mInteractionMode = DRAW_MODE;

    // background color of the library
    private int mBackgroundColor = Color.WHITE;
    // default style for the library
    private Paint.Style mStyle = Paint.Style.STROKE;

    // default stroke size for the library
    private float mSize = 5f;

    // flag indicating whether or not the background needs to be redrawn
    private boolean mRedrawBackground;

    // background mode for the library, default to blank
    private int mBackgroundMode = BACKGROUND_STYLE_BLANK;

    // Default Notebook left line color
    public static final int NOTEBOOK_LEFT_LINE_COLOR = Color.RED;

    // Flag indicating that we are waiting for a location for the text
    private boolean mTextExpectTouch;

    // Vars to decrease dirty area and increase performance
    private float lastTouchX, lastTouchY;
    private final RectF dirtyRect = new RectF();
    
    // keep track of path and paint being in use
    CPath currentPath;
    Paint currentPaint;

    /*********************************************************************************************/
    /************************************     FLAGS    *******************************************/
    /*********************************************************************************************/
    // Default Background Styles
    public static final int BACKGROUND_STYLE_BLANK = 0;
    public static final int BACKGROUND_STYLE_NOTEBOOK_PAPER = 1;
    public static final int BACKGROUND_STYLE_GRAPH_PAPER = 2;

    // Interactive Modes
    public static final int DRAW_MODE = 0;
    public static final int SELECT_MODE = 1; // TODO Support Object Selection.
    public static final int ROTATE_MODE = 2; // TODO Support Object ROtation.
    public static final int LOCKED_MODE = 3;

    /*********************************************************************************************/
    /**********************************     CONSTANTS    *****************************************/
    /*********************************************************************************************/
    public static final int NOTEBOOK_LEFT_LINE_PADDING = 120;

    /*********************************************************************************************/
    /************************************     TO-DOs    ******************************************/
    /*********************************************************************************************/
    private float mZoomLevel = 1.0f; //TODO Support Zoom
    private float mHorizontalOffset = 1, mVerticalOffset = 1; // TODO Support Offset and Viewport
    public int mAutoscrollDistance = 100; // TODO Support Autoscroll

    /**
     * Default Constructor, sets sane values.
     *
     * @param context the activity that containts the view
     * @param attrs   view attributes
     */
    public FabricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setBackgroundColor(mBackgroundColor);
        mTextExpectTouch = false;

    }

    /**
     * Called when there is the canvas is being re-drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // check if background needs to be redrawn
        drawBackground(canvas, mBackgroundMode);

        // go through each item in the list and draw it
        for (int i = 0; i < mDrawableList.size(); i++) {
            try {
                mDrawableList.get(i).draw(canvas);
            }

            catch(Exception ex)
            {

            }
        }
    }


    /*********************************************************************************************/
    /*******************************     Handling User Touch    **********************************/
    /*********************************************************************************************/

    /**
     * Handles user touch event
     *
     * @param event the user's motion event
     * @return true, the event is consumed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // delegate action to the correct method
        if (getInteractionMode() == DRAW_MODE)
            return onTouchDrawMode(event);
        else if (getInteractionMode() == SELECT_MODE)
            return onTouchSelectMode(event);
        else if (getInteractionMode() == ROTATE_MODE)
            return onTouchRotateMode(event);
        // if none of the above are selected, delegate to locked mode
        else
            return onTouchLockedMode(event);
    }

    /**
     * Handles touch event if the mode is set to locked
     * @param event the event to handle
     * @return false, shouldn't do anything with it for now
     */
    private boolean onTouchLockedMode(MotionEvent event) {
        // return false since we don't want to do anything so far
        return false;
    }

    /**
     * Handles the touch input if the mode is set to rotate
     * @param event the touch event
     * @return the result of the action
     */
    private boolean onTouchRotateMode(MotionEvent event) {
        return false;
    }


    /**
     * Handles the touch input if the mode is set to draw
     * @param event the touch event
     * @return the result of the action
     */
    public boolean onTouchDrawMode(MotionEvent event)
    {
        // get location of touch
        float eventX = event.getX();
        float eventY = event.getY();

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

        // Include some padding to ensure nothing is clipped
        invalidate(
                (int) (dirtyRect.left - 20),
                (int) (dirtyRect.top - 20),
                (int) (dirtyRect.right + 20),
                (int) (dirtyRect.bottom + 20));

        // register most recent touch locations
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }

    /**
     * Handles the touch input if the mode is set to select
     * @param event the touch event
     */
    private boolean onTouchSelectMode(MotionEvent event) {
        // TODO Implement Method
        return false;
    }


    /*******************************************
     * Drawing Events
     ******************************************/
    /**
     * Draw the background on the canvas
     * @param canvas the canvas to draw on
     * @param backgroundMode one of BACKGROUND_STYLE_GRAPH_PAPER, BACKGROUND_STYLE_NOTEBOOK_PAPER, BACKGROUND_STYLE_BLANK
     */
    public void drawBackground(Canvas canvas, int backgroundMode) {
        canvas.drawColor(mBackgroundColor);
        if(backgroundMode != BACKGROUND_STYLE_BLANK) {
            Paint linePaint = new Paint();
            linePaint.setColor(Color.argb(50, 0, 0, 0));
            linePaint.setStyle(mStyle);
            linePaint.setStrokeJoin(Paint.Join.ROUND);
            linePaint.setStrokeWidth(mSize - 2f);
            switch (backgroundMode) {
                case BACKGROUND_STYLE_GRAPH_PAPER:
                    drawGraphPaperBackground(canvas, linePaint);
                    break;
                case BACKGROUND_STYLE_NOTEBOOK_PAPER:
                    drawNotebookPaperBackground(canvas, linePaint);
                default:
                    break;
            }
        }
        mRedrawBackground = false;
    }

    /**
     * Draws a graph paper background on the view
     * @param canvas the canvas to draw on
     * @param paint the paint to use
     */
    private void drawGraphPaperBackground(Canvas canvas, Paint paint) {
        int i = 0;
        boolean doneH = false, doneV = false;

        // while we still need to draw either H or V
        while (!(doneH && doneV)) {

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
            i += 75;
        }
    }

    /**
     * Draws a notebook paper background on the view
     * @param canvas the canvas to draw on
     * @param paint the paint to use
     */
    private void drawNotebookPaperBackground(Canvas canvas, Paint paint) {
        int i = 0;
        boolean doneV = false;
        // draw horizental lines
        while (!(doneV)) {
            if (i < canvas.getHeight())
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
            else
                doneV = true;
            i += 75;
        }
        // change line color
        paint.setColor(NOTEBOOK_LEFT_LINE_COLOR);
        // draw side line
        canvas.drawLine(NOTEBOOK_LEFT_LINE_PADDING, 0,
                NOTEBOOK_LEFT_LINE_PADDING, canvas.getHeight(), paint);


    }

    /**
     * Draw text on the screen
     * @param text the text to draw
     * @param x the x location of the text
     * @param y the y location of the text
     * @param p the paint to use
     */
    public void drawText(String text, int x, int y, Paint p) {
        mDrawableList.add(new CText(text, x, y, p));
        invalidate();
    }

    /**
     * Capture Text from the keyboard and draw it on the screen
     * //TODO Implement the method
     */
    private void drawTextFromKeyboard() {
        Toast.makeText(getContext(), "Touch where you want the text to be", Toast.LENGTH_LONG).show();
        //TODO
        mTextExpectTouch = true;
    }

    /**
     * Retrieve the region needing to be redrawn
     * @param eventX The current x location of the touch
     * @param eventY the current y location of the touch
     */
    private void cleanDirtyRegion(float eventX, float eventY) {
        // figure out the sides of the dirty region
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }



    public void undo() {
        if (mDrawableList.size() > 0) {

            mUndoList.add(mDrawableList.get(mDrawableList.size()-1));
            mDrawableList.remove(mDrawableList.size()-1);

            invalidate();
        }
    }

    public void redo() {
        if (mUndoList.size() > 0) {

            mDrawableList.add(mUndoList.get(mUndoList.size()-1));
            mUndoList.remove(mUndoList.size()-1);

            invalidate();
        }
    }

    /**
     * Clean the canvas, remove everything drawn on the canvas.
     */
    public void cleanPage() {
        // remove everything from the list
        while (!(mDrawableList.isEmpty())) {
            mDrawableList.remove(0);
        }
        // request to redraw the canvas
        invalidate();
    }

    /**
     * Draws an image on the canvas
     *
     * @param x      location of the image
     * @param y      location of the image
     * @param width  the width of the image
     * @param height the height of the image
     * @param pic    the image itself
     */
    public void drawImage(int x, int y, int width, int height, Bitmap pic) {
        CBitmap bitmap = new CBitmap(pic, x, y);
        bitmap.setWidth(width);
        bitmap.setHeight(height);
        mDrawableList.add(bitmap);
        invalidate();
    }


    /*******************************************
     * Getters and Setters
     ******************************************/


    /**
     * Gets what has been drawn on the canvas so far as a bitmap
     * @return Bitmap of the canvas.
     */
    public Bitmap getCanvasBitmap()
    {
        // build drawing cache of the canvas, use it to create a new bitmap, then destroy it.
        buildDrawingCache();
        Bitmap mCanvasBitmap = Bitmap.createBitmap(getDrawingCache());
        destroyDrawingCache();

        // return the created bitmap.
        return mCanvasBitmap;
    }

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
        invalidate();
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


    public int getInteractionMode() {
        return mInteractionMode;
    }

    public void setInteractionMode(int interactionMode) {

        // if the value passed is not any of the flags, set the library to locked mode
        if (interactionMode > LOCKED_MODE)
            interactionMode = LOCKED_MODE;
        else if (interactionMode < DRAW_MODE)
            interactionMode = LOCKED_MODE;

        this.mInteractionMode = interactionMode;
    }


}
