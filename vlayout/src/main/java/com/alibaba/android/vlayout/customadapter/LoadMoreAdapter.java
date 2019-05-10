package com.alibaba.android.vlayout.customadapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.DefaultLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.RangeGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.List;

/***
 *   created by android on 2019/4/29
 */
public abstract class LoadMoreAdapter<T> extends CustomAdapter<T> {
    private Handler handler;
    private long click_interval = 900; // 阻塞时间间隔
    private long lastClickTime;
    private int pageSize;

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

    /*是否请求完成，防止上滑下滑重复触发请求*/
    private boolean isEndRequest = true;

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

    @Override
    public void setLayoutHelper(LayoutHelper layoutHelper) {
        super.setLayoutHelper(layoutHelper);
        setLastViewStyle(this.layoutHelper);
    }

    private LayoutHelper setLastViewStyle(LayoutHelper layoutHelper){
        if(layoutHelper==null||hasLoadMore()==false){
            return layoutHelper;
        }
        if(layoutHelper instanceof GridLayoutHelper){
            final GridLayoutHelper gridLayoutHelper = (GridLayoutHelper) layoutHelper;
            gridLayoutHelper.setSpanSizeLookup(getSpanSizeLookup(gridLayoutHelper.getSpanCount()));
            return layoutHelper;
        }
        if(layoutHelper instanceof RangeGridLayoutHelper){
            RangeGridLayoutHelper r = (RangeGridLayoutHelper) layoutHelper;
            r.setSpanSizeLookup(getSpanSizeLookup(r.getSpanCount()));
            return layoutHelper;
        }
        return layoutHelper;
    }

    @NonNull
    private GridLayoutHelper.SpanSizeLookup getSpanSizeLookup(final int spanCount) {
        return new GridLayoutHelper.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position==getItemCount()-1&&getNotLoadViewCount()<getItemCount()){
                    return spanCount;
                }
                return 1;
            }
        };
    }

    public LoadMoreAdapter(int layoutId, int pageSize) {
        super(layoutId);
        this.pageSize=pageSize;
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
        if(position>= getNotLoadViewCount()&&isHiddenPromptView==false){
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
                            //改变状态会触发加载，所以这里不需要手动调用
                            setStatus(load);
//                            loadMoreData();
                        }
                        lastClickTime=currentTime;
                    }
                });
            }
            return viewHolder;
        }

        return super.onCreateViewHolder(viewGroup,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int position) {
        if (getItemViewType(position)==load) {
            loadMoreData();
            return;
        }
        if(isLoadMoreView(getItemViewType(position))){
            return;
        }
        super.onBindViewHolder(viewHolder,position);
    }
    @Override
    public int getItemCount() {
        int size = getDataCount() + getHeaderCount() + getFooterCount();
        if((isHiddenPromptView&&noMore==status)||hasLoadMore()==false){
            return size;
        }
        return size+1;
    }

    private boolean isLoadMoreView(int viewType){
        return (viewType==load||viewType==noMore||viewType==error);
    }

    private void loadMoreData(){
        if(isEndRequest&&onLoadMoreListener!=null){
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    isEndRequest=false;
                    onLoadMoreListener.loadMore(new LoadInter() {
                        @Override
                        public void result(int status) {
                            setStatus(status);
                        }
                    });
                }
            });
        }
    }
    private Handler getHandler(){
        if(handler==null){
            handler=new Handler(Looper.getMainLooper());
        }
        return handler;
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

    @Override
    public void setList(List<T> list) {
        judgeHasMore(list);
        super.setList(list);
    }
    @Override
    public void setList(List<T> list, boolean isNotifyData) {
        judgeHasMore(list);
        super.setList(list, isNotifyData);
    }

    @Override
    public void addList(List<T> list) {
        judgeHasMore(list);
        super.addList(list);
    }
    @Override
    public void addList(List<T> list, boolean isNotifyData) {
        judgeHasMore(list);
        super.addList(list, isNotifyData);
    }

    private void judgeHasMore(List<T> list){
        if(list==null||list.size()<pageSize){
            setStatus(noMore,false);
        }else{
            completeRequest();
        }
    }

    public void setStatus(@Status int status) {
        setStatus(status,true);
    }
    public void setStatus(@Status int status,boolean isNotify) {
        completeRequest();
        this.status = status;
        if(isNotify){
//            notifyDataSetChanged();
            notifyItemChanged(getItemCount()-1);
        }
    }

    /*设置请求完成*/
    private void completeRequest(){
        isEndRequest=true;
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

    private boolean hasLoadMore(){
        return onLoadMoreListener!=null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if(isLoadMoreView(viewType)){
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null) {
                        return spanSizeLookup.getSpanSize(position);
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull CustomViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //如果不显示底部布局
        if(hasLoadMore()==false||getNotLoadViewCount()>=getItemCount()||holder.getAdapterPosition()!=getItemCount()-1){
            return;
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sglm = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            sglm.setFullSpan(true);
        }
    }
    public int getNotLoadViewCount(){
        return getDataCount()+getHeaderCount()+getFooterCount();
    }
}
