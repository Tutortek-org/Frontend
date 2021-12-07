package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class LearningMaterial(
    val id: Long,
    var name: String,
    var description: String,
    var link: String
) : Serializable {

    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name"),
        body.getString("description"),
        body.getString("link")
    )
}
