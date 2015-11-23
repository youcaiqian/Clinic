package com.clinic.clinicqueue.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoResizeTextView extends TextView {
private interface SizeTester {
    /**
     * 
     * @param suggestedSize
     *            Size of text to be tested
     * @param availableSpace
     *            available space in which text must fit
     * @return an integer < 0 if after applying {@code suggestedSize} to
     *         text, it takes less space than {@code availableSpace}, > 0
     *         otherwise
     */
    public int onTestSize(int suggestedSize, RectF availableSpace);
}

private RectF mTextRect;

private RectF mAvailableSpaceRect;

private TextPaint mPaint;

private float mMaxTextSize;

private float mSpacingMult = 1.0f;

private float mSpacingAdd = 0.0f;

private float mMinTextSize = 20;

private static final int NO_LINE_LIMIT = -1;

private boolean mInitiallized;
private SizeTester mSizeTester;

public AutoResizeTextView(Context context) {
    super(context);
    initialize();
}

public AutoResizeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize();
}

public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize();
}

private void initialize() {
	if(!mInitiallized)
	{
	    mPaint = new TextPaint(getPaint());
	    mMaxTextSize = getTextSize();
	    mAvailableSpaceRect = new RectF();
	    mTextRect = new RectF();
	    mInitiallized = true;
	    mSpacingMult = 1.0f;
	    mSpacingAdd = 0.0f;
	    mMinTextSize = 20;
	    mSizeTester = new SizeTester() {
	        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	        @Override
	        public int onTestSize(int suggestedSize, RectF availableSPace) {
	            mPaint.setTextSize(suggestedSize);
	            String text = getText().toString();
	            mTextRect.bottom = mPaint.getFontSpacing();
	            mTextRect.right = mPaint.measureText(text);
	            mTextRect.offsetTo(0, 0);
	            if (availableSPace.contains(mTextRect)) {
	                // may be too small, don't worry we will find the best match
	                return -1;
	            } else {
	                // too big
	                return 1;
	            }
	        }
	    };
	}
}

@Override
public void setTextSize(float size) {
    mMaxTextSize = size;
}

public void adjustTextSize(int width, int height) {
	initialize();
	
    int startSize = (int) mMinTextSize;
    mAvailableSpaceRect.right = width;
    mAvailableSpaceRect.bottom = height;
    super.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            efficientTextSizeSearch(startSize, (int) mMaxTextSize,
                    mSizeTester, mAvailableSpaceRect));
}

private int efficientTextSizeSearch(int start, int end,
        SizeTester sizeTester, RectF availableSpace) {
    int size = binarySearch(start, end, sizeTester, availableSpace);
    return size;
}

private static int binarySearch(int start, int end, SizeTester sizeTester,
        RectF availableSpace) {
    int lastBest = start;
    int lo = start;
    int hi = end - 1;
    int mid = 0;
    while (lo <= hi) {
        mid = (lo + hi) >>> 1;
        int midValCmp = sizeTester.onTestSize(mid, availableSpace);
        if (midValCmp < 0) {
            lastBest = lo;
            lo = mid + 1;
        } else if (midValCmp > 0) {
            hi = mid - 1;
            lastBest = hi;
        } else {
            return mid;
        }
    }
    // make sure to return last best
    // this is what should always be returned
    return lastBest;

}

}
