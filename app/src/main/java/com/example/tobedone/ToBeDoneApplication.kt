package com.example.tobedone

import android.app.Application
import com.example.tobedone.data.AppContainer
import com.example.tobedone.data.DefaultAppContainer

class ToBeDoneApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(context = this)
    }
}