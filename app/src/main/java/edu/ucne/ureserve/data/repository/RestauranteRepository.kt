package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class RestauranteRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getRestaurantes(): Flow<Resource<List<RestaurantesDto>>> = flow {
        try {
            emit(Resource.Loading())
            val restaurantes = remoteDataSource.getRestaurantes()
            emit(Resource.Success(restaurantes))
        } catch (e: HttpException) {
            Log.e("Retrofit No connection", "Error de conexión ${e.message}", e)
            emit(Resource.Error("Error de internet: ${e.message}"))
        } catch (e: Exception) {
            Log.e("Retrofit Unknown", "Error desconocido: ${e.message}", e)
            emit(Resource.Error("Error desconocido: ${e.message}"))
        }
    }

    suspend fun getRestaurante(id: Int): RestaurantesDto = remoteDataSource.getRestaurante(id)

    suspend fun createRestaurante(restaurante: RestaurantesDto): RestaurantesDto =
        remoteDataSource.createRestaurante(restaurante)

    suspend fun updateRestaurante(restaurante: RestaurantesDto): RestaurantesDto =
        remoteDataSource.updateRestaurante(restaurante.restauranteId, restaurante)

    suspend fun deleteRestaurante(id: Int) = remoteDataSource.deleteRestaurante(id)
}
