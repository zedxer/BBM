package com.example.naqi.bebettermuslim.realm
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by naqi on 04,May,2019
 */


class RealmConfigs {

    companion object {
        fun getDefaultRealm(): Realm {
            return Realm.getInstance(RealmConfiguration.Builder()
                .modules(DefaultModule())
                .schemaVersion(DefaultMigration.DB_VERSION.toLong())
                .deleteRealmIfMigrationNeeded()
//                    .migration(DefaultMigration())
                .build())
        }

    }
}