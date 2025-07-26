package edu.ucne.ureserve.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.ureserve.data.local.database.UReserveDb
import edu.ucne.ureserve.data.local.entity.UsuarioEntity
import edu.ucne.ureserve.data.local.entity.toEntity
import edu.ucne.ureserve.data.local.entity.toDto
import edu.ucne.ureserve.data.remote.RemoteDataSource
import edu.ucne.ureserve.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val remoteDataSource: RemoteDataSource,
    private val db: UReserveDb
) {

    fun login(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        try {
            emit(Resource.Loading())
            val result = auth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = result.user
            if (firebaseUser != null) {
                val usuarioDto = remoteDataSource.getUsuarios()
                    .find { it.correoInstitucional == firebaseUser.email }

                usuarioDto?.let {
                    db.usuarioDao().save(it.toEntity())  // guarda localmente para offline
                }
            }

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido al iniciar sesión"))
        }
    }

    fun getLocalUserByEmail(email: String): Flow<UsuarioEntity?> =
        db.usuarioDao().getUserByEmail(email)

    // Signup con Firebase, guarda usuario en Room después de registro exitoso
    fun signUp(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        try {
            emit(Resource.Loading())
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Puedes crear el usuario en tu API aquí si quieres (no incluido)

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido al registrarse"))
        }
    }

    // Obtener el usuario actualmente guardado localmente (offline)
    fun getLocalUser(): Flow<UsuarioEntity?> = flow {
        val localUser = db.usuarioDao().getAll().first().firstOrNull()
        emit(localUser)
    }

    // Obtener email del usuario autenticado en Firebase
    fun getUserEmail(): String? = auth.currentUser?.email

    // Cerrar sesión en Firebase
    fun logout() {
        auth.signOut()
    }

    // Obtener usuario Firebase actual
    fun getCurrentUser() = auth.currentUser

}
