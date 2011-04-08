package com.camptocamp.android.gis.control;

import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.nutiteq.BasicMapComponent;

public class MyMapView extends com.nutiteq.android.MapView {

    private static final int MIN = 60;
    private static final int MAX = 120;
    private boolean mGestureInProgress;

    private float mOldDist;
    private float mNewDist;
    private float mAmount = 0;
    private float mAmountAbs;

    public MyMapView(Context context, BasicMapComponent component) {
        super(context, component);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mapComponent == null) {
            // FIXME: Why is this happening ?
            // adb shell monkey -p de.georepublic.android.gis -v 500 --throttle 20
            return false;
        }
        final int action = event.getAction();
        if (!mGestureInProgress) {
            if ((action == MotionEvent.ACTION_POINTER_1_DOWN || action == MotionEvent.ACTION_POINTER_2_DOWN)
                    && event.getPointerCount() >= 2) {
                mOldDist = getDistance(event);
                // if (mOldDist > 10f) {
                // // Start MT gesture
                mGestureInProgress = true;
                // }
            }
            else {
                // Normal touch action
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mapComponent.pointerPressed(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mapComponent.pointerDragged(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        mapComponent.pointerReleased(x, y);
                        mGestureInProgress = false;
                        break;
                }
            }
        }
        else if (action == MotionEvent.ACTION_UP) {
            mGestureInProgress = false;
        }
        else {
            if (mGestureInProgress) {
                mNewDist = getDistance(event);
                mAmount += mNewDist - mOldDist;
                mAmountAbs = Math.abs(mAmount);
                if (mAmountAbs > MIN && mAmountAbs < MAX) {
                    if (mAmount > 0) {
                        mapComponent.zoomIn();
                    }
                    else {
                        mapComponent.zoomOut();
                    }
                    mAmount = 0;
                }
                else if (mAmountAbs > MAX) {
                    mAmount = 0;
                }
                mOldDist = mNewDist;
            }
        }
        return true;
    }

    private float getDistance(final MotionEvent event) {
        final float x = event.getX(0) - event.getX(1);
        final float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
}
