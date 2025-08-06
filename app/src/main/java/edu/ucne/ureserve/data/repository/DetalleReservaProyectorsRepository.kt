package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class DetalleReservaProyectorsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getDetalleReservaProyectors(): Flow<Resource<List<DetalleReservaProyectorsDto>>> = flow {
        try {
            emit(Resource.Loading())
            val detalles = remoteDataSource.getAllDetalleReservaProyector()
            emit(Resource.Success(detalles))
        } catch (e: HttpException) {
            Log.e("Retrofit", "HTTP error: ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit", "Unknown error: ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getDetalleReservaProyector(id: Int): DetalleReservaProyectorsDto =
        remoteDataSource.getDetalleReservaProyector(id)

    suspend fun createDetalleReservaProyector(
        detalle: DetalleReservaProyectorsDto
    ): Response<DetalleReservaProyectorsDto> {
        return remoteDataSource.createDetalleReservaProyector(detalle)
    }

    suspend fun updateDetalleReservaProyector(detalle: DetalleReservaProyectorsDto): DetalleReservaProyectorsDto =
        remoteDataSource.updateDetalleReservaProyector(detalle.detalleReservaProyectorId, detalle)

    suspend fun deleteDetalleReservaProyector(id: Int) =
        remoteDataSource.deleteDetalleReservaProyector(id)
}