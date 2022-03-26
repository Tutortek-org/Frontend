package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class Topic(
    val id: Long,
    var name: String,
    var description: String,
    var userId: Long
) : Serializable {

    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name"),
        body.getString("description"),
        body.getLong("userId")
    )
}
