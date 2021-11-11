package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val rating: Float,
    val birthDate: String
) : Serializable {
    constructor(body: JSONObject) : this(
        body.getString("firstName"),
        body.getString("lastName"),
        body.getDouble("rating").toFloat(),
        body.getString("birthDate")
    )
}
