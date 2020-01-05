//package com.example.naqi.bebettermuslim.adapter;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.support.v4.view.PagerAdapter;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import com.example.naqi.bebettermuslim.BeBetterMuslim;
//import com.example.naqi.bebettermuslim.R;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//
//import java.util.List;
//
///**
// * Created by naqi on 26,June,2019
// */
//public class AdapterSingleViewPager extends PagerAdapter {
//        Context context;
//        List<AdBannerModel> images;
//        LayoutInflater layoutInflater;
//
//
//        public AdapterSingleViewPager(Context context, List<String> images) {
//            this.context = context;
//            this.images = images;
//            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            return images.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == ((RelativeLayout) object);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, final int position) {
//            View itemView = layoutInflater.inflate(R.layout.layout_prayer_timing, container, false);
//
//            final ImageView imageView = (ImageView) itemView.findViewById(R.id.banner_image);
//            ((BeBetterMuslim) context.getApplicationContext()).imageLoader.displayImage(images.get(position).getURL(), imageView, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                    imageView.setVisibility(View.GONE);
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    adBannerProgress.setVisibility(View.VISIBLE);
//                    imageView.setVisibility(View.GONE);
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    adBannerProgress.setVisibility(View.GONE);
//                    imageView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                    adBannerProgress.setVisibility(View.VISIBLE);
//                    imageView.setVisibility(View.GONE);
//                }
//            });
//            container.addView(itemView);
//            return itemView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((RelativeLayout) object);
//        }
//    }
//}
