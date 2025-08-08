package edu.ucne.ureserve.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ProyectorRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getProyectoresDisponibles(
        fecha: String,
        horaInicio: String,
        horaFin: String
    ): List<ProyectoresDto> {
        val todosProyectores = remoteDataSource.getProyectores()
        return todosProyectores
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createDetalleReservaProyector(
        detalle: DetalleReservaProyectorsDto
    ): Response<DetalleReservaProyectorsDto> {
        return remoteDataSource.insertDetalleReservaProyector(detalle)
    }

    suspend fun getDetalleReservaProyector(id: Int): DetalleReservaProyectorsDto {
        return remoteDataSource.getDetalleReservaProyector(id)
    }

    fun getAllDetalleReservaProyector(): Flow<Resource<List<DetalleReservaProyectorsDto>>> = flow {
        try {
            emit(Resource.Loading())
            val detalles = remoteDataSource.getAllDetalleReservaProyector()
            emit(Resource.Success(detalles))
        } catch (e: HttpException) {
            Log.e("Repository", "HTTP error: ${e.message}", e)
            emit(Resource.Error("Error de red: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Repository", "Error desconocido: ${e.message}", e)
            emit(Resource.Error("Error inesperado: ${e.message}"))
        }
    }

    suspend fun updateDetalleReservaProyector(
        id: Int,
        detalle: DetalleReservaProyectorsDto
    ): DetalleReservaProyectorsDto {
        return remoteDataSource.updateDetalleReservaProyector(id, detalle)
    }

    suspend fun deleteDetalleReservaProyector(id: Int) {
        remoteDataSource.deleteDetalleReservaProyector(id)
    }

    fun getProyectores(): Flow<Resource<List<ProyectoresDto>>> = flow {
        try {
            emit(Resource.Loading())
            val proyectores = remoteDataSource.getProyectores()
            emit(Resource.Success(proyectores))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getProyector(id: Int): ProyectoresDto =
        remoteDataSource.getProyector(id)

    suspend fun createProyector(proyector: ProyectoresDto): ProyectoresDto =
        remoteDataSource.createProyector(proyector)

    suspend fun updateProyector(proyector: ProyectoresDto): ProyectoresDto =
        remoteDataSource.updateProyector(proyector.proyectorId, proyector)

    suspend fun deleteProyector(id: Int) =
        remoteDataSource.deleteProyector(id)
}