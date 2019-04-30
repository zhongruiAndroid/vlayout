package com.alibaba.android.vlayout.customadapter;

import android.support.v7.widget.RecyclerView;


/**
 * Created by Administrator on 2018/2/28.
 */

public class DividerHelper {

    private int firstDividerHeight=2;
    private int lastDividerWidth=2;
    private boolean isShowFirstDivider;
    //倒数第一个和倒数第二个item分割线
    private boolean isShowLastDivider=true;

    public int getFirstDividerHeight() {
        return firstDividerHeight;
    }

    public void setFirstDividerHeight(int firstDividerHeight) {
        this.firstDividerHeight = firstDividerHeight;
    }

    public int getLastDividerWidth() {
        return lastDividerWidth;
    }

    public void setLastDividerWidth(int lastDividerWidth) {
        this.lastDividerWidth = lastDividerWidth;
    }

    public boolean isShowFirstDivider() {
        return isShowFirstDivider;
    }

    public void setShowFirstDivider(boolean showFirstDivider) {
        isShowFirstDivider = showFirstDivider;
    }

    public boolean isShowLastDivider() {
        return isShowLastDivider;
    }

    public void setShowLastDivider(boolean showLastDivider) {
        isShowLastDivider = showLastDivider;
    }



    public int getFirstDividerHeightOffset(int itemPosition){
        int offset=0;
        if(itemPosition==0&&isShowFirstDivider()){
            //第一个item绘制偏移量
            offset=getFirstDividerHeight();
        }
        return offset;
    }
    public boolean isMyAdapter(RecyclerView.Adapter adapter){
//        return true;
        return false;
    }

    public int getViewCount(RecyclerView.Adapter adapter){
        return adapter.getItemCount();
    }
    public int getDataCount(RecyclerView.Adapter adapter){
        return adapter.getItemCount();
    }
    public int getHeaderViewCount(RecyclerView.Adapter adapter){
//        if(adapter instanceof MyLoadMoreAdapter){
//            return ((MyLoadMoreAdapter) adapter).getHeaderCount();
//        }else{
            return 0;
//        }
    }
    public int getFooterViewCount(RecyclerView.Adapter adapter){
//        if(adapter instanceof MyLoadMoreAdapter){
//            return ((MyLoadMoreAdapter) adapter).getFooterCount();
//        }else{
            return 0;
//        }
    }
    public boolean isFooterViewPos(int position,RecyclerView.Adapter adapter) {
        return false;
        /*int loadMoreViewCount=0;
        int dataCount=adapter.getItemCount();
        if(adapter instanceof MyLoadMoreAdapter &&((MyLoadMoreAdapter) adapter).onLoadMoreListener!=null){
            loadMoreViewCount=1;
            dataCount=((MyLoadMoreAdapter)adapter).getDataCount();
        }else if(adapter instanceof MyLoadMoreAdapter){
            dataCount=((MyLoadMoreAdapter)adapter).getDataCount();
        }
        if (position >= getHeaderViewCount(adapter) + dataCount&& position < adapter.getItemCount() - loadMoreViewCount) {
            return true;
        } else {
            return false;
        }*/
    }
}
