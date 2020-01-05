package com.example.naqi.bebettermuslim.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by naqi on 22,January,2019
 */

public class MyRecyclerView extends RecyclerView {

    private ScaleGestureDetector mScaleDetector;
    MyRecyclerView recyclerView;
    SimpleSectionedRecyclerViewAdapter adapter;
    WithTranslationAdapter withTranslationAdapter;
    WithoutTranslationAdapter withoutTranslationAdapter;
    private float mScaleFactor = 1f;

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        recyclerView = MyRecyclerView.this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getPointerCount() > 1) {
            mScaleDetector.onTouchEvent(e);
        }
        return super.onTouchEvent(e);

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();

            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 2.5f));

            Log.d("Factor", String.valueOf(mScaleFactor));

            if (adapter.isTranslation()){

                withTranslationAdapter.setTextSize(60 * mScaleFactor);
                withTranslationAdapter.notifyDataSetChanged();

            }else{
                withoutTranslationAdapter.setTextSize(60 * mScaleFactor);
                withoutTranslationAdapter.notifyDataSetChanged();
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            adapter = (SimpleSectionedRecyclerViewAdapter) recyclerView.getAdapter();

            if (adapter.isTranslation()){
                withTranslationAdapter = (WithTranslationAdapter) adapter.getmBaseAdapter();
            }else{
                withoutTranslationAdapter = (WithoutTranslationAdapter) adapter.getmBaseAdapter();
            }

            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            System.out.println("Data Changed");
            super.onScaleEnd(detector);

        }
    }
}
