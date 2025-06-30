package edu.ucne.ureserve.data.repository

import android.util.Log
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class CubiculoRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getCubiculosDisponibles(
        fecha: String,
        horaInicio: String,
        horaFin: String
    ): List<CubiculosDto> {
        // Implementar lógica para obtener proyectores disponibles
        // Esto puede variar según tu API real
        val todosCubiculos = remoteDataSource.getCubiculos()
        return todosCubiculos
    }

    suspend fun getCubiculo(id: Int): CubiculosDto = remoteDataSource.getCubiculo(id)

    suspend fun createCubiculo(cubiculo: CubiculosDto): CubiculosDto =
        remoteDataSource.createCubiculo(cubiculo)

    suspend fun updateCubiculo(cubiculo: CubiculosDto): CubiculosDto =
        remoteDataSource.updateCubiculo(cubiculo.cubiculoId, cubiculo)

    suspend fun deleteCubiculo(id: Int) = remoteDataSource.deleteCubiculo(id)
}
