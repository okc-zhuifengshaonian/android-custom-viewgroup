package com.example.custom02.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.custom02.R;
import com.example.custom02.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final int DEFAULT_LINE = -1;
    private static final int DEFAULT_HORIZONTAL_MARGIN = SizeUtils.dip2px(5);
    private static final int DEFAULT_VERTICAL_MARGIN = SizeUtils.dip2px(5);
    private static final String TAG = "FlowLayout";

    private int mMaxLine;
    private int mHorizontalMargin;
    private int mVerticalMargin;

    private List<String> mData = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mMaxLine = a.getInteger(R.styleable.FlowLayout_maxLine, DEFAULT_LINE);
        if(mMaxLine != -1 && mMaxLine < 1) {
            throw new IllegalArgumentException("FlowLayout的mMaxLine属性不能小于 1");
        }
        mHorizontalMargin = (int)a.getDimension(R.styleable.FlowLayout_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mVerticalMargin = (int)a.getDimension(R.styleable.FlowLayout_itemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        a.recycle();
        Log.d(TAG, "mMaxLine ===> " + mMaxLine);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View firstChild = getChildAt(0);
        int currentLeft = mHorizontalMargin + getPaddingLeft();
        int currentTop = mVerticalMargin + getPaddingTop();
        int currentRight = mHorizontalMargin + getPaddingLeft();
        int currentBottom = firstChild.getMeasuredHeight() + mVerticalMargin + getPaddingTop();

        //外层循环遍历二维数组的每一行
        for(List<View> line : mLines) {
            // 遍历每一行中的每一个元素
            for(View view : line) {
                //布局每一行
                int width = view.getMeasuredWidth();
                currentRight += width;
                if(currentRight > getMeasuredWidth() - mHorizontalMargin - getPaddingRight()) {
                    currentRight = getMeasuredWidth() - mHorizontalMargin - getPaddingRight();
                }
                view.layout(currentLeft,currentTop,currentRight,currentBottom);
                currentLeft = currentRight + mHorizontalMargin;
                currentRight += mHorizontalMargin;
            }
            currentLeft = mHorizontalMargin + getPaddingLeft();
            currentTop += firstChild.getMeasuredHeight() + mVerticalMargin;
            currentRight = mHorizontalMargin + getPaddingLeft();
            currentBottom += firstChild.getMeasuredHeight() + mVerticalMargin;
        }
    }

    public void setTextList(List<String> data) {
        this.mData.clear();
        this.mData.addAll(data);
        //根据数据创建子View，并添加进来
        setUpChildren();
    }

    private void setUpChildren() {
        //先清空原来的内容
        removeAllViews();
        for(String data : mData) {
            TextView textView = (TextView)LayoutInflater.from(getContext()).inflate(
                    R.layout.item_flow_text, this, false);
            textView.setText(data);

            textView.setOnClickListener(v -> {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(v, data);
                }
            });
            addView(textView);
        }
    }
    private OnItemClickListener mItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View v, String text);
    }

    //定义二维数组
    private List<List<View>> mLines = new ArrayList<>();
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        if(childCount == 0) {
            return;
        }
        //清空二维数组
        mLines.clear();
        //给二维数组里先添加一行
        List<View> line = new ArrayList<>();
        mLines.add(line);

        //根据提供的大小和模式创建 Measure Specification
        int childWidthSpace = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
        int childHeightSpace = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);

        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() != VISIBLE) {
                //如果子View不可见，则跳出此次循环
                continue;
            }

            //测量孩子
            measureChild(child, childWidthSpace, childHeightSpace);

            if(line.size() == 0) {
                line.add(child);
            } else {
                //判断是否可以添加到当前行
                boolean canBeAdd = checkChildCanBeAdd(line, child, parentWidthSize);
                if(!canBeAdd) {
                    if(mMaxLine != -1 && mLines.size() >= mMaxLine) {
                        //跳出循环
                        break;
                    }
                    //新建一行
                    line = new ArrayList<>();
                    mLines.add(line);
                }
                line.add(child);
            }
        }

        //根据尺寸计算所有行高
        View child = getChildAt(0);
        int childHeight = child.getMeasuredHeight();
        int parentHeightTargetSize = childHeight * mLines.size()
                                + (mLines.size() + 1) * mVerticalMargin
                                + getPaddingTop()
                                + getPaddingBottom();

        //测量自己
        setMeasuredDimension(parentWidthSize, parentHeightTargetSize);
    }

    private boolean checkChildCanBeAdd(List<View> line, View child, int parentWidthSize) {
        int totalWidth = mHorizontalMargin + getPaddingLeft();
        for(View view : line) {
            //之前已经添加进来的子 View 的总宽度
            totalWidth += view.getMeasuredWidth() + mHorizontalMargin;
        }
        //加上这个控件本身的宽度
        int measuredWidth = child.getMeasuredWidth();
        totalWidth += measuredWidth + mHorizontalMargin + getPaddingRight();
        //如果超出限额宽度，则不可以再添加，否则可以添加
        return totalWidth <= parentWidthSize;
    }

    public void setMaxLine(int mMaxLine) {
        this.mMaxLine = mMaxLine;
    }
}
