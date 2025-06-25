package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import retrofit2.http.*

interface ReservacionesApi {
    @GET("api/Reservaciones")
    suspend fun getAll(): List<ReservacionesDto>

    @GET("api/Reservaciones/{id}")
    suspend fun getById(@Path("id") id: Int): ReservacionesDto

    @POST("api/Reservaciones")
    suspend fun insert(@Body reservacion: ReservacionesDto): ReservacionesDto

    @PUT("api/Reservaciones/{id}")
    suspend fun update(@Path("id") id: Int, @Body reservacion: ReservacionesDto): ReservacionesDto

    @DELETE("api/Reservaciones/{id}")
    suspend fun delete(@Path("id") id: Int)
}