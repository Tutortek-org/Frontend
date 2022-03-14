package com.tutortekorg.tutortek.requests.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface FileDownloadService {
    @GET("profiles/picture")
    fun downloadProfilePicture(@Header("Authorization") authHeader: String): Call<ResponseBody>
}
