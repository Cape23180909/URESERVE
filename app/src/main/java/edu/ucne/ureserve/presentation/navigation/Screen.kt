package edu.ucne.ureserve.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    // Estado inicial y autenticación
    @Serializable
    data object LoadStart : Screen()

    @Serializable
    data object Login : Screen()

    // Calendarios de reservas
    @Serializable
    data object CalendarioProyector : Screen()

    @Serializable
    data object CalendarioCubiculo : Screen()

    @Serializable
    data object CalendarioLaboratorio : Screen()

    // Estudiantes
    @Serializable
    data object EstudianteList : Screen()

    @Serializable
    data class EstudianteDetail(val estudianteId: Int) : Screen()

    @Serializable
    data class EditEstudiante(val estudianteId: Int) : Screen()

    @Serializable
    data class DeleteEstudiante(val estudianteId: Int) : Screen()

    // Usuarios
    @Serializable
    data object UsuarioList : Screen()

    @Serializable
    data class UsuarioDetail(val usuarioId: Int) : Screen()

    @Serializable
    data class EditUsuario(val usuarioId: Int) : Screen()

    @Serializable
    data class DeleteUsuario(val usuarioId: Int) : Screen()

    // TipoCargos
    @Serializable
    data object TipoCargoList : Screen()

    @Serializable
    data class TipoCargoDetail(val tipoCargoId: Int) : Screen()

    @Serializable
    data class EditTipoCargo(val tipoCargoId: Int) : Screen()

    @Serializable
    data class DeleteTipoCargo(val tipoCargoId: Int) : Screen()

    // Tarjetas de crédito
    @Serializable
    data object TarjetaCreditoList : Screen()

    @Serializable
    data class TarjetaCreditoDetail(val tarjetaCreditoId: Int) : Screen()

    @Serializable
    data class EditTarjetaCredito(val tarjetaCreditoId: Int) : Screen()

    @Serializable
    data class DeleteTarjetaCredito(val tarjetaCreditoId: Int) : Screen()

    // Restaurantes
    @Serializable
    data object RestauranteList : Screen()

    @Serializable
    data class RestauranteDetail(val restauranteId: Int) : Screen()

    @Serializable
    data class EditRestaurante(val restauranteId: Int) : Screen()

    @Serializable
    data class DeleteRestaurante(val restauranteId: Int) : Screen()

    // Reservaciones
    @Serializable
    data object ReservacionList : Screen()

    @Serializable
    data class ReservacionDetail(val reservacionId: Int) : Screen()

    @Serializable
    data class EditReservacion(val reservacionId: Int) : Screen()

    @Serializable
    data class DeleteReservacion(val reservacionId: Int) : Screen()

    // Reportes
    @Serializable
    data object ReporteList : Screen()

    @Serializable
    data class ReporteDetail(val reporteId: Int) : Screen()

    @Serializable
    data class EditReporte(val reporteId: Int) : Screen()

    @Serializable
    data class DeleteReporte(val reporteId: Int) : Screen()

    // Proyectores
    @Serializable
    data object ProyectorList : Screen()

    @Serializable
    data class ProyectorDetail(val proyectorId: Int) : Screen()

    @Serializable
    data class EditProyector(val proyectorId: Int) : Screen()

    @Serializable
    data class DeleteProyector(val proyectorId: Int) : Screen()

    // Laboratorios
    @Serializable
    data object LaboratorioList : Screen()

    @Serializable
    data class LaboratorioDetail(val laboratorioId: Int) : Screen()

    @Serializable
    data class EditLaboratorio(val laboratorioId: Int) : Screen()

    @Serializable
    data class DeleteLaboratorio(val laboratorioId: Int) : Screen()

    // Detalle Reserva Restaurantes
    @Serializable
    data object DetalleReservaRestaurantesList : Screen()

    @Serializable
    data class DetalleReservaRestaurantesDetail(val detalleReservaRestauranteId: Int) : Screen()

    @Serializable
    data class EditDetalleReservaRestaurante(val detalleReservaRestauranteId: Int) : Screen()

    @Serializable
    data class DeleteDetalleReservaRestaurante(val detalleReservaRestauranteId: Int) : Screen()

    // Detalle Reserva Proyectores
    @Serializable
    data object DetalleReservaProyectoresList : Screen()

    @Serializable
    data class DetalleReservaProyectorDetail(val detalleReservaProyectorId: Int) : Screen()

    @Serializable
    data class EditDetalleReservaProyector(val detalleReservaProyectorId: Int) : Screen()

    @Serializable
    data class DeleteDetalleReservaProyector(val detalleReservaProyectorId: Int) : Screen()

    // Detalle Reserva Laboratorios
    @Serializable
    data object DetalleReservaLaboratoriosList : Screen()

    @Serializable
    data class DetalleReservaLaboratorioDetail(val detalleReservaLaboratorioId: Int) : Screen()

    @Serializable
    data class EditDetalleReservaLaboratorio(val detalleReservaLaboratorioId: Int) : Screen()

    @Serializable
    data class DeleteDetalleReservaLaboratorio(val detalleReservaLaboratorioId: Int) : Screen()

    // Detalle Reserva Cubiculos
    @Serializable
    data object DetalleReservaCubiculosList : Screen()

    @Serializable
    data class DetalleReservaCubiculoDetail(val detalleReservaCubiculoId: Int) : Screen()

    @Serializable
    data class EditDetalleReservaCubiculo(val detalleReservaCubiculoId: Int) : Screen()

    @Serializable
    data class DeleteDetalleReservaCubiculo(val detalleReservaCubiculoId: Int) : Screen()

    // Cubículos
    @Serializable
    data object CubiculoList : Screen()

    @Serializable
    data class CubiculoDetail(val cubiculoId: Int) : Screen()

    @Serializable
    data class EditCubiculo(val cubiculoId: Int) : Screen()

    @Serializable
    data class DeleteCubiculo(val cubiculoId: Int) : Screen()
}
