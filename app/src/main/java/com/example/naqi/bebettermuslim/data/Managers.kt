package com.example.naqi.bebettermuslim.data

import retrofit2.Callback
import android.util.Log
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.API_CALL_TAG
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.GOOGLE_MAP_KEY
import com.example.naqi.bebettermuslim.adapter.AlbumExpandableChildModel
import com.example.naqi.bebettermuslim.adapter.RecitersExpandModel
import com.example.naqi.bebettermuslim.models.*
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response

abstract class Manager : DataManager() {
    abstract fun createObjectInDatabase(id: String): RealmObject?
    abstract fun updateAllObjectsInDatabase(list: List<RealmObject>)
    abstract fun getObjectFromDatabase(id: String): RealmObject?
    abstract fun getAllObjectsFromDatabase(): List<RealmObject>?
}

class TestManagers {
    private fun getMovies() {
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val call = apiInterface.getNaats()
        call.enqueue(object : Callback<List<PersonalModel>> {
            override fun onResponse(call: Call<List<PersonalModel>>, response: Response<List<PersonalModel>>) {
                if (response.isSuccessful) {
//                for (movie in response.body()) {
////                    movies.add(movie)
//                }
//                moviesAdapter.notifyDataSetChanged()
                } else {
                    Log.e(API_CALL_TAG, response.message())
                }
            }

            override fun onFailure(call: Call<List<PersonalModel>>, t: Throwable) {
                Log.e(API_CALL_TAG, t.message)
            }
        })

    }

}

class GoogleMapsManager private constructor() {
    companion object Singleton {
        val instance: GoogleMapsManager by lazy { GoogleMapsManager() }
    }

    fun getGoogleMapData(location: String, timestamp: String, callback: Callback<TimezoneModel>) {
        val service = ApiClient.createGoogleService(ApiInterface::class.java)
        val call = service.getCityResults(location, timestamp, GOOGLE_MAP_KEY)
        callback.let { call.enqueue(it) }
    }
}

class GetManager private constructor() {
    companion object Singleton {
        val instance: GetManager by lazy { GetManager() }
    }

    fun getAlbumsOfReciter(id: String, callback: Callback<AlbumResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getAlbumsOfReciter(id)
        callback.let {
            call.enqueue(it)
        }
    }

    fun getAllAlbumsOfReciter(callback: Callback<AlbumResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getAllAlbums()
        callback.let {
            call.enqueue(it)
        }
    }

    fun getAllLangs(callback: Callback<LanguageResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getAllLangs()
        callback.let {
            call.enqueue(it)
        }
    }

    fun getAllReciters(callback: Callback<RecitersResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getAllReciters()
        callback.let {
            call.enqueue(it)
        }
    }

    fun getNaatsOfAlbum(albumId: String, reciterId: String, callback: Callback<NaatsResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getNaatsOfAlbum(albumId, reciterId)
        callback.let {
            call.enqueue(it)
        }
    }

    fun getNaatsOfReciter(reciterId: String, callback: Callback<NaatsResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getNaatsOfReciter(reciterId)
        callback.let {
            call.enqueue(it)
        }
    }


}

class LanguageManager private constructor() : Manager() {
    companion object Singleton {
        val instance: LanguageManager by lazy { LanguageManager() }
    }

    override fun createObjectInDatabase(id: String): RealmObject? {
        var item = LanguageModel()
        item.langId = id
        realm.executeTransaction {
            item = realm.createObject(LanguageModel::class.java, item.id)
        }
        return item
    }

    fun createObjectInDatabase(id: Int): LanguageModel? {
        var item = LanguageModel()
        item.id = id
        item = realm.createObject(LanguageModel::class.java, item.id)

        return item
    }

    override fun updateAllObjectsInDatabase(list: List<RealmObject>) {
        realm.executeTransaction {
            var tempLang: LanguageModel? = null
            for (lang in list) {
                if (lang is LanguageModel) {
                    tempLang = realm.where(LanguageModel::class.java).equalTo("id", lang.id).findFirst()
                    if (tempLang != null) {
//                        if (celebrity.id == 169) {
//                            Log.v("HERE", celebrity.pic)
//                        }
                        updateProperties(tempLang, lang.name, lang.version!!)

                    } else {
//                        if (celebrity.id == 169) {
//                            Log.v("HERE", celebrity.pic)
//                        }
                        addObjectInDatabase(lang.id, lang.name, lang.version!!)
                    }
                }
            }
        }
    }

