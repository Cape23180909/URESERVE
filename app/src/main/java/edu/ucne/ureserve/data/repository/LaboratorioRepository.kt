package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.UsuarioApi
import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class LaboratorioRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val usuarioApi: UsuarioApi
) {
    fun getLaboratorios(): Flow<Resource<List<LaboratoriosDto>>> = flow {
        try {
            emit(Resource.Loading())
            val laboratorios = remoteDataSource.getLaboratorios()
            emit(Resource.Success(laboratorios))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

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

    suspend fun getLaboratorio(id: Int): LaboratoriosDto =
        remoteDataSource.getLaboratorio(id)

    suspend fun createLaboratorio(laboratorio: LaboratoriosDto): LaboratoriosDto =
        remoteDataSource.createLaboratorio(laboratorio)

    suspend fun updateLaboratorio(laboratorio: LaboratoriosDto): LaboratoriosDto =
        remoteDataSource.updateLaboratorio(laboratorio.laboratorioId, laboratorio)

    suspend fun deleteLaboratorio(id: Int) =
        remoteDataSource.deleteLaboratorio(id)
}
