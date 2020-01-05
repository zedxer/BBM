package com.example.naqi.bebettermuslim.realm

import com.example.naqi.bebettermuslim.models.*
import io.realm.annotations.RealmModule

/**
 * Created by naqi on 04,May,2019
 */

@RealmModule(library = true, classes = arrayOf(AlbumModel::class, RecitersModel::class,
    LanguageModel::class, NaatModel::class, TimezoneModel::class, PlaylistModel::class, PreAzanAlarmModel::class))
open class DefaultModule