package importnew.importnewclient.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleBlockAdapter;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.parser.HomePagerParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 首页Fragment
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private Context mContext;

    private List<ArticleBlock> articles;
    private ArticleBlockAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public HomePageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.homepage_swiperefresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mRefreshLayout.setEnabled(false);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mRefreshLayout.setRefreshing(true);
                getHtmlAndParser();
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.homepage_recycler);
        mRecyclerView.setVisibility(View.GONE);

    }

    private void getHtmlAndParser(){


        try {
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url("http://www.importnew.com").build();
            Response response=client.newCall(request).execute();
            articles= HomePagerParser.paserHomePage(response.body().toString());

            


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
