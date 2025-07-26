package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.ReporteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {
    @Upsert
    suspend fun save(reporte: ReporteEntity)

    @Query("""
        SELECT *
        FROM Reportes
        WHERE reporteId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): ReporteEntity?

    @Delete
    suspend fun delete(reporte: ReporteEntity)

    @Query("SELECT * FROM Reportes")
    fun getAll(): Flow<List<ReporteEntity>>
}
