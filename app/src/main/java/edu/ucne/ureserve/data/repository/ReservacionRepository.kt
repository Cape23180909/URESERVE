package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class ReservacionRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getReservaciones(): Flow<Resource<List<ReservacionesDto>>> = flow {
        try {
            emit(Resource.Loading())
            val reservaciones = remoteDataSource.getReservaciones()
            emit(Resource.Success(reservaciones))
        } catch (e: HttpException) {
            Log.e("Retrofit Error", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Error", "Error desconocido: ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getReservacion(id: Int): ReservacionesDto = remoteDataSource.getReservacion(id)
    suspend fun createReservacion(reservacion: ReservacionesDto): ReservacionesDto =
        remoteDataSource.createReservacion(reservacion)
    suspend fun updateReservacion(reservacion: ReservacionesDto): ReservacionesDto =
        remoteDataSource.updateReservacion(reservacion.reservacionId, reservacion)
    suspend fun deleteReservacion(id: Int) = remoteDataSource.deleteReservacion(id)
}
