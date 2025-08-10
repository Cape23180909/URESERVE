package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DetalleReservaProyectorsApi {
    @GET("api/DetalleReservaProyectors")
    suspend fun getAll(): List<DetalleReservaProyectorsDto>

    @GET("api/DetalleReservaProyectors/{id}")
    suspend fun getById(@Path("id") id: Int): DetalleReservaProyectorsDto

    @POST("api/DetalleReservaProyectors")
    suspend fun insert(
        @Body detalle: DetalleReservaProyectorsDto
    ): Response<DetalleReservaProyectorsDto>

    @PUT("api/DetalleReservaProyectors/{id}")
    suspend fun update(@Path("id") id: Int, @Body detalle: DetalleReservaProyectorsDto): DetalleReservaProyectorsDto

    @DELETE("api/DetalleReservaProyectors/{id}")
    suspend fun delete(@Path("id") id: Int)
}