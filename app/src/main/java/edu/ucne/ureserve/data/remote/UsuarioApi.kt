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
    suspend fun getUsuarios(): List<UsuarioDTO>

    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): UsuarioDTO

    @POST("api/Usuarios")
    suspend fun createUsuarios(@Body usuarioDto: UsuarioDTO): UsuarioDTO

    @PUT("api/Usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuarioDto: UsuarioDTO): UsuarioDTO

    @DELETE("api/Usuarios/{id}")
    suspend fun deleteUsuarios(@Path("id") id: Int)
}