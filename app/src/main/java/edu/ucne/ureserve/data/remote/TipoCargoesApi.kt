package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.TipoCargoesDto
import retrofit2.http.*

interface TipoCargoesApi {
    @GET("api/TipoCargoes")
    suspend fun getAll(): List<TipoCargoesDto>

    @GET("api/TipoCargoes/{id}")
    suspend fun getById(@Path("id") id: Int): TipoCargoesDto

    @POST("api/TipoCargoes")
    suspend fun insert(@Body tipoCargo: TipoCargoesDto): TipoCargoesDto

    @PUT("api/TipoCargoes/{id}")
    suspend fun update(@Path("id") id: Int, @Body tipoCargo: TipoCargoesDto): TipoCargoesDto

    @DELETE("api/TipoCargoes/{id}")
    suspend fun delete(@Path("id") id: Int)
}
