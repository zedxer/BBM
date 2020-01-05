package com.example.naqi.bebettermuslim.Utils

import android.content.ServiceConnection
import com.example.naqi.bebettermuslim.models.NaatModel
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*
import kotlin.collections.ArrayList

class Constants {
    companion object {
        const val USER_LATITUDE = "LATITUDE"
        const val USER_LONGITUDE = "LONGITUDE"
        const val USER_TIMEZONE = "TIMEZONE"
        const val BASE_API_URL = "https://bbmserver.herokuapp.com/"
        const val API_CALL_TAG = "APICALL"
        const val GOOGLE_MAP_BASE_URL = "https://maps.googleapis.com"
        const val GOOGLE_MAP_KEY = "AIzaSyCJ9xrlZagau5alvmtSezL9RhA6DqqCdH4"
        const val RESULT_ACTIVITY_LOGIN = 10
        const val PREF_FILE_NAME = "MyPrefsFile"
        const val TIME_24 = 0
        const val TIME_12 = 1
        const val TIME_12NS = 2
        const val SAMPLE_NAAT_ID = "56f4dcc09892650300cf82ca"
        const val SAMPLE_RECITER_ID = "56f4dbca9892650300cf82c7"
        const val SAMPLE_ALBUM_ID = "56f4dbe79892650300cf82c8"
        const val TIME_HOURS_SWITCH = "TFOURS"

        const val NAMAZ_POSITION = "NAMAZ_POSITION"

        const val MEDIA_SESSION_TAG = "sample_exoplayer_app"
        const val NOTIFICATION_CHANNEL_NAME = "Player Controls"
        const val NOTIFICATION_CHANNEL_DESC = "Player Controls on StatusBar"
        const val NOTIFICATION_CHANNEL_ID = "sample_player_notification_01"
        const val NOTIFICATION_TAG = "sample_player_notification_TAG"
        const val NOTIFICATION_ID = 0



/***     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*  **/
//                variables
/***      *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* **/

        // NAATS
        var exoplayerInstace: Player? = null
        var titleNaat: String = ""
        var artistNameNaat: String = ""
        var imageUrlNaat: String = ""
        var audioConnection: ServiceConnection? = null
        var listOfSentNaats = ArrayList<NaatModel>()
        //PRAYER TIMES
        var staticFormattedTimings: ArrayList<Date>? = null

/***      -*-*-*-*-*-*-*-*-*-                 **/

        const val FROM_KEY = "FROM"
        const val RECIT_KEY = "RECITER"

        const val ALB_KEY = "ALBUM"
        const val NOTIF_KEY = "NOTIF_CLK"
        const val ALBUM_ID_CONTS = "ALBUM_ID"
        const val RECITER_ID_CONTS = "RECITER_ID "
        const val NAAT_ID_CONTS = "NAAT_ID"

        const val COLOR_TINT_RED = 0
        const val COLOR_TINT_DARKBLUE = 1
        const val COLOR_TINT_GREEN = 2
        const val COLOR_TINT_LIGHTBLUE = 3
        const val COLOR_TINT_ORANGE = 4
        const val COLOR_TINT_YELLOW = 5

        //JVM FIELDS


        const val FAJAR_CONSTANT_KEY = "0"
        const val DHUHR_CONSTANT_KEY = "2"
        const val ASR_CONSTANT_KEY= "3"
        const val MAGHRIB_CONSTANT_KEY= "4" //after removing  sunset 5
        const val ISHA_CONSTANT_KEY= "5" //after removing sunset 6

        const val SUNRISE_CONSTANT_KEY= "1"
        const val SUNSET_CONSTANT_KEY= "4"

        const val GOOGLE_PLACES_KEY = "AIzaSyCJ9xrlZagau5alvmtSezL9RhA6DqqCdH4"

        const val FAJAR_CONSTANT = 0
        const val DHUHR_CONSTANT = 2
        const val ASR_CONSTANT = 3
        const val MAGHRIB_CONSTANT = 4 //after removing  sunset 5
        const val ISHA_CONSTANT = 5 //after removing sunset 6

        const val SUNRISE_CONSTANT = 1
        const val SUNSET_CONSTANT = 4

        const val MUTED_AZAN = "MUTED_AZAN"

        @JvmField
        val MONTHS = arrayOf(
            "Muharram",
            "Safar",
            "Rabiul Awwal",
            "Rabiul Akhir",
            "Jumadal Ula",
            "Jumadal Akhira",
            "Rajab",
            "Shaaban",
            "Ramadhan",
            "Shawwal",
            "Dhulqaada",
            "Dhulhijja"
        )
        @JvmField
        val USER_SETTING = "SETTING"

        @JvmField
        val PRAYER_TIMING = "PRAYER_TIMING"
        @JvmField
        val USER_ADDRESS = "ADDRESS"
        @JvmField
        val CALCULATION_METHOD = "CALCULATION_METHOD"
        @JvmField
        val ASR_JURISTIC = "ASR_JURISTIC"
        @JvmField
        val HIGH_LATITUDE_ADJUSTMENT = "HIGH_LATITUDE_ADJUSTMENT"
        @JvmField
        val TIME_FORMAT = "TIME_FORMAT"
        @JvmField
        val OFFSETS = "OFFSETS"
        @JvmField
        val APP_FIRST_RUN = "FIRST_RUN"

        @JvmField
        val PRAYER_ALARM_TABLE = "prayer_alarms"
        @JvmField
        val PREAZAN_ALARM_TABLE = "preazan_alarms"


        @JvmField
        val ALARMS_DATABASE_NAME = "alarms.sqlite"
        @JvmField
        val ALARMS_DATABASE_VERSION = 1

        @JvmField
        val COLUMN_ALARM_ID = "id"
        @JvmField
        val COLUMN_ALARM_TIME = "alarm_time"
        @JvmField
        val COLUMN_ALARM_NAME = "alarm_name"
        @JvmField
        val COLUMN_ALARM_STATUS = "alarm_status"

        @JvmField
        val ALARM_TONE = "ALARM_TONE"

        @JvmField
        val QURAN_SETTINGS = "QURAN_SETTINGS"
        @JvmField
        val TRANSLATION_STATE = "TRANSLATION_STATE"
        @JvmField
        val TRANSLATION_LANGUAGE = "TRANSLATION_LANGUAGE"
        @JvmField
        val RECITER = "RECITER"
        @JvmField
        val SCROLL_STATE = "SCROLL_STATE"
        @JvmField
        val SCREEN_STATE = "SCREEN_STATE"
        @JvmField
        val RECITER_WEB_ADDRESS = "https://bbmserver.herokuapp.com"
        @JvmField
        val QURAN_WEB_ADDRESS = "https://technollage.com/BBM/"
        @JvmField
        val QURAN_AUDIO_PATH = QURAN_WEB_ADDRESS + "quranData/quranAudio/"
        @JvmField
        val QURAN_TRANSLATION_PATH = QURAN_WEB_ADDRESS + "quran_translations/"

        @JvmField
        val COLUMN_MONGO_ID = "mongoId"
        @JvmField
        val RECITER_TABLE = "reciters"
        @JvmField
        val RECITER_COLUMN_NAME = "name"
        @JvmField
        val RECITER_COLUMN_THUMBNAIL_URL = "thumbnailURL"
        @JvmField
        val RECITER_COLUMN_LANGUAGE_ID = "langId"
        @JvmField
        val RECITER_COLUMN_VERSION = "version"

        @JvmField
        val ALBUMS_TABLE = "albums"
        @JvmField
        val ALBUMS_COLUMN_TITLE = "title"
        @JvmField
        val ALBUMS_COLUMN_YEAR = "year"
        @JvmField
        val ALBUMS_COLUMN_RECITER = "recitersModel"
        @JvmField
        val ALBUMS_COLUMN_VERSION = "version"

        @JvmField
        val LANGUAGE_TABLE = "languages"
        @JvmField
        val LANGUAGE_ID = "Id"
        @JvmField
        val LANGUAGE_NAME = "name"

        @JvmField
        val NAAT_DATABASE_NAME = "naatBBM.db"
        @JvmField
        val NAAT_DATABASE_VERSION = 1
        @JvmField
        val NAAT_TABLE = "naats"
        @JvmField
        val NAAT_COLUMN_TITLE = "title"
        @JvmField
        val NAAT_COLUMN_URL = "url"
        @JvmField
        val NAAT_COLUMN_DURATION = "duration"
        @JvmField
        val NAAT_COLUMN_RECITER_ID = "recitersModel"
        @JvmField
        val NAAT_COLUMN_ALBUM_ID = "albumModel"
        @JvmField
        val NAAT_COLUMN_BOOKMARKED = "bookmarked"
        @JvmField
        val NAAT_COLUMN_VERSION = "version"

        @JvmField
        val PLAYLIST_ARG_PAGE = "ARG_PAGE"
        @JvmField
        val PLAYLIST_TABLE = "playlists"
        @JvmField
        val PLAYLIST_COLUMN_ID = "Id"
        @JvmField
        val PLAYLIST_COLUMN_NAME = "name"
        @JvmField
        val PLAYLIST_COLUMN_COLOR_TAG = "colorTag"
        @JvmField
        val PLAYLIST_COLUMN_NAAT_COUNT = "naatCount"

        @JvmField
        val SALAH_TRACKER_DATABASE_NAME = "salahTracker"
        @JvmField
        val TRACKING_INFO_TABLE_NAME = "trackingInfo"
        @JvmField
        val TRACKING_INFO_COLUMN_ID = "_id"
        @JvmField
        val TRACKING_INFO_COLUMN_SALAH_STATE = "salahState"
        @JvmField
        val TRACKING_INFO_COLUMN_SALAH_DATE = "salahDate"
        @JvmField
        val TRACKING_INFO_COLUMN_PRAYER_TYPE = "prayer"


        @JvmField
        var surahNames = arrayOf(
            "The Opening",
            "The Cow",
            "The Family Of Imran",
            "Women",
            "The Food",
            "The Cattle",
            "The Elevated Places",
            "The Spoils Of War",
            "Repentance",
            "Jonah",
            "Hud",
            "Joseph",
            "The Thunder",
            "Abraham",
            "The Rock",
            "The Bee",
            "The Night Journey",
            "The Cave",
            "Mary",
            "Ta Ha",
            "The Prophets",
            "The Pilgrimage",
            "The Believers",
            "The Light",
            "The Criterion",
            "The Poets",
            "The Ant",
            "The Narrative",
            "The Spider",
            "The Romans",
            "Lukman",
            "The Adoration",
            "The Allies",
            "Sheba",
            "The Creator",
            "Ya Sin",
            "The Rangers",
            "Sad",
            "The Companies",
            "The Forgiving One",
            "Revelations Well Expounded",
            "The Counsel",
            "The Embellishment",
            "The Evident Smoke",
            "The Kneeling",
            "The Sandhills",
            "Muhammad",
            "The Victory",
            "The Chambers",
            "Qaf",
            "The Scatterers",
            "The Mountain",
            "The Star",
            "The Moon",
            "The Merciful",
            "That Which is Coming",
            "The Iron",
            "She Who Pleaded",
            "The Exile",
            "She Who is Tested",
            "The Ranks",
            "The Day of Congregation",
            "The Hypocrites",
            "The Cheating",
            "The Divorce",
            "The Prohibition",
            "The Kingdom",
            "The Pen",
            "The Inevitable",
            "The Ladders",
            "Noah",
            "The Jinn",
            "The Mantled One",
            "The Clothed One",
            "The Resurrection",
            "The Man",
            "The Emissaries",
            "The Tidings",
            "Those Who Pull Out",
            "He Frowned",
            "The Cessation",
            "The Cleaving Asunder",
            "The Defrauders",
            "The Rending",
            "The Constellations",
            "The Night-Comer",
            "The Most High",
            "The Overwhelming Calamity",
            "The Dawn",
            "The City",
            "The Sun",
            "The Night",
            "The Early Hours",
            "The Expansion",
            "The Fig",
            "The Clot",
            "The Majesty",
            "The Proof",
            "The Shaking",
            "The Assaulters",
            "The Terrible Calamity",
            "Worldly Gain",
            "Time",
            "The Slanderer",
            "The Elephant",
            "The Quraish",
            "The Daily Necessaries",
            "Abundance",
            "The Unbelievers",
            "The Help",
            "The Palm Fibre",
            "The Unity",
            "The Daybreak",
            "The Men"
        )
        // Juristic Methods
        const val HANNAFI_METHOD = 1
        const val SHAFII_METHOD = 0

        //              Calculation Methods
        const val CALC_JAFARI = 0
        const val CALC_KARACHI = 1
        const val CALC_ISNA = 2
        const val CALC_MWL = 3
        const val CALC_MAKKAH = 4
        const val CALC_EGYPT = 5
        const val CALC_TEHRAN = 6
        const val CALC_CUSTOM = 7

    }
}