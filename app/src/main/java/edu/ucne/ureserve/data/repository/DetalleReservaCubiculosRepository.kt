package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.DetalleReservaCubiculosApi
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaCubiculosDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class DetalleReservaCubiculosRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getDetalleReservaCubiculos(): Flow<Resource<List<DetalleReservaCubiculosDto>>> = flow {
        try {
            emit(Resource.Loading())
            val detalles = remoteDataSource.getDetalleReservaCubiculos()
            emit(Resource.Success(detalles))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getDetalleReservaCubiculo(id: Int): DetalleReservaCubiculosDto =
        remoteDataSource.getDetalleReservaCubiculo(id)

    suspend fun createDetalleReservaCubiculo(detalle: DetalleReservaCubiculosDto): DetalleReservaCubiculosDto =
        remoteDataSource.createDetalleReservaCubiculo(detalle)

    suspend fun updateDetalleReservaCubiculo(detalle: DetalleReservaCubiculosDto): DetalleReservaCubiculosDto =
        remoteDataSource.updateDetalleReservaCubiculo(detalle.detalleReservaCubiculoId, detalle)

    suspend fun deleteDetalleReservaCubiculo(id: Int) =
        remoteDataSource.deleteDetalleReservaCubiculo(id)
}
