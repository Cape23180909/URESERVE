package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TarjetaCreditoApi {
    @GET("api/TarjetaCreditoes")
    suspend fun getAll(): List<TarjetaCreditoDto>

    @GET("api/TarjetaCreditoes/{id}")
    suspend fun getById(@Path("id") id: Int): TarjetaCreditoDto

    @POST("api/TarjetaCreditoes")
    suspend fun insert(@Body tarjetacredito: TarjetaCreditoDto): TarjetaCreditoDto

    @PUT("api/TarjetaCreditoes/{id}")
    suspend fun update(@Path("id") id: Int, @Body tipoCargo: TarjetaCreditoDto): TarjetaCreditoDto

    @DELETE("api/TarjetaCreditoes/{id}")
    suspend fun delete(@Path("id") id: Int)
}