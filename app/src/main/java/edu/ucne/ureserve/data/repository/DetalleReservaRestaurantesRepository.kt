package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaRestaurantesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class DetalleReservaRestaurantesRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getDetalleReservaRestaurantes(): Flow<Resource<List<DetalleReservaRestaurantesDto>>> = flow {
        try {
            emit(Resource.Loading())
            val detalles = remoteDataSource.getDetalleReservaRestaurantes()
            emit(Resource.Success(detalles))
        } catch (e: HttpException) {
            Log.e("Retrofit", "HTTP error: ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit", "Unknown error: ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getDetalleReservaRestaurante(id: Int): DetalleReservaRestaurantesDto =
        remoteDataSource.getDetalleReservaRestaurante(id)

    suspend fun createDetalleReservaRestaurante(detalle: DetalleReservaRestaurantesDto): DetalleReservaRestaurantesDto =
        remoteDataSource.createDetalleReservaRestaurante(detalle)

    suspend fun updateDetalleReservaRestaurante(detalle: DetalleReservaRestaurantesDto): DetalleReservaRestaurantesDto =
        remoteDataSource.updateDetalleReservaRestaurante(detalle.detalleReservaRestauranteId, detalle)

    suspend fun deleteDetalleReservaRestaurante(id: Int) =
        remoteDataSource.deleteDetalleReservaRestaurante(id)
}
