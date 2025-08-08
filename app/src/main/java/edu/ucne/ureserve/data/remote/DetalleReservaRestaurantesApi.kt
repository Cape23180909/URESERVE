package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.DetalleReservaRestaurantesDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DetalleReservaRestaurantesApi {
    @GET("api/DetalleReservaRestaurantes")
    suspend fun getAll(): List<DetalleReservaRestaurantesDto>

    @GET("api/DetalleReservaRestaurantes/{id}")
    suspend fun getById(@Path("id") id: Int): DetalleReservaRestaurantesDto

    @POST("api/DetalleReservaRestaurantes")
    suspend fun insert(@Body detalle: DetalleReservaRestaurantesDto): DetalleReservaRestaurantesDto

    @PUT("api/DetalleReservaRestaurantes/{id}")
    suspend fun update(@Path("id") id: Int, @Body detalle: DetalleReservaRestaurantesDto): DetalleReservaRestaurantesDto

    @DELETE("api/DetalleReservaRestaurantes/{id}")
    suspend fun delete(@Path("id") id: Int)
}
