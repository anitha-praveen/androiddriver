package com.rodaClone.driver.ut

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rodaClone.driver.drawer.DrawerActivity

class MyWorkerClass(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var contextCopy = context
    private val session:SessionMaintainence = SessionMaintainence(context)
    private val driverStatus = session.getBoolean(SessionMaintainence.DriverStatus)
    private val isServiceStarted = session.getBoolean(SessionMaintainence.ISSERVICESTARTED)

    override fun doWork(): Result {

        if (driverStatus){
            if (!isServiceStarted){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    contextCopy.startForegroundService(Intent(contextCopy, FloatingService::class.java))
                } else contextCopy.startService(Intent(contextCopy, FloatingService::class.java))
            }
        }
        else{
            val intentStop = Intent(contextCopy, FloatingService::class.java)
            intentStop.action = DrawerActivity.ACTION_STOP_FOREGROUND
            contextCopy.startService(intentStop)
            contextCopy.stopService(Intent(contextCopy, FloatingService::class.java))
        }
        return Result.success()
    }
}