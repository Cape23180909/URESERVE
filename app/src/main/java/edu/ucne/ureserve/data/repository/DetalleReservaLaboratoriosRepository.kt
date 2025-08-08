package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.DetalleReservaLaboratoriosDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class DetalleReservaLaboratoriosRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getDetalleReservaLaboratorios(): Flow<Resource<List<DetalleReservaLaboratoriosDto>>> = flow {
        try {
            emit(Resource.Loading())
            val detalles = remoteDataSource.getDetalleReservaLaboratorios()
            emit(Resource.Success(detalles))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }
    suspend fun getDetalleReservaLaboratorio(id: Int): DetalleReservaLaboratoriosDto =
        remoteDataSource.getDetalleReservaLaboratorio(id)

    suspend fun createDetalleReservaLaboratorio(detalle: DetalleReservaLaboratoriosDto): DetalleReservaLaboratoriosDto =
        remoteDataSource.createDetalleReservaLaboratorio(detalle)

    suspend fun updateDetalleReservaLaboratorio(detalle: DetalleReservaLaboratoriosDto): DetalleReservaLaboratoriosDto =
        remoteDataSource.updateDetalleReservaLaboratorio(detalle.detalleReservaLaboratorioId, detalle)

    suspend fun deleteDetalleReservaLaboratorio(id: Int) =
        remoteDataSource.deleteDetalleReservaLaboratorio(id)
}
