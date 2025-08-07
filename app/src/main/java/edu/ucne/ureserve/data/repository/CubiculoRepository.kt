package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.local.database.UReserveDb
import edu.ucne.ureserve.data.local.entity.CubiculosEntity
import edu.ucne.ureserve.data.local.entity.toDto
import edu.ucne.ureserve.data.local.entity.toEntity
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.UsuarioApi
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CubiculoRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val usuarioApi: UsuarioApi,
    private val db: UReserveDb
) {

    suspend fun save(cubiculo: CubiculosEntity) {
        try {
            if (cubiculo.cubiculoId != 0) {
                remoteDataSource.updateCubiculo(cubiculo.cubiculoId, cubiculo.toDto())
            } else {
                remoteDataSource.createCubiculo(cubiculo.toDto())
            }
        } catch (e: Exception) {
            Log.e("CubiculoRepository", "Error sincronizando con API (save)", e)
        }

        db.cubiculosDao().save(cubiculo)
    }

    suspend fun delete(cubiculo: CubiculosEntity) {
        try {
            if (cubiculo.cubiculoId != 0) {
                remoteDataSource.deleteCubiculo(cubiculo.cubiculoId)
            }
        } catch (e: Exception) {
            Log.e("CubiculoRepository", "Error sincronizando con API (delete)", e)
        }

        db.cubiculosDao().delete(cubiculo)
    }

    suspend fun find(id: Int): CubiculosEntity? {
        return db.cubiculosDao().find(id)
    }

    fun getAll(): Flow<Resource<List<CubiculosEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val localData = db.cubiculosDao().getAll().first()
            emit(Resource.Success(localData))

            val remoteCubiculos = remoteDataSource.getCubiculos()
            remoteCubiculos.forEach {
                db.cubiculosDao().save(it.toEntity())
            }

            val updatedData = db.cubiculosDao().getAll().first()
            emit(Resource.Success(updatedData))
        } catch (e: Exception) {
            Log.e("CubiculoRepository", "Error sincronizando datos", e)
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    suspend fun getCubiculosDisponibles(fecha: String, horaInicio: String, horaFin: String) =
        remoteDataSource.getCubiculos()

    suspend fun getUsuarioById(id: Int): UsuarioDTO {
        return try {
            usuarioApi.getById(id)
        } catch (e: Exception) {
            throw Exception("Error al obtener usuario: ${e.message}")
        }
    }

    suspend fun buscarUsuarioPorMatricula(matricula: String): UsuarioDTO? {
        return try {
            val usuarios = usuarioApi.getAll()
            val normalizedMatricula = matricula.replace("-", "")
            usuarios.find {
                val userMatricula = it.estudiante?.matricula?.replace("-", "") ?: ""
                userMatricula.equals(normalizedMatricula, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e("CubiculoRepository", "Error buscando usuario", e)
            null
        }
    }
}