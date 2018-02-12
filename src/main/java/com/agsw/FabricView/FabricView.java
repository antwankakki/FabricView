package com.agsw.FabricView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.agsw.FabricView.DrawableObjects.CBitmap;
import com.agsw.FabricView.DrawableObjects.CDrawable;
import com.agsw.FabricView.DrawableObjects.CPath;
import com.agsw.FabricView.DrawableObjects.CRotation;
import com.agsw.FabricView.DrawableObjects.CScale;
import com.agsw.FabricView.DrawableObjects.CText;
import com.agsw.FabricView.DrawableObjects.CTransform;
import com.agsw.FabricView.DrawableObjects.CTranslation;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Created by antwan on 10/3/2015.
 * A library for creating graphics on an object model on top of canvas.
 * How to use:
 * <H1>Layout</H1>
 * Create a view in your layout, like this: <pre>
 &lt;com.agsw.FabricView.FabricView
 android:id="@+id/my_fabric_view"
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 android:padding="20dp"
 android:text="@string/my_fabric_view_title"
 /&gt;</pre>
 * <H1>Activity code</H1>
 * Retrieve and configure your FabricView: <pre>
FabricView myFabricView = (FabricView) parent.findViewById(R.id.my_fabric_view); //Retrieve by ID
 //Configuration. All of which is optional. Defaults are marked with an asterisk here.
myFabricView.setBackgroundMode(BACKGROUND_STYLE_BLANK); //Set the background style (BACKGROUND_STYLE_BLANK*, BACKGROUND_STYLE_NOTEBOOK_PAPER, BACKGROUND_STYLE_GRAPH_PAPER)

myFabricView.setInteractionMode(FabricView.DRAW_MODE); //Set its draw mode (DRAW_MODE*, SELECT_MODE, ROTATE_MODE, LOCKED_MODE)
myFabricView.setDeleteIcon(deleteIcon); //If you don't like the default delete icon
myFabricView.setColor(R.color.purple); //Line color
myFabricView.setSize(10); //Line width
myFabricView.setSelectionColor(R.color.lightergray); //Selection box color
//To be notified of any deletion:
myFabricView.setDeletionListener(new FabricView.DeletionListener() {
  public void deleted(CDrawable drawable) {
    doSomethingAboutThis(drawable);
  }
});

//Manipulations... The following functions could be attached to buttons of your choosing:
myFabricView.cleanPage(); //Erases everything.
myFabricView.undo(); //Cancels the last operation.
myFabricView.redo(); //Reinstates the last undone operation.
myFabricView.selectLastDrawn(); //Mark the last drawn object as selected.
myFabricView.deSelect(); //Unmark all objects for selection.
myFabricView.deleteSelection(); //Removes all selected objects and its transforms.
myFabricView.deleteDrawable(); //Removes a single object and its transforms.

//Retrieving the picture from the view:
Bitmap fullResult = myFabricView.getCanvasBitmap(); //Gets a copy of the whole view. This includes decorations such as selection rectangle. So make sure you switch to LOCKED_MODE before calling.
Bitmap croppedResult = myFabricView.getCroppedCanvasBitmap(); //Same as previous, except with no margin around the picture.
List&lt;CDrabable&gt; drawablesList = myFabricView.getDrawablesList(); //Returns all the drawables of the view. See next sections.
CDrawable currentSelection = myFabricView.getSelection();

//Save point functions
boolean everythingIsSaved = myFabricView.isSaved(); //true = there were no operations added or undone after the last call to markSaved().
markSaved(); //Indicates that everything was saved. You can save the bitmap or the drawable objects. (See previous section.)
revertUnsaved(); //Restore the drawables to the last save point.
List&lt;CDrabable&gt; unsavedDrawablesList = getUnsavedDrawablesList(); //Returns all the drawables that were not saved yet.</pre>
 * <H1>Drawables and Transforms</H1>
 * The list of visible objects inside the view is a stack. There are two kinds: CDrawable and CTransform (subclass of the latter).
 * A CDrawable is an object that can be "drawn" on the canvas. A CTransform represents a modification of a CDrawable.
 * A CDrawable is linked to its CTransforms (see getTransforms() and hasTransforms()), and each CTransform is aware of its
 * CDrawable (see getDrawable()).
 *
 * The subclasses of CDrawable are CPath (a set of continuous lines), CBitmap, and CText. Another subclass is CTransform, and this one
 * has its one subclasses which are CRotation, CScale, and CTranslation.
 *
 * The rotate mode allows both rotation and scaling. The rotate mode must be set by you.
 * It can be triggered by an external mean (e.g. a button) or by a pinch gesture internally.
 * If you want to use a pinch gesture to start the rotate mode, use setRotationListener()
 * and in the listener's startRotate() return 'true'.
 */
