package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.EstudianteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstudianteDao {
    @Upsert
    suspend fun save(estudiante: EstudianteEntity)

    @Query("""
        SELECT *
        FROM estudiantes
        WHERE estudianteId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): EstudianteEntity?

    @Delete
    suspend fun delete(estudiante: EstudianteEntity)

    @Query("SELECT * FROM estudiantes")
    fun getAll(): Flow<List<EstudianteEntity>>
}
