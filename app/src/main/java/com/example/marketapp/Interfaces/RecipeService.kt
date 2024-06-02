package com.example.marketapp.Interfaces

import com.example.marketapp.Models.Recipe
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RecipeService {
    @GET("/api/recipes/listWithIngredients")
    fun getAllRecipes(): Call<List<Recipe>>

    @POST("/api/recipes/byIngredients")
    fun getRecipesByIngredients(@Body ingredientNames: List<String>): Call<List<Recipe>>
}