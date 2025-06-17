package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import javax.inject.Inject

class RemoteDataSource @Inject constructor (
    private val api: UsuarioApi
){
    suspend fun getUsuarios(): List<UsuarioDTO> = api.getUsuarios()

    suspend fun createUsuario(laboratorio: UsuarioDTO): UsuarioDTO =
        api.createUsuarios(laboratorio)

    suspend fun getUsuario(id: Int): UsuarioDTO = api.getUsuario(id)

    suspend fun updateUsuario(id: Int, laboratorio: UsuarioDTO): UsuarioDTO =
        api.updateUsuario(id, laboratorio)

    suspend fun deleteUsuario(id: Int) = api.deleteUsuarios(id)
}