package com.test.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.test.R;

/**
 * Created by zhangjun on 2017/4/25.
 */

public class ShapeIndicatorView extends View implements TabLayout.OnTabSelectedListener,ViewPager.OnPageChangeListener{

    public static final int ImgMode = 0;
    public static final int DrawMode = 1;

    private int mode = ImgMode;

    private Path mDrawPath;
    private Paint mImgPaint;
    private Paint mDrawPaint;

    private Rect mImgRect;

    private int mShapeHorizontalSpace;
    private int mShapeColor = Color.GREEN;

    private Bitmap bitmap;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public ShapeIndicatorView(Context context) {
        this(context,null);
    }

    public ShapeIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ShapeIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context,attrs,defStyleAttr,0);
    }

    private void initViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ShapeIndicatorView ,defStyleRes, 0);
        mShapeColor = array.getColor(R.styleable.ShapeIndicatorView_strokecolor, Color.GREEN);
        int radius = array.getInteger(R.styleable.ShapeIndicatorView_roundraduis, 40);
        int imgRes = array.getResourceId(R.styleable.ShapeIndicatorView_img,R.mipmap.bg_indicator);
        mode = array.getInt(R.styleable.ShapeIndicatorView_mode,ImgMode);
        array.recycle();

        mShapeHorizontalSpace = 15;

        if(mode == ImgMode) {
            bitmap = ((BitmapDrawable) getResources().getDrawable(imgRes)).getBitmap();

            mImgPaint = new Paint();
            mImgPaint.setAntiAlias(true);
            mImgPaint.setDither(true);
            mImgPaint.setColor(mShapeColor);

            // Important for certain APIs
            setLayerType(LAYER_TYPE_SOFTWARE, mImgPaint);
        }else {
            mDrawPaint = new Paint();
            mDrawPaint.setAntiAlias(true);
            mDrawPaint.setDither(true);
            mDrawPaint.setColor(mShapeColor);
            mDrawPaint.setStyle(Paint.Style.STROKE);
            mDrawPaint.setPathEffect(new CornerPathEffect(radius));
            mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
            mDrawPaint.setStrokeWidth(2);

            // Important for certain APIs
            setLayerType(LAYER_TYPE_SOFTWARE, mDrawPaint);
        }

    }

    public void setupWithTabLayout(final TabLayout tableLayout) {
        mTabLayout = tableLayout;

        tableLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tableLayout.setOnTabSelectedListener(this);

        tableLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mTabLayout.getScrollX() != getScrollX())
                    scrollTo(mTabLayout.getScrollX(), mTabLayout.getScrollY());
            }
        });

        ViewCompat.setElevation(this, ViewCompat.getElevation(mTabLayout));
        tableLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mTabLayout.getTabCount() > 0)
                    onTabSelected(mTabLayout.getTabAt(0));

            }
        });

        //清除Tab background
        for (int tab = 0; tab < tableLayout.getTabCount(); tab++) {
            View tabView = getTabViewByPosition(tab);
            tabView.setBackgroundResource(0);
        }
    }

    public void setupWithViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    private View getTabViewByPosition(int position) {
        if (mTabLayout != null && mTabLayout.getTabCount() > 0) {
            ViewGroup tabStrip = (ViewGroup) mTabLayout.getChildAt(0);
            return tabStrip != null ? tabStrip.getChildAt(position) : null;
        }

        return null;
    }

    /**
     * generate indicator shape
     * imgMode: generate Rect
     * drawMode: generate Path
     * */
    private void generateShape(int position, float positionOffset) {

        RectF range = new RectF();
        View tabView = getTabViewByPosition(position);
        if (tabView == null)
            return;

        int left, top, right, bottom;
        left = top = right = bottom = 0;
        if (positionOffset > 0.f && position < mTabLayout.getTabCount() - 1) {
            View nextTabView = getTabViewByPosition(position + 1);
            left += (int) (nextTabView.getLeft() * positionOffset + tabView.getLeft() * (1.f - positionOffset));
            right += (int) (nextTabView.getRight() * positionOffset + tabView.getRight() * (1.f - positionOffset));
            left += mShapeHorizontalSpace;
            right -= mShapeHorizontalSpace;
            top = tabView.getTop() + getPaddingTop();
            bottom = tabView.getBottom() - getPaddingBottom();
            range.set(left, top, right, bottom);
        } else {
            left = tabView.getLeft() + mShapeHorizontalSpace;
            right = tabView.getRight() - mShapeHorizontalSpace;
            top = tabView.getTop() + getPaddingTop();
            bottom = tabView.getBottom() - getPaddingBottom();
            range.set(left, top, right, bottom);
            if (range.isEmpty()) {
                return;
            }
        }

        switch (mode){
            case ImgMode:
                mImgRect = new Rect(left,top,right,bottom);
                break;
            case DrawMode:
                if(null ==mDrawPath)
                    mDrawPath = new Path();

                Rect tabsRect = getTabArea();
                tabsRect.right += range.width();
                tabsRect.left -= range.width();

                mDrawPath.reset();
                mDrawPath.moveTo(range.left,range.bottom);
                mDrawPath.lineTo(range.left,range.top);
                mDrawPath.lineTo(range.right,range.top);
                mDrawPath.lineTo(range.right,range.bottom);
                mDrawPath.lineTo(range.left,range.bottom);
                mDrawPath.close();
                break;
        }

    }

    private Rect getTabArea() {
        Rect rect = null;
        if (mTabLayout != null) {
            View view = mTabLayout.getChildAt(0);
            rect = new Rect();
            view.getHitRect(rect);
        }
        return rect;
    }

    /**
     * draw Indicator Shape
     * */
    private void drawShape(Canvas canvas) {
        int savePos = canvas.save();
        switch (mode){
            case ImgMode:
                if(mImgRect == null || mImgRect.isEmpty())
                    return;
                canvas.drawBitmap(bitmap,null,mImgRect,mImgPaint);
                break;
            case DrawMode:
                if(mDrawPath == null || mDrawPath.isEmpty())
                    return;
                canvas.drawPath(mDrawPath,mDrawPaint);
                break;
        }

        canvas.restoreToCount(savePos);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawShape(canvas);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (mViewPager != null) {
            if (tab.getPosition() != mViewPager.getCurrentItem())
                mViewPager.setCurrentItem(tab.getPosition());
        } else {
            generateShape(tab.getPosition(), 0);
            invalidate();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        generateShape(position, positionOffset);
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabLayout.getSelectedTabPosition() != position)
            mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}