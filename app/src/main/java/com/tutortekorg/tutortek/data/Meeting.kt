package com.tutortekorg.tutortek.data

import java.io.Serializable

data class Meeting(
    val id: Long,
    val name: String,
    val date: String,
    val maxAttendants: Int,
    val address: String,
    val description: String
) : Serializable
