package com.tutortekorg.tutortek.data

import org.json.JSONObject

data class Topic(
    val id: Long,
    val name: String
) {
    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name")
    )
}
