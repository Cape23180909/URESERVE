package edu.ucne.ureserve.data.remote

import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaCubiculosDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaLaboratoriosDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaRestaurantesDto
import edu.ucne.ureserve.data.remote.dto.ProyectoresDto
import edu.ucne.ureserve.data.remote.dto.ReportesDto
import edu.ucne.ureserve.data.remote.dto.ReservacionesDto
import edu.ucne.ureserve.data.remote.dto.RestaurantesDto
import edu.ucne.ureserve.data.remote.dto.TipoCargoesDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: UsuarioApi,
    private val apicubiculo: CubiculosApi,
    private val apiReportes: ReportesApi,
    private val apiReservaciones: ReservacionesApi,
    private val apiRestaurantes: RestaurantesApi,
    private val apiTipoCargoes: TipoCargoesApi,
    private val apiproyector: ProyectoresApi,
    private val apiDetalleReservaCubiculos: DetalleReservaCubiculosApi,
    private val apiDetalleReservaLaboratorios: DetalleReservaLaboratoriosApi,
    private val apiDetalleReservaProyectors: DetalleReservaProyectorsApi,
    private val apiDetalleReservaRestaurantes: DetalleReservaRestaurantesApi
) {
    suspend fun getUsuarios(): List<UsuarioDTO> = api.getAll()

    suspend fun getCubiculos(): List<CubiculosDto> = apicubiculo.getAll()
    suspend fun createCubiculo(cubiculo: CubiculosDto): CubiculosDto = apicubiculo.insert(cubiculo)
    suspend fun updateCubiculo(id: Int, cubiculo: CubiculosDto): CubiculosDto =
        apicubiculo.update(id, cubiculo)

    suspend fun deleteCubiculo(id: Int) = apicubiculo.delete(id)

    suspend fun getProyectores(): List<ProyectoresDto> = apiproyector.getAll()
    suspend fun createProyector(proyector: ProyectoresDto): ProyectoresDto =
        apiproyector.insert(proyector)

    suspend fun getProyector(id: Int): ProyectoresDto = apiproyector.getById(id)
    suspend fun updateProyector(id: Int, proyector: ProyectoresDto): ProyectoresDto =
        apiproyector.update(id, proyector)

    suspend fun deleteProyector(id: Int) = apiproyector.delete(id)

    suspend fun getReportes(): List<ReportesDto> = apiReportes.getAll()
    suspend fun getReporte(id: Int): ReportesDto = apiReportes.getById(id)
    suspend fun createReporte(reporte: ReportesDto): ReportesDto = apiReportes.insert(reporte)
    suspend fun updateReporte(id: Int, reporte: ReportesDto): ReportesDto =
        apiReportes.update(id, reporte)

    suspend fun deleteReporte(id: Int) = apiReportes.delete(id)

    suspend fun getReservaciones(): List<ReservacionesDto> = apiReservaciones.getAll()
    suspend fun getReservacion(id: Int): ReservacionesDto = apiReservaciones.getById(id)
    suspend fun createReservacion(reservacion: ReservacionesDto): ReservacionesDto {
        val response = apiReservaciones.insert(reservacion)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("La respuesta del servidor está vacía")
        } else {
            throw Exception("Error al crear la reservación: ${response.code()} - ${response.message()}")
        }
    }
    suspend fun getReservasByMatricula(matricula: String): List<ReservacionesDto> =
        apiReservaciones.getReservasByMatricula(matricula)

    suspend fun updateReservacion(id: Int, reservacion: ReservacionesDto): Response<ReservacionesDto> =
        apiReservaciones.update(id, reservacion)

    suspend fun getRestaurantes(): List<RestaurantesDto> = apiRestaurantes.getAll()
    suspend fun createRestaurante(restaurante: RestaurantesDto): RestaurantesDto =
        apiRestaurantes.insert(restaurante)

    suspend fun getRestaurante(id: Int): RestaurantesDto = apiRestaurantes.getById(id)
    suspend fun updateRestaurante(restauranteId: Int, restaurante: RestaurantesDto): RestaurantesDto =
        apiRestaurantes.update(restauranteId, restaurante)

    suspend fun deleteRestaurante(id: Int) = apiRestaurantes.delete(id)

    suspend fun getTipoCargoes(): List<TipoCargoesDto> = apiTipoCargoes.getAll()
    suspend fun getTipoCargo(id: Int): TipoCargoesDto = apiTipoCargoes.getById(id)
    suspend fun createTipoCargo(tipoCargo: TipoCargoesDto): TipoCargoesDto =
        apiTipoCargoes.insert(tipoCargo)

    suspend fun updateTipoCargo(id: Int, tipoCargo: TipoCargoesDto): TipoCargoesDto =
        apiTipoCargoes.update(id, tipoCargo)

    suspend fun deleteTipoCargo(id: Int) = apiTipoCargoes.delete(id)

    suspend fun getDetalleReservaCubiculos(): List<DetalleReservaCubiculosDto> =
        apiDetalleReservaCubiculos.getAll()

    suspend fun getDetalleReservaCubiculo(id: Int): DetalleReservaCubiculosDto =
        apiDetalleReservaCubiculos.getById(id)

    suspend fun createDetalleReservaCubiculo(detalle: DetalleReservaCubiculosDto): DetalleReservaCubiculosDto =
        apiDetalleReservaCubiculos.insert(detalle)

    suspend fun updateDetalleReservaCubiculo(
        id: Int,
        detalle: DetalleReservaCubiculosDto
    ): DetalleReservaCubiculosDto =
        apiDetalleReservaCubiculos.update(id, detalle)

    suspend fun deleteDetalleReservaCubiculo(id: Int) =
        apiDetalleReservaCubiculos.delete(id)

    suspend fun getDetalleReservaLaboratorios(): List<DetalleReservaLaboratoriosDto> =
        apiDetalleReservaLaboratorios.getAll()

    suspend fun getDetalleReservaLaboratorio(id: Int): DetalleReservaLaboratoriosDto =
        apiDetalleReservaLaboratorios.getById(id)

    suspend fun createDetalleReservaLaboratorio(detalle: DetalleReservaLaboratoriosDto): DetalleReservaLaboratoriosDto =
        apiDetalleReservaLaboratorios.insert(detalle)

    suspend fun updateDetalleReservaLaboratorio(
        id: Int,
        detalle: DetalleReservaLaboratoriosDto
    ): DetalleReservaLaboratoriosDto =
        apiDetalleReservaLaboratorios.update(id, detalle)

    suspend fun deleteDetalleReservaLaboratorio(id: Int) =
        apiDetalleReservaLaboratorios.delete(id)

    suspend fun insertDetalleReservaProyector(
        detalle: DetalleReservaProyectorsDto
    ): Response<DetalleReservaProyectorsDto> {
        return apiDetalleReservaProyectors.insert(detalle)
    }

    suspend fun getAllDetalleReservaProyector(): List<DetalleReservaProyectorsDto> {
        return apiDetalleReservaProyectors.getAll()
    }

    suspend fun getDetalleReservaProyector(id: Int): DetalleReservaProyectorsDto {
        return apiDetalleReservaProyectors.getById(id)
    }

    suspend fun updateDetalleReservaProyector(
        id: Int,
        detalle: DetalleReservaProyectorsDto
    ): DetalleReservaProyectorsDto {
        return apiDetalleReservaProyectors.update(id, detalle)
    }

    suspend fun createDetalleReservaProyector(
        detalle: DetalleReservaProyectorsDto
    ): Response<DetalleReservaProyectorsDto> {
        return apiDetalleReservaProyectors.insert(detalle)
    }

    suspend fun getDetalleReservaRestaurantes(): List<DetalleReservaRestaurantesDto> =
        apiDetalleReservaRestaurantes.getAll()

    suspend fun getDetalleReservaRestaurante(id: Int): DetalleReservaRestaurantesDto =
        apiDetalleReservaRestaurantes.getById(id)

    suspend fun createDetalleReservaRestaurante(detalle: DetalleReservaRestaurantesDto): DetalleReservaRestaurantesDto =
        apiDetalleReservaRestaurantes.insert(detalle)

    suspend fun updateDetalleReservaRestaurante(
        id: Int,
        detalle: DetalleReservaRestaurantesDto
    ): DetalleReservaRestaurantesDto =
        apiDetalleReservaRestaurantes.update(id, detalle)

    suspend fun deleteDetalleReservaRestaurante(id: Int) =
        apiDetalleReservaRestaurantes.delete(id)
}