    override fun getObjectFromDatabase(id: String): LanguageModel? =
        realm.where(LanguageModel::class.java).equalTo("id", id).findFirst()

    fun getObjectFromDatabase(id: Int): LanguageModel? =
        realm.where(LanguageModel::class.java).equalTo("id", id).findFirst()

    override fun getAllObjectsFromDatabase(): RealmResults<LanguageModel>? =
        realm.where(LanguageModel::class.java).findAll()


    private fun addObjectInDatabase(id: Int, name: String?, version: Int) {
        val language = createObjectInDatabase(id)
        language?.let { updateProperties(it, name, version) }
    }

    private fun updateProperties(languageToBeUpdated: LanguageModel, name: String?, version: Int): LanguageModel {
        languageToBeUpdated.name = name
        languageToBeUpdated.version = version
        return languageToBeUpdated
    }

    fun getAllLangsFromServer(callback: Callback<LanguageResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getAllLangs()
        callback.let {
            call.enqueue(it)
        }
    }
}

class ReciterManager : Manager() {

    companion object Singleton {
        val instance: ReciterManager by lazy { ReciterManager() }
    }

    override fun createObjectInDatabase(id: String): RecitersModel? {
        var item = RecitersModel()
        item.id = id
        item = realm.createObject(RecitersModel::class.java, item.id)
        return item
    }

    override fun updateAllObjectsInDatabase(list: List<RealmObject>) {
        realm.executeTransaction {
            var tempReciter: RecitersModel? = null
            for (reciter in list) {
                if (reciter is RecitersModel) {
                    tempReciter = realm.where(RecitersModel::class.java).equalTo("id", reciter.id).findFirst()
                    if (tempReciter != null) {
                        updateProperties(
                            tempReciter,
                            reciter.language,
                            reciter.pictureUrl,
                            reciter.name,
                            reciter.version!!
                        )
                    } else {
                        addObjectInDatabase(
                            reciter.id,
                            reciter.language,
                            reciter.pictureUrl,
                            reciter.name,
                            reciter.version!!
                        )
                    }
                }
            }
        }
    }


    fun getAlbumExpandableList(
        reciterArray: ArrayList<RecitersModel>,
        albumArray: ArrayList<AlbumModel>
    ): ArrayList<RecitersExpandModel> {
        val reciterExapdableList = ArrayList<RecitersExpandModel>()
        for (reciter in reciterArray.withIndex()) {
            val albumList = ArrayList<AlbumExpandableChildModel>()
            albumList.add(AlbumExpandableChildModel("", "", 0, "", 0, true))
            for (album in albumArray) {
                if (album.reciter == reciter.value.id) {
                    albumList.add(
                        AlbumExpandableChildModel(
                            album._id,
                            album.reciter!!,
                            album.release_year!!,
                            album.title!!,
                            album.__v!!, false
                        )
                    )
                }
            }

            val recitersExpandModel = RecitersExpandModel(reciter.value, reciter.value.name!!, albumList)
            reciterExapdableList.add(recitersExpandModel)

        }
        return reciterExapdableList
    }

    private fun addObjectInDatabase(
        id: String,
        languageId: Int?,
        picture_url: String?,
        name: String?,
        version: Int
    ) {
        val language = createObjectInDatabase(id)
        language?.let { updateProperties(it, languageId, picture_url, name, version) }
    }

    private fun updateProperties(
        recitersToBeUpdated: RecitersModel,
        languageId: Int?,
        picture_url: String?,
        name: String?,
        version: Int
    ): RecitersModel {
        recitersToBeUpdated.language = languageId
        recitersToBeUpdated.languageModel = LanguageManager.instance.getObjectFromDatabase(languageId!!)
        recitersToBeUpdated.pictureUrl = picture_url
        recitersToBeUpdated.name = name
        recitersToBeUpdated.version = version

        return recitersToBeUpdated
    }

    override fun getObjectFromDatabase(id: String): RecitersModel? =
        realm.where(RecitersModel::class.java).equalTo("id", id).findFirst()

    override fun getAllObjectsFromDatabase(): RealmResults<RecitersModel>? =
        realm.where(RecitersModel::class.java).findAll()

    fun getAllReciterFromServer(callback: Callback<RecitersResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getAllReciters()
        callback.let {
            call.enqueue(it)
        }
    }

}

