package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.DetalleReservaProyectoresEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleReservaProyectoresDao {
    @Upsert
    suspend fun save(detalleReservaProyector: DetalleReservaProyectoresEntity)

    @Query("""
        SELECT *
        FROM detalle_reserva_proyectores
        WHERE detalleReservaProyectorId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): DetalleReservaProyectoresEntity?

    @Delete
    suspend fun delete(detalleReservaProyector: DetalleReservaProyectoresEntity)

    @Query("SELECT * FROM detalle_reserva_proyectores")
    fun getAll(): Flow<List<DetalleReservaProyectoresEntity>>
}
