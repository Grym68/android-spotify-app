package co.uk.android.spotify

import android.app.Application
import co.uk.android.spotify.module.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.*
class ComposeApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ComposeApp)
            modules(AppModule().module)
        }
    }
}