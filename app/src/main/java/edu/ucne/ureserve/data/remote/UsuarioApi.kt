package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApi {
    @GET("api/Usuarios")
    suspend fun getAll(): List<UsuarioDTO>

    @GET("api/Usuarios/{id}")
    suspend fun getById(@Path("id") id: Int): UsuarioDTO

    @POST("api/Usuarios")
    suspend fun insert(@Body usuario: UsuarioDTO): UsuarioDTO

    @PUT("api/Usuarios/{id}")
    suspend fun update(@Path("id") id: Int, @Body usuario: UsuarioDTO): UsuarioDTO

    @DELETE("api/Usuarios/{id}")
    suspend fun delete(@Path("id") id: Int)
}