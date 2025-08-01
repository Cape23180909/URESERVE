package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.ProyectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectorDao {
    @Upsert
    suspend fun save(proyector: ProyectorEntity)

    @Query("""
        SELECT *
        FROM Proyectores
        WHERE proyectorId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): ProyectorEntity?

    @Delete
    suspend fun delete(proyector: ProyectorEntity)

    @Query("SELECT * FROM Proyectores")
    fun getAll(): Flow<List<ProyectorEntity>>
}
