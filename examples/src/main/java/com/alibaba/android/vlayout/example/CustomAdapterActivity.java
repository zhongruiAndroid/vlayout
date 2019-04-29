package com.alibaba.android.vlayout.example;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
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

        delegateAdapter.addAdapter(customAdapter);
        rv.setLayoutManager(new VirtualLayoutManager(this));
        rv.setAdapter(delegateAdapter);
    }
}
