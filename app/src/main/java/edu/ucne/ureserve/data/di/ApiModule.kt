package edu.ucne.ureserve.data.di

import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.ureserve.data.remote.CubiculosApi
import edu.ucne.ureserve.data.remote.DetalleReservaCubiculosApi
import edu.ucne.ureserve.data.remote.DetalleReservaLaboratoriosApi
import edu.ucne.ureserve.data.remote.DetalleReservaProyectorsApi
import edu.ucne.ureserve.data.remote.DetalleReservaRestaurantesApi
import edu.ucne.ureserve.data.remote.LaboratoriosApi
import edu.ucne.ureserve.data.remote.ProyectoresApi
import edu.ucne.ureserve.data.remote.ReportesApi
import edu.ucne.ureserve.data.remote.ReservacionesApi
import edu.ucne.ureserve.data.remote.RestaurantesApi
import edu.ucne.ureserve.data.remote.TarjetaCreditoApi
import edu.ucne.ureserve.data.remote.TipoCargoesApi
import edu.ucne.ureserve.data.remote.UsuarioApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "https://ureserve-hghra5gdhzgzdghk.eastus2-01.azurewebsites.net/"

    val api: UsuarioApi by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UsuarioApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Usuarios
    @Provides
    @Singleton
    fun provideUsuarioApi(retrofit: Retrofit): UsuarioApi {
        return retrofit.create(UsuarioApi::class.java)
    }

    // Cubículos
    @Provides
    @Singleton
    fun provideCubiculosApi(retrofit: Retrofit): CubiculosApi {
        return retrofit.create(CubiculosApi::class.java)
    }

    // Laboratorios
    @Provides
    @Singleton
    fun provideLaboratoriosApi(retrofit: Retrofit): LaboratoriosApi {
        return retrofit.create(LaboratoriosApi::class.java)
    }

    // Proyectores
    @Provides
    @Singleton
    fun provideProyectoresApi(retrofit: Retrofit): ProyectoresApi {
        return retrofit.create(ProyectoresApi::class.java)
    }

    // Reportes
    @Provides
    @Singleton
    fun provideReportesApi(retrofit: Retrofit): ReportesApi {
        return retrofit.create(ReportesApi::class.java)
    }

    // Reservaciones
    @Provides
    @Singleton
    fun provideReservacionesApi(retrofit: Retrofit): ReservacionesApi {
        return retrofit.create(ReservacionesApi::class.java)
    }

    // Restaurantes
    @Provides
    @Singleton
    fun provideRestaurantesApi(retrofit: Retrofit): RestaurantesApi {
        return retrofit.create(RestaurantesApi::class.java)
    }

    // TipoCargoes
    @Provides
    @Singleton
    fun provideTipoCargoesApi(retrofit: Retrofit): TipoCargoesApi {
        return retrofit.create(TipoCargoesApi::class.java)
    }

    // DetalleReservaCubiculos
    @Provides
    @Singleton
    fun provideDetalleReservaCubiculosApi(retrofit: Retrofit): DetalleReservaCubiculosApi {
        return retrofit.create(DetalleReservaCubiculosApi::class.java)
    }

    // DetalleReservaLaboratorios
    @Provides
    @Singleton
    fun provideDetalleReservaLaboratoriosApi(retrofit: Retrofit): DetalleReservaLaboratoriosApi {
        return retrofit.create(DetalleReservaLaboratoriosApi::class.java)
    }

    // DetalleReservaProyectors
    @Provides
    @Singleton
    fun provideDetalleReservaProyectorsApi(retrofit: Retrofit): DetalleReservaProyectorsApi {
        return retrofit.create(DetalleReservaProyectorsApi::class.java)
    }

    // DetalleReservaRestaurantes
    @Provides
    @Singleton
    fun provideDetalleReservaRestaurantesApi(retrofit: Retrofit): DetalleReservaRestaurantesApi {
        return retrofit.create(DetalleReservaRestaurantesApi::class.java)
    }

    // TarjetaCredito
    @Provides
    @Singleton
    fun provideTarjetaCreditoApi(retrofit: Retrofit): TarjetaCreditoApi {
        return retrofit.create(TarjetaCreditoApi::class.java)
    }

    // Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
