package com.star.customviewgrouppartii;


import android.content.Context;
import android.graphics.PointF;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class ViewDragHelperViewGroup extends LinearLayout {

    private ViewDragHelper mViewDragHelper;

    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;

    private PointF mAutoBackOriginalPosition;

    public ViewDragHelperViewGroup(Context context) {
        this(context, null);
    }

    public ViewDragHelperViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewDragHelperViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mViewDragHelper = ViewDragHelper.create(this, 1, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mDragView || child == mAutoBackView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int leftBound = getPaddingLeft();
                int rightBound = getMeasuredWidth() - getPaddingRight() - child.getWidth();

                return Math.min(Math.max(left, leftBound), rightBound);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                int topBound = getPaddingTop();
                int bottomBound = getMeasuredHeight() - getPaddingBottom() - child.getHeight();

                return Math.min(Math.max(top, topBound), bottomBound);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == mAutoBackView) {
                    mViewDragHelper.settleCapturedViewAt(
                            (int) mAutoBackOriginalPosition.x,
                            (int) mAutoBackOriginalPosition.y);
                    invalidate();
                }
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mViewDragHelper.captureChildView(mEdgeTrackerView, pointerId);
            }
        });

        mViewDragHelper.setEdgeTrackingEnabled(mViewDragHelper.EDGE_LEFT);

        mAutoBackOriginalPosition = new PointF();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mViewDragHelper.processTouchEvent(event);

        return true;
    }

    @Override
    public void computeScroll() {

        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginalPosition.x = mAutoBackView.getLeft();
        mAutoBackOriginalPosition.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }

}
