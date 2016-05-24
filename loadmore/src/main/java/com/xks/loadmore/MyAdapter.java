package com.xks.loadmore;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Xingfeng on 2016/5/21.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.BaseVH> {

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private LayoutInflater mInflater;
    private List<String> mDatas;

    private RecyclerView recyclerView;

    private int totalItemNum;
    private int lastVisibleItemNum;

    private boolean isLoading;

    public MyAdapter(Context context, List<String> datas, RecyclerView recyclerView) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        this.recyclerView = recyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemNum = linearLayoutManager.getItemCount();
                lastVisibleItemNum = linearLayoutManager.findLastVisibleItemPosition();
                if (!cannotLoadMore&&lastVisibleItemNum == totalItemNum - 1 && onLoadMoreListener != null && !isLoading) {
                    isLoading = true;
                    onLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }

    private boolean cannotLoadMore;

    public void setCannotLoadMore() {
        cannotLoadMore = true;
    }

    @Override
    public BaseVH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new MyVH(mInflater.inflate(R.layout.item_layout, parent, false));
        else
            return new FootVH(mInflater.inflate(R.layout.foot_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseVH holder, int position) {

        if (holder instanceof MyVH) {
            ((MyVH) holder).title.setText(mDatas.get(position));
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position) == null)
            return 2;
        else
            return 1;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class BaseVH extends RecyclerView.ViewHolder {

        public BaseVH(View itemView) {
            super(itemView);
        }
    }

    class MyVH extends BaseVH {

        TextView title;

        public MyVH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    class FootVH extends BaseVH {

        public FootVH(View itemView) {
            super(itemView);
        }
    }


}
