package bonch.dev.networking.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass


@RealmClass
open class Photo : RealmObject() {

    var url: String? = null
}