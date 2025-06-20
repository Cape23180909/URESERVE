package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CubiculosApi {
    @GET("api/Cubiculos")
    suspend fun getAll(): List<CubiculosDto>

    @GET("api/Cubiculos/{id}")
    suspend fun getById(@Path("id") id: Int): CubiculosDto

    @POST("api/Cubiculos")
    suspend fun insert(@Body cubiculo: CubiculosDto): CubiculosDto

    @PUT("api/Cubiculos/{id}")
    suspend fun update(@Path("id") id: Int, @Body cubiculo: CubiculosDto): CubiculosDto

    @DELETE("api/Cubiculos/{id}")
    suspend fun delete(@Path("id") id: Int)
}
