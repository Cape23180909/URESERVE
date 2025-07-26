package edu.ucne.ureserve.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.ureserve.data.local.entity.DetalleReservaRestaurantesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleReservaRestaurantesDao {
    @Upsert
    suspend fun save(detalleReservaRestaurante: DetalleReservaRestaurantesEntity)

    @Query("""
        SELECT *
        FROM detalle_reserva_restaurantes
        WHERE detalleReservaRestauranteId = :id
        LIMIT 1
    """)
    suspend fun find(id: Int?): DetalleReservaRestaurantesEntity?

    @Delete
    suspend fun delete(detalleReservaRestaurante: DetalleReservaRestaurantesEntity)

    @Query("SELECT * FROM detalle_reserva_restaurantes")
    fun getAll(): Flow<List<DetalleReservaRestaurantesEntity>>
}
