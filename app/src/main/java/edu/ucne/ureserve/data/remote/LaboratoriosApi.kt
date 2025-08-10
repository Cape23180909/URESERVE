package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LaboratoriosApi {
    @GET("api/Laboratorios")
    suspend fun getAll(): List<LaboratoriosDto>

    @GET("api/Laboratorios/{id}")
    suspend fun getById(@Path("id") id: Int): LaboratoriosDto

    @POST("api/Laboratorios")
    suspend fun insert(@Body laboratorio: LaboratoriosDto): LaboratoriosDto

    @PUT("api/Laboratorios/{id}")
    suspend fun update(@Path("id") id: Int, @Body laboratorio: LaboratoriosDto): LaboratoriosDto

    @DELETE("api/Laboratorios/{id}")
    suspend fun delete(@Path("id") id: Int)
}