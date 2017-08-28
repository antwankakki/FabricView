package com.agsw.FabricView.DrawableObjects;

import android.graphics.Canvas;

/**
 * Created by emmanuel.proulx on 2017-08-27.
 */

public abstract class CTransform extends CDrawable {
    abstract Canvas applyTransform(Canvas canvas);
}
