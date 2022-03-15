package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val rating: Float,
    val birthDate: String,
    val description: String,
    val topicCount: Int,
    val roles: List<String>,
    var photoPath: String = ""
) : Serializable {
    constructor(body: JSONObject, roles: List<String>) : this(
        body.getString("firstName"),
        body.getString("lastName"),
        body.getDouble("rating").toFloat(),
        body.getString("birthDate"),
        body.getString("description"),
        body.getInt("topicCount"),
        roles
    )
}
