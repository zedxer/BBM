package com.example.naqi.bebettermuslim.views;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by naqi on 22,January,2019
 */

public abstract class MyClickableSpan extends ClickableSpan {
    String ayaText;
    int surah;
    int aya;
    int position;

    boolean highlightWord = false;

    public boolean isHighlightWord() {
        return highlightWord;
    }

    public void setHighlightWord(boolean highlightWord) {
        this.highlightWord = highlightWord;
    }

    public MyClickableSpan(String ayaText, int surah, int aya, int position) {
        super();
        this.ayaText = ayaText;
        this.surah = surah;
        this.aya = aya;
        this.position = position;
    }

    public String getAyaText() {
        return ayaText;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        if (highlightWord){
            ds.setColor(Color.RED);
        }else{
            ds.setUnderlineText(false);
            ds.setColor(Color.BLACK);
            ds.bgColor = Color.TRANSPARENT;
        }

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setAyaText(String ayaText) {
        this.ayaText = ayaText;
    }

    public int getSurah() {
        return surah;
    }

    public void setSurah(int surah) {
        this.surah = surah;
    }

    public int getAya() {
        return aya;
    }

    public void setAya(int aya) {
        this.aya = aya;
    }
}
