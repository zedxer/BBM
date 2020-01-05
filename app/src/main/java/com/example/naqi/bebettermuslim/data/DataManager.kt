package com.example.naqi.bebettermuslim.data

import com.example.naqi.bebettermuslim.realm.RealmConfigs
import io.realm.Realm

/**
 * Created by naqi on 06,May,2019
 */


abstract class DataManager() {

    interface RealmUpdatedCallback {
        fun onUpdated()
    }

    protected var apiService: ApiInterface? = ApiClient.client?.create(ApiInterface::class.java)
    lateinit var realm: Realm

    private var callback: RealmUpdatedCallback? = null

    init {
       realm = RealmConfigs.getDefaultRealm()

    }

    fun setRealmUpdatedCallback(callback: RealmUpdatedCallback) {
        this.callback = callback;
    }

    fun openNewRealmInstance() {
        closeRealm()
        openRealm()
    }

    fun closeRealm() {
        realm.close()
    }

    fun openRealm() {
        realm = RealmConfigs.getDefaultRealm()
    }
}