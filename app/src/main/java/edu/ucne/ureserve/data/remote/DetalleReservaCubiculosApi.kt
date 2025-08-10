package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.DetalleReservaCubiculosDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DetalleReservaCubiculosApi {
    @GET("api/DetalleReservaCubiculos")
    suspend fun getAll(): List<DetalleReservaCubiculosDto>

    @GET("api/DetalleReservaCubiculos/{id}")
    suspend fun getById(@Path("id") id: Int): DetalleReservaCubiculosDto

    @POST("api/DetalleReservaCubiculos")
    suspend fun insert(@Body detalle: DetalleReservaCubiculosDto): DetalleReservaCubiculosDto

    @PUT("api/DetalleReservaCubiculos/{id}")
    suspend fun update(@Path("id") id: Int, @Body detalle: DetalleReservaCubiculosDto): DetalleReservaCubiculosDto

    @DELETE("api/DetalleReservaCubiculos/{id}")
    suspend fun delete(@Path("id") id: Int)
}
