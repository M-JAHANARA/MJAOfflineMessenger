package com.mja.messenger

import android.app.Application
import com.mja.messenger.data.Repo
import com.mja.messenger.net.LocalNet

class MjaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Repo.init(this)
        LocalNet.start(this)
    }

    override fun onTerminate() {
        LocalNet.stop()
        super.onTerminate()
    }
}
