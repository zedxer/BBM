package com.example.naqi.bebettermuslim.views;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FadeOutTransformation implements ViewPager.PageTransformer {
//    @Override
//    public void transformPage(View page, float position) {
//
//        page.setTranslationX(-position*page.getWidth());
//        page.setAlpha(1-Math.abs(position));
//
//
//    }

    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);

        if (position <= -1.0F || position >= 1.0F) {
            view.setAlpha(0.0F);
        } else if (position == 0.0F) {
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setAlpha(1.0F - Math.abs(position));
        }
    }
}
