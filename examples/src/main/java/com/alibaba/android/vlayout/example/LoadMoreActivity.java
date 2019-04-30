package com.alibaba.android.vlayout.example;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.customadapter.BaseDividerGridItem;
import com.alibaba.android.vlayout.customadapter.BaseDividerListItem;
import com.alibaba.android.vlayout.customadapter.CustomViewHolder;
import com.alibaba.android.vlayout.customadapter.LoadInter;
import com.alibaba.android.vlayout.customadapter.LoadMoreAdapter;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.RangeGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadMoreActivity extends AppCompatActivity {
    RecyclerView rv;

    Button btLoad;
    Button btError;
    Button btComplete;
    private LoadMoreAdapter adapter;
    private int tempCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);

        rv=findViewById(R.id.rv);
        btLoad=findViewById(R.id.btLoad);
        btLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setStatus(LoadMoreAdapter.load);
            }
        });

        btError=findViewById(R.id.btError);
        btError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setStatus(LoadMoreAdapter.error);
            }
        });

        btComplete=findViewById(R.id.btComplete);
        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setStatus(LoadMoreAdapter.noMore);
            }
        });



        VirtualLayoutManager virtualLayoutManager=new VirtualLayoutManager(this);
        DelegateAdapter delegateAdapter=new DelegateAdapter(virtualLayoutManager);

        adapter = new LoadMoreAdapter<String>(R.layout.item,10) {
            @Override
            public void bindData(CustomViewHolder holder, int position, String item) {
                int i = (new Random().nextInt(10) + 1)*30;
                VirtualLayoutManager.LayoutParams layoutParams = new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i);
                holder.itemView.setLayoutParams(layoutParams);
                holder.setText(R.id.title, item);
            }
        };
        adapter.setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void loadMore(LoadInter loadInter) {
                Log.i("=====","loadMore");
                adapter.addList(getNewData(),true);
            }

            private List getNewData() {
                tempCount++;
                if(tempCount>=4){
                    return null;
                }
                List<String> list=new ArrayList<>();
                for (int i = 0; i < 25; i++) {
                    list.add(""+i);
                }
                return list;
            }
        });
//        RangeGridLayoutHelper helper=new RangeGridLayoutHelper(4);
        StaggeredGridLayoutHelper helper=new StaggeredGridLayoutHelper(4);
//        GridLayoutHelper helper=new GridLayoutHelper(4);
        adapter.setLayoutHelper(helper);
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(""+i);
        }
        adapter.setList(list);
        delegateAdapter.addAdapter(adapter);

//        rv.setLayoutManager(new GridLayoutManager(this,4));
        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new BaseDividerListItem(this,15,R.color.design_default_color_primary));
//        rv.addItemDecoration(new BaseDividerGridItem(this,15,R.color.design_default_color_primary));
        rv.setAdapter(adapter);
    }
}
