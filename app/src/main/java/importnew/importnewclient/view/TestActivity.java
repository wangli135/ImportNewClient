package importnew.importnewclient.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import importnew.importnewclient.R;

public class TestActivity extends AppCompatActivity {

    private LoadMoreListView loadMoreListView;
    private List<String> datas;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        loadMoreListView = (LoadMoreListView) findViewById(R.id.loadmore_lv);
        initDatas();

    }

    private boolean hasLoaded;

    private void initDatas() {

        datas = new ArrayList<>();
        for (int i = 1; i < 41; i++) {
            datas.add("Hello World " + i);
        }

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        loadMoreListView.setAdapter(mAdapter);


        loadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoad() {

                if (!hasLoaded) {

                    try {
                        Thread.sleep(2*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (int i = 41; i < 80; i++) {
                        datas.add("Hello World " + i);
                    }
                    mAdapter.notifyDataSetChanged();
                    hasLoaded = !hasLoaded;
                }


            }
        });
    }

}