public class FabricView extends View {

    /**********************************************************************************************/
    /*************************************     Vars    *******************************************/
    /*********************************************************************************************/
    // painting objects and properties
    private ArrayList<CDrawable> mDrawableList = new ArrayList<>();
    private ArrayList<CDrawable> mUndoList = new ArrayList<>();
    private CDrawable selected = null;
    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private CDrawable hovering = null;
    private CTranslation hoveringTranslation = null;

    private int mColor = Color.BLACK;
    private int savePoint = -1;
    private Bitmap deleteIcon;
    private RectF deleteIconPosition = new RectF(-1, -1, -1, -1);
    private DeletionListener deletionListener = null;
    private DeletionConfirmationListener deletionConfirmationListener = null;

    // Canvas interaction modes
    private int mInteractionMode = DRAW_MODE;

    //Mode prior to rotation.
    private Integer mOldInteractionMode = null;

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

    /**
     * Default Notebook left line color. Value = Color.Red.
     */
    public static final int NOTEBOOK_LEFT_LINE_COLOR = Color.RED;

    // Flag indicating that we are waiting for a location for the text
    private boolean mTextExpectTouch;

    //This handles gestures for the PinchGestureListener and ROTATE_MODE.
    private ScaleRotationGestureDetector mScaleDetector;

    //This is a listener for pinching gestures.
    private ScaleRotateListener mScaleRotateListener;

    //During a rotation gesture, this is the rotation of the selected object.
    private CRotation mCurrentRotation;

    //During a rotation gesture, this is the scale of the selected object.
    private CScale mCurrentScale;

    // Vars to decrease dirty area and increase performance
    private float lastTouchX, lastTouchY;
    private final RectF dirtyRect = new RectF();

    // keep track of path and paint being in use
    CPath currentPath;
    Paint currentPaint;
    Paint selectionPaint;

    private int selectionColor = Color.DKGRAY;
    private static final int MAX_CLICK_DURATION = 1000;
    private static final int MAX_CLICK_DISTANCE = 15;

    /*********************************************************************************************/
    /************************************     FLAGS    *******************************************/
    /*********************************************************************************************/

    //Background modes:
    /**
     * Background mode, used in setBackgroundMode(). No lines will be drawn on the background. This is the default.
     */
    public static final int BACKGROUND_STYLE_BLANK = 0;
    /**
     * Background mode, used in setBackgroundMode(). Will draw blue lines horizontally and a red line on the left vertically.
     */
    public static final int BACKGROUND_STYLE_NOTEBOOK_PAPER = 1;
    /**
     * Background mode, used in setBackgroundMode(). Will draw blue lines horizontally and vertically.
     */
    public static final int BACKGROUND_STYLE_GRAPH_PAPER = 2;

    //Interactive Modes:
    /**
     * Interactive modes: Will let the user draw. This is the default.
     */
    public static final int DRAW_MODE = 0;
    /**
     * Interactive modes: Will let the user select objects.
     */
    public static final int SELECT_MODE = 1;
    /**
     * Interactive modes: Will let the user rotate and scale objects.
     */
    public static final int ROTATE_MODE = 2;
    /**
     * Interactive modes: Will remove all decorations and the user won't be able to modify anything. This is the mode to use when retrieving the bitmaps with getCroppedCanvasBitmap() or getCanvasBitmap().
     */
    public static final int LOCKED_MODE = 3;

