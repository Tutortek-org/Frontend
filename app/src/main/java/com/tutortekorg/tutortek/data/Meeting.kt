package com.tutortekorg.tutortek.data

import org.json.JSONObject
import java.io.Serializable

data class Meeting(
    val id: Long,
    var name: String,
    var date: String,
    var maxAttendants: Int,
    var address: String,
    var description: String,
    var price: Double
) : Serializable {

    constructor(body: JSONObject) : this(
        body.getLong("id"),
        body.getString("name"),
        body.getString("date"),
        body.getInt("maxAttendants"),
        body.getString("address"),
        body.getString("description"),
        body.getDouble("price")
    )
}
