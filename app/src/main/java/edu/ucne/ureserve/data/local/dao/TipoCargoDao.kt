package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.TipoCargoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoCargoDao {
    @Upsert
    suspend fun save(tipoCargo: TipoCargoEntity)

    @Query("""
        SELECT *
        FROM TipoCargos
        WHERE tipoCargoId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): TipoCargoEntity?

    @Delete
    suspend fun delete(tipoCargo: TipoCargoEntity)

    @Query("SELECT * FROM TipoCargos")
    fun getAll(): Flow<List<TipoCargoEntity>>
}
