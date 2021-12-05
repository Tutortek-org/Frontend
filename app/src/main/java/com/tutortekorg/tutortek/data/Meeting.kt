package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class Meeting(
    val id: Long,
    val name: String,
    val date: String,
    val maxAttendants: Int,
    val address: String,
    val description: String
) : Serializable {

    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name"),
        body.getString("date"),
        body.getInt("maxAttendants"),
        body.getString("address"),
        body.getString("description")
    )
}
