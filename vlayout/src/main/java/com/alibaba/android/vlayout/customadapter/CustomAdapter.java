package com.alibaba.android.vlayout.customadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
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
    protected List<T> mList;
    protected LayoutInflater mInflater;
    protected int layoutId;

    private long click_interval = 900; // 阻塞时间间隔
    private long lastClickTime;
    protected OnItemFastClickListener mFastClickListener;
    protected OnItemClickListener mClickListener;
    protected OnItemLongClickListener mLongClickListener;

    protected LayoutHelper layoutHelper=new LinearLayoutHelper();

    /**
     * 假数据测试设置list大小
     **/
    protected int testListSize = 0;


    public abstract void bindData(CustomViewHolder holder, int position, T item);

    public View getCustomView(){
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
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        CustomViewHolder holder;
        View customView = getCustomView();
        if(customView!=null){
            holder = new CustomViewHolder(customView);
        }else{
            holder = new CustomViewHolder(mInflater.inflate(layoutId, viewGroup, false));
        }
        holder.isContentView=true;
        setItemFastClickListener(holder);
        setItemClickListener(holder);
        setItemLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (mList == null || position >= mList.size()) {
            bindData(holder, position, null);
        } else {
            bindData(holder, position, mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (testListSize > 0) {
            return testListSize;
        } else {
            return mList == null ? 0 : mList.size();
        }
    }


    public void setTestListSize(int testListSize) {
        this.testListSize = testListSize;
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

    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View itemView, int position);
    }

    public interface OnItemFastClickListener {
        public void onItemFastClick(View itemView, int position);
    }

    private void setItemFastClickListener(final CustomViewHolder holder) {
        if (mFastClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFastClickListener.onItemFastClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    private void setItemClickListener(final CustomViewHolder holder) {
        if (mClickListener != null  ) {
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
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
