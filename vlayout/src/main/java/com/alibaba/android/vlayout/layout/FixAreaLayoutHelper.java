

package com.alibaba.android.vlayout.layout;

import com.alibaba.android.vlayout.LayoutManagerHelper;

import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * LayoutHelper that will be located as fix position
 */
public abstract class FixAreaLayoutHelper extends BaseLayoutHelper {
    protected FixAreaAdjuster mAdjuster = FixAreaAdjuster.mDefaultAdjuster;

    protected FixViewAnimatorHelper mFixViewAnimatorHelper;

    public void setAdjuster(FixAreaAdjuster adjuster) {
        this.mAdjuster = adjuster;
    }

    public void setFixViewAnimatorHelper(
            FixViewAnimatorHelper fixViewAnimatorHelper) {
        mFixViewAnimatorHelper = fixViewAnimatorHelper;
    }

    @Override
    public void adjustLayout(int startPosition, int endPosition, LayoutManagerHelper helper) {

    }

    public boolean isFixLayout() {
        return true;
    }

    public interface FixViewAnimatorHelper {

        ViewPropertyAnimator onGetFixViewAppearAnimator(View fixView);

        ViewPropertyAnimator onGetFixViewDisappearAnimator(View fixView);

    }

    @Override
    public int computeMarginStart(int offset, boolean isLayoutEnd, boolean useAnchor, LayoutManagerHelper helper) {
        return 0;
    }

    @Override
    public int computeMarginEnd(int offset, boolean isLayoutEnd, boolean useAnchor, LayoutManagerHelper helper) {
        return 0;
    }

    @Override
    public int computePaddingStart(int offset, boolean isLayoutEnd, boolean useAnchor, LayoutManagerHelper helper) {
        return 0;
    }

    @Override
    public int computePaddingEnd(int offset, boolean isLayoutEnd, boolean useAnchor, LayoutManagerHelper helper) {
        return 0;
    }
}
