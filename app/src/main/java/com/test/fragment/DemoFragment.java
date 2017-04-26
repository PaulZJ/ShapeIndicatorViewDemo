package com.test.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.test.BookModel;
import com.test.R;
import com.test.adapter.ListViewAdapter;
import com.test.adapter.SimpleBaseAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangjun on 2017/4/25.
 */

public class DemoFragment extends Fragment{

    public SimpleBaseAdapter mBookAdapter;

    @BindView(R.id.listview)
    ListView mListView;

    public static DemoFragment newFragment(){
        DemoFragment fragment = new DemoFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.demo_fragment_layout,null);
        ButterKnife.bind(this,root);
        ArrayList<BookModel> bookModels = new ArrayList<>();
        for (int i=0;i<10;i++)
            bookModels.add(new BookModel());
        mBookAdapter = new ListViewAdapter(getActivity(),bookModels);
        mListView.setAdapter(mBookAdapter);
        return root;
    }
}
