package com.lzp.leftslider;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * Created by SKJP on 2016/7/21.
 */
public class DrawerFrame extends FrameLayout {
    private Context mContext;
    private ViewGroup mContentView;
    private ViewGroup mLeftView;
    private ViewGroup mContentParent;
    private int startX, startY;
    private int intercepX, intercepY;
    private int scaledTouchSlop;
    private boolean isOpen = false, isMoving = false;
    private VelocityTracker mVelocityTracker;

    public DrawerFrame(Context context, ViewGroup contentView, ViewGroup leftView) {
        super(context);
//        Log.e("Test", "DrawerFrame");
        reconfigureViewHierarchy(contentView, leftView);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mVelocityTracker = VelocityTracker.obtain();
    }

    private void reconfigureViewHierarchy(ViewGroup contentView, ViewGroup leftView) {
        LayoutParams params = null;

        if (mLeftView != leftView) {
            if (mLeftView != null) {
                removeView(mLeftView);
            }
            mLeftView = leftView;
            if (mLeftView != null) {
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                addView(mLeftView, params);
            }
        }
        if (contentView != mContentView) {
            if (mContentView != null) {
                removeView(mContentView);
                if (mContentParent != null) {
                    mContentParent.removeView(this);
                    mContentParent.addView(mContentView);
                    mContentParent.setOnClickListener(null);
                    mContentParent = null;
                }
            }
            mContentView = contentView;
            if (mContentView != null) {
                ViewParent vp = mContentView.getParent();
                if (vp instanceof ViewGroup) {
                    mContentParent = (ViewGroup) vp;
                    mContentParent.removeView(mContentView);
                } else {
                    mContentParent = null;
                }
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                addView(mContentView, params);
            }
            if (mContentParent != null) {
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mContentParent.addView(this, params);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.e("Test", "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        if (mLeftView != null && mLeftView.getVisibility() != View.GONE) {
            mLeftView.layout(left-100, top, right-100, bottom);
        }
        if (mContentView != null && mContentView.getVisibility() != View.GONE) {
            mContentView.layout(mContentView.getLeft(), mContentView.getTop(), mContentView
                    .getLeft() + right, bottom);
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean inTercept = false;
//        int left = mContentView.getLeft();
//        int width = mContentView.getWidth();
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                intercepX = (int) ev.getX();
//                intercepY = (int) ev.getY();
//                inTercept = true;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int curX = (int) ev.getX();
//                int curY = (int) ev.getY();
//                int deltaX = curX - intercepX;
//                int deltaY = curY - intercepY;
//                if (Math.abs(deltaX) > Math.abs(deltaY)) {
//                    if ((isOpen && deltaX > 0) || (!isOpen && deltaX < 0)) {
//                        inTercept = false;
//                    } else {
//                        inTercept = true;
//                    }
//                } else {
//                    inTercept = false;
//                }
//                intercepX = curX;
//                intercepY = curY;
//                break;
//            case MotionEvent.ACTION_UP:
//                inTercept = false;
//                break;
//            default:
//                break;
//        }
//        return inTercept;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int curY = (int) event.getY();
                int deltaX = curX - startX;
                int deltaY = curY - startY;
                if ((isOpen && deltaX > 0) || (!isOpen && deltaX < 0)) {
                    break;
                }
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    int left = (int) mContentView.getX();
                    if (deltaX < 0) {
                        if (left == 0) {
                            break;
                        }
                        if (left + deltaX < 0) {
                            deltaX = -left;
                        }
                    } else {
                        if (left == (mContentView.getWidth() / 2)) {
                            break;
                        }
                        if ((left + deltaX) > (mContentView.getWidth() / 2)) {
                            deltaX = mContentView.getWidth() / 2 - left;
                        }
                    }
                    int desPos = (int) mContentView.getX() + deltaX;
                    ViewCompat.animate(mContentView).x(desPos).setDuration(0).start();
                    desPos = (int) mLeftView.getX() + deltaX / 10;
                    ViewCompat.animate(mLeftView).x(desPos).setDuration(0).start();
                }
                if (mContentView.getX() == mContentView.getWidth() / 2) {
                    isOpen = true;
                } else if (mContentView.getX() == 0) {
                    isOpen = false;
                } else {
                    isMoving = true;
                }
                startX = curX;
                startY = curY;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                float vx = mVelocityTracker.getXVelocity();
                if (Math.abs(vx) > 100f && isMoving) {
                    if (vx > 0) {
                        ViewCompat.animate(mContentView).x(mContentView.getWidth() /
                                2).setDuration(200).withLayer().setInterpolator(new
                                LinearInterpolator()).start();
                        ViewCompat.animate(mLeftView).x(0).setDuration(200).withLayer()
                                .setInterpolator(new LinearInterpolator()).start();
                        isOpen = true;
                        isMoving = false;
                    } else {
                        ViewCompat.animate(mContentView).x(0).setDuration(200)
                                .withLayer().setInterpolator(new
                                LinearInterpolator()).start();
                        ViewCompat.animate(mLeftView).x(-100).setDuration(200).withLayer()
                                .setInterpolator(new LinearInterpolator()).start();
                        isOpen = false;
                        isMoving = false;
                    }
                }
                mVelocityTracker.clear();
                break;
            default:
                break;
        }
        return true;
    }
}
