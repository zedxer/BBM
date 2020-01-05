package com.example.naqi.bebettermuslim.data

import com.example.naqi.bebettermuslim.models.AlbumModel
import com.example.naqi.bebettermuslim.models.LanguageModel
import com.example.naqi.bebettermuslim.models.NaatModel
import com.example.naqi.bebettermuslim.models.RecitersModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Naqi on 1/17/18.
 */
class AlbumResponse {
    @SerializedName("error")
    var error: Boolean? = null
    @SerializedName("albums")
    var albums: ArrayList<AlbumModel>? = null
}

class LanguageResponse {
    @SerializedName("error")
    var error: Boolean? = null
    @SerializedName("Languages")
    var languages: ArrayList<LanguageModel>? = null
}

class RecitersResponse {
    @SerializedName("error")
    var error: Boolean? = null
    @SerializedName("reciters")
    var reciters: ArrayList<RecitersModel>? = null
}

class NaatsResponse {
    @SerializedName("error")
    var error: Boolean? = null
    @SerializedName("naats")
    var naats: ArrayList<NaatModel>? = null
}