package com.alibaba.android.vlayout.example;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.customadapter.BaseDividerListItem;
import com.alibaba.android.vlayout.customadapter.CustomAdapter;
import com.alibaba.android.vlayout.customadapter.CustomViewHolder;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterActivity extends AppCompatActivity {
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_adapter);

        rv=findViewById(R.id.rv);

        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        DelegateAdapter delegateAdapter=new DelegateAdapter(virtualLayoutManager,false);

        CustomAdapter<String> customAdapter = new CustomAdapter<String>(R.layout.item) {
            @Override
            public void bindData(CustomViewHolder holder, int position, String item) {
                holder.setText(R.id.title, item);
            }
        };

        List<String> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(""+i);
        }
        customAdapter.setList(list);

        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100));
            textView.setText("a"+(i+1));
            textView.setGravity(Gravity.CENTER);
            customAdapter.addHeaderView(textView);
        }

        for (int i = 7; i < 12; i++) {
            TextView textView = new TextView(this);
            textView.setWidth(getResources().getDisplayMetrics().widthPixels);
            textView.setHeight(90);
//            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100));
            textView.setText("a"+(i+1));
            textView.setGravity(Gravity.CENTER);
            customAdapter.addFooterView(textView);
        }

        delegateAdapter.addAdapter(customAdapter);
//        rv.setLayoutManager(new VirtualLayoutManager(this));
//        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new GridLayoutManager(this,2));


        rv.addItemDecoration(new BaseDividerListItem(this,15,R.color.design_default_color_primary));

        rv.setAdapter(customAdapter);
    }
}
