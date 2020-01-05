package com.example.naqi.bebettermuslim.views;


import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class DepthTransformation implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

//        View view = page;
       /* if (position < -1){    // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        }
        else if (position <= 0){    // [-1,0]
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
//            page.setScaleX(0.7f - 0.05f * position);
//            page.setScaleY(0.7f);
//            page.setTranslationX(page.getWidth() * position);
//            page.setTranslationY(30 * position);

        }
        else if (position <= 1){    // (0,1]
//            page.setTranslationX(-position*page.getWidth());
//            page.setAlpha(1-Math.abs(position));
//            page.setScaleX(1-Math.abs(position));
//            page.setScaleY(1-Math.abs(position));

            page.setScaleX(0.7f - 0.05f * position);
            page.setScaleY(0.7f);
            page.setTranslationX(-page.getWidth() * position);
            page.setTranslationY(200 * position);

        }
        else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setTranslationX(-position*page.getWidth());
            page.setAlpha(1-Math.abs(position));
            page.setScaleX(1-Math.abs(position));
            page.setScaleY(1-Math.abs(position));
//            page.setAlpha(1);

        }*/
       /* if (position >= 0) {
            page.setScaleX(0.7f - 0.05f * position);
            page.setScaleY(0.7f);
            page.setTranslationX(-page.getWidth() * position);
            page.setTranslationY(30 * position);
        }*/
        /*if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0f);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                page.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0f);
        }
*/
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(1);
//            Log.d("PAGE_TAG", "[-Infinity,-1)");


        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
//            page.setAlpha((position+1));
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            page.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            page.setTranslationX(0f);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
//            Log.d("PAGE_TAG", "[-1,0]");


        } else if (position <= 1) { // (0,1]
            // Fade the page out.
//            page.setAlpha(1);
            float scaleFactor2 = Math.max(MIN_SCALE, 1 - Math.abs(position));

            page.setAlpha(MIN_ALPHA +
                    (scaleFactor2 - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            // Counteract the default slide transition
            page.setTranslationX(pageWidth * -position);
            page.setTranslationY(pageWidth * position / 2);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
//            page.setAlpha(position-position/2);


        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleY(scaleFactor);
            page.setScaleX(scaleFactor);

//            page.setAlpha(scaleFactor/position);
            page.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            page.setTranslationY(pageWidth/2);
            page.setTranslationX(pageWidth * -1f);

        }

    }
}