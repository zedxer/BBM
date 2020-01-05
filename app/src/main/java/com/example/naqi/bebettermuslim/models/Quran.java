package com.example.naqi.bebettermuslim.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naqi on 22,January,2019
 */
public class Quran {
    @SerializedName("sura")
    private Sura[] sura;

    public Sura[] getSura ()
    {
        return sura;
    }

    public void setSura (Sura[] sura)
    {
        this.sura = sura;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [sura = "+sura+"]";
    }
}