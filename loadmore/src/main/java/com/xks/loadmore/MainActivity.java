package com.xks.loadmore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycleView;
    private List<String> mDatas;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecycleView = (RecyclerView) findViewById(R.id.recycleview);
        mDatas = new ArrayList<>();
        initDatas(0);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        myAdapter = new MyAdapter(this, mDatas, mRecycleView);
        mRecycleView.setAdapter(myAdapter);
        myAdapter.setOnLoadMoreListener(new MyAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mDatas.add(null);
                myAdapter.notifyItemInserted(mDatas.size());

                mRecycleView.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mDatas.remove(mDatas.size() - 1);
                        myAdapter.notifyItemRemoved(mDatas.size());


                        for (int i = 40; i < 60; i++) {
                            mDatas.add("String " + i);
                            myAdapter.notifyItemInserted(mDatas.size());
                        }
                        myAdapter.setLoaded();

                        myAdapter.setCannotLoadMore();
                    }
                }, 2000);


            }
        });
    }

    private int end;

    private void initDatas(int start) {
        for (int i = start; i < start + 30; i++) {
            mDatas.add("Hello " + i);
        }
    }
}
