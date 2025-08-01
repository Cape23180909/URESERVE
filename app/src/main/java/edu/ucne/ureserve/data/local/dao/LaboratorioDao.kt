package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.LaboratorioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaboratorioDao {
    @Upsert
    suspend fun save(laboratorio: LaboratorioEntity)

    @Query("""
        SELECT *
        FROM Laboratorios
        WHERE laboratorioId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): LaboratorioEntity?

    @Delete
    suspend fun delete(laboratorio: LaboratorioEntity)

    @Query("SELECT * FROM Laboratorios")
    fun getAll(): Flow<List<LaboratorioEntity>>
}
