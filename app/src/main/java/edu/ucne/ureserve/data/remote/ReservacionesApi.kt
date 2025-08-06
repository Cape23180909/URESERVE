package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import retrofit2.http.*
import retrofit2.Response

interface ReservacionesApi {
    @GET("api/Reservaciones")
    suspend fun getAll(): List<ReservacionesDto>

    @GET("api/reservaciones/usuario/{matricula}")
    suspend fun getReservasByMatricula(@Path("matricula") matricula: String): List<ReservacionesDto>

    @GET("api/Reservaciones/{id}")
    suspend fun getById(@Path("id") id: Int?): ReservacionesDto

    @POST("api/Reservaciones")
    suspend fun insert(@Body reservacion: ReservacionesDto): Response<ReservacionesDto>



    @PUT("api/Reservaciones/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body reservacion: ReservacionesDto
    ): Response<ReservacionesDto> //  CAMBIO CLAVE

    @DELETE("api/Reservaciones/{id}")
    suspend fun delete(@Path("id") id: Int)
}