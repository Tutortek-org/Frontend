package com.tutortekorg.tutortek.requests.retrofit

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface FileUploadService {
    @Multipart
    @PUT("profiles/picture")
    fun upload(@Part photo: MultipartBody.Part, @Header("Authorization") authHeader: String): Call<ResponseBody>
}
