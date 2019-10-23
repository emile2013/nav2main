package com.yumibb.android.nav2main.demo

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yumibb.android.lib.nav2main.Nav2Main

/**
 *  Demo App
 *
 * @author y.huang
 * @since 2019-10-23
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Nav2Main.getInstance().main(MainActivity::class.java).init(this) { _: Context, _: Intent ->
            Log.i("Nav2Main"," now back to  main activity")
        }
    }
}