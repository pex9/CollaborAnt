package it.polito.lab5.model

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.lab5.R
import it.polito.lab5.viewModels.pickRandomColor
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthentication(private val context: Context, private val oneTapClient: SignInClient) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val authResult = auth.signInWithCredential(googleCredentials).await()
            val user = authResult.user
            val isNewUser = authResult.additionalUserInfo?.isNewUser

            if (isNewUser == true) {
                user?.let {
                    val name = user.displayName?.split(" ")

                    name?.let {
                        (context.applicationContext as MyApplication).model.createUser(

                            User(
                                id = user.uid,
                                first = name.first(),
                                last = if(name.size > 1) name[1] else " ",
                                nickname = "",
                                email = user.email ?: "",
                                telephone = user.phoneNumber ?: "",
                                location = "",
                                description = "",
                                imageProfile = Empty(pickRandomColor()),
                                joinedTeams = 0,
                                kpiValues = emptyMap(),
                                categories = listOf("Recently Assigned")
                            )
                        )
                    }
                }
            }

            SignInResult(
                signedInUserId = user?.uid,
                errorMessage = null
            )

        } catch(e: Exception) {
            e.printStackTrace()

            SignInResult(
                signedInUserId = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSignedInUserId(): String? {
        return auth.currentUser?.uid
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.server_client_id)) //take from firebase
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
