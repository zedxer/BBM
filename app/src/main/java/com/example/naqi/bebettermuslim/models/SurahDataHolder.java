package com.example.naqi.bebettermuslim.models;

/**
 * Created by hp on 2/24/2016.
 */
public class SurahDataHolder {

    private int rowPosition;
    private int index;
    private String englishSurahName;
    private String arabicSurahName;

    public SurahDataHolder(int rowPosition, int index, String englishSurahName, String arabicSurahName) {
        this.rowPosition = rowPosition;
        this.index = index;
        this.englishSurahName = englishSurahName;
        this.arabicSurahName = arabicSurahName;
    }

    public int getRowPosition() {
        return rowPosition;
    }

    public int getIndex() {
        return index;
    }

    public String getEnglishSurahName() {
        return englishSurahName;
    }

    public String getArabicSurahName() {
        return arabicSurahName;
    }
}