class AlbumManager private constructor() : Manager() {
    companion object Singleton {
        val instance: AlbumManager by lazy { AlbumManager() }
    }

    override fun createObjectInDatabase(id: String): AlbumModel? {
        var item = AlbumModel()
        item._id = id
        item = realm.createObject(AlbumModel::class.java, item._id)

        return item
    }

    fun getAllAlbumsFromServer(callback: Callback<AlbumResponse>) {
//        val service = ApiClient.createService(ApiInterface::class.java)
        val call = apiService?.getAllAlbums()
        callback.let {
            call?.enqueue(it)
        }
    }

    override fun updateAllObjectsInDatabase(list: List<RealmObject>) {
        realm.executeTransaction {
            var tempAlbum: AlbumModel? = null
            for (album in list) {
                if (album is AlbumModel) {
                    tempAlbum = realm.where(AlbumModel::class.java).equalTo("_id", album._id).findFirst()
                    if (tempAlbum != null) {
                        updateProperties(tempAlbum, album.title, album.reciter, album.release_year, album.__v!!)

                    } else {
                        addObjectInDatabase(album._id, album.title, album.reciter, album.release_year, album.__v!!)
                    }
                }
            }
        }
    }

    override fun getObjectFromDatabase(id: String): AlbumModel? =
        realm.where(AlbumModel::class.java).equalTo("_id", id).findFirst()

    override fun getAllObjectsFromDatabase(): RealmResults<AlbumModel>? =
        realm.where(AlbumModel::class.java).findAll()

    private fun addObjectInDatabase(
        id: String,
        title: String?,
        reciterId: String?,
        releaseYear: Int?,
        version: Int
    ) {
        val album = createObjectInDatabase(id)
        album?.let { updateProperties(album, title, reciterId, releaseYear, version) }
    }

    private fun updateProperties(
        albumToBeUpdated: AlbumModel,
        title: String?,
        reciterId: String?,
        releaseYear: Int?,
        version: Int
    ): AlbumModel {
        albumToBeUpdated.title = title
        albumToBeUpdated.reciter = reciterId
        albumToBeUpdated.recitersModel = ReciterManager.instance.getObjectFromDatabase(reciterId!!)
        albumToBeUpdated.release_year = releaseYear
        albumToBeUpdated.__v = version
        return albumToBeUpdated
    }

}

class PlayListManager : Manager() {
    companion object Singleton {
        val INSTANCE: PlayListManager by lazy {
            PlayListManager()
        }
    }

    fun removeItemFromDatabase(id: String) {
        realm.executeTransaction {
            realm.where(PlaylistModel::class.java).equalTo("id", id).findFirst()?.deleteFromRealm()
//        realm.where(PlaylistModel::class.java).equalTo("id", id)

        }
    }


    fun addNaatToPlaylist(id: String, naat: NaatModel) {
        realm.executeTransaction { getObjectFromDatabase(id)?.naatList?.add(naat) }
    }

    fun isNaatAlreadyExist(playlistId: String, naatId: String): Boolean {

        realm.where(PlaylistModel::class.java).equalTo("id", playlistId).findFirst()?.naatList?.let {
            val tempNaatList = it
            for (item in tempNaatList) {
                if (naatId.equals(item.id)) {
                    return true
                }
            }
            return false
        }
        return false
    }

    override fun createObjectInDatabase(id: String): PlaylistModel? {
        var item = PlaylistModel()
        item.id = id
        item = realm.createObject(PlaylistModel::class.java, item.id)

        return item
    }

    private fun addObjectInDatabase(
        id: String,
        name: String?, colorInt: Int?,
        naatList: RealmList<NaatModel>?
    ) {
        val naat = createObjectInDatabase(id)
        naat?.let { updateProperties(naat, name, colorInt, naatList) }
    }

    fun addSingleObjectInDatabase(
        id: String,
        name: String?, colorInt: Int?,
        naatList: RealmList<NaatModel>?
    ) {
        realm.executeTransaction {
            addObjectInDatabase(
                id,
                name,
                colorInt,
                naatList
            )
        }
    }

    override fun updateAllObjectsInDatabase(list: List<RealmObject>) {
        realm.executeTransaction {
            var tempPlaylist: PlaylistModel? = null
            for (playlist in list) {
                if (playlist is PlaylistModel) {
                    tempPlaylist = realm.where(PlaylistModel::class.java).equalTo("id", playlist.id).findFirst()
                    if (tempPlaylist != null) {
                        updateProperties(
                            tempPlaylist,
                            playlist.name,
                            playlist.colorInt,
                            playlist.naatList
                        )

                    } else {
                        addObjectInDatabase(
                            playlist.id,
                            playlist.name,
                            playlist.colorInt,
                            playlist.naatList
                        )
                    }
                }
            }
        }
    }

