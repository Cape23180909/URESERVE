package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.TarjetaCreditoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TarjetaCreditoDao {
    @Upsert
    suspend fun save(tarjetaCredito: TarjetaCreditoEntity)

    @Query("""
        SELECT *
        FROM TarjetasCredito
        WHERE tarjetaCreditoId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): TarjetaCreditoEntity?

    @Delete
    suspend fun delete(tarjetaCredito: TarjetaCreditoEntity)

    @Query("SELECT * FROM TarjetasCredito")
    fun getAll(): Flow<List<TarjetaCreditoEntity>>
}
