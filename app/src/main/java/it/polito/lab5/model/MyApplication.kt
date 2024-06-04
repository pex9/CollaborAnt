package it.polito.lab5.model

import android.app.Application
import com.google.android.gms.auth.api.identity.Identity

class MyApplication: Application() {
    val model = MyModel()
    lateinit var auth : GoogleAuthentication

    override fun onCreate() {
        super.onCreate()
        auth= GoogleAuthentication(
            context = this,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

}