    override fun getObjectFromDatabase(id: String): PlaylistModel? {
        return realm.where(PlaylistModel::class.java).equalTo("id", id).findFirst()
    }

    override fun getAllObjectsFromDatabase(): List<PlaylistModel>? {
        return realm.where(PlaylistModel::class.java).findAll()
    }

    private fun updateProperties(
        playlistToBeUpdated: PlaylistModel, name: String?, colorInt: Int?,
        naatList: RealmList<NaatModel>?
    ): PlaylistModel {

        playlistToBeUpdated.name = name
        playlistToBeUpdated.colorInt = colorInt
        if (naatList != null) {
            var list: RealmList<NaatModel>? = null
            if (!naatList.isManaged) {
                list = RealmList()
                for (item in naatList) {
                    if (item.isManaged)
                        list.add(item)
                    else
                        realm.where(NaatModel::class.java).equalTo("id", item.id).findFirst()?.let {
                            list.add(it)
                        }

                }
            }
            playlistToBeUpdated.naatList = list
        }
        return playlistToBeUpdated
    }
}

class NaatManager : Manager() {

    companion object Singleton {
        val instance: NaatManager by lazy {
            NaatManager()
        }
    }

    override fun createObjectInDatabase(id: String): NaatModel? {
        var item = NaatModel()
        item.id = id
        item = realm.createObject(NaatModel::class.java, item.id)
        return item
    }

    override fun updateAllObjectsInDatabase(list: List<RealmObject>) {
        realm.executeTransaction {
            var tempNaat: NaatModel? = null
            for (naat in list) {
                if (naat is NaatModel) {
                    tempNaat = realm.where(NaatModel::class.java).equalTo("id", naat.id).findFirst()
                    if (tempNaat != null) {
                        updateProperties(
                            tempNaat,
                            naat.reciter,
                            naat.album,
                            naat.playTime,
                            naat.naatUrl,
                            naat.title,
                            naat.version!!
                        )

                    } else {
                        addObjectInDatabase(
                            naat.id,
                            naat.reciter,
                            naat.album,
                            naat.playTime,
                            naat.naatUrl,
                            naat.title,
                            naat.version!!
                        )
                    }
                }
            }
        }
    }


    private fun addObjectInDatabase(
        id: String,
        reciterId: String?,
        albumId: String?,
        playTime: Int?,
        naatUrl: String?,
        title: String?, version: Int
    ) {
        val naat = createObjectInDatabase(id)
        naat?.let { updateProperties(naat, reciterId, albumId, playTime, naatUrl, title, version) }
    }

    override fun getObjectFromDatabase(id: String): NaatModel? =
        realm.where(NaatModel::class.java).equalTo("id", id).findFirst()

    override fun getAllObjectsFromDatabase(): RealmResults<NaatModel>? =
        realm.where(NaatModel::class.java).findAll()

    fun getAllNaatOfReciterFromDatabase(reciterId: String): RealmResults<NaatModel> {
        return realm.where(NaatModel::class.java).equalTo("reciter", reciterId).findAll()
    }

    fun getAllNaatsOfAlbumFromDatabase(albumId: String): RealmResults<NaatModel> {
        return realm.where(NaatModel::class.java).equalTo("album", albumId).findAll()
    }

    private fun updateProperties(
        naatToBeUpdated: NaatModel,
        reciterId: String?,
        albumId: String?,
        playTime: Int?,
        naatUrl: String?,
        title: String?,
        version: Int
    ): NaatModel {
        naatToBeUpdated.title = title
        naatToBeUpdated.reciter = reciterId
        naatToBeUpdated.recitersModel = ReciterManager.instance.getObjectFromDatabase(reciterId!!)
        naatToBeUpdated.album = albumId
        if (albumId == null) {
            naatToBeUpdated.albumModel = AlbumManager.instance.getObjectFromDatabase("anonymous")
        } else {
            naatToBeUpdated.albumModel = AlbumManager.instance.getObjectFromDatabase(albumId)
        }
        naatToBeUpdated.playTime = playTime
        naatToBeUpdated.naatUrl = naatUrl
        naatToBeUpdated.version = version
        return naatToBeUpdated
    }


