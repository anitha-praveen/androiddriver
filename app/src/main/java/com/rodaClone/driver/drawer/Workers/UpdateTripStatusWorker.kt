package com.rodaClone.driver.drawer.Workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// Worker class to update trip status in request node firebase
open class UpdateTripStatusWorker(context: Context, params: WorkerParameters): Worker(context,params){
    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        val requestData = java.util.HashMap<String, Any>()
        var requestRef: DatabaseReference? = null
        var database = FirebaseDatabase.getInstance()
        requestRef = database.getReference("requests")
        // Get the input data
        val status = inputData.getInt("driver_trip_status",0)
        val reqId = inputData.getString("reqID")
        requestData["driver_trip_status"] = status
        requestRef.child(reqId!!)
            .updateChildren(requestData)
        return Result.Success()
    }

}