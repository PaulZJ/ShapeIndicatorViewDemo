package com.test.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.BookModel;
import com.test.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangjun on 2017/4/25.
 */

public class ListViewAdapter extends SimpleBaseAdapter<BookModel> {

    @BindView(R.id.book_cover)
    ImageView bookCover;
    @BindView(R.id.book_name)
    TextView bookName;
    @BindView(R.id.book_size)
    TextView bookSize;
    @BindView(R.id.book_origin)
    TextView bookOrigin;

    public ListViewAdapter(Context context, ArrayList<BookModel> data){
        super(context,data);
    }

    @Override
    public int getItemResource() {
        return R.layout.item_layout;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        ButterKnife.bind(this,convertView);
        return convertView;
    }
}
