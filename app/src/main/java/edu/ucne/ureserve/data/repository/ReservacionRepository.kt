package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.DetalleReservaRestaurantesApi
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.TarjetaCreditoApi
import edu.ucne.ureserve.data.remote.dto.DetalleReservaRestaurantesDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.TarjetaCreditoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ReservacionRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val api: ReservacionesApi,
    private val apiTarjeta: TarjetaCreditoApi,
    private val detalleReservaRestaurantesApi: DetalleReservaRestaurantesApi
) {
    suspend fun getDetalleReserva(reservacionId: Int): DetalleReservaRestaurantesDto? {
        return try {
            detalleReservaRestaurantesApi.getById(reservacionId)
        } catch (e: Exception) {
            Log.e("Repository", "Error al obtener detalle de la reserva", e)
            null
        }
    }

    suspend fun aceptarTarjeta(tarjeta: TarjetaCreditoDto) {
        apiTarjeta.insert(tarjeta)
    }

    suspend fun getReservasByMatricula(matricula: String): List<ReservacionesDto> {
        val reservas = remoteDataSource.getReservasByMatricula(matricula)
        Log.d("RESERVAS_DEBUG", "Reservas obtenidas: ${reservas.map { it.tipoReserva }}")
        return reservas
    }

    fun guardarReserva(
        reservacionDto: ReservacionesDto
    ): Flow<Resource<ReservacionesDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.insert(reservacionDto)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al guardar la reserva: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Error de red: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Error inesperado: ${e.message}"))
        }
    }

    fun getReservas(): Flow<Resource<List<ReservacionesDto>>> = flow {
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

    suspend fun guardarDetalleRestaurante(detalleDto: DetalleReservaRestaurantesDto): Boolean {
        return try {
            remoteDataSource.createDetalleReservaRestaurante(detalleDto)
            true
        } catch (e: Exception) {
            Log.e("Repository", "Error al guardar detalle restaurante", e)
            false
        }
    }

    suspend fun getReservacion(id: Int): ReservacionesDto = remoteDataSource.getReservacion(id)

    suspend fun createReservacion(reservacion: ReservacionesDto): ReservacionesDto =
        remoteDataSource.createReservacion(reservacion)

    suspend fun updateReservacion(reservacion: ReservacionesDto): Response<ReservacionesDto> =
        remoteDataSource.updateReservacion(reservacion.reservacionId, reservacion)
}