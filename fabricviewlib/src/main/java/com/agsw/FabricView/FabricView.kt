package com.agsw.FabricView

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.agsw.FabricView.DrawableObjects.*
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by antwan on 10/3/2015.
 * A library for creating graphics on an object model on top of canvas.
 * How to use:
 * <H1>Layout</H1>
 * Create a view in your layout, like this: <pre>
 * &lt;com.agsw.FabricView.FabricView
 * android:id="@+id/my_fabric_view"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:padding="20dp"
 * android:text="@string/my_fabric_view_title"
 * /&gt;</pre>
 * <H1>Activity code</H1>
 * Retrieve and configure your FabricView: <pre>
 * FabricView myFabricView = (FabricView) parent.findViewById(R.id.my_fabric_view); //Retrieve by ID
 * //Configuration. All of which is optional. Defaults are marked with an asterisk here.
 * myFabricView.setBackgroundMode(BACKGROUND_STYLE_BLANK); //Set the background style (BACKGROUND_STYLE_BLANK*, BACKGROUND_STYLE_NOTEBOOK_PAPER, BACKGROUND_STYLE_GRAPH_PAPER)
 *
 *
 * myFabricView.setInteractionMode(FabricView.DRAW_MODE); //Set its draw mode (DRAW_MODE*, SELECT_MODE, ROTATE_MODE, LOCKED_MODE)
 * myFabricView.setDeleteIcon(deleteIcon); //If you don't like the default delete icon
 * myFabricView.setColor(R.color.purple); //Line color
 * myFabricView.setSize(10); //Line width
 * myFabricView.setSelectionColor(R.color.lightergray); //Selection box color
 * //To be notified of any deletion:
 * myFabricView.setDeletionListener(new FabricView.DeletionListener() {
 * public void deleted(CDrawable drawable) {
 * doSomethingAboutThis(drawable);
 * }
 * });
 *
 *
 * //Manipulations... The following functions could be attached to buttons of your choosing:
 * myFabricView.cleanPage(); //Erases everything.
 * myFabricView.undo(); //Cancels the last operation.
 * myFabricView.redo(); //Reinstates the last undone operation.
 * myFabricView.selectLastDrawn(); //Mark the last drawn object as selected.
 * myFabricView.deSelect(); //Unmark all objects for selection.
 * myFabricView.deleteSelection(); //Removes all selected objects and its transforms.
 * myFabricView.deleteDrawable(); //Removes a single object and its transforms.
 *
 *
 * //Retrieving the picture from the view:
 * Bitmap fullResult = myFabricView.getCanvasBitmap(); //Gets a copy of the whole view. This includes decorations such as selection rectangle. So make sure you switch to LOCKED_MODE before calling.
 * Bitmap croppedResult = myFabricView.getCroppedCanvasBitmap(); //Same as previous, except with no margin around the picture.
 * List&lt;CDrabable&gt; drawablesList = myFabricView.getDrawablesList(); //Returns all the drawables of the view. See next sections.
 * CDrawable currentSelection = myFabricView.getSelection();
 *
 *
 * //Save point functions
 * boolean everythingIsSaved = myFabricView.isSaved(); //true = there were no operations added or undone after the last call to markSaved().
 * markSaved(); //Indicates that everything was saved. You can save the bitmap or the drawable objects. (See previous section.)
 * revertUnsaved(); //Restore the drawables to the last save point.
 * List&lt;CDrabable&gt; unsavedDrawablesList = getUnsavedDrawablesList(); //Returns all the drawables that were not saved yet.</pre>
 * <H1>Drawables and Transforms</H1>
 * The list of visible objects inside the view is a stack. There are two kinds: CDrawable and CTransform (subclass of the latter).
 * A CDrawable is an object that can be "drawn" on the canvas. A CTransform represents a modification of a CDrawable.
 * A CDrawable is linked to its CTransforms (see getTransforms() and hasTransforms()), and each CTransform is aware of its
 * CDrawable (see getDrawable()).
 *
 *
 * The subclasses of CDrawable are CPath (a set of continuous lines), CBitmap, and CText. Another subclass is CTransform, and this one
 * has its one subclasses which are CRotation, CScale, and CTranslation.
 */
