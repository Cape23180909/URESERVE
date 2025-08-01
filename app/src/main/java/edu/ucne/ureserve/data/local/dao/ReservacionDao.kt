package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.ReservacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservacionDao {
    @Upsert
    suspend fun save(reservacion: ReservacionEntity)

    @Query("""
        SELECT *
        FROM Reservaciones
        WHERE reservacionId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): ReservacionEntity?

    @Delete
    suspend fun delete(reservacion: ReservacionEntity)

    @Query("SELECT * FROM Reservaciones")
    fun getAll(): Flow<List<ReservacionEntity>>
}
