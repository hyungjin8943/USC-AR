package edu.usc.UscAR.custom;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Youngmin on 2016. 5. 30..
 */
public class CustomSlidingPaneLayout extends SlidingPaneLayout{
    public CustomSlidingPaneLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomSlidingPaneLayout(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public CustomSlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
