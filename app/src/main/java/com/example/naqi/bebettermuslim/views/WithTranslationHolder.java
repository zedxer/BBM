package com.example.naqi.bebettermuslim.views;

/**
 * Created by naqi on 22,January,2019
 */
public class WithTranslationHolder {

    int surahNumber;
    int ayaNumber;
    String arabicAya;
    String translationAya;
    int totalAya;

    public WithTranslationHolder(int surahNumber, int ayaNumber, String arabicAya, String translationAya, int totalAya) {
        this.surahNumber = surahNumber;
        this.ayaNumber = ayaNumber;
        this.arabicAya = arabicAya;
        this.translationAya = translationAya;
        this.totalAya = totalAya;
    }

    public int getSurahNumber() {
        return surahNumber;
    }

    public int getAyaNumber() {
        return ayaNumber;
    }

    public String getArabicAya() {
        return arabicAya;
    }

    public String getTranslationAya() {
        return translationAya;
    }

    public int getTotalAya() {
        return totalAya;
    }
}
