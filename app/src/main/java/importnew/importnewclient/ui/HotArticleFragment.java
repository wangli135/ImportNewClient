package importnew.importnewclient.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import importnew.importnewclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotArticleFragment extends Fragment {


    public HotArticleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_article, container, false);
    }

}
