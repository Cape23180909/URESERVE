package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class LaboratorioRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
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

    suspend fun getLaboratorio(id: Int): LaboratoriosDto =
        remoteDataSource.getLaboratorio(id)

    suspend fun createLaboratorio(laboratorio: LaboratoriosDto): LaboratoriosDto =
        remoteDataSource.createLaboratorio(laboratorio)

    suspend fun updateLaboratorio(laboratorio: LaboratoriosDto): LaboratoriosDto =
        remoteDataSource.updateLaboratorio(laboratorio.laboratorioId, laboratorio)

    suspend fun deleteLaboratorio(id: Int) =
        remoteDataSource.deleteLaboratorio(id)
}
