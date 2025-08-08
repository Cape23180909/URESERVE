package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.TipoCargoesDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