    /*********************************************************************************************/
    /**********************************     CONSTANTS    *****************************************/
    /*********************************************************************************************/
    /**
     * Number of pixels that will be on the left side of the red line when in BACKGROUND_STYLE_GRAPH_PAPER background mode.
     */
    public static final int NOTEBOOK_LEFT_LINE_PADDING = 120;
    private static final int SELECTION_LINE_WIDTH = 2;

    /*********************************************************************************************/
    /************************************     TO-DOs    ******************************************/
    /*********************************************************************************************/
    private float mZoomLevel = 1.0f; //TODO Support Zoom
    private float mHorizontalOffset = 1, mVerticalOffset = 1; // TODO Support Offset and Viewport
    /**
     * Unused at this time.
     */
    public int mAutoscrollDistance = 100; // TODO Support Autoscroll
    private Rect cropBounds = null;

    /**
     *  Constructor, sets defaut values.
     *
     * @param context the activity that containts the view
     * @param attrs   view attributes
     */
    public FabricView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setBackgroundColor(mBackgroundColor);
        mTextExpectTouch = false;

        selectionPaint = new Paint();
        selectionPaint.setAntiAlias(true);
        selectionPaint.setColor(selectionColor);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeJoin(Paint.Join.ROUND);
        selectionPaint.setStrokeWidth(SELECTION_LINE_WIDTH);
        selectionPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        deleteIcon = BitmapFactory.decodeResource(context.getResources(),
                android.R.drawable.ic_menu_delete);

