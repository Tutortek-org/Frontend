package com.tutortekorg.tutortek.requests.retrofit

import com.tutortekorg.tutortek.constants.TutortekConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    private val builder = Retrofit.Builder()
        .baseUrl(TutortekConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = builder.build()

    private val httpClient = OkHttpClient.Builder()

    fun<T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}
