package com.alibaba.android.vlayout.customadapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


/**
 * @author zhy
 */
public class BaseDividerGridItem extends RecyclerView.ItemDecoration {
    protected DividerHelper dividerHelper;
    private String TAG = this.getClass().getSimpleName();

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerWidth;
    private int mDividerWidthHalf;
    //底部有加载view的时候防止间距过大
    private boolean bottomSpacing;


    public BaseDividerGridItem(Context context) {
        this(context, -1);
    }

    public BaseDividerGridItem(Context context, int dividerHeight) {
        this(context, dividerHeight, null);
    }

    public BaseDividerGridItem(Context context, int dividerHeight, @DrawableRes int drawableId) {
        this(context, dividerHeight, ContextCompat.getDrawable(context, drawableId));
    }

    public BaseDividerGridItem(Context context, int dividerHeight, Drawable drawable) {
        dividerHelper=new DividerHelper();
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if (drawable == null) {
            mDivider = a.getDrawable(0);
        } else {
            mDivider = drawable;
        }
        if (dividerHeight < 0) {
            mDividerHeight = mDivider.getIntrinsicHeight();
            mDividerWidth = mDivider.getIntrinsicWidth();
        } else {
            mDividerHeight = dividerHeight;
            mDividerWidth = dividerHeight;
        }
        if(mDividerWidth>1){
            mDividerWidthHalf=mDividerWidth/2;
        }else{
            mDividerWidthHalf=mDividerWidth;
        }
        a.recycle();
    }

    public void setBottomSpacing(boolean bottomSpacing) {
        this.bottomSpacing = bottomSpacing;
    }

    private int getDividerHeight() {
        return mDividerHeight;
    }

    private int getDividerWidth() {
        return mDividerWidth;
    }

    public int getDividerWidthHalf() {
        return mDividerWidthHalf;
    }

    private int getAdapterBottomViewCount(RecyclerView parent) {
        /*RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof MyLoadMoreAdapter && ((MyLoadMoreAdapter) adapter).onLoadMoreListener != null) {
            return ((MyLoadMoreAdapter) adapter).getLoadMoreViewCount();
        }*/
        return 0;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        final boolean reverseLayout = layoutManager.getReverseLayout();

        if(reverseLayout){
            drawHorizontalForReverse(c, parent);
            drawVertical(c, parent);
        }else{
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontalForReverse(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + getDividerWidth();
            final int top = child.getTop() - params.topMargin-getDividerHeight();
            final int bottom = child.getTop() - params.topMargin;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + getDividerWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + getDividerHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + getDividerHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        pos=pos-dividerHelper.getHeaderViewCount(parent.getAdapter());
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        pos=pos-dividerHelper.getHeaderViewCount(parent.getAdapter());
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int ranger=childCount % spanCount;
            if(ranger == 0){
                ranger = spanCount;
            }
            childCount = childCount - ranger;
            // 如果是最后一行，则不需要绘制底部
            if (pos >= childCount){
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //偏移量包着margin包着padding
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        RecyclerView.Adapter adapter = parent.getAdapter();

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        boolean reverseLayout = layoutManager.getReverseLayout();
        if(reverseLayout){
            if(dividerHelper.getFooterViewCount(adapter)>0&&itemPosition==parent.getAdapter().getItemCount()-1){
                // 如果是最后一个是脚布局，这不需要偏移顶部
                outRect.set(0,0,0,0);
            }else /*if(itemPosition==parent.getAdapter().getItemCount()-1&&adapter instanceof MyLoadMoreAdapter&&((MyLoadMoreAdapter) adapter).onLoadMoreListener!=null) {
                // 如果是最后一行，则不需要绘制底部
                outRect.set(0,0, 0, 0);
            }else if(itemPosition==parent.getAdapter().getItemCount()-1&&adapter instanceof MyLoadMoreAdapter &&((MyLoadMoreAdapter) adapter).onLoadMoreListener!=null){
                outRect.set(0,0, 0, 0);
            }else */if(isLastColum(parent, itemPosition, spanCount,  childCount-getAdapterBottomViewCount(parent))&&isLastRaw(parent, itemPosition, spanCount, childCount-getAdapterBottomViewCount(parent))){
                //既是最后一个行又是最后一列
                outRect.set(0,0, 0, 0);
            }else if(itemPosition<dividerHelper.getHeaderViewCount(parent.getAdapter())){
                //如果是headerview则只偏移顶部
                outRect.set(0, getDividerHeight(),0, 0);
            }else if(dividerHelper.getFooterViewCount(parent.getAdapter())>0&&dividerHelper.isFooterViewPos(itemPosition,adapter)){
                //如果是footerview则只偏移顶部
                outRect.set(0, getDividerHeight(),0, 0);
            }else if (isLastColum(parent, itemPosition, spanCount, childCount-getAdapterBottomViewCount(parent))) {
                // 如果是最后一列且不是最后一个，则不需要绘制右边
                outRect.set(0, getDividerHeight(), 0, 0);
            }else if (isLastRaw(parent, itemPosition, spanCount, childCount-getAdapterBottomViewCount(parent))) {
                // 如果是最后一行，则不需要绘制底部
                outRect.set(0, 0, getDividerWidth(), 0);
            }else {
                outRect.set(0, getDividerHeight(), getDividerHeight(), 0);
            }
        }else{
            if(dividerHelper.getFooterViewCount(adapter)>0&&itemPosition==parent.getAdapter().getItemCount()-1){
                // 如果是最后一个是脚布局，这不需要偏移底部
                outRect.set(0,0,0, 0);
            }else /*if(itemPosition==parent.getAdapter().getItemCount()-1&&adapter instanceof MyLoadMoreAdapter&&((MyLoadMoreAdapter) adapter).onLoadMoreListener!=null) {
                // 如果是最后一行，则不需要绘制底部
                outRect.set(0,0,0,0);
            }else if(itemPosition==parent.getAdapter().getItemCount()-1&&adapter instanceof MyLoadMoreAdapter &&((MyLoadMoreAdapter) adapter).onLoadMoreListener!=null) {
                outRect.set(0,0,0,0);
            }else*/ if(isLastColum(parent, itemPosition, spanCount,  childCount-getAdapterBottomViewCount(parent))&&isLastRaw(parent, itemPosition, spanCount, childCount-getAdapterBottomViewCount(parent))){
                //既是最后一个行又是最后一列
                outRect.set(0,0, 0, 0);
            }else if(itemPosition<dividerHelper.getHeaderViewCount(parent.getAdapter())){
                //如果是headerview则只偏移底部
                outRect.set(0, 0, 0, getDividerHeight());
            }else if(dividerHelper.getFooterViewCount(parent.getAdapter())>0&&dividerHelper.isFooterViewPos(itemPosition,adapter)){
                //如果是footerview则只偏移底部
                outRect.set(0, 0, 0, getDividerHeight());
            }else if (isLastColum(parent, itemPosition, spanCount, childCount-getAdapterBottomViewCount(parent))) {
                // 如果是最后一列且不是最后一个，则不需要绘制右边
                outRect.set(0, 0, 0, getDividerHeight());
            }else if (isLastRaw(parent, itemPosition, spanCount, childCount-getAdapterBottomViewCount(parent))) {
                // 如果是最后一行，则不需要绘制底部
                outRect.set(0, 0, getDividerWidth(), 0);
            }else {
                outRect.set(0, 0, getDividerHeight(), getDividerHeight());
            }
        }

    }

}