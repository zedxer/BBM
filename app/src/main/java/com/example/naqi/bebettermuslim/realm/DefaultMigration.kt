package com.example.naqi.bebettermuslim.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Created by naqi on 04,May,2019
 */

class DefaultMigration : RealmMigration {

    companion object {
        const val DB_VERSION = 17
    }

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        val schema = realm?.schema

    }


    override fun hashCode(): Int = 37

    override fun equals(other: Any?): Boolean = other is DefaultMigration
}