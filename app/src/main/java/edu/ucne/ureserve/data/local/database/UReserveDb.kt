package edu.ucne.ureserve.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.ureserve.data.local.dao.*
import edu.ucne.ureserve.data.local.entity.*

@Database(
    entities = [
        EstudianteEntity::class,
        UsuarioEntity::class,
        TipoCargoEntity::class,
        TarjetaCreditoEntity::class,
        RestauranteEntity::class,
        ReservacionEntity::class,
        ReporteEntity::class,
        ProyectorEntity::class,
        LaboratorioEntity::class,
        DetalleReservaRestaurantesEntity::class,
        DetalleReservaProyectoresEntity::class,
        DetalleReservaLaboratoriosEntity::class,
        DetalleReservaCubiculosEntity::class,
        CubiculosEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UReserveDb: RoomDatabase() {
    abstract fun estudianteDao(): EstudianteDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tipoCargoDao(): TipoCargoDao
    abstract fun tarjetaCreditoDao(): TarjetaCreditoDao
    abstract fun restauranteDao(): RestauranteDao
    abstract fun reservacionDao(): ReservacionDao
    abstract fun reporteDao(): ReporteDao
    abstract fun proyectorDao(): ProyectorDao
    abstract fun laboratorioDao(): LaboratorioDao
    abstract fun detalleReservaRestaurantesDao(): DetalleReservaRestaurantesDao
    abstract fun detalleReservaProyectoresDao(): DetalleReservaProyectoresDao
    abstract fun detalleReservaLaboratoriosDao(): DetalleReservaLaboratoriosDao
    abstract fun detalleReservaCubiculosDao(): DetalleReservaCubiculosDao
    abstract fun cubiculosDao(): CubiculosDao
}
