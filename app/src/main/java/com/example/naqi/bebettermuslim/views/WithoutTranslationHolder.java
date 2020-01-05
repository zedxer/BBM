package com.example.naqi.bebettermuslim.views;

import android.text.SpannableString;

/**
 * Created by naqi on 22,January,2019
 */
public class WithoutTranslationHolder {

    SpannableString aya;

    public SpannableString getAya() {
        return aya;
    }

    public void setAya(SpannableString aya) {
        this.aya = aya;
    }

    public WithoutTranslationHolder(SpannableString aya) {

        this.aya = aya;
    }
}
