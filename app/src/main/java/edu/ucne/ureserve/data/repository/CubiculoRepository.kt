package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class CubiculoRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getCubiculos(): Flow<Resource<List<CubiculosDto>>> = flow {
        try {
            emit(Resource.Loading())
            val cubiculos = remoteDataSource.getCubiculos()
            emit(Resource.Success(cubiculos))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getCubiculo(id: Int): CubiculosDto = remoteDataSource.getCubiculo(id)

    suspend fun createCubiculo(cubiculo: CubiculosDto): CubiculosDto =
        remoteDataSource.createCubiculo(cubiculo)

    suspend fun updateCubiculo(cubiculo: CubiculosDto): CubiculosDto =
        remoteDataSource.updateCubiculo(cubiculo.cubiculoId, cubiculo)

    suspend fun deleteCubiculo(id: Int) = remoteDataSource.deleteCubiculo(id)
}
