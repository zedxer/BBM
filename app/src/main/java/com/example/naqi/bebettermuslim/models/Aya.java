package com.example.naqi.bebettermuslim.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naqi on 22,January,2019
 */
public class Aya {
    @SerializedName("@index")
    private String index;

    @SerializedName("@text")
    private String text;

    public String getIndex ()
    {
        return index;
    }

    public void setIndex (String index)
    {
        this.index = index;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [index = "+index+", text = "+text+"]";
    }


}