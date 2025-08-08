package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
