package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.TipoCargoesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class TipoCargoRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getTipoCargoes(): Flow<Resource<List<TipoCargoesDto>>> = flow {
        try {
            emit(Resource.Loading())
            val result = remoteDataSource.getTipoCargoes()
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            Log.e("TipoCargo Retrofit", "Error conexión ${e.message}", e)
            emit(Resource.Error("Error de red: ${e.message}"))
        } catch (e: Exception) {
            Log.e("TipoCargo Error", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getTipoCargo(id: Int) = remoteDataSource.getTipoCargo(id)

    suspend fun createTipoCargo(tipoCargo: TipoCargoesDto) =
        remoteDataSource.createTipoCargo(tipoCargo)

    suspend fun updateTipoCargo(tipoCargo: TipoCargoesDto) =
        remoteDataSource.updateTipoCargo(tipoCargo.tipoCargoId, tipoCargo)

    suspend fun deleteTipoCargo(id: Int) = remoteDataSource.deleteTipoCargo(id)
}
