package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import retrofit2.http.*

interface RestaurantesApi {
    @GET("api/Restaurantes")
    suspend fun getAll(): List<RestaurantesDto>

    @GET("api/Restaurantes/{id}")
    suspend fun getById(@Path("id") id: Int): RestaurantesDto

    @POST("api/Restaurantes")
    suspend fun insert(@Body restaurante: RestaurantesDto): RestaurantesDto

    @PUT("api/Restaurantes/{id}")
    suspend fun update(@Path("id") id: Int, @Body restaurante: RestaurantesDto): RestaurantesDto


    @DELETE("api/Restaurantes/{id}")
    suspend fun delete(@Path("id") id: Int)
}
