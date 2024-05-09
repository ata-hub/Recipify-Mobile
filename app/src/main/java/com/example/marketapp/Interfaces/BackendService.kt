package com.example.marketapp.Interfaces

import com.example.marketapp.Models.Recipe
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BackendService {

    @Multipart
    @POST("/api/imagestorage/uploadImage")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("category") category: RequestBody
    ): Call<List<Recipe>>

}