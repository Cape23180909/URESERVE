package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.CubiculosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CubiculosDao {
    @Upsert
    suspend fun save(cubiculo: CubiculosEntity)

    @Query("""
        SELECT *
        FROM cubiculos
        WHERE cubiculoId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): CubiculosEntity?

    @Delete
    suspend fun delete(cubiculo: CubiculosEntity)

    @Query("SELECT * FROM cubiculos")
    fun getAll(): Flow<List<CubiculosEntity>>
}
