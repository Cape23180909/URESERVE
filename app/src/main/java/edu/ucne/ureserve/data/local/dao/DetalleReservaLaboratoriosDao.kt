package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.DetalleReservaLaboratoriosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleReservaLaboratoriosDao {
    @Upsert
    suspend fun save(detalleReservaLaboratorio: DetalleReservaLaboratoriosEntity)

    @Query("""
        SELECT *
        FROM detalle_reserva_laboratorios
        WHERE detalleReservaLaboratorioId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): DetalleReservaLaboratoriosEntity?

    @Delete
    suspend fun delete(detalleReservaLaboratorio: DetalleReservaLaboratoriosEntity)

    @Query("SELECT * FROM detalle_reserva_laboratorios")
    fun getAll(): Flow<List<DetalleReservaLaboratoriosEntity>>
}
