package com.alibaba.android.vlayout.customadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.R;
import com.alibaba.android.vlayout.layout.DefaultLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/***
 *   created by android on 2019/4/29
 */
public abstract class CustomAdapter<T> extends DelegateAdapter.Adapter<CustomViewHolder> {
    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View itemView, int position);
    }

    public interface OnItemFastClickListener {
        public void onItemFastClick(View itemView, int position);
    }

    /********************************header********************************/
    public interface OnHeaderClickListener {
        public void onHeaderClick(View itemView, int position);
    }

    public interface OnHeaderLongClickListener {
        public void onHeaderLongClick(View itemView, int position);
    }

    /********************************footer********************************/
    public interface OnFooterClickListener {
        public void onFooterClick(View itemView, int position);
    }

    public interface OnFooterLongClickListener {
        public void onFooterLongClick(View itemView, int position);
    }

    protected LayoutHelper layoutHelper = new LinearLayoutHelper();

    protected List<T> mList;
    protected LayoutInflater mInflater;
    protected int layoutId;

    private long click_interval = 900; // 阻塞时间间隔
    private long lastClickTime;
    protected OnItemFastClickListener mFastClickListener;
    protected OnItemClickListener mClickListener;
    protected OnItemLongClickListener mLongClickListener;


    private final int header_view = 10000;
    private final int footer_view = 20000;
    protected SparseArrayCompat<View> headerView;
    protected SparseArrayCompat<View> footerView;


    protected OnHeaderClickListener mHeaderClickListener;
    protected OnHeaderLongClickListener mHeaderLongClickListener;
    protected OnFooterClickListener mFooterClickListener;
    protected OnFooterLongClickListener mFooterLongClickListener;


    public abstract void bindData(CustomViewHolder holder, int position, T item);

    public View getCustomView() {
        return null;
    }

    public CustomAdapter(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    public LayoutHelper getLayoutHelper() {
        return layoutHelper;
    }

    public void setLayoutHelper(LayoutHelper layoutHelper) {
        this.layoutHelper = layoutHelper;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return headerView.keyAt(position);
        }
        if (isFooterViewPos(position)) {
            return footerView.keyAt(position - getHeaderCount() - getDataCount());
        }
        return super.getItemViewType(position);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        CustomViewHolder holder;
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        /*添加头布局*/
        if (this.headerView != null) {
            View hView = this.headerView.get(viewType);
            if (hView != null) {
                holder = new CustomViewHolder(hView);
                holder.isHeaderView = true;
                setHeaderItemClick(holder);
                setHeaderItemLongClick(holder);
                return holder;
            }
        }
        /*添加脚布局*/
        if (this.footerView != null) {
            View fView = this.footerView.get(viewType);
            if (fView != null) {
                holder = new CustomViewHolder(fView);
                holder.isFooterView = true;
                setFooterItemClick(holder);
                setFooterItemLongClick(holder);
                return holder;
            }
        }

        View customView = getCustomView();
        if (customView != null) {
            holder = new CustomViewHolder(customView);
        } else {
            holder = new CustomViewHolder(mInflater.inflate(layoutId, viewGroup, false));
        }
        holder.isContentView = true;
        setItemFastClickListener(holder);
        setItemClickListener(holder);
        setItemLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            Object tag = holder.itemView.getTag(R.id.tag_layout_params);
            if(tag==null){
                return;
            }
            if (tag != null && getDataCount() > 0) {
                ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) tag;
                holder.itemView.setLayoutParams(layoutParams);
            } else {
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }
            return;
        }
        if (mList == null || getDataPosition(position) >= mList.size()) {
            bindData(holder,getDataPosition(position), null);
        } else {
            bindData(holder,getDataPosition(position),mList.get(getDataPosition(position)));
        }
    }
    public int getDataPosition(int position){
        return position - getHeaderCount();
    }
    @Override
    public int getItemCount() {
        int otherSize = getLoadMoreViewCount() + getHeaderCount() + getFooterCount();
        return mList == null ? otherSize : mList.size() + otherSize;
    }

    public int getDataCount() {
        return mList == null ? 0 : mList.size();
    }

    public int getLoadMoreViewCount() {
        return 0;
    }

    /*****************************headerView*******************************/
    public void addHeaderView(View view) {
        addHeaderView(view, false);
    }

    public void addHeaderView(View view, boolean hideNoData) {
        if (headerView == null) {
            headerView = new SparseArrayCompat<>();
        }
        if (hideNoData) {
            view.setTag(R.id.tag_layout_params, view.getLayoutParams());
        }
        headerView.put(headerView.size() + header_view, view);
    }
    public void removeHeaderView(int position){
        removeHeaderView(position,false);
    }
    public void removeHeaderView(int position,boolean notify){
        if (headerView == null) {
            return;
        }
        headerView.removeAt(position);
        if(notify){
            notifyDataSetChanged();
        }
    }
    public void removeHeaderView(View view ){
        removeHeaderView(view,false);
    }

    public void removeHeaderView(View view ,boolean notify){
        if (headerView == null) {
            return;
        }
        if(headerView.containsValue(view)){
            int index = headerView.indexOfValue(view);
            removeHeaderView(index,notify);
        }
    }

    public boolean isHeaderViewPos(int position) {
        return position < getHeaderCount();
    }

    public int getHeaderCount() {
        return headerView == null ? 0 : headerView.size();
    }

    public SparseArrayCompat<View> getHeaderView() {
        if (headerView == null) {
            headerView = new SparseArrayCompat<>();
        }
        return headerView;
    }

    public void setHeaderView(SparseArrayCompat<View> headerViewList) {
        this.headerView = headerViewList;
    }

    /*****************************footerView*******************************/
    public void addFooterView(View view) {
        addFooterView(view, false);
    }

    public void addFooterView(View view, boolean hideNoData) {
        if (footerView == null) {
            footerView = new SparseArrayCompat<>();
        }
        if (hideNoData) {
            view.setTag(R.id.tag_layout_params, view.getLayoutParams());
        }
        footerView.put(footerView.size() + footer_view, view);
    }
    public void removeFooterView(int position){
        removeFooterView(position,false);
    }
    public void removeFooterView(int position,boolean notify){
        if(footerView==null){
            return;
        }
        footerView.removeAt(position);
        if(notify){
            notifyDataSetChanged();
        }
    }
    public void removeFooterView(View view){
        removeFooterView(view,false);
    }
    public void removeFooterView(View view,boolean notify){
        if(footerView==null){
            return;
        }
        if (footerView.containsValue(view)) {
            int index = footerView.indexOfValue(view);
            removeFooterView(index,notify);
        }
    }


    public boolean isFooterViewPos(int position) {
        if (position >= getHeaderCount() + getDataCount() && position < getItemCount() - getLoadMoreViewCount()) {
            return true;
        } else {
            return false;
        }
    }

    public int getFooterCount() {
        return footerView == null ? 0 : footerView.size();
    }

    public SparseArrayCompat<View> getFooterView() {
        if (footerView == null) {
            footerView = new SparseArrayCompat<>();
        }
        return footerView;
    }

    public void setFooterView(SparseArrayCompat<View> footerViewList) {
        this.footerView = footerViewList;
    }

    public void setList(List<T> list) {
        setList(list, false);
    }

    public void setList(List<T> list, boolean isNotifyData) {
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
        if (isNotifyData) {
            notifyDataSetChanged();
        }
    }

    public void addList(List<T> list) {
        addList(list, false);
    }

    public void addList(List<T> list, boolean isNotifyData) {
        if (this.mList == null) {
            this.mList = new ArrayList<>();
        }
        if (list != null) {
            this.mList.addAll(list);
        }
        if (isNotifyData) {
            notifyDataSetChanged();
        }
    }


    public List<T> getList() {
        return mList;
    }

    public void delete(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteNotifyData(int position, boolean isNotifyData) {
        mList.remove(position);
        if (isNotifyData) {
            notifyDataSetChanged();
        }
    }


    public void setOnItemFastClickListener(OnItemFastClickListener mFastClickListener) {
        this.mFastClickListener = mFastClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mHeaderClickListener = listener;
    }

    public void setOnHeaderLongClickListener(OnHeaderLongClickListener listener) {
        mHeaderLongClickListener = listener;
    }

    public void setOnFooterClickListener(OnFooterClickListener listener) {
        mFooterClickListener = listener;
    }

    public void setOnFooterLongClickListener(OnFooterLongClickListener listener) {
        mFooterLongClickListener = listener;
    }


    private void setHeaderItemClick(final CustomViewHolder holder) {
        if (mHeaderClickListener != null && holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHeaderClickListener.onHeaderClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    private void setHeaderItemLongClick(final CustomViewHolder holder) {
        if (mHeaderLongClickListener != null && holder.itemView != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mHeaderLongClickListener.onHeaderLongClick(holder.itemView, holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    private void setFooterItemLongClick(final CustomViewHolder holder) {
        if (mFooterLongClickListener != null && holder.itemView != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mFooterLongClickListener.onFooterLongClick(holder.itemView, holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    private void setFooterItemClick(final CustomViewHolder holder) {
        if (mFooterClickListener != null && holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFooterClickListener.onFooterClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }


    private void setItemFastClickListener(final CustomViewHolder holder) {
        if (mFastClickListener != null && holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFastClickListener.onItemFastClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    private void setItemClickListener(final CustomViewHolder holder) {
        if (mClickListener != null && holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    long interval = currentTime - lastClickTime;
                    if (interval >= click_interval) {
                        mClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                        lastClickTime = currentTime;
                    }
                }
            });
        }
    }

    private void setItemLongClickListener(final CustomViewHolder holder) {
        if (mLongClickListener != null && holder.itemView != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, holder.getAdapterPosition());
                    return true;
                }
            });
        }
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
                    if(isHeaderViewPos(position)||isFooterViewPos(position)){
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
        if(isHeaderViewPos(holder.getAdapterPosition())||isFooterViewPos(holder.getAdapterPosition())){
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams sglm = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                sglm.setFullSpan(true);
            }
        }

    }
}
