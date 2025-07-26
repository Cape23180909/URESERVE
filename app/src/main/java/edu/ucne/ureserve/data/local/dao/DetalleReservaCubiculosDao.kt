package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.DetalleReservaCubiculosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleReservaCubiculosDao {
    @Upsert
    suspend fun save(detalleReservaCubiculo: DetalleReservaCubiculosEntity)

    @Query("""
        SELECT *
        FROM detalle_reserva_cubiculos
        WHERE detalleReservaCubiculoId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): DetalleReservaCubiculosEntity?

    @Delete
    suspend fun delete(detalleReservaCubiculo: DetalleReservaCubiculosEntity)

    @Query("SELECT * FROM detalle_reserva_cubiculos")
    fun getAll(): Flow<List<DetalleReservaCubiculosEntity>>
}
