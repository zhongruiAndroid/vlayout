

package com.alibaba.android.vlayout;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * An implement of {@link LayoutHelperFinder} which finds layoutHelpers by position
 */
public class RangeLayoutHelperFinder extends LayoutHelperFinder {

    @NonNull
    private List<LayoutHelperItem> mLayoutHelperItems = new LinkedList<>();

    @NonNull
    private List<LayoutHelper> mLayoutHelpers = new LinkedList<>();

    @NonNull
    private List<LayoutHelper> mReverseLayoutHelpers =new LinkedList<>();

    private LayoutHelperItem[] mSortedLayoutHelpers = null;

    @NonNull
    private Comparator<LayoutHelperItem> mLayoutHelperItemComparator = new Comparator<LayoutHelperItem>() {
        @Override
        public int compare(LayoutHelperItem lhs, LayoutHelperItem rhs) {
            return lhs.getStartPosition() - rhs.getStartPosition();
        }
    };

    @Override
    protected List<LayoutHelper> reverse() {
        return mReverseLayoutHelpers;
    }

    /**
     * @param layouts layoutHelpers that handled
     */
    @Override
    public void setLayouts(@Nullable List<LayoutHelper> layouts) {
        mLayoutHelpers.clear();
        mReverseLayoutHelpers.clear();
        mLayoutHelperItems.clear();
        if (layouts != null) {
            ListIterator<LayoutHelper> iterator = layouts.listIterator();
            LayoutHelper helper = null;
            while (iterator.hasNext()) {
                helper = iterator.next();
                mLayoutHelpers.add(helper);
                mLayoutHelperItems.add(new LayoutHelperItem(helper));
            }

            while (iterator.hasPrevious()) {
                mReverseLayoutHelpers.add(iterator.previous());
            }

            // Collections.sort(mLayoutHelperItems, mLayoutHelperItemComparator);
            mSortedLayoutHelpers = mLayoutHelperItems.toArray(new LayoutHelperItem[mLayoutHelperItems.size()]);
            Arrays.sort(mSortedLayoutHelpers, mLayoutHelperItemComparator);
        }
    }

    @NonNull
    @Override
    protected List<LayoutHelper> getLayoutHelpers() {
        return mLayoutHelpers;
    }

    @Nullable
    @Override
    public LayoutHelper getLayoutHelper(int position) {
        if (mSortedLayoutHelpers == null || mSortedLayoutHelpers.length == 0) {
            return null;
        }
        final int count = mSortedLayoutHelpers.length;

        int s = 0, e = count - 1, m;
        LayoutHelperItem rs = null;
        // binary search range
        while (s <= e) {
            m = (s + e) / 2;
            rs = mSortedLayoutHelpers[m];
            if (rs.getStartPosition() > position) {
                e = m - 1;
            } else if (rs.getEndPosition() < position) {
                s = m + 1;
            } else if (rs.getStartPosition() <= position && rs.getEndPosition() >= position) {
                break;
            }

            rs = null;
        }

        return rs == null ? null : rs.layoutHelper;
    }

    static class LayoutHelperItem {

        LayoutHelperItem(LayoutHelper helper) {
            this.layoutHelper = helper;
        }

        LayoutHelper layoutHelper;

        public int getStartPosition() {
            return layoutHelper.getRange().getLower();
        }

        public int getEndPosition() {
            return layoutHelper.getRange().getUpper();
        }

    }
}
