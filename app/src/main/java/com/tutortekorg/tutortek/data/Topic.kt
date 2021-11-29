package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class Topic(
    val id: Long,
    var name: String
) : Serializable {

    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name")
    )
}
