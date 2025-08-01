package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.RestauranteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestauranteDao {
    @Upsert
    suspend fun save(restaurante: RestauranteEntity)

    @Query("""
        SELECT *
        FROM Restaurantes
        WHERE restauranteId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): RestauranteEntity?

    @Delete
    suspend fun delete(restaurante: RestauranteEntity)

    @Query("SELECT * FROM Restaurantes")
    fun getAll(): Flow<List<RestauranteEntity>>
}
