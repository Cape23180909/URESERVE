package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: UsuarioApi
) {
    suspend fun getUsuarios(): List<UsuarioDTO> = api.getAll()

    suspend fun createUsuario(usuario: UsuarioDTO): UsuarioDTO =
        api.insert(usuario)

    suspend fun getUsuario(id: Int): UsuarioDTO = api.getById(id)

    suspend fun updateUsuario(id: Int, usuario: UsuarioDTO): UsuarioDTO =
        api.update(id, usuario)

    suspend fun deleteUsuario(id: Int) = api.delete(id)
}