package com.tutortekorg.tutortek.requests.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface FileDownloadService {
    @GET("profiles/{id}/picture")
    fun downloadProfilePicture(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): Call<ResponseBody>
}
