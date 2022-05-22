package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class Topic(
    val id: Long,
    var name: String,
    var description: String,
    var profileId: Long,
    var isApproved: Boolean
) : Serializable {

    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name"),
        body.getString("description"),
        body.getLong("profileId"),
        body.getBoolean("isApproved")
    )
}
