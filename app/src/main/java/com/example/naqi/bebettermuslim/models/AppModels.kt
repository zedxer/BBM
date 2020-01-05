package com.example.naqi.bebettermuslim.models

import com.example.naqi.bebettermuslim.data.DataManager
import java.io.Serializable
import java.util.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlin.collections.ArrayList


class PersonalModel : Serializable {
    var lat: Double? = 0.0
    var longi: Double? = 0.0
    var timezone: Double? = 0.0
    var date: Calendar? = null
}

class CustomeModel {
    var prayerTimeLong: Long = 0
    var prayerTime: String = ""
    var prayerName: String = ""
    var position: Int = 0
}


open class AlbumModel : RealmObject() {
    @PrimaryKey
    @SerializedName("_id")
    var _id: String = ""

    @SerializedName("reciter")
    var reciter: String? = null

    var recitersModel: RecitersModel? = null

    @SerializedName("release_year")
    var release_year: Int? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("__v")
    var __v: Int? = null
}

open class RecitersModel : RealmObject() {
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    var id: String = ""

    @SerializedName("language")
    @Expose
    var language: Int? = null

    var languageModel: LanguageModel? = null

    @SerializedName("picture_url")
    @Expose
    var pictureUrl: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("__v")
    @Expose
    var version: Int? = null
}

open class PlaylistModel : RealmObject() {
    @PrimaryKey
    var id: String = ""

    var name: String? = ""
    var colorInt: Int? = 0
    var naatList: RealmList<NaatModel>? = null

}

open class LanguageModel : RealmObject() {

    @SerializedName("_id")
    @Expose
    var langId: String = ""

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("name")
    @Expose
    var name: String? = ""

    @SerializedName("__v")
    @Expose
    var version: Int? = null

}

open class NaatModel : RealmObject() {

    @PrimaryKey
    @SerializedName("_id")
    @Expose
    var id: String = ""

    @SerializedName("reciter")
    @Expose
    var reciter: String? = null

    var recitersModel: RecitersModel? = null

    @SerializedName("album")
    @Expose
    var album: String? = null

    var albumModel: AlbumModel? = null

    @SerializedName("playTime")
    @Expose
    var playTime: Int? = null

    @SerializedName("naat_url")
    @Expose
    var naatUrl: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("__v")
    @Expose
    var version: Int? = null


}

open class TimezoneModel : RealmObject() {

    @SerializedName("dstOffset")
    @Expose
    var dstOffset: Int? = null
    @SerializedName("rawOffset")
    @Expose
    var rawOffset: Int? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("timeZoneId")
    @Expose
    var timeZoneId: String? = null
    @SerializedName("timeZoneName")
    @Expose
    var timeZoneName: String? = null

}

open class Holder(var prayNames: String, var prayTimes: String)

open class PreAzanAlarmModel(
    @PrimaryKey var pnAlarmID: String ="",
    var namazName: String = "",
    var namazTime: String= "",
    var isActivated: Boolean= false,
    var isMuted: Boolean= false,
    var alarmTime: String="00:00"
) : RealmObject()