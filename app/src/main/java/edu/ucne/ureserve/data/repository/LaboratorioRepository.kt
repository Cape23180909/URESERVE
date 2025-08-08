package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.UsuarioApi
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import javax.inject.Inject

class LaboratorioRepository @Inject constructor(
    private val usuarioApi: UsuarioApi
) {
    suspend fun buscarUsuarioPorMatricula(matricula: String): UsuarioDTO? {
        return try {
            val usuarios = usuarioApi.getAll()
            Log.d("Repository", "Usuarios recuperados: ${usuarios.size}")
            val normalizedMatricula = matricula.replace("-", "")
            val usuario = usuarios.find { usuario ->
                val userMatricula = usuario.estudiante?.matricula?.replace("-", "") ?: ""
                userMatricula.equals(normalizedMatricula, ignoreCase = true)
            }
            if (usuario != null) {
                Log.d("Repository", "Usuario encontrado: ${usuario.nombres}")
            } else {
                Log.d("Repository", "Usuario no encontrado para la matrícula: $matricula")
            }
            usuario
        } catch (e: Exception) {
            Log.e("Repository", "Error buscando usuario", e)
            null
        }
    }
}