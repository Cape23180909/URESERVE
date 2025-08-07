package edu.ucne.ureserve.data.di


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.ureserve.data.local.database.UReserveDb
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.LaboratoriosApi
import edu.ucne.ureserve.data.remote.ProyectoresApi
import edu.ucne.ureserve.data.remote.RestaurantesApi
import edu.ucne.ureserve.presentation.empleados.EmpleadoViewModel
import edu.ucne.ureserve.presentation.login.AuthManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUReserveDb(@ApplicationContext context: Context): UReserveDb =
        Room.databaseBuilder(
            context,
            UReserveDb::class.java,
            "UReserve.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideEstudianteDao(db: UReserveDb) = db.estudianteDao()

    @Provides
    @Singleton
    fun provideUsuarioDao(db: UReserveDb) = db.usuarioDao()

    @Provides
    @Singleton
    fun provideTipoCargoDao(db: UReserveDb) = db.tipoCargoDao()

    @Provides
    @Singleton
    fun provideTarjetaCreditoDao(db: UReserveDb) = db.tarjetaCreditoDao()

    @Provides
    @Singleton
    fun provideRestauranteDao(db: UReserveDb) = db.restauranteDao()

    @Provides
    @Singleton
    fun provideReservacionDao(db: UReserveDb) = db.reservacionDao()

    @Provides
    @Singleton
    fun provideReporteDao(db: UReserveDb) = db.reporteDao()

    @Provides
    @Singleton
    fun provideProyectorDao(db: UReserveDb) = db.proyectorDao()

    @Provides
    @Singleton
    fun provideLaboratorioDao(db: UReserveDb) = db.laboratorioDao()

    @Provides
    @Singleton
    fun provideDetalleReservaRestaurantesDao(db: UReserveDb) = db.detalleReservaRestaurantesDao()

    @Provides
    @Singleton
    fun provideDetalleReservaProyectoresDao(db: UReserveDb) = db.detalleReservaProyectoresDao()

    @Provides
    @Singleton
    fun provideDetalleReservaLaboratoriosDao(db: UReserveDb) = db.detalleReservaLaboratoriosDao()

    @Provides
    @Singleton
    fun provideDetalleReservaCubiculosDao(db: UReserveDb) = db.detalleReservaCubiculosDao()

    @Provides
    @Singleton
    fun provideCubiculosDao(db: UReserveDb) = db.cubiculosDao()

    @Provides
    @Singleton
    fun provideAuthManager(): AuthManager {
        return AuthManager
    }

    @Provides
    @Singleton
    fun provideProyectoresViewModel(
        api: ProyectoresApi,
        apiLaboratorios: LaboratoriosApi,
        apiCubiculos: CubiculosApi,
        apiRestaurantes: RestaurantesApi
    ): EmpleadoViewModel {
        return EmpleadoViewModel(api, apiLaboratorios, apiCubiculos, apiRestaurantes)
    }
}