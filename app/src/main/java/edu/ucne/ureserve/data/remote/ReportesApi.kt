package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.ReportesDto
import retrofit2.http.*

interface ReportesApi {
    @GET("api/Reportes")
    suspend fun getAll(): List<ReportesDto>

    @GET("api/Reportes/{id}")
    suspend fun getById(@Path("id") id: Int): ReportesDto

    @POST("api/Reportes")
    suspend fun insert(@Body reporte: ReportesDto): ReportesDto

    @PUT("api/Reportes/{id}")
    suspend fun update(@Path("id") id: Int, @Body reporte: ReportesDto): ReportesDto

    @DELETE("api/Reportes/{id}")
    suspend fun delete(@Path("id") id: Int)
}
