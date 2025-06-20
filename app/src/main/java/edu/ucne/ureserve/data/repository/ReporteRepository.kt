package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.ReportesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class ReporteRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getReportes(): Flow<Resource<List<ReportesDto>>> = flow {
        try {
            emit(Resource.Loading())
            val reportes = remoteDataSource.getReportes()
            emit(Resource.Success(reportes))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getReporte(id: Int): ReportesDto = remoteDataSource.getReporte(id)

    suspend fun createReporte(reporte: ReportesDto): ReportesDto =
        remoteDataSource.createReporte(reporte)

    suspend fun updateReporte(reporte: ReportesDto): ReportesDto =
        remoteDataSource.updateReporte(reporte.reporteId, reporte)

    suspend fun deleteReporte(id: Int) = remoteDataSource.deleteReporte(id)
}
