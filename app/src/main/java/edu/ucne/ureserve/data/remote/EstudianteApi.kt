package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EstudianteApi {
    @GET("api/Estudiantes")
    suspend fun getAll(): List<EstudianteDto>

    @GET("api/Estudiantes/{id}")
    suspend fun getById(@Path("id") id: Int): EstudianteDto

    @POST("api/Estudiantes")
    suspend fun insert(@Body estudiante: EstudianteDto): EstudianteDto

    @PUT("api/Estudiantes/{id}")
    suspend fun update(@Path("id") id: Int, @Body estudiante: EstudianteDto): EstudianteDto

    @DELETE("api/Estudiantes/{id}")
    suspend fun delete(@Path("id") id: Int)
}