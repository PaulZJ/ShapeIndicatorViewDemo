package com.test.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.test.adapter.DemoFragmentPagerAdapter;
import com.test.R;
import com.test.fragment.DemoFragment;
import com.test.view.ShapeIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangjun on 2017/4/25.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView titleView;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.custom_indicator)
    ShapeIndicatorView shapeIndicatorView;

    private DemoFragmentPagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        titleView.setText("ShapedIndicatorDemo");

        String[] categorys = new String[]{"Category 1","Category 2","Category 3","Category 4","Category 5"};
        setupViewPager(categorys);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        shapeIndicatorView.setupWithTabLayout(mTabLayout);
        shapeIndicatorView.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(String[] categorys) {

        mPagerAdapter = new DemoFragmentPagerAdapter(getSupportFragmentManager());
        for (String category:categorys){
            mPagerAdapter.addFragment(DemoFragment.newFragment(),category);
        }

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                DemoFragment fragment=
                        (DemoFragment) mPagerAdapter.getItem(position);
                fragment.mBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
