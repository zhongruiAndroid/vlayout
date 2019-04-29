package com.alibaba.android.vlayout.customadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.DefaultLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.RangeGridLayoutHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/***
 *   created by android on 2019/4/29
 */
public abstract class LoadMoreAdapter<T> extends CustomAdapter<T> {
    private long click_interval = 900; // 阻塞时间间隔
    private long lastClickTime;

//    private LayoutHelper layoutHelper;
    private View loadView;
    private View errorView;
    private View noMoreView;

    /*显示加载更多*/
    public static final int load = LoadInter.load;
    /*暂无更多数据*/
    public static final int noMore = LoadInter.noMore;
    /*加载失败*/
    public static final int error = LoadInter.error;

    private int status = load;

    @IntDef({load,noMore,error})
    @Retention(RetentionPolicy.SOURCE)
    private  @interface Status{};

    /*** 是否隐藏暂无内容的提示*/
    private boolean isHiddenPromptView = false;
    private String loadViewText = "正在加载更多...";
    private String noMoreViewText = "暂无更多";
    private String errorViewText = "加载失败,点击重试";
    private int loadViewHeight = 40;

    private int bottomViewBackground = Color.TRANSPARENT;
    /*回调方法,触发加载更多*/
    public OnLoadMoreListener onLoadMoreListener;
    public interface OnLoadMoreListener {
        void loadMore(LoadInter loadInter);
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public LayoutHelper getLayoutHelper() {
        return setLastViewStyle(super.getLayoutHelper());
    }
    private LayoutHelper setLastViewStyle(LayoutHelper layoutHelper){
        if(layoutHelper==null){
            return null;
        }
        if(layoutHelper instanceof RangeGridLayoutHelper){
            if(isHiddenPromptView==false){
                RangeGridLayoutHelper.GridRangeStyle gridRangeStyle = new RangeGridLayoutHelper.GridRangeStyle();
                gridRangeStyle.setSpanCount(1);
                ((RangeGridLayoutHelper) layoutHelper).addRangeStyle(getDataCount(),getDataCount(),gridRangeStyle);
            }
        }
        return layoutHelper;
    }
    public LoadMoreAdapter(int layoutId) {
        super(layoutId);
        loadView=getLoadView();
        errorView=getErrorView();
        noMoreView=getNoMoreView();

        isHiddenPromptView=isHiddenPromptView();
        loadViewText=getLoadViewText();
        noMoreViewText=getNoMoreViewText();
        errorViewText=getErrorViewText();
        loadViewHeight=getLoadViewHeight();
        bottomViewBackground=getBottomViewBackground();

    }

    @Override
    public int getItemViewType(int position) {
        if(position>= getDataCount()&&isHiddenPromptView==false){
            return status;
        }
        return super.getItemViewType(position);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(isLoadMoreView(viewType)){
            CustomViewHolder viewHolder = new CustomViewHolder(getStatusView(viewGroup.getContext(), viewType));
            if(viewType==error){
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long currentTime = Calendar.getInstance().getTimeInMillis();
                        long interval=currentTime-lastClickTime;
                        if(interval>=click_interval){
                            loadMoreData();
                            lastClickTime=currentTime;
                        }
                    }
                });
            }
            return viewHolder;
        }

        return super.onCreateViewHolder(viewGroup,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int position) {
        if (isLoadMoreView(getItemViewType(position))) {
            loadMoreData();
        }else{
            super.onBindViewHolder(viewHolder,position);
        }
    }
    public int getDataCount(){
        return super.getItemCount();
    }
    @Override
    public int getItemCount() {
        if(isHiddenPromptView&&noMore==status){
            return super.getItemCount();
        }
        return super.getItemCount()+1;
    }

    private boolean isLoadMoreView(int viewType){
        return (viewType==load||viewType==noMore||viewType==error);
    }

    private void loadMoreData(){
        if(onLoadMoreListener!=null){
            onLoadMoreListener.loadMore(new LoadInter() {
                @Override
                public void result(int status) {
                    setStatus(status);
                }
            });
        }
    }

    public View getLoadView() {
        return loadView;
    }

    public View getErrorView() {
        return errorView;
    }

    public View getNoMoreView() {
        return noMoreView;
    }

    private View getStatusView(Context context, int viewType) {
        View view = null;
        switch (viewType) {
            case load:
                /*显示加载更多*/
                if (getLoadView() != null) {
                    view = getLoadView();
                } else {
                    view = getDefaultView(context, loadViewText);
                }
                break;
            case noMore:
                /*暂无更多数据*/
                if (getNoMoreView() != null) {
                    view = getLoadView();
                } else {
                    view = getDefaultView(context, noMoreViewText);
                }
                break;
            case error:
                /*加载失败*/
                if (getErrorView() != null) {
                    view = getLoadView();
                } else {
                    view = getDefaultView(context, errorViewText);
                }
                break;
        }
        return view;
    }

    private TextView getDefaultView(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, loadViewHeight));
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(bottomViewBackground);
        textView.setText(text);
        return textView;
    }

    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@Status int status) {
        setStatus(status,true);
    }
    public void setStatus(@Status int status,boolean isNotify) {
        this.status = status;
        if(isNotify){
            notifyDataSetChanged();
        }
    }

    public void setLoadView(View loadView) {
        this.loadView = loadView;
    }

    public void setErrorView(View errorView) {
        this.errorView = errorView;
    }

    public void setNoMoreView(View noMoreView) {
        this.noMoreView = noMoreView;
    }

    public void setHiddenPromptView(boolean hiddenPromptView) {
        isHiddenPromptView = hiddenPromptView;
        setLastViewStyle(getLayoutHelper());
    }

    public void setLoadViewText(String loadViewText) {
        this.loadViewText = loadViewText;
    }

    public void setNoMoreViewText(String noMoreViewText) {
        this.noMoreViewText = noMoreViewText;
    }

    public void setErrorViewText(String errorViewText) {
        this.errorViewText = errorViewText;
    }

    public void setLoadViewHeight(int loadViewHeight) {
        this.loadViewHeight = loadViewHeight;
    }

    public void setBottomViewBackground(@ColorInt int bottomViewBackground) {
        this.bottomViewBackground = bottomViewBackground;
    }

    public boolean isHiddenPromptView() {
        return isHiddenPromptView;
    }

    public String getLoadViewText() {
        return loadViewText;
    }

    public String getNoMoreViewText() {
        return noMoreViewText;
    }

    public String getErrorViewText() {
        return errorViewText;
    }

    public int getLoadViewHeight() {
        return loadViewHeight;
    }

    public int getBottomViewBackground() {
        return bottomViewBackground;
    }
}
