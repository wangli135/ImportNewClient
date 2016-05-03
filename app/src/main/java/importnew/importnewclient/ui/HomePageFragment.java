package importnew.importnewclient.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.adapter.ArticleBlockAdapter;
import importnew.importnewclient.bean.ArticleBlock;
import importnew.importnewclient.parser.HomePagerParser;


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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {

                mRefreshLayout.setRefreshing(false);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter = new ArticleBlockAdapter(mContext, mRecyclerView,articles);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mRecyclerView.setAdapter(mAdapter);

            }

        }
    };

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

        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.setVisibility(View.GONE);
                mRefreshLayout.setRefreshing(true);
                getHtmlAndParser();
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.homepage_recycler);
        mRecyclerView.setVisibility(View.GONE);
        getHtmlAndParser();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cancelAllTasks();
    }

    private void getHtmlAndParser() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                try {

                    conn = (HttpURLConnection) new URL("http://www.importnew.com").openConnection();
                    conn.setReadTimeout(10 * 1000);
                    conn.setConnectTimeout(5 * 1000);
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result=new StringBuilder();
                    String line="";
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }

                    articles = HomePagerParser.paserHomePage(result.toString());
                    handler.sendEmptyMessage(1);

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(conn!=null)
                        conn.disconnect();

                    try {
                        if(br!=null)
                            br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }


}
