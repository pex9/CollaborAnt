package it.polito.lab5.model

import android.app.Application
import com.google.android.gms.auth.api.identity.Identity

class MyApplication: Application() {
    lateinit var model: MyModel
    lateinit var auth : GoogleAuthentication

    override fun onCreate() {
        super.onCreate()

        model = MyModel(this)

        auth = GoogleAuthentication(
            context = this,
            oneTapClient = Identity.getSignInClient(this)
        )
    }

}