    fun getNaatsOfAlbum(albumId: String, reciterId: String, callback: Callback<NaatsResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getNaatsOfAlbum(albumId, reciterId)
        callback.let {
            call.enqueue(it)
        }
    }

    fun getNaatsOfReciter(reciterId: String, callback: Callback<NaatsResponse>) {
        val service = ApiClient.createService(ApiInterface::class.java)
        val call = service.getNaatsOfReciter(reciterId)
        callback.let {
            call.enqueue(it)
        }
    }


}

class PreNamazAlarmManager : Manager() {

    companion object Singleton {
        val INSTANCE: PreNamazAlarmManager by lazy {
            PreNamazAlarmManager()
        }
    }

    override fun createObjectInDatabase(id: String): PreAzanAlarmModel? {
        var item = PreAzanAlarmModel()
        item.pnAlarmID = id
        item = realm.createObject(PreAzanAlarmModel::class.java, item.pnAlarmID)

        return item

    }

    private fun addObjectInDatabase(
        pnAlarmID: String,
        namazName: String,
        namazTime: String,
        isActivated: Boolean,
        isMuted: Boolean,
        alarmTime: String
    ) {
        val preAzan = createObjectInDatabase(pnAlarmID)
        preAzan?.let { updateProperties(it, namazName, namazTime, isActivated, isMuted, alarmTime) }
    }

    fun updateSingleObjectInDatabase(id: String, isActivated: Boolean, isMuted: Boolean, alarmTime: String) {
        realm.executeTransaction {
            var preAzan =
                realm.where(PreAzanAlarmModel::class.java).equalTo("pnAlarmID", id).findFirst()
            if (preAzan != null) {
                updateSingleProperty(
                    preAzan,
//                    preAzanAlarm.namazName,
//                    preAzanAlarm.namazTime,
                    isActivated,
                    isMuted,
                    alarmTime
                )

            }
        }
    }


    fun updateSingleProperty(
        preAzanToBeUpdated: PreAzanAlarmModel,
        isActivated: Boolean,
        isMuted: Boolean,
        alarmTime: String
    ): PreAzanAlarmModel {
        preAzanToBeUpdated.isActivated = isActivated
        preAzanToBeUpdated.isMuted = isMuted
        preAzanToBeUpdated.alarmTime = alarmTime

        return preAzanToBeUpdated
    }

    private fun updateProperties(
        preAzanToBeUpdated: PreAzanAlarmModel,
        namazName: String,
        namazTime: String,
        isActivated: Boolean,
        isMuted: Boolean,
        alarmTime: String
    ): PreAzanAlarmModel {

        preAzanToBeUpdated.namazName = namazName
        preAzanToBeUpdated.namazTime = namazTime
        preAzanToBeUpdated.isActivated = isActivated
        preAzanToBeUpdated.isMuted = isMuted
        preAzanToBeUpdated.alarmTime = alarmTime

        return preAzanToBeUpdated
    }

    override fun updateAllObjectsInDatabase(list: List<RealmObject>) {
        realm.executeTransaction {
            var preAzan: PreAzanAlarmModel? = null
            for (preAzanAlarm in list) {
                if (preAzanAlarm is PreAzanAlarmModel) {
                    preAzan = realm.where(PreAzanAlarmModel::class.java).equalTo("pnAlarmID", preAzanAlarm.pnAlarmID)
                        .findFirst()
                    if (preAzan != null) {
                        updateProperties(
                            preAzan,
                            preAzanAlarm.namazName,
                            preAzanAlarm.namazTime,
                            preAzanAlarm.isActivated,
                            preAzanAlarm.isMuted,
                            preAzanAlarm.alarmTime
                        )

                    } else {
                        addObjectInDatabase(
                            preAzanAlarm.pnAlarmID,
                            preAzanAlarm.namazName,
                            preAzanAlarm.namazTime,
                            preAzanAlarm.isActivated,
                            preAzanAlarm.isMuted,
                            preAzanAlarm.alarmTime
                        )
                    }
                }
            }
        }
    }

    override fun getObjectFromDatabase(id: String): PreAzanAlarmModel? {
        return realm.where(PreAzanAlarmModel::class.java).equalTo("pnAlarmID", id).findFirst()
    }

    override fun getAllObjectsFromDatabase(): List<PreAzanAlarmModel>? {
        return realm.where(PreAzanAlarmModel::class.java).findAll()
    }

}