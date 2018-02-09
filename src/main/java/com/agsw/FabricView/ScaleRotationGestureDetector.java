package com.agsw.FabricView;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by emmanuel.proulx on 2018-02-05.
 */

public class ScaleRotationGestureDetector extends ScaleGestureDetector {
    private float rotation;
    private static final int NO_FINGER = -1;
    private int firstFingerId;
    private float firstFingerX;
    private float firstFingerY;
    private float secondFingerX;
    private float secondFingerY;
    private int secondFingerId;
    private boolean rotationInProgress = false;
    private OnScaleRotationGestureListener rotationListener;
    private float startSpan = -1;

    public ScaleRotationGestureDetector(Context context, OnScaleRotationGestureListener rotationListener) {
        super(context, rotationListener);
        firstFingerId = NO_FINGER;
        secondFingerId = NO_FINGER;
        this.rotationListener = rotationListener;
    }

    public float getRotation() {
        return rotation;
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                firstFingerId = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                secondFingerId = event.getPointerId(event.getActionIndex());
                secondFingerX = event.getX(event.findPointerIndex(firstFingerId));
                secondFingerY = event.getY(event.findPointerIndex(firstFingerId));
                firstFingerX = event.getX(event.findPointerIndex(secondFingerId));
                firstFingerY = event.getY(event.findPointerIndex(secondFingerId));
                break;
            case MotionEvent.ACTION_MOVE:
                if(firstFingerId != NO_FINGER && secondFingerId != NO_FINGER){
                    float nfX, nfY, nsX, nsY;
                    nsX = event.getX(event.findPointerIndex(firstFingerId));
                    nsY = event.getY(event.findPointerIndex(firstFingerId));
                    nfX = event.getX(event.findPointerIndex(secondFingerId));
                    nfY = event.getY(event.findPointerIndex(secondFingerId));

                    rotation = angleBetweenLines(firstFingerX, firstFingerY, secondFingerX, secondFingerY, nfX, nfY, nsX, nsY);

                    if (rotationListener != null) {
                        rotationListener.onRotate(this);
                    }
                    if(startSpan == -1) {
                        startSpan = getCurrentSpan();
                    }
                    rotationInProgress = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                firstFingerId = NO_FINGER;
                startSpan = -1;
                rotationInProgress = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                secondFingerId = NO_FINGER;
                startSpan = -1;
                rotationInProgress = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                firstFingerId = NO_FINGER;
                secondFingerId = NO_FINGER;
                startSpan = -1;
                rotationInProgress = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private float angleBetweenLines (float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY)
    {
        float angle1 = (float) Math.atan2( (fY - sY), (fX - sX) );
        float angle2 = (float) Math.atan2( (nfY - nsY), (nfX - nsX) );

        float angle = ((float)Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    public boolean isInProgress() {
        if(rotationInProgress)
            return true;
        return super.isInProgress();
    }

    /**
     * Overriding getScaleFactor() because the base class scales based on the ever-changing scale
     * factor of that very moment. What we want is the ratio of scale since the beginning of
     * the pinch, not the ratio of scale since the last measurement.
     *
     * @return The ratio between the current span and the span at the beginning of the pinch.
     */
    public float getScaleFactor() {
        float result = startSpan > 0 ? getCurrentSpan() / startSpan : 1;
        if(result < 0.01f) {
            //Do not go below 1% of the original size.
            return 0.01f;
        }
        if(result > 100f) {
            //Do not go above 100 times the original size.
            return 100f;
        }
        return result;
    }

    public interface OnScaleRotationGestureListener extends OnScaleGestureListener {
        /**
         * Responds to rotation events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *          retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         *          as handled. If an event was not handled, the detector
         *          will continue to accumulate movement until an event is
         *          handled. This can be useful if an application, for example,
         *          only wants to update scaling factors if the change is
         *          greater than 0.01.
         */
        public abstract boolean onRotate(ScaleRotationGestureDetector detector);
    }
}
