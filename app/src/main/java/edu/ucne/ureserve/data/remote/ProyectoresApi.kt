package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import retrofit2.http.*

interface ProyectoresApi {
    @GET("api/Proyectores")
    suspend fun getAll(): List<ProyectoresDto>

    @GET("api/Proyectores/{id}")
    suspend fun getById(@Path("id") id: Int): ProyectoresDto

    @POST("api/Proyectores")
    suspend fun insert(@Body proyector: ProyectoresDto): ProyectoresDto

    @PUT("api/Proyectores/{id}")
    suspend fun update(@Path("id") id: Int, @Body proyector: ProyectoresDto): ProyectoresDto

    @DELETE("api/Proyectores/{id}")
    suspend fun delete(@Path("id") id: Int)
}
