package edu.ucne.ureserve.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.ureserve.data.local.dao.CubiculosDao
import edu.ucne.ureserve.data.local.dao.DetalleReservaCubiculosDao
import edu.ucne.ureserve.data.local.dao.DetalleReservaLaboratoriosDao
import edu.ucne.ureserve.data.local.dao.DetalleReservaProyectoresDao
import edu.ucne.ureserve.data.local.dao.DetalleReservaRestaurantesDao
import edu.ucne.ureserve.data.local.dao.EstudianteDao
import edu.ucne.ureserve.data.local.dao.LaboratorioDao
import edu.ucne.ureserve.data.local.dao.ProyectorDao
import edu.ucne.ureserve.data.local.dao.ReporteDao
import edu.ucne.ureserve.data.local.dao.ReservacionDao
import edu.ucne.ureserve.data.local.dao.RestauranteDao
import edu.ucne.ureserve.data.local.dao.TarjetaCreditoDao
import edu.ucne.ureserve.data.local.dao.TipoCargoDao
import edu.ucne.ureserve.data.local.dao.UsuarioDao
import edu.ucne.ureserve.data.local.entity.CubiculosEntity
import edu.ucne.ureserve.data.local.entity.DetalleReservaCubiculosEntity
import edu.ucne.ureserve.data.local.entity.DetalleReservaLaboratoriosEntity
import edu.ucne.ureserve.data.local.entity.DetalleReservaProyectoresEntity
import edu.ucne.ureserve.data.local.entity.DetalleReservaRestaurantesEntity
import edu.ucne.ureserve.data.local.entity.EstudianteEntity
import edu.ucne.ureserve.data.local.entity.LaboratorioEntity
import edu.ucne.ureserve.data.local.entity.ProyectorEntity
import edu.ucne.ureserve.data.local.entity.ReporteEntity
import edu.ucne.ureserve.data.local.entity.ReservacionEntity
import edu.ucne.ureserve.data.local.entity.RestauranteEntity
import edu.ucne.ureserve.data.local.entity.TarjetaCreditoEntity
import edu.ucne.ureserve.data.local.entity.TipoCargoEntity
import edu.ucne.ureserve.data.local.entity.UsuarioEntity

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
    version = 2,
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
