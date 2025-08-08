package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.DetalleReservaLaboratoriosDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DetalleReservaLaboratoriosApi {
    @GET("api/DetalleReservaLaboratorios")
    suspend fun getAll(): List<DetalleReservaLaboratoriosDto>

    @GET("api/DetalleReservaLaboratorios/{id}")
    suspend fun getById(@Path("id") id: Int): DetalleReservaLaboratoriosDto

    @POST("api/DetalleReservaLaboratorios")
    suspend fun insert(@Body detalle: DetalleReservaLaboratoriosDto): DetalleReservaLaboratoriosDto

    @PUT("api/DetalleReservaLaboratorios/{id}")
    suspend fun update(@Path("id") id: Int, @Body detalle: DetalleReservaLaboratoriosDto): DetalleReservaLaboratoriosDto

    @DELETE("api/DetalleReservaLaboratorios/{id}")
    suspend fun delete(@Path("id") id: Int)
}
