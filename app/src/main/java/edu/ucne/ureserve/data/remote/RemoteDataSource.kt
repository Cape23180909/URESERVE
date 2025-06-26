package edu.ucne.ureserve.data.remote


import edu.ucne.ureserve.data.remote.dto.CubiculosDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaCubiculosDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaLaboratoriosDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaProyectorsDto
import edu.ucne.ureserve.data.remote.dto.DetalleReservaRestaurantesDto
import edu.ucne.ureserve.data.remote.dto.LaboratoriosDto
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
    private val apilaboratorio: LaboratoriosApi,
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
    //Usuarios
    suspend fun getUsuarios(): List<UsuarioDTO> = api.getAll()

    suspend fun createUsuario(usuario: UsuarioDTO): UsuarioDTO =
        api.insert(usuario)

    suspend fun getUsuario(id: Int): UsuarioDTO = api.getById(id)

    suspend fun updateUsuario(id: Int, usuario: UsuarioDTO): UsuarioDTO =
        api.update(id, usuario)

    suspend fun deleteUsuario(id: Int) = api.delete(id)


    // Cubículos
    suspend fun getCubiculos(): List<CubiculosDto> = apicubiculo.getAll()
    suspend fun createCubiculo(cubiculo: CubiculosDto): CubiculosDto = apicubiculo.insert(cubiculo)
    suspend fun getCubiculo(id: Int): CubiculosDto = apicubiculo.getById(id)
    suspend fun updateCubiculo(id: Int, cubiculo: CubiculosDto): CubiculosDto =
        apicubiculo.update(id, cubiculo)

    suspend fun deleteCubiculo(id: Int) = apicubiculo.delete(id)

    // Laboratorios
    suspend fun getLaboratorios(): List<LaboratoriosDto> = apilaboratorio.getAll()
    suspend fun createLaboratorio(laboratorio: LaboratoriosDto): LaboratoriosDto =
        apilaboratorio.insert(laboratorio)

    suspend fun getLaboratorio(id: Int): LaboratoriosDto = apilaboratorio.getById(id)
    suspend fun updateLaboratorio(id: Int, laboratorio: LaboratoriosDto): LaboratoriosDto =
        apilaboratorio.update(id, laboratorio)

    suspend fun deleteLaboratorio(id: Int) = apilaboratorio.delete(id)

    // Proyectores
    suspend fun getProyectores(): List<ProyectoresDto> = apiproyector.getAll()
    suspend fun createProyector(proyector: ProyectoresDto): ProyectoresDto =
        apiproyector.insert(proyector)

    suspend fun getProyector(id: Int): ProyectoresDto = apiproyector.getById(id)
    suspend fun updateProyector(id: Int, proyector: ProyectoresDto): ProyectoresDto =
        apiproyector.update(id, proyector)

    suspend fun deleteProyector(id: Int) = apiproyector.delete(id)

    // Reportes
    suspend fun getReportes(): List<ReportesDto> = apiReportes.getAll()
    suspend fun getReporte(id: Int): ReportesDto = apiReportes.getById(id)
    suspend fun createReporte(reporte: ReportesDto): ReportesDto = apiReportes.insert(reporte)
    suspend fun updateReporte(id: Int, reporte: ReportesDto): ReportesDto =
        apiReportes.update(id, reporte)

    suspend fun deleteReporte(id: Int) = apiReportes.delete(id)

    // Reservaciones
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


    suspend fun updateReservacion(id: Int, reservacion: ReservacionesDto): ReservacionesDto =
        apiReservaciones.update(id, reservacion)

    suspend fun deleteReservacion(id: Int) = apiReservaciones.delete(id)

    // Restaurantes
    suspend fun getRestaurantes(): List<RestaurantesDto> = apiRestaurantes.getAll()
    suspend fun createRestaurante(restaurante: RestaurantesDto): RestaurantesDto =
        apiRestaurantes.insert(restaurante)

    suspend fun getRestaurante(id: Int): RestaurantesDto = apiRestaurantes.getById(id)
    suspend fun updateRestaurante(id: Int, restaurante: RestaurantesDto): RestaurantesDto =
        apiRestaurantes.update(id, restaurante)

    suspend fun deleteRestaurante(id: Int) = apiRestaurantes.delete(id)

    // TipoCargoes
    suspend fun getTipoCargoes(): List<TipoCargoesDto> = apiTipoCargoes.getAll()
    suspend fun getTipoCargo(id: Int): TipoCargoesDto = apiTipoCargoes.getById(id)
    suspend fun createTipoCargo(tipoCargo: TipoCargoesDto): TipoCargoesDto =
        apiTipoCargoes.insert(tipoCargo)

    suspend fun updateTipoCargo(id: Int, tipoCargo: TipoCargoesDto): TipoCargoesDto =
        apiTipoCargoes.update(id, tipoCargo)

    suspend fun deleteTipoCargo(id: Int) = apiTipoCargoes.delete(id)

    // DetalleReservaCubiculos
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

    // DetalleReservaLaboratorios
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

    // DetalleReservaProyectors
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

    suspend fun deleteDetalleReservaProyector(id: Int) {}

        // DetalleReservaRestaurantes
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