class FabricView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /** */
    /*************************************     Vars     */
    /** */ // painting objects and properties
    private val mDrawableList = ArrayList<CDrawable?>()
    private val mUndoList = ArrayList<CDrawable?>()

    /**
     * @return The currently selected CDrawable.
     */
    var selection: CDrawable? = null
        private set
    private var pressStartTime: Long = 0
    private var pressedX = 0f
    private var pressedY = 0f
    private var hovering: CDrawable? = null
    private var hoveringTranslation: CTranslation? = null
    /**
     * @return the drawing line color. Default is Color.BLACK.
     */
    /**
     * Setter for the the drawing line color.
     *
     * @param mColor The new color.
     */
    private var color = Color.BLACK
    private var savePoint = 0
    private var deleteIcon: Bitmap
    private var deleteIconPosition = RectF(-1f, -1f, -1f, -1f)
    private var deletionListener: DeletionListener? = null
    /**
     * @return The current gestureDetector instance
     */
    /**
     * Setter for the gesture for singleTap and doubleTap.
     *
     * @param gestureDetector
     */
    var gestureDetector: GestureDetector? = null

    // Canvas interaction modes
    private var mInteractionMode = DRAW_MODE
    /**
     * @return The drawing style. Can be Paint.Style.FILL, Paint.Style.STROKE, or Paint.Style.FILL_AND_STROKE. Default is Paint.Style.STROKE.
     */
    /**
     * Setter for the drawing style.
     *
     * @param mStyle The new drawing style. Can be Can be FILL, STROKE, or FILL_AND_STROKE.
     */
    // background color of the library
    private var mBackgroundColor: Int = Color.WHITE

    /**
     * @return The width of the line for drawing.
     */
    /**
     * Setter for the line width. The default is 5.
     *
     * @param mSize The new width for the line.
     */
    // default style for the library
    private var mStyle = Paint.Style.STROKE

    // default stroke size for the library
    private var mSize = 5f

    // flag indicating whether or not the background needs to be redrawn
    private var mRedrawBackground = false

    // background mode for the library, default to blank
    private var mBackgroundMode = BACKGROUND_STYLE_BLANK

    // Flag indicating that we are waiting for a location for the text
    private var mTextExpectTouch: Boolean

    // Vars to decrease dirty area and increase performance
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private val dirtyRect = RectF()

    // keep track of path and paint being in use
    var currentPath: CPath? = null
    var currentPaint: Paint? = null
    private var selectionPaint: Paint
    /**
     * @return The current selection rectangle color.  The default is Color.DKGRAY.
     */
    /**
     * Setter for the selection rectangle color.
     *
     * @param selectionColor The new selection rectangle color.
     */
    var selectionColor = Color.DKGRAY
    /** */
    /************************************     TO-DOs     */
    /** */
    private val mZoomLevel = 1.0f //TODO Support Zoom
    private val mHorizontalOffset = 1f
    private val mVerticalOffset = 1f // TODO Support Offset and Viewport

    /**
     * Unused at this time.
     */
    var mAutoScrollDistance = 100 // TODO Support AutoScroll
    private var cropBounds: Rect? = null

    companion object {
        /**
         * Default Notebook left line color. Value = Color.Red.
         */
        const val NOTEBOOK_LEFT_LINE_COLOR = Color.RED
        private const val MAX_CLICK_DURATION = 1000
        private const val MAX_CLICK_DISTANCE = 15
        /** */
        /************************************     FLAGS     */
        /** */ //Background modes:
        /**
         * Background mode, used in setBackgroundMode(). No lines will be drawn on the background. This is the default.
         */
        const val BACKGROUND_STYLE_BLANK = 0

        /**
         * Background mode, used in setBackgroundMode(). Will draw blue lines horizontally and a red line on the left vertically.
         */
        const val BACKGROUND_STYLE_NOTEBOOK_PAPER = 1

        /**
         * Background mode, used in setBackgroundMode(). Will draw blue lines horizontally and vertically.
         */
        const val BACKGROUND_STYLE_GRAPH_PAPER = 2
        //Interactive Modes:
        /**
         * Interactive modes: Will let the user draw. This is the default.
         */
        const val DRAW_MODE = 0

        /**
         * Interactive modes: Will let the user select objects.
         */
        const val SELECT_MODE = 1

        /**
         * Interactive modes: Will let the user rotate objects. This is not yet supported.
         */
        const val ROTATE_MODE = 2 // TODO Support Object Rotation.

        /**
         * Interactive modes: Will remove all decorations and the user won't be able to modify anything. This is the mode to use when retrieving the bitmaps with getCroppedCanvasBitmap() or getCanvasBitmap().
         */
        const val LOCKED_MODE = 3
        /** */
        /**********************************     CONSTANTS     */
        /** */
        /**
         * Number of pixels that will be on the left side of the red line when in BACKGROUND_STYLE_GRAPH_PAPER background mode.
         */
        const val NOTEBOOK_LEFT_LINE_PADDING = 120
        private const val SELECTION_LINE_WIDTH = 2
        private const val TOUCH_TOLERANCE = 4f
    }

    /**
     * Constructor, sets defaut values.
     *
     * @param context the activity that containts the view
     * @param attrs   view attributes
     */
    init {
        setWillNotDraw(false)
        isFocusable = true
        isFocusableInTouchMode = true
        setBackgroundColor(mBackgroundColor)
        mTextExpectTouch = false
        selectionPaint = Paint()
        selectionPaint.isAntiAlias = true
        selectionPaint.color = selectionColor
        selectionPaint.style = Paint.Style.STROKE
        selectionPaint.strokeJoin = Paint.Join.ROUND
        selectionPaint.strokeWidth = SELECTION_LINE_WIDTH.toFloat()
        selectionPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)
        deleteIcon = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_menu_delete
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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
        val minw = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(minw, widthMeasureSpec, 1)

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        val minh = paddingTop + paddingBottom + suggestedMinimumHeight
        val h = resolveSizeAndState(minh, heightMeasureSpec, 1)
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    /**
     * Called when there is the canvas is being re-drawn.
     */
    override fun onDraw(canvas: Canvas) {
        // check if background needs to be redrawn
        drawBackground(canvas, mBackgroundMode)
        val totalBounds = Rect(canvas.width, canvas.height, 0, 0)

        // go through each item in the list and draw it
        for (i in mDrawableList.indices) {
            try {
                val d = mDrawableList[i]
                if (d is CTransform) {
                    continue
                }
                val bounds = d?.computeBounds()
                totalBounds.union(bounds)
                d?.draw(canvas)
                if (mInteractionMode == SELECT_MODE && d == selection) {
                    if (bounds != null) {
                        growRect(bounds, SELECTION_LINE_WIDTH)
                    }
                    canvas.drawRect(RectF(bounds), selectionPaint)
                    deleteIconPosition = RectF()
                    if (bounds != null) {
                        deleteIconPosition.left = bounds.right - (deleteIcon.width / 2).toFloat()
                        deleteIconPosition.top = bounds.top - (deleteIcon.height / 2).toFloat()
                    }
                    deleteIconPosition.right = deleteIconPosition.left + deleteIcon.width
                    deleteIconPosition.bottom = deleteIconPosition.top + deleteIcon.height
                    canvas.drawBitmap(
                        deleteIcon,
                        deleteIconPosition.left,
                        deleteIconPosition.top,
                        d?.paint
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        cropBounds = if (totalBounds.width() <= 0) {
            //No bounds
            null
        } else {
            totalBounds
        }
    }

    private fun growRect(rect: Rect, amount: Int) {
        rect.left -= amount
        rect.top -= amount
        rect.bottom += amount
        rect.right += amount
    }
    /** */
    /*******************************     Handling User Touch     */
    /** */
    /**
     * Handles user touch events.
     *
     * @param event the user's motion event
     * @return true, the event is consumed.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector?.onTouchEvent(event)
        // delegate action to the correct method
        if (interactionMode == DRAW_MODE) return onTouchDrawMode(event)
        if (interactionMode == SELECT_MODE) return onTouchSelectMode(event)
        return if (interactionMode == ROTATE_MODE) onTouchRotateMode(event) else onTouchLockedMode(
            event
        )
        // if none of the above are selected, delegate to locked mode
    }

    /**
     * Handles touch event if the mode is set to locked
     *
     * @param event the event to handle
     * @return false, shouldn't do anything with it for now
     */
    private fun onTouchLockedMode(event: MotionEvent): Boolean {
        // return false since we don't want to do anything so far
        return false
    }

    /**
     * Handles the touch input if the mode is set to rotate
     *
     * @param event the touch event
     * @return the result of the action
     */
    private fun onTouchRotateMode(event: MotionEvent): Boolean {
        return false
    }

    /**
     * Handles the touch input if the mode is set to draw
     *
     * @param event the touch event
     * @return the result of the action
     */
    fun onTouchDrawMode(event: MotionEvent): Boolean {
        // get location of touch
        var eventX = event.x
        var eventY = event.y
        if (eventX < 0) {
            eventX = 0f
        }
        if (eventY < 0) {
            eventY = 0f
        }
        if (eventX > width) {
            eventX = width.toFloat()
        }
        if (eventY > height) {
            eventY = height.toFloat()
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // create new path and paint
                currentPath = CPath()
                currentPaint = Paint()
                currentPaint?.isAntiAlias = true
                currentPaint?.color = color
                currentPaint?.style = mStyle
                currentPaint?.strokeJoin = Paint.Join.ROUND
                currentPaint?.strokeWidth = mSize
                currentPath?.paint = currentPaint
                currentPath?.moveTo(eventX, eventY)
                // capture touched locations
                lastTouchX = eventX
                lastTouchY = eventY
                mDrawableList.add(currentPath)
                mUndoList.clear()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(eventX - lastTouchX)
                val dy = abs(eventY - lastTouchY)
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    currentPath?.quadTo(
                        lastTouchX,
                        lastTouchY,
                        (eventX + lastTouchX) / 2,
                        (eventY + lastTouchY) / 2
                    )
                    lastTouchX = eventX
                    lastTouchY = eventY
                }
                //                int historySize = event.getHistorySize();
//                for (int i = 0; i < historySize; i++) {
//                    float historicalX = event.getHistoricalX(i);
//                    float historicalY = event.getHistoricalY(i);
//                    currentPath.lineTo(historicalX, historicalY);
//                }

                // After replaying history, connect the line to the touch point.
                //  currentPath.lineTo(eventX, eventY);
                val xcoords = (currentPath?.xcoords ?: 0f).toFloat()
                val ycoords = (currentPath?.ycoords ?: 0f).toFloat()
                val width: Float = (currentPath?.width ?: 0f).toFloat()
                val height: Float = (currentPath?.height ?: 0f).toFloat()
                dirtyRect.left =
                    (xcoords).toDouble().coerceAtMost(dirtyRect.left.toDouble())
                        .toFloat()
                dirtyRect.right =
                    (xcoords + width).coerceAtLeast(dirtyRect.right)
                dirtyRect.top = ycoords.coerceAtMost(dirtyRect.top)
                dirtyRect.bottom = (ycoords + height).coerceAtLeast(dirtyRect.bottom)

                // After replaying history, connect the line to the touch point.
                cleanDirtyRegion(eventX, eventY)
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.lineTo(eventX, eventY)
                parent.requestDisallowInterceptTouchEvent(false)
            }
            else -> return false
        }

        // Include some padding to ensure nothing is clipped
        invalidate()
        //                (int) (dirtyRect.left - 20),
//                (int) (dirtyRect.top - 20),
//                (int) (dirtyRect.right + 20),
//                (int) (dirtyRect.bottom + 20));

        // register most recent touch locations
        lastTouchX = eventX
        lastTouchY = eventY
        return true
    }

    /**
     * Handles the touch input if the mode is set to select
     *
     * @param event the touch event
     */
    private fun onTouchSelectMode(event: MotionEvent): Boolean {
        val li: ListIterator<CDrawable?> = mDrawableList.listIterator(mDrawableList.size)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                hovering = null
                pressStartTime = SystemClock.uptimeMillis()
                pressedX = event.x
                pressedY = event.y
                while (li.hasPrevious()) {
                    val d = li.previous()
                    if (d is CTransform) {
                        continue
                    }
                    val rect = d?.computeBounds()
                    if (rect?.contains(pressedX.toInt(), pressedY.toInt()) == true) {
                        hovering = d
                        break
                    }
                }
                if (hovering != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (hovering != null) {
                    updateHoveringPosition(event)
                    invalidate()
                    return true
                } else {
                    //break //Nothing is being dragged.
                }
            }
            MotionEvent.ACTION_UP -> {
                if (hovering != null) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                val pressDuration = SystemClock.uptimeMillis() - pressStartTime
                val distance = sqrt(
                    (event.x - pressedX).toDouble().pow(2.0) + (event.y - pressedY).toDouble()
                        .pow(2.0)
                )
                if (pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE) {
                    //It was a click not a drag.
                    if (hovering == null && deleteIconPosition.contains(event.x, event.y)) {
                        deleteSelection()
                        return true
                    }
                    selection = hovering
                    if (hovering != null) {
                        hoveringTranslation?.let { hovering?.removeTransform(it) }
                        mDrawableList.remove(hoveringTranslation)
                    }
                } else if (distance > MAX_CLICK_DISTANCE) {
                    //It was a drag. Move the object there.
                    if (hovering != null) {
                        updateHoveringPosition(event)
                    }
                }
                invalidate()
                hovering = null
                hoveringTranslation = null
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                if (hovering != null) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    hoveringTranslation?.let { hovering?.removeTransform(it) }
                    mDrawableList.remove(hoveringTranslation)
                    hovering = null
                    hoveringTranslation = null
                }
                return true
            }
        }
        return false
    }

    private fun updateHoveringPosition(event: MotionEvent) {
        val distance = sqrt(
            (event.x - pressedX).toDouble().pow(2.0) + (event.y - pressedY).toDouble().pow(2.0)
        )
        if (distance < MAX_CLICK_DISTANCE) {
            return  //Movement too small
        }
        if (hoveringTranslation == null) {
            hoveringTranslation = hovering?.let { CTranslation(it) }
            val v = Vector<Int>(2)
            v.add((event.x - pressedX).toInt())
            v.add((event.y - pressedY).toInt())
            hoveringTranslation?.direction = v
            hoveringTranslation?.let { hovering?.addTransform(it) }
            mDrawableList.add(hoveringTranslation)
            mUndoList.clear()
        } else {
            //Last transform was a translation. Replace translation with new coordinates.
            val v = Vector<Int>(2)
            v.add((event.x - pressedX).toInt())
            v.add((event.y - pressedY).toInt())
            hoveringTranslation?.direction = v
        }
    }
    /*******************************************
     * Drawing Events
     */
    /**
     * Draw the background on the canvas
     *
     * @param canvas         the canvas to draw on
     * @param backgroundMode one of BACKGROUND_STYLE_GRAPH_PAPER, BACKGROUND_STYLE_NOTEBOOK_PAPER, BACKGROUND_STYLE_BLANK
     */
    fun drawBackground(canvas: Canvas, backgroundMode: Int) {
        canvas.drawColor(mBackgroundColor)
        if (backgroundMode != BACKGROUND_STYLE_BLANK) {
            val linePaint = Paint()
            linePaint.color = Color.argb(50, 0, 0, 0)
            linePaint.style = mStyle
            linePaint.strokeJoin = Paint.Join.ROUND
            linePaint.strokeWidth = mSize - 2f
            when (backgroundMode) {
                BACKGROUND_STYLE_GRAPH_PAPER -> drawGraphPaperBackground(canvas, linePaint)
                BACKGROUND_STYLE_NOTEBOOK_PAPER -> drawNotebookPaperBackground(canvas, linePaint)
                else -> {
                    return
                }
            }
        }
        mRedrawBackground = false
    }

    /**
     * Draws a graph paper background on the view
     *
     * @param canvas the canvas to draw on
     * @param paint  the paint to use
     */
    private fun drawGraphPaperBackground(canvas: Canvas, paint: Paint) {
        var i = 0
        var doneH = false
        var doneV = false

        // while we still need to draw either H or V
        while (!(doneH && doneV)) {

            // check if there is more H lines to draw
            if (i < canvas.height) canvas.drawLine(
                0f,
                i.toFloat(),
                canvas.width.toFloat(),
                i.toFloat(),
                paint
            ) else doneH = true

            // check if there is more V lines to draw
            if (i < canvas.width) canvas.drawLine(
                i.toFloat(),
                0f,
                i.toFloat(),
                canvas.height.toFloat(),
                paint
            ) else doneV = true

            // declare as done
            i += 75
        }
    }

    /**
     * Draws a notebook paper background on the view
     *
     * @param canvas the canvas to draw on
     * @param paint  the paint to use
     */
    private fun drawNotebookPaperBackground(canvas: Canvas, paint: Paint) {
        var i = 0
        var doneV = false
        // draw horizental lines
        while (!doneV) {
            if (i < canvas.height) canvas.drawLine(
                0f,
                i.toFloat(),
                canvas.width.toFloat(),
                i.toFloat(),
                paint
            ) else doneV = true
            i += 75
        }
        // change line color
        paint.color = NOTEBOOK_LEFT_LINE_COLOR
        // draw side line
        canvas.drawLine(
            NOTEBOOK_LEFT_LINE_PADDING.toFloat(), 0f,
            NOTEBOOK_LEFT_LINE_PADDING.toFloat(), canvas.height.toFloat(), paint
        )
    }

    /**
     * Draw text on the screen
     *
     * @param text the text to draw
     * @param x    the x location of the text
     * @param y    the y location of the text
     * @param p    the paint to use. This is used for the TextSize, color. If null, the defaut is black with 20sp size.
     */
    fun drawText(text: String?, x: Int, y: Int, p: Paint?) {
        var p = p
        if (p == null) {
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                20f,
                context.resources.displayMetrics
            ).toInt()
            p = Paint()
            p.textSize = px.toFloat()
            p.color = Color.BLACK
        }
        mDrawableList.add(text?.let { CText(it, x, y, p) })
        mUndoList.clear()
        invalidate()
    }

    /**
     * Capture Text from the keyboard and draw it on the screen
     * //TODO Implement the method
     */
    private fun drawTextFromKeyboard() {
        Toast.makeText(context, "Touch where you want the text to be", Toast.LENGTH_LONG).show()
        //TODO
        mTextExpectTouch = true
    }

    /**
     * Retrieve the region needing to be redrawn
     *
     * @param eventX The current x location of the touch
     * @param eventY the current y location of the touch
     */
    private fun cleanDirtyRegion(eventX: Float, eventY: Float) {
        // figure out the sides of the dirty region
        dirtyRect.left = lastTouchX.coerceAtMost(eventX)
        dirtyRect.right = lastTouchX.coerceAtLeast(eventX)
        dirtyRect.top = lastTouchY.coerceAtMost(eventY)
        dirtyRect.bottom = lastTouchY.coerceAtLeast(eventY)
    }

    /**
     * Cancels the last operation. Works on both CDrawable and CTransform.
     */
    fun undo() {
        if (mDrawableList.size > 0) {
            val toUndo = mDrawableList[mDrawableList.size - 1]
            mUndoList.add(toUndo)
            mDrawableList.removeAt(mDrawableList.size - 1)
            if (toUndo is CTransform) {
                toUndo.drawable?.removeTransform(toUndo)
            }
            invalidate()
        }
    }

    /**
     * Re-instates the last undone operation.
     */
    fun redo() {
        if (mUndoList.size > 0) {
            val toRedo = mUndoList[mUndoList.size - 1]
            mDrawableList.add(toRedo)
            if (toRedo != null) {
                mDrawableList.addAll(toRedo.transforms)
            }
            mUndoList.remove(toRedo)
            if (toRedo is CTransform) {
                toRedo.drawable?.addTransform(toRedo)
            }
            invalidate()
        }
    }

    /**
     * Clean the canvas, remove everything drawn on the canvas.
     * WARNING: Before calling this, ask the user to confirm because **this cannot be undone**.
     */
    fun cleanPage() {
        // remove everything from the list
        mDrawableList.clear()
        currentPath = null
        mUndoList.clear()
        savePoint = 0
        // request to redraw the canvas
        invalidate()
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
    fun drawImage(x: Int, y: Int, width: Int, height: Int, pic: Bitmap?) {
        val bitmap = pic?.let { CBitmap(it, x, y) }
        bitmap?.width = width
        bitmap?.height = height
        mDrawableList.add(bitmap)
        mUndoList.clear()
        invalidate()
    }
    /*******************************************
     * Getters and Setters
     */// build drawing cache of the canvas, use it to create a new bitmap, then destroy it.

    // return the created bitmap.
    /**
     * Gets what has been drawn on the canvas so far as a bitmap
     *
     * @return Bitmap of the canvas.
     */
    val canvasBitmap: Bitmap
        get() {
            // build drawing cache of the canvas, use it to create a new bitmap, then destroy it.
            buildDrawingCache()
            val mCanvasBitmap = Bitmap.createBitmap(
                drawingCache
            )
            destroyDrawingCache()

            // return the created bitmap.
            return mCanvasBitmap
        }//No pixels at all

    /**
     * Gets what has been drawn on the canvas so far as a bitmap. Removes any margin around the drawn objects.
     *
     * @return Bitmap of the canvas, cropped.
     */
    val croppedCanvasBitmap: Bitmap?
        get() {
            if (cropBounds == null) {
                //No pixels at all
                return null
            }
            val mCanvasBitmap = canvasBitmap
            return Bitmap.createBitmap(
                mCanvasBitmap,
                cropBounds?.left ?: 0,
                cropBounds?.top ?: 0,
                cropBounds?.width() ?: 0,
                cropBounds?.height() ?: 0
            )
        }

    /**
     * @return the background color. Default is Color.WHITE.
     */
    fun getBackgroundColor(): Int {
        return mBackgroundColor
    }

    /**
     * Setter for the background color.
     *
     * @param mBackgroundColor The new background color.
     */
    override fun setBackgroundColor(mBackgroundColor: Int) {
        this.mBackgroundColor = mBackgroundColor
    }
    /**
     * @return the background decorations mode. Default is BACKGROUND_STYLE_BLANK.
     */
    /**
     * Setter for the background decorations mode. Can be BACKGROUND_STYLE_BLANK*, BACKGROUND_STYLE_NOTEBOOK_PAPER, or BACKGROUND_STYLE_GRAPH_PAPER.
     *
     * @param mBackgroundMode
     */
    var backgroundMode: Int
        get() = mBackgroundMode
        set(mBackgroundMode) {
            this.mBackgroundMode = mBackgroundMode
            invalidate()
        }

    /**
     * @return The drawing style. Can be Paint.Style.FILL, Paint.Style.STROKE, or Paint.Style.FILL_AND_STROKE. Default is Paint.Style.STROKE.
     */
    fun getStyle(): Paint.Style? {
        return mStyle
    }

    /**
     * Setter for the drawing style.
     *
     * @param mStyle The new drawing style. Can be Can be FILL, STROKE, or FILL_AND_STROKE.
     */
    fun setStyle(mStyle: Paint.Style) {
        this.mStyle = mStyle
    }

    /**
     * @return The width of the line for drawing.
     */
    fun getSize(): Float {
        return mSize
    }

    /**
     * Setter for the line width. The default is 5.
     *
     * @param mSize The new width for the line.
     */
    fun setSize(mSize: Float) {
        this.mSize = mSize
    }

    /**
     * @return The interaction mode. The default is DRAW_MODE.
     */// if the value passed is not any of the flags, set the library to locked mode
    /**
     * Setter for the interaction mode. Can be DRAW_MODE, SELECT_MODE, ROTATE_MODE, or LOCKED_MODE.
     *
     * @param interactionMode
     */
    var interactionMode: Int
        get() = mInteractionMode
        set(interactionMode) {

            // if the value passed is not any of the flags, set the library to locked mode
            var interactionMode = interactionMode
            if (interactionMode > LOCKED_MODE) interactionMode =
                LOCKED_MODE else if (interactionMode < DRAW_MODE) interactionMode = LOCKED_MODE
            mInteractionMode = interactionMode
            invalidate()
        }

    /**
     * @return the list of all CDrawables in order of insertion.
     */
    val drawablesList: List<CDrawable?>
        get() = mDrawableList

    /**
     * Indicates that all CDrawables in the list have been saved.
     */
    fun markSaved() {
        savePoint = mDrawableList.size
    }

    /**
     * @return true if there were no new operations done after the last call to markSaved().
     */
    val isSaved: Boolean
        get() = savePoint == mDrawableList.size//Some things were deleted.

    /**
     * @return The list of all CDrawables that have been added after the last call to markSaved().
     */
    val unsavedDrawablesList: List<CDrawable?>
        get() = if (savePoint > mDrawableList.size) {
            //Some things were deleted.
            ArrayList()
        } else mDrawableList.subList(savePoint, mDrawableList.size)

    /**
     * Deletes all CDrawables that were added after the last call to markSaved().
     */
    fun revertUnsaved() {
        val unsaved: List<CDrawable?> = ArrayList(
            unsavedDrawablesList
        )
        for (d in unsaved) {
            deleteDrawable(d)
        }
    }

    /**
     * Marks the last inserted CDrawable as the selected object.
     */
    fun selectLastDrawn() {
        if (mDrawableList.isEmpty()) {
            return
        }
        val li: ListIterator<CDrawable?> = mDrawableList.listIterator(mDrawableList.size)
        while (li.hasPrevious()) {
            val d = li.previous()
            if (d is CTransform) {
                continue
            }
            selection = d
            break
        }
        invalidate()
    }

    /**
     * Cancels all selection. No object will be selected.
     */
    fun deSelect() {
        selection = null
        invalidate()
    }

    /**
     * Deletes the currently selected object. No object will be selected after that.
     */
    fun deleteSelection() {
        if (selection == null) {
            return
        }
        deleteDrawable(selection)
        selection = null
    }

    /**
     * Removes a specific CDrawable.
     *
     * @param drawable The object to remove.
     */
    fun deleteDrawable(drawable: CDrawable?) {
        if (drawable == null) {
            return
        }
        val toDelete = ArrayList<CDrawable?>()
        toDelete.add(drawable)
        toDelete.addAll(drawable.transforms)
        mDrawableList.removeAll(toDelete)
        if (deletionListener != null) {
            deletionListener?.deleted(drawable)
        }
        mUndoList.add(drawable)
        invalidate()
    }

    /**
     * Setter for the "delete" icon. The default is android.R.drawable.ic_menu_delete.
     *
     * @param newIcon The new delete icon.
     */
    fun setDeleteIcon(newIcon: Bitmap) {
        deleteIcon = newIcon
    }

    /**
     * Setter for the deletion event listener. Refer to the Observer pattern.
     *
     * @param newListener The listener for any deletion event.
     */
    fun setDeletionListener(newListener: DeletionListener?) {
        deletionListener = newListener
    }

    /**
     * This interface must be implemented by your deletion event listener.
     */
    interface DeletionListener {
        /**
         * This method will be called whenever a CDrawable is deleted.
         *
         * @param drawable The object that was deleted.
         */
        fun deleted(drawable: CDrawable?)
    }
}