        mScaleDetector = new ScaleRotationGestureDetector(context, new ScaleRotationGestureDetector.OnScaleRotationGestureListener() {

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                handleScaleEnd();
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return handleScaleBegin((ScaleRotationGestureDetector) detector);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return handleScale((ScaleRotationGestureDetector) detector);
            }

            @Override
            public boolean onRotate(ScaleRotationGestureDetector detector) {
                return handleScale((ScaleRotationGestureDetector) detector);
            }

        });
    }

    private boolean handleScaleBegin(ScaleRotationGestureDetector detector) {
        boolean consumed = false;
        if(mScaleRotateListener != null && selected != null) {
            try {
                consumed = mScaleRotateListener.startRotate();
                if(consumed) {
                    mOldInteractionMode = mInteractionMode;
                    setInteractionMode(ROTATE_MODE);
                    mCurrentRotation = new CRotation(selected, selected.getLastBounds().centerX(), selected.getLastBounds().centerY());
                    mCurrentScale = new CScale(selected, selected.getLastBounds().centerX(), selected.getLastBounds().centerY());
                    selected.addTransform(mCurrentRotation);
                    mDrawableList.add(mCurrentRotation);
                    selected.addTransform(mCurrentScale);
                    mDrawableList.add(mCurrentScale);

                    handleScale(detector);
                }
            }
            catch(Exception e) {
                //Do nothing.
            }
        }
        return consumed;
    }

    private void handleScaleEnd() {
        if(mScaleRotateListener != null) {
            try {
                mScaleRotateListener.endRotate();
                if(mOldInteractionMode != null) {
                    setInteractionMode(mOldInteractionMode);
                    mOldInteractionMode = null;
                }
                mCurrentScale = null;
                mCurrentRotation = null;
            }
            catch(Exception e) {
                //Do nothing.
            }
        }
    }

    public void setScaleRotateListener(ScaleRotateListener listener) {
        mScaleRotateListener = listener;
    }

    /**
     * This interface is used to decide what to do when the user does a pinch gesture for
     * rotating and resizing.
     */
    public interface ScaleRotateListener {

        /**
         * If you want FabricView to hanlde rotations and resizing, return true.
         * @return true if the rotation will be handled by the FabricView. false to ignore the guesture.
         */
        boolean startRotate();

        /**
         * Called when the rotation gesture is done.
         */
        void endRotate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int minHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
//        int minWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
//
//        if (minHeight > 0 && MeasureSpec.getSize(heightMeasureSpec) < minHeight) {
//            minHeight =
//            //heightMeasureSpec = MeasureSpec.makeMeasureSpec(minHeight, MeasureSpec.EXACTLY);
//        }
//        if (minWidth > 0 && MeasureSpec.getSize(widthMeasureSpec) < minWidth) {
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(minWidth, MeasureSpec.EXACTLY);
//        }
//
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);

        setMeasuredDimension(w, h);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Called when there is the canvas is being re-drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // check if background needs to be redrawn
        drawBackground(canvas, mBackgroundMode);
        Rect totalBounds = new Rect(canvas.getWidth(), canvas.getHeight(), 0, 0);

        // go through each item in the list and draw it
        for (int i = 0; i < mDrawableList.size(); i++) {
            try {
                CDrawable d = mDrawableList.get(i);
                if (d instanceof CTransform) {
                    continue;
                }

                Rect bounds = d.computeBounds();
                totalBounds.union(bounds);
                d.draw(canvas);
                if (mInteractionMode == SELECT_MODE && d.equals(selected)) {
                    growRect(bounds, SELECTION_LINE_WIDTH);
                    canvas.drawRect(new RectF(bounds), selectionPaint);
                    deleteIconPosition = new RectF();
                    deleteIconPosition.left = bounds.right - (deleteIcon.getWidth() / 2);
                    deleteIconPosition.top = bounds.top - (deleteIcon.getHeight() / 2);
                    deleteIconPosition.right = deleteIconPosition.left + deleteIcon.getWidth();
                    deleteIconPosition.bottom = deleteIconPosition.top + deleteIcon.getHeight();
                    canvas.drawBitmap(deleteIcon, deleteIconPosition.left, deleteIconPosition.top, d.getPaint());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if(totalBounds.width() <= 0) {
            //No bounds
            cropBounds = null;
        }
        else {
            cropBounds = totalBounds;
        }
    }

    private void growRect(Rect rect, int amount) {
        rect.left -= amount;
        rect.top -= amount;
        rect.bottom += amount;
        rect.right += amount;
    }


    /*********************************************************************************************/
    /*******************************     Handling User Touch    **********************************/
    /*********************************************************************************************/

    /**
     * Handles user touch events.
     *
     * @param event the user's motion event
     * @return true, the event is consumed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //ROTATE_MODE is processed inside this:
        mScaleDetector.onTouchEvent(event);
        if(mScaleDetector.isInProgress()) {
            return true;
        }
        // delegate action to the correct method
        if (getInteractionMode() == DRAW_MODE)
            return onTouchDrawMode(event);
        if (getInteractionMode() == SELECT_MODE)
            return onTouchSelectMode(event);
        // if none of the above are selected, delegate to locked mode
        return onTouchLockedMode(event);
    }

    /**
     * Handles touch event if the mode is set to locked
     *
     * @param event the event to handle
     * @return false, shouldn't do anything with it for now
     */
    private boolean onTouchLockedMode(MotionEvent event) {
        // return false since we don't want to do anything so far
        return false;
    }

    /**
     * Takes care of scaling and rotating.
     * @return true if the scaling gesture is consumed.
     */
    private boolean handleScale(ScaleRotationGestureDetector detector) {
        if(mInteractionMode != ROTATE_MODE) {
            return false;
        }
        mCurrentScale.setFactor(detector.getScaleFactor(), Math.min(getWidth(), getHeight()));

        mCurrentRotation.setRotation((int)detector.getRotation());

        invalidate();
        return true;
    }

    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Handles the touch input if the mode is set to draw
     *
     * @param event the touch event
     * @return the result of the action
     */
    public boolean onTouchDrawMode(MotionEvent event) {
        // get location of touch
        float eventX = event.getX();
        float eventY = event.getY();
        if (eventX < 0) {
            eventX = 0;
        }
        if (eventY < 0) {
            eventY = 0;
        }
        if (eventX > getWidth()) {
            eventX = getWidth();
        }
        if (eventY > getHeight()) {
            eventY = getHeight();
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
                currentPath.setPaint(currentPaint);
                currentPath.moveTo(eventX, eventY);
                // capture touched locations
                lastTouchX = eventX;
                lastTouchY = eventY;
                mDrawableList.add(currentPath);
                mUndoList.clear();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(eventX - lastTouchX);
                float dy = Math.abs(eventY - lastTouchY);

                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

                    currentPath.quadTo(lastTouchX, lastTouchY, (eventX + lastTouchX) / 2, (eventY + lastTouchY) / 2);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                }
//                int historySize = event.getHistorySize();
//                for (int i = 0; i < historySize; i++) {
//                    float historicalX = event.getHistoricalX(i);
//                    float historicalY = event.getHistoricalY(i);
//                    currentPath.lineTo(historicalX, historicalY);
//                }

                // After replaying history, connect the line to the touch point.
                //  currentPath.lineTo(eventX, eventY);

                dirtyRect.left = Math.min(currentPath.getXcoords(), dirtyRect.left);
                dirtyRect.right = Math.max(currentPath.getXcoords() + currentPath.getWidth(), dirtyRect.right);
                dirtyRect.top = Math.min(currentPath.getYcoords(), dirtyRect.top);
                dirtyRect.bottom = Math.max(currentPath.getYcoords() + currentPath.getHeight(), dirtyRect.bottom);

                // After replaying history, connect the line to the touch point.
                cleanDirtyRegion(eventX, eventY);
                break;
            case MotionEvent.ACTION_UP:
                currentPath.lineTo(eventX, eventY);
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                return false;
        }

        // Include some padding to ensure nothing is clipped
        invalidate();
//                (int) (dirtyRect.left - 20),
//                (int) (dirtyRect.top - 20),
//                (int) (dirtyRect.right + 20),
//                (int) (dirtyRect.bottom + 20));

        // register most recent touch locations
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }

    /**
     * Handles the touch input if the mode is set to select
     *
     * @param event the touch event
     */
    private boolean onTouchSelectMode(MotionEvent event) {
        ListIterator<CDrawable> li = mDrawableList.listIterator(mDrawableList.size());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hovering = null;
                pressStartTime = SystemClock.uptimeMillis();
                pressedX = event.getX();
                pressedY = event.getY();

                while (li.hasPrevious()) {
                    CDrawable d = li.previous();
                    if (d instanceof CTransform) {
                        continue;
                    }
                    Rect rect = d.computeBounds();
                    if (rect.contains((int)pressedX, (int)pressedY)) {
                        hovering = d;
                        break;
                    }
                }
                if(hovering != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(hovering == null) {
                    break; //Nothing is being dragged.
                }
                updateHoveringPosition(event);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if(hovering != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                long pressDuration = SystemClock.uptimeMillis() - pressStartTime;
                double distance = Math.sqrt(Math.pow((event.getX() - pressedX), 2) + Math.pow((event.getY() - pressedY), 2));
                if (pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE) {
                    //It was a click not a drag.
                    if (hovering == null && deleteIconPosition.contains(event.getX(), event.getY())) {
                        deleteSelection();
                        return true;
                    }
                    selected = hovering;
                    if(hovering != null) {
                        hovering.removeTransform(hoveringTranslation);
                        mDrawableList.remove(hoveringTranslation);
                    }
                } else if (distance > MAX_CLICK_DISTANCE) {
                    //It was a drag. Move the object there.
                    if (hovering != null) {
                        updateHoveringPosition(event);
                    }
                }
                invalidate();
                hovering = null;
                hoveringTranslation = null;
                return true;
            case MotionEvent.ACTION_CANCEL:
                if(hovering != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    hovering.removeTransform(hoveringTranslation);
                    mDrawableList.remove(hoveringTranslation);
                    hovering = null;
                    hoveringTranslation = null;
                }
                return true;

        }
        return false;
    }

    private void updateHoveringPosition(MotionEvent event) {

        double distance = Math.sqrt(Math.pow((event.getX() - pressedX), 2) + Math.pow((event.getY() - pressedY), 2));
        if (distance < MAX_CLICK_DISTANCE) {
            return; //Movement too small
        }

        if(hoveringTranslation == null) {
            hoveringTranslation = new CTranslation(hovering);
            Vector<Integer> v = new Vector<>(2);
            v.add((int) (event.getX() - pressedX));
            v.add((int) (event.getY() - pressedY));
            hoveringTranslation.setDirection(v);
            hovering.addTransform(hoveringTranslation);
            mDrawableList.add(hoveringTranslation);
            mUndoList.clear();
        }
        else {
            //Last transform was a translation. Replace translation with new coordinates.
            Vector<Integer> v = new Vector<>(2);
            v.add((int) (event.getX() - pressedX));
            v.add((int) (event.getY() - pressedY));
            hoveringTranslation.setDirection(v);
        }
    }


    /*******************************************
     * Drawing Events
     ******************************************/
    /**
     * Draw the background on the canvas
     *
     * @param canvas         the canvas to draw on
     * @param backgroundMode one of BACKGROUND_STYLE_GRAPH_PAPER, BACKGROUND_STYLE_NOTEBOOK_PAPER, BACKGROUND_STYLE_BLANK
     */
    public void drawBackground(Canvas canvas, int backgroundMode) {
        if(mBackgroundColor != Color.TRANSPARENT) {
            canvas.drawColor(mBackgroundColor);
        }
        if (backgroundMode != BACKGROUND_STYLE_BLANK) {
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
     *
     * @param canvas the canvas to draw on
     * @param paint  the paint to use
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
     *
     * @param canvas the canvas to draw on
     * @param paint  the paint to use
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
     *
     * @param text the text to draw
     * @param x    the x location of the text
     * @param y    the y location of the text
     * @param p    the paint to use. This is used for the TextSize, color. If null, the defaut is black with 20sp size.
     */
    public void drawText(String text, int x, int y, Paint p) {
        if(p==null) {
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getContext().getResources().getDisplayMetrics());
            p = new Paint();
            p.setTextSize(px);
            p.setColor(Color.BLACK);
        }
        mDrawableList.add(new CText(text, x, y, p));
        mUndoList.clear();
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
     *
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

    /**
     * Cancels the last operation. Works on both CDrawable and CTransform.
     */
    public void undo() {
        if (mDrawableList.size() > 0) {
            CDrawable toUndo = mDrawableList.get(mDrawableList.size() - 1);
            mUndoList.add(toUndo);
            mDrawableList.remove(mDrawableList.size() - 1);
            if(toUndo instanceof CTransform) {
                CTransform t = (CTransform)toUndo;
                t.getDrawable().removeTransform(t);
            }

            invalidate();
        }
    }

    /**
     * Re-instates the last undone operation.
     */
    public void redo() {
        if (mUndoList.size() > 0) {
            CDrawable toRedo = mUndoList.get(mUndoList.size() - 1);
            mDrawableList.add(toRedo);
            mDrawableList.addAll(toRedo.getTransforms());
            mUndoList.remove(toRedo);
            if(toRedo instanceof CTransform) {
                CTransform t = (CTransform)toRedo;
                t.getDrawable().addTransform(t);
            }

            invalidate();
        }
    }

    /**
     * Clean the canvas, remove everything drawn on the canvas.
     * WARNING: Before calling this, ask the user to confirm because <b>this cannot be undone</b>.
     */
    public void cleanPage() {
        // remove everything from the list
        mDrawableList.clear();
        currentPath = null;
        mUndoList.clear();
        savePoint = -1;
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
        mUndoList.clear();
        invalidate();
    }


    /*******************************************
     * Getters and Setters
     ******************************************/


    /**
     * Gets what has been drawn on the canvas so far as a bitmap
     *
     * @return Bitmap of the canvas.
     */
    public Bitmap getCanvasBitmap() {
        // build drawing cache of the canvas, use it to create a new bitmap, then destroy it.
        buildDrawingCache();
        Bitmap mCanvasBitmap = Bitmap.createBitmap(getDrawingCache());
        destroyDrawingCache();

        // return the created bitmap.
        return mCanvasBitmap;
    }

    /**
     * Gets what has been drawn on the canvas so far as a bitmap. Removes any margin around the drawn objects.
     *
     * @return Bitmap of the canvas, cropped.
     */
    public Bitmap getCroppedCanvasBitmap() {
        if(cropBounds == null) {
            //No pixels at all
            return null;
        }
        Bitmap mCanvasBitmap = getCanvasBitmap();

        Rect size = new Rect(cropBounds);
        if(size.left < 0) {
            size.left = 0;
        }
        if(size.top < 0) {
            size.top = 0;
        }
        if(size.right > mCanvasBitmap.getWidth()) {
            size.right = mCanvasBitmap.getWidth();
        }
        if(size.bottom > mCanvasBitmap.getHeight()) {
            size.bottom = mCanvasBitmap.getHeight();
        }

        Bitmap cropped = Bitmap.createBitmap(mCanvasBitmap, size.left, size.top, size.width(), size.height());
        return cropped;
    }

    /**
     * @return the drawing line color. Default is Color.BLACK.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Setter for the the drawing line color.
     * @param mColor The new color.
     */
    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    /**
     * @return the background color. Default is Color.WHITE.
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Setter for the background color.
     * @param mBackgroundColor The new background color.
     */
    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    /**
     * @return the background decorations mode. Default is BACKGROUND_STYLE_BLANK.
     */
    public int getBackgroundMode() {
        return mBackgroundMode;
    }

    /**
     * Setter for the background decorations mode. Can be BACKGROUND_STYLE_BLANK*, BACKGROUND_STYLE_NOTEBOOK_PAPER, or BACKGROUND_STYLE_GRAPH_PAPER.
     * @param mBackgroundMode
     */
    public void setBackgroundMode(int mBackgroundMode) {
        this.mBackgroundMode = mBackgroundMode;
        invalidate();
    }

    /**
     * @return The drawing style. Can be Paint.Style.FILL, Paint.Style.STROKE, or Paint.Style.FILL_AND_STROKE. Default is Paint.Style.STROKE.
     */
    public Paint.Style getStyle() {
        return mStyle;
    }

    /**
     * Setter for the drawing style.
     * @param mStyle The new drawing style. Can be Can be FILL, STROKE, or FILL_AND_STROKE.
     */
    public void setStyle(Paint.Style mStyle) {
        this.mStyle = mStyle;
    }

    /**
     * @return The width of the line for drawing.
     */
    public float getSize() {
        return mSize;
    }

    /**
     * Setter for the line width. The default is 5.
     * @param mSize The new width for the line.
     */
    public void setSize(float mSize) {
        this.mSize = mSize;
    }

    /**
     * @return The interaction mode. The default is DRAW_MODE.
     */
    public int getInteractionMode() {
        return mInteractionMode;
    }

    /**
     * Setter for the interaction mode. Can be DRAW_MODE, SELECT_MODE, ROTATE_MODE, or LOCKED_MODE.
     * @param interactionMode
     */
    public void setInteractionMode(int interactionMode) {

        // if the value passed is not any of the flags, set the library to locked mode
        if (interactionMode > LOCKED_MODE)
            interactionMode = LOCKED_MODE;
        else if (interactionMode < DRAW_MODE)
            interactionMode = LOCKED_MODE;

        this.mInteractionMode = interactionMode;
        invalidate();
    }

    /**
     * @return the list of all CDrawables in order of insertion.
     */
    public List<CDrawable> getDrawablesList() {
        return mDrawableList;
    }

    /**
     * Indicates that all CDrawables in the list have been saved.
     */
    public void markSaved() {
        savePoint = mDrawableList.size()-1;
    }

    /**
     * @return true if there were no new operations done after the last call to markSaved().
     */
    public boolean isSaved() {
        return savePoint < mDrawableList.size();
    }

    /**
     * @return The list of all CDrawables that have been added after the last call to markSaved().
     */
    public List<CDrawable> getUnsavedDrawablesList() {
        if (savePoint >= mDrawableList.size()) {
            //Some things were deleted.
            return new ArrayList<>();
        }
        return mDrawableList.subList(savePoint+1, mDrawableList.size());
    }

    /**
     * Deletes all CDrawables that were added after the last call to markSaved().
     * Does not trigger DeletionConfirmationListener.
     */
    public void revertUnsaved() {
        List<CDrawable> unsaved = new ArrayList<>(getUnsavedDrawablesList());
        for (CDrawable d :
                unsaved) {
            deletionConfirmed(d);
        }
    }

    /**
     * Marks the last inserted CDrawable as the selected object.
     */
    public void selectLastDrawn() {
        if (mDrawableList.isEmpty()) {
            return;
        }

        ListIterator<CDrawable> li = mDrawableList.listIterator(mDrawableList.size());
        while (li.hasPrevious()) {
            CDrawable d = li.previous();
            if (d instanceof CTransform) {
                continue;
            }
            selected = d;
            break;
        }
        invalidate();
    }

    /**
     * @return The currently selected CDrawable.
     */
    public CDrawable getSelection() {
        return selected;
    }

    /**
     * Cancels all selection. No object will be selected.
     */
    public void deSelect() {
        selected = null;
        invalidate();
    }

    /**
     * Deletes the currently selected object. No object will be selected after that.
     */
    public void deleteSelection() {
        if (selected == null) {
            return;
        }
        deleteDrawable(selected);
        selected = null;
    }

    /**
     * Removes a specific CDrawable, with confirmation if required.
     * @param drawable The object to remove.
     */
    public void deleteDrawable(CDrawable drawable) {
        if (drawable == null) {
            return;
        }
        if (deletionConfirmationListener != null) {
            try {
                deletionConfirmationListener.confirmDeletion(drawable);
            }
            catch(Exception e) {
                //Do nothing
            }
            return;
        }
        deletionConfirmed(drawable);
    }

    /**
     * Removes a specific CDrawable, without confirmation. Must be called by your
     * DeletionConfirmationListener.confirmDeletion() to finish the deletion.
     * @param drawable The object to remove.
     */
    public void deletionConfirmed(CDrawable drawable) {
        if (drawable == null) {
            return;
        }
        ArrayList<CDrawable> toDelete = new ArrayList<>();
        toDelete.add(drawable);
        toDelete.addAll(drawable.getTransforms());
        for (CDrawable d :
                toDelete) {
            if(mDrawableList.indexOf(d) <= savePoint) {
                savePoint--;
            }
            mDrawableList.remove(d);
        }
        mUndoList.add(drawable);
        if (deletionListener != null) {
            try {
                deletionListener.deleted(drawable);
            }
            catch(Exception e) {
                //Do nothing
            }
        }
        invalidate();
    }

    /**
     * Setter for the "delete" icon. The default is android.R.drawable.ic_menu_delete.
     * @param newIcon The new delete icon.
     */
    public void setDeleteIcon(Bitmap newIcon) {
        deleteIcon = newIcon;
    }

    /**
     * Setter for the deletion event listener. Refer to the Observer pattern.
     * @param newListener The listener for any deletion event.
     */
    public void setDeletionListener(DeletionListener newListener) {
        deletionListener = newListener;
    }

    /**
     * This interface must be implemented by your deletion event listener.
     */
    public interface DeletionListener {
        /**
         * This method will be called after a CDrawable is deleted.
         * @param drawable The object that was deleted.
         */
        void deleted(CDrawable drawable);
    }

    /**
     * Setter for the listener that will confirm deletion. Refer to the Observer pattern.
     * @param newListener The listener for any deletion confirmation request.
     */
    public void setDeletionConfirmationListener(DeletionConfirmationListener newListener) {
        deletionConfirmationListener = newListener;
    }

    /**
     * This interface must be implemented by a listener for confirming deletion. If confirmed,
     * the listener must call confirmDeletion(CDrawable).
     */
    public interface DeletionConfirmationListener {
        /**
         * This method will be called before a CDrawable is deleted in order to confirm the deletion.
         * If the deletion is allowed, call FabricView.deletionConfirmed(CDrawable).
         * @param drawable The object that's about to be deleted.
         */
        void confirmDeletion(CDrawable drawable);
    }

    /**
     * @return The current selection rectangle color.  The default is Color.DKGRAY.
     */
    public int getSelectionColor() {
        return selectionColor;
    }

    /**
     * Setter for the selection rectangle color.
     * @param selectionColor The new selection rectangle color.
     */
    public void setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
